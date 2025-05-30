package com.backend.TTP.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.backend.TTP.service.CustomUserDetailsServiceImpl;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsServiceImpl customUserDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                  CustomUserDetailsServiceImpl customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        logger.debug("Processing request for path: {}", path);
        
        // Skip JWT check for authentication endpoints and public paths
        if (path.startsWith("/api/auth/") || 
            path.startsWith("/h2-console") || 
            path.startsWith("/error")) {
            logger.debug("Skipping JWT validation for public path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", authorizationHeader);
        
        String token = getJwtFromRequest(request);
        
        if (token != null) {
            logger.debug("JWT token found in request");
            try {
                String username = jwtUtil.extractUsername(token);
                logger.debug("Extracted username from token: {}", username);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                    logger.debug("Loaded user details for: {}", username);
                    
                    boolean isValid = jwtUtil.validateToken(token, userDetails);
                    logger.debug("Token validation result: {}", isValid);
                    
                    if (isValid) {
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                            );
                        authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.debug("Authentication set in SecurityContext for user: {}", username);
                    } else {
                        logger.warn("Invalid token for user: {}", username);
                    }
                } else {
                    logger.debug("Username null or authentication already exists in context");
                }
            } catch (Exception e) {
                logger.error("Cannot set user authentication: {}", e.getMessage(), e);
            }
        } else {
            logger.debug("No JWT token found in request");
        }
        
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}