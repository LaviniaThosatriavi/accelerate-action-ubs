import styled, { keyframes } from 'styled-components';
import { Card, Box } from '@mui/material';

// Animations
export const fadeInUp = keyframes`
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
`;

export const slideIn = keyframes`
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
`;

export const scaleIn = keyframes`
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
`;

export const ReportContainer = styled.div`
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  animation: ${fadeInUp} 0.6s ease-out;
`;

export const HeaderCard = styled(Card)`
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
`;

export const MetricCard = styled(Card) <{ gradient?: string }>`
  background: ${props => props.gradient || 'linear-gradient(135deg, #ffffff 0%, #f8fafc 100%)'};
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  }
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: ${props => props.gradient || 'linear-gradient(90deg, #667eea, #764ba2)'};
  }
`;

export const ChartContainer = styled(Box)`
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  margin-bottom: 24px;
  
  .recharts-wrapper {
    font-family: 'Inter', sans-serif;
  }
  
  .recharts-legend-wrapper {
    padding-top: 20px;
  }
  
  .recharts-tooltip-wrapper {
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
`;

export const IconWrapper = styled.div<{ bgColor?: string }>`
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: ${props => props.bgColor || 'linear-gradient(135deg, #667eea, #764ba2)'};
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 12px;
  transition: transform 0.2s ease;
  
  &:hover {
    transform: scale(1.05);
  }
`;

export const ProgressBar = styled.div<{ progress: number; color: string }>`
  width: 100%;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    height: 100%;
    width: ${props => props.progress}%;
    background: ${props => props.color};
    border-radius: 4px;
    transition: width 1.2s cubic-bezier(0.4, 0, 0.2, 1);
  }
`;

export const CircularProgress = styled.div<{ size: number; progress: number; color: string }>`
  width: ${props => props.size}px;
  height: ${props => props.size}px;
  border-radius: 50%;
  background: conic-gradient(
    ${props => props.color} ${props => props.progress * 3.6}deg,
    #e2e8f0 ${props => props.progress * 3.6}deg
  );
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  
  &::before {
    content: '';
    width: ${props => props.size - 16}px;
    height: ${props => props.size - 16}px;
    border-radius: 50%;
    background: white;
    position: absolute;
  }
`;

export const ProgressContent = styled.div`
  position: relative;
  z-index: 1;
  text-align: center;
`;

// Insight Cards
export const InsightCard = styled(Card) <{ variant?: 'success' | 'warning' | 'info' | 'error' }>`
  border-radius: 12px;
  margin: 8px 0;
  border: 1px solid ${props => {
        switch (props.variant) {
            case 'success': return '#d1fae5';
            case 'warning': return '#fef3c7';
            case 'info': return '#dbeafe';
            case 'error': return '#fee2e2';
            default: return '#e2e8f0';
        }
    }};
  background: ${props => {
        switch (props.variant) {
            case 'success': return 'linear-gradient(135deg, #ecfdf5, #d1fae5)';
            case 'warning': return 'linear-gradient(135deg, #fffbeb, #fef3c7)';
            case 'info': return 'linear-gradient(135deg, #eff6ff, #dbeafe)';
            case 'error': return 'linear-gradient(135deg, #fef2f2, #fee2e2)';
            default: return 'linear-gradient(135deg, #f8fafc, #e2e8f0)';
        }
    }};
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  animation: ${slideIn} 0.4s ease-out;
`;

export const TabPanel = styled.div<{ active: boolean }>`
  display: ${props => props.active ? 'block' : 'none'};
  animation: ${props => props.active ? scaleIn : 'none'} 0.4s ease-out;
`;

export const SkillBadge = styled.div<{ level: 'high' | 'medium' | 'low' }>`
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  background: ${props => {
        switch (props.level) {
            case 'high': return 'linear-gradient(135deg, #10b981, #059669)';
            case 'medium': return 'linear-gradient(135deg, #f59e0b, #d97706)';
            case 'low': return 'linear-gradient(135deg, #ef4444, #dc2626)';
        }
    }};
  color: white;
  display: inline-flex;
  align-items: center;
  gap: 4px;
`;

export const RankCard = styled(Card)`
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px;
  overflow: hidden;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 100px;
    height: 100px;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  }
`;

// Stats Grid
export const StatsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
`;

// Section Container
export const SectionContainer = styled.div`
  margin-bottom: 32px;
`;

export const SectionTitle = styled.h3`
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 16px;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 8px;
`;

// Loading Container
export const LoadingContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 50vh;
  flex-direction: column;
  gap: 16px;
`;

// Color Palette
export const colors = {
    primary: '#667eea',
    secondary: '#764ba2',
    success: '#10b981',
    warning: '#f59e0b',
    error: '#ef4444',
    info: '#3b82f6',
    gray: {
        50: '#f8fafc',
        100: '#f1f5f9',
        200: '#e2e8f0',
        300: '#cbd5e1',
        400: '#94a3b8',
        500: '#64748b',
        600: '#475569',
        700: '#334155',
        800: '#1e293b',
        900: '#0f172a',
    }
};