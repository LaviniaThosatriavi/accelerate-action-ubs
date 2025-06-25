import React from 'react';
import {
  Typography,
  Box,
  Chip,
  Alert,
  CardContent,
  Grid,
} from '@mui/material';
import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  Tooltip,
  Legend,
} from 'recharts';
import {
  FiUsers,
  FiTrendingUp,
  FiAward,
  FiStar,
} from 'react-icons/fi';
import {
  IoMdTrophy,
  IoMdRocket,
} from 'react-icons/io';

import type { CompetitiveData } from '../../types/ReportTypes';
import {
  ChartContainer,
  ProgressBar,
  InsightCard,
  MetricCard,
  RankCard,
  StatsGrid,
  SectionContainer,
  SectionTitle,
  colors,
} from '../../styles/ReportStyles';

interface CustomTooltipProps {
  active?: boolean;
  payload?: Array<{
    name: string;
    value: number;
    color: string;
  }>;
  label?: string;
}

interface CompetitiveTabProps {
  competitive: CompetitiveData | null;
  activeTab: number;
  customTooltip: React.ComponentType<CustomTooltipProps>;
  getBadgeColor: (level: string) => string;
}

const getResponsiveRadius = (minRadius: number, maxRadius: number) => {
  if (typeof window !== 'undefined') {
    const vw = window.innerWidth;
    const ratio = Math.min(Math.max((vw - 280) / (1200 - 280), 0), 1);
    return Math.round(minRadius + (maxRadius - minRadius) * ratio);
  }
  return maxRadius;
};

const getResponsiveHeight = () => {
  if (typeof window !== 'undefined') {
    return window.innerWidth < 500 ? 180 : 250;
  }
  return 250;
};

const CompetitiveTab: React.FC<CompetitiveTabProps> = ({
  competitive,
  activeTab,
  customTooltip: CustomTooltip,
  getBadgeColor,
}) => {
  const rankingPositionData = [
    { name: 'Users Above You', value: (competitive?.metrics.currentRank || 1) - 1, fill: colors.gray[300] },
    { name: 'Your Position', value: 1, fill: colors.primary },
    { name: 'Users Below You', value: (competitive?.metrics.totalUsers || 1250) - (competitive?.metrics.currentRank || 142), fill: colors.success }
  ];

  return (
    <>
      {activeTab === 4 && (
        <CardContent sx={{ padding: '16px' }}>
          <SectionTitle style={{
            marginBottom: '1vh',
            fontSize: 'clamp(1rem, 2vw, 1.5rem)'
          }}>
            <FiUsers 
              style={{ fontSize: 'clamp(18px, 3vw, 24px)' }}
              color={colors.primary} 
            />
            Competitive Analysis
          </SectionTitle>
          <Typography 
            textAlign="left" 
            variant="body1" 
            color="text.secondary" 
            sx={{ 
              marginBottom: '1.5vh',
              fontSize: 'clamp(0.7rem, 2vw, 0.9rem)'
            }}
          >
            {competitive?.summary}
          </Typography>

          <Grid 
            container 
            spacing={4} 
            sx={{ 
              marginBottom: 'clamp(1rem, 2vw, 2rem)',
              '@media (max-width: 600px)': {
                rowGap: '0', 
                '& > .MuiGrid-root': {
                  paddingTop: '0 !important',
                }
              }
            }}
          >
            <Grid size={{ xs: 12, sm: 6 }}>
              <ChartContainer>
                <Typography 
                  variant="h6" 
                  fontWeight="bold" 
                  sx={{ 
                    marginBottom: '0.5vh',
                    fontSize: 'clamp(0.9rem, 2.5vw, 1.25rem)'
                  }}
                >
                  Your Position
                </Typography>
                <ResponsiveContainer width="100%" height={getResponsiveHeight()}>
                  <PieChart>
                    <Pie
                      data={rankingPositionData}
                      cx="50%"
                      cy="50%"
                      innerRadius={getResponsiveRadius(25, 45)}
                      outerRadius={getResponsiveRadius(45, 80)}
                      dataKey="value"
                    >
                      {rankingPositionData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.fill} />
                      ))}
                    </Pie>
                    <Tooltip content={<CustomTooltip />} />
                    <Legend 
                      wrapperStyle={{
                        fontSize: 'clamp(0.75rem, 2vw, 1rem)'
                      }}
                    />
                  </PieChart>
                </ResponsiveContainer>
              </ChartContainer>
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <RankCard>
                <CardContent sx={{ 
                  textAlign: 'center', 
                  position: 'relative', 
                  zIndex: 1,
                  padding: 'clamp(10px, 2vw, 16px) !important'
                }}>
                  <FiUsers 
                    style={{ fontSize: 'clamp(48px, 8vw, 64px)' }}
                    color="black"
                  />
                  <Typography 
                    variant="h2" 
                    fontWeight="bold" 
                    color="red"
                    sx={{ fontSize: 'clamp(2rem, 6vw, 3.75rem)' }}
                  >
                    #{competitive?.metrics.currentRank}
                  </Typography>
                  <Typography 
                    variant="h6" 
                    color="black" 
                    sx={{ 
                      marginTop: 0,
                      fontSize: 'clamp(1rem, 2.5vw, 1.25rem)'
                    }}
                  >
                    Current Rank
                  </Typography>
                  <Typography 
                    variant="body2" 
                    color="black"
                    sx={{ fontSize: 'clamp(0.75rem, 2vw, 0.875rem)' }}
                  >
                    out of {competitive?.metrics.totalUsers.toLocaleString()} users
                  </Typography>
                  <Chip 
                    label={competitive?.metrics.percentile}
                    sx={{ 
                      marginTop: 0,
                      background: 'rgba(255,255,255,0.2)', 
                      color: 'black',
                      fontWeight: 'bold',
                      fontSize: 'clamp(0.7rem, 1.5vw, 0.8rem)'
                    }}
                  />
                </CardContent>
              </RankCard>
            </Grid>
          </Grid>

          <StatsGrid>
            <MetricCard gradient={`linear-gradient(135deg, ${colors.success}, #059669)`}>
              <CardContent sx={{ 
                textAlign: 'center',
                padding: 'clamp(10px, 2vw, 16px) !important'
              }}>
                <FiStar 
                  style={{ fontSize: 'clamp(28px, 5vw, 36px)' }}
                  color="blue" 
                />
                <Box>
                  <Typography 
                    variant="h4" 
                    fontWeight="bold" 
                    color="blue" 
                    sx={{ 
                      marginTop: 1,
                      fontSize: 'clamp(1.5rem, 4vw, 2.125rem)'
                    }}
                  >
                    {competitive?.metrics.pointsThisWeek}
                  </Typography>
                  <Typography 
                    variant="caption" 
                    color="text.secondary"
                    sx={{ fontSize: 'clamp(0.7rem, 1.5vw, 0.75rem)' }}
                  >
                    Points This Week
                  </Typography>
                </Box>
              </CardContent>
            </MetricCard>
            
            <MetricCard>
              <CardContent sx={{ 
                textAlign: 'center',
                padding: 'clamp(10px, 2vw, 16px) !important'
              }}>
                <FiTrendingUp 
                  style={{ fontSize: 'clamp(28px, 5vw, 36px)' }}
                  color={colors.success} 
                />
                <Typography 
                  variant="h4" 
                  fontWeight="bold" 
                  sx={{ 
                    marginTop: 1,
                    fontSize: 'clamp(1.5rem, 4vw, 2.125rem)'
                  }}
                >
                  {competitive?.metrics.pointsToNextRank}
                </Typography>
                <Typography 
                  variant="caption" 
                  color="text.secondary"
                  sx={{ fontSize: 'clamp(0.7rem, 1.5vw, 0.75rem)' }}
                >
                  Points to Next Rank
                </Typography>
              </CardContent>
            </MetricCard>
            
            <MetricCard>
              <CardContent sx={{ 
                textAlign: 'center',
                padding: 'clamp(10px, 2vw, 16px) !important'
              }}>
                <FiAward 
                  style={{ fontSize: 'clamp(28px, 5vw, 36px)' }}
                  color={getBadgeColor(competitive?.metrics.badgeLevel || 'SILVER')} 
                />
                <Typography 
                  variant="h4" 
                  fontWeight="bold" 
                  sx={{ 
                    marginTop: 1,
                    fontSize: 'clamp(1.5rem, 4vw, 2.125rem)'
                  }}
                >
                  {competitive?.metrics.pointsToNextBadge}
                </Typography>
                <Typography 
                  variant="caption" 
                  color="text.secondary"
                  sx={{ fontSize: 'clamp(0.7rem, 1.5vw, 0.75rem)' }}
                >
                  Points to Next Badge
                </Typography>
              </CardContent>
            </MetricCard>
          </StatsGrid>

          <SectionContainer
            style={{
              marginBottom: 'clamp(1rem, 2vw, 2rem)'
            }}
          >
            <SectionTitle style={{
              fontSize: 'clamp(1rem, 2vw, 1.5rem)',
              marginBottom: 'clamp(0.5rem, 1vw, 1rem)'
            }}>
              Progress to Next Rank
            </SectionTitle>
            <MetricCard>
              <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px)' }}>
                <Box display="flex" justifyContent="space-between" sx={{ marginBottom: 1 }}>
                  <Typography 
                    variant="body2" 
                    color="text.secondary"
                    sx={{ fontSize: 'clamp(0.75rem, 2vw, 0.875rem)' }}
                  >
                    Current Progress
                  </Typography>
                  <Typography 
                    variant="body2" 
                    fontWeight="bold"
                    sx={{ fontSize: 'clamp(0.75rem, 2vw, 0.875rem)' }}
                  >
                    {Math.round(((competitive?.metrics.pointsThisWeek || 0) / ((competitive?.metrics.pointsThisWeek || 0) + (competitive?.metrics.pointsToNextRank || 1))) * 100)}%
                  </Typography>
                </Box>
                <ProgressBar 
                  progress={Math.round(((competitive?.metrics.pointsThisWeek || 0) / ((competitive?.metrics.pointsThisWeek || 0) + (competitive?.metrics.pointsToNextRank || 1))) * 100)}
                  color={colors.primary}
                />
              </CardContent>
            </MetricCard>
          </SectionContainer>

          <Grid 
            container 
            spacing={3}
            sx={{
              marginTop: 'clamp(0.5rem, 1.5vw, 1.5rem)',
              marginBottom: 'clamp(1rem, 2vw, 2rem)'
            }}
          >
            <Grid size={{ xs: 12, md: 6 }}>
              <SectionTitle style={{
                fontSize: 'clamp(1rem, 2vw, 1.5rem)',
                marginBottom: 'clamp(0.5rem, 1vw, 1rem)'
              }}>
                Competitive Insights
              </SectionTitle>
              {competitive?.competitiveInsights.map((insight: string, index: number) => (
                <InsightCard key={index} variant="warning">
                  <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px)' }}>
                    <Box display="flex" alignItems="center" sx={{ gap: 'clamp(0.5rem, 1vw, 1rem)' }}>
                      <IoMdTrophy 
                        style={{ fontSize: 'clamp(16px, 2.5vw, 20px)' }}
                        color={colors.warning} 
                      />
                      <Typography 
                        variant="body2"
                        sx={{ fontSize: 'clamp(0.75rem, 2vw, 0.875rem)' }}
                      >
                        {insight}
                      </Typography>
                    </Box>
                  </CardContent>
                </InsightCard>
              ))}
            </Grid>

            <Grid size={{ xs: 12, md: 6 }}>
              <SectionTitle style={{
                fontSize: 'clamp(1rem, 2vw, 1.5rem)',
                marginBottom: 'clamp(0.5rem, 1vw, 1rem)'
              }}>
                Keep Going!
              </SectionTitle>
              {competitive?.motivationalMessages.map((message: string, index: number) => (
                <Alert 
                  key={index}
                  severity="success"
                  icon={<IoMdRocket style={{ fontSize: 'clamp(16px, 2.5vw, 20px)' }} />}
                  sx={{ 
                    marginBottom: 'clamp(0.5rem, 1vw, 1rem)',
                    borderRadius: '12px',
                    background: 'linear-gradient(135deg, #ecfdf5, #d1fae5)'
                  }}
                >
                  <Typography 
                    textAlign="left" 
                    variant="body2" 
                    fontWeight="500"
                    sx={{ fontSize: 'clamp(0.75rem, 2vw, 0.875rem)' }}
                  >
                    {message}
                  </Typography>
                </Alert>
              ))}
            </Grid>
          </Grid>
        </CardContent>
      )}
    </>
  );
};

export default CompetitiveTab;