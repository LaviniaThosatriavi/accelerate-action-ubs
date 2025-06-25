import React, { useState } from 'react';
import {
  Typography,
  Box,
  Alert,
  CardContent,
  Grid,
  Tooltip,
  IconButton,
  Popover,
  Paper,
} from '@mui/material';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip as RechartsTooltip,
  Legend,
  ResponsiveContainer,
  LineChart,
  Line,
} from 'recharts';
import {
  FiClock,
  FiActivity,
  FiTarget,
  FiZap,
  FiInfo,
} from 'react-icons/fi';
import { FaRegLightbulb } from "react-icons/fa";
import {
  IoMdTimer,
  IoMdFlame,
} from 'react-icons/io';

import type { TimeManagement, ConsistencyData } from '../../types/ReportTypes';
import {
  ChartContainer,
  InsightCard,
  MetricCard,
  StatsGrid,
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

interface TimeConsistencyTabProps {
  timeManagement: TimeManagement | null;
  consistency: ConsistencyData | null;
  activeTab: number;
  customTooltip: React.ComponentType<CustomTooltipProps>;
  actualHoursThisWeek: number;
}

interface InfoPopupProps {
  title: string;
  description: string;
  calculation: string;
  anchorEl: HTMLElement | null;
  onClose: () => void;
}

const InfoPopup: React.FC<InfoPopupProps> = ({ 
  title, 
  description, 
  calculation, 
  anchorEl, 
  onClose 
}) => {
  const open = Boolean(anchorEl);

  return (
    <Popover
      open={open}
      anchorEl={anchorEl}
      onClose={onClose}
      anchorOrigin={{
        vertical: 'bottom',
        horizontal: 'center',
      }}
      transformOrigin={{
        vertical: 'top',
        horizontal: 'center',
      }}
      sx={{
        '& .MuiPopover-paper': {
          maxWidth: 'clamp(280px, 90vw, 320px)',
          padding: 'clamp(1rem, 2vw, 2rem)',
          borderRadius: '12px',
          boxShadow: '0 8px 32px rgba(0,0,0,0.12)',
        }
      }}
    >
      <Paper elevation={0}>
        <Typography 
          variant="subtitle1" 
          fontWeight="bold" 
          sx={{ 
            marginBottom: 1, 
            color: colors.primary,
            fontSize: 'clamp(0.875rem, 2.5vw, 1rem)'
          }}
        >
          {title}
        </Typography>
        <Typography 
          variant="body2" 
          sx={{ 
            marginBottom: 2, 
            color: 'text.secondary',
            fontSize: 'clamp(0.75rem, 2vw, 0.875rem)'
          }}
        >
          {description}
        </Typography>
        <Box sx={{ 
          backgroundColor: colors.gray[50], 
          padding: 'clamp(0.75rem, 2vw, 1.5rem)', 
          borderRadius: '8px',
          border: `1px solid ${colors.gray[200]}`
        }}>
          <Typography 
            variant="caption" 
            fontWeight="bold" 
            sx={{ 
              display: 'block', 
              marginBottom: 0.5,
              fontSize: 'clamp(0.7rem, 1.5vw, 0.75rem)'
            }}
          >
            How it's calculated:
          </Typography>
          <Typography 
            variant="caption" 
            sx={{ 
              color: 'text.secondary', 
              lineHeight: 1.4,
              fontSize: 'clamp(0.65rem, 1.5vw, 0.75rem)'
            }}
          >
            {calculation}
          </Typography>
        </Box>
      </Paper>
    </Popover>
  );
};

const getResponsiveHeight = () => {
  if (typeof window !== 'undefined') {
    return window.innerWidth < 500 ? 180 : 250;
  }
  return 250;
};

const TimeConsistencyTab: React.FC<TimeConsistencyTabProps> = ({
  timeManagement,
  consistency,
  activeTab,
  customTooltip: CustomTooltip,
  actualHoursThisWeek,
}) => {
  const [learningPaceAnchor, setLearningPaceAnchor] = useState<HTMLElement | null>(null);
  const [optimalHoursAnchor, setOptimalHoursAnchor] = useState<HTMLElement | null>(null);

  const handleLearningPaceClick = (event: React.MouseEvent<HTMLElement>) => {
    setLearningPaceAnchor(event.currentTarget);
  };

  const handleOptimalHoursClick = (event: React.MouseEvent<HTMLElement>) => {
    setOptimalHoursAnchor(event.currentTarget);
  };

  const handleCloseLearningPace = () => {
    setLearningPaceAnchor(null);
  };

  const handleCloseOptimalHours = () => {
    setOptimalHoursAnchor(null);
  };

  // Prepare chart data using the prop value
  const timeData = [
    { name: 'Planned', hours: timeManagement?.timeAnalysis.plannedHoursPerWeek || 0, color: colors.info },
    { name: 'Actual', hours: actualHoursThisWeek, color: colors.primary },
    { name: 'Optimal', hours: timeManagement?.timeAnalysis.optimalHoursPerWeek || 0, color: colors.success }
  ];

  const consistencyTrendData = [
    { week: 'Week 1', streak: 5, goals: 8 },
    { week: 'Week 2', streak: 7, goals: 10 },
    { week: 'Week 3', streak: 12, goals: 12 },
    { week: 'Week 4', streak: consistency?.metrics.currentLoginStreak || 14, goals: consistency?.metrics.goalsCompletedThisWeek || 12 }
  ];

  const utilizationRate = timeManagement?.timeAnalysis.plannedHoursPerWeek 
    ? Math.round((actualHoursThisWeek / timeManagement.timeAnalysis.plannedHoursPerWeek) * 100)
    : 0;

  return (
    <>
      {activeTab === 2 && (
        <CardContent sx={{ padding: '16px' }}>
          <SectionTitle style={{
            marginBottom: '1vh',
            fontSize: 'clamp(1rem, 2vw, 1.5rem)'
          }}>
            <FiClock 
              style={{ fontSize: 'clamp(18px, 3vw, 24px)' }} 
              color={colors.primary} 
            />
            Time Management Analysis
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
            {timeManagement?.summary}
          </Typography>

          <ChartContainer>
            <Typography 
              variant="h6" 
              fontWeight="bold" 
              sx={{ 
                marginBottom: '0.5vh',
                fontSize: 'clamp(0.9rem, 2.5vw, 1.25rem)'
              }}
            >
              Weekly Hours Comparison
            </Typography>
            <ResponsiveContainer width="100%" height={getResponsiveHeight()}>
              <BarChart data={timeData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" stroke={colors.gray[200]} />
                <XAxis 
                  dataKey="name" 
                  tick={{ fontSize: 'clamp(10, 2vw, 12)' }}
                />
                <YAxis />
                <RechartsTooltip content={<CustomTooltip />} />
                <Legend 
                  wrapperStyle={{
                    fontSize: 'clamp(0.75rem, 2vw, 1rem)'
                  }}
                />
                <Bar dataKey="hours" fill={colors.primary} radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </ChartContainer>

          <StatsGrid>
            <MetricCard gradient={`linear-gradient(135deg, ${colors.primary}, ${colors.secondary})`}>
              <CardContent sx={{ textAlign: 'center', padding: 'clamp(10px, 2vw, 16px) !important' }}>
                <IoMdTimer 
                  style={{ fontSize: 'clamp(28px, 5vw, 38px)' }} 
                  color="black" 
                />
                <Typography 
                  variant="h5" 
                  fontWeight="bold" 
                  sx={{ 
                    fontSize: 'clamp(1.25rem, 4vw, 2rem)'
                  }}
                >
                  {utilizationRate}%
                </Typography>
                <Typography 
                  variant="caption" 
                  color="text.secondary"
                  sx={{ fontSize: 'clamp(0.7rem, 1.5vw, 0.75rem)' }}
                >
                  Utilization Rate
                </Typography>
              </CardContent>
            </MetricCard>

            <MetricCard>
              <CardContent sx={{ textAlign: 'center', padding: 'clamp(10px, 2vw, 16px) !important' }}>
                <FiClock 
                  style={{ fontSize: 'clamp(24px, 4vw, 32px)' }} 
                  color={colors.success} 
                />
                <Typography 
                  variant="h5" 
                  fontWeight="bold" 
                  sx={{ 
                    marginTop: 1,
                    fontSize: 'clamp(1rem, 3vw, 1.5rem)'
                  }}
                >
                  {actualHoursThisWeek}h / {timeManagement?.timeAnalysis.plannedHoursPerWeek}h
                </Typography>
                <Typography 
                  variant="caption" 
                  color="text.secondary"
                  sx={{ fontSize: 'clamp(0.7rem, 1.5vw, 0.75rem)' }}
                >
                  This Week's Progress
                </Typography>
              </CardContent>
            </MetricCard>

            <MetricCard>
              <CardContent sx={{ textAlign: 'center', position: 'relative', padding: 'clamp(10px, 2vw, 16px) !important' }}>
                <FiTarget 
                  style={{ fontSize: 'clamp(24px, 4vw, 32px)' }} 
                  color={colors.warning} 
                />
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 0.5 }}>
                  <Typography 
                    variant="h5" 
                    fontWeight="bold" 
                    sx={{ 
                      marginTop: 1,
                      fontSize: 'clamp(1rem, 3vw, 1.5rem)'
                    }}
                  >
                    {timeManagement?.timeAnalysis.optimalHoursPerWeek}h
                  </Typography>
                  <Tooltip title="Click for calculation details">
                    <IconButton 
                      size="small" 
                      onClick={handleOptimalHoursClick}
                      sx={{ 
                        marginTop: 0.5,
                        '&:hover': { 
                          backgroundColor: 'rgba(0,0,0,0.04)',
                          transform: 'scale(1.1)'
                        },
                        transition: 'all 0.2s ease'
                      }}
                    >
                      <FiInfo 
                        style={{ fontSize: 'clamp(12px, 2vw, 16px)' }} 
                        color={colors.info} 
                      />
                    </IconButton>
                  </Tooltip>
                </Box>
                <Typography 
                  variant="caption" 
                  color="text.secondary"
                  sx={{ fontSize: 'clamp(0.7rem, 1.5vw, 0.75rem)' }}
                >
                  Optimal Weekly Target
                </Typography>
              </CardContent>
            </MetricCard>

            <MetricCard>
              <CardContent sx={{ textAlign: 'center', position: 'relative', padding: 'clamp(10px, 2vw, 16px) !important' }}>
                <FiActivity 
                  style={{ fontSize: 'clamp(24px, 4vw, 32px)' }} 
                  color={colors.info} 
                />
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 0.5 }}>
                  <Typography 
                    variant="h5" 
                    fontWeight="bold" 
                    sx={{ 
                      marginTop: 1,
                      fontSize: 'clamp(1rem, 3vw, 1.5rem)'
                    }}
                  >
                    {timeManagement?.timeAnalysis.learningPace}
                  </Typography>
                  <Tooltip title="Click for calculation details">
                    <IconButton 
                      size="small" 
                      onClick={handleLearningPaceClick}
                      sx={{ 
                        marginTop: 0.5,
                        '&:hover': { 
                          backgroundColor: 'rgba(0,0,0,0.04)',
                          transform: 'scale(1.1)'
                        },
                        transition: 'all 0.2s ease'
                      }}
                    >
                      <FiInfo 
                        style={{ fontSize: 'clamp(12px, 2vw, 16px)' }} 
                        color={colors.info} 
                      />
                    </IconButton>
                  </Tooltip>
                </Box>
                <Typography 
                  variant="caption" 
                  color="text.secondary"
                  sx={{ fontSize: 'clamp(0.7rem, 1.5vw, 0.75rem)' }}
                >
                  Learning Pace
                </Typography>
              </CardContent>
            </MetricCard>
          </StatsGrid>

          {/* Info Popups */}
          <InfoPopup
            title="Learning Pace Calculation"
            description="Your learning pace is determined by how effectively you complete enrolled courses."
            calculation="Based on completion rate: Fast (≥80%), Moderate (50-79%), Slow (<50%). Completion rate = (completed courses ÷ total enrolled courses) × 100%"
            anchorEl={learningPaceAnchor}
            onClose={handleCloseLearningPace}
          />

          <InfoPopup
            title="Optimal Hours Calculation"
            description="Recommended weekly study hours based on your current performance and completion rate."
            calculation="High performers (≥80% completion): +2 hours (max 15h). Low performers (<50%): -2 hours (min 3h). Moderate (50-79%): maintain current target."
            anchorEl={optimalHoursAnchor}
            onClose={handleCloseOptimalHours}
          />

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
                Time Insights
              </SectionTitle>
              {timeManagement?.timeInsights.map((insight: string, index: number) => (
                <InsightCard key={index} variant="info">
                  <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px)' }}>
                    <Box display="flex" alignItems="center" sx={{ gap: 'clamp(0.5rem, 1vw, 1rem)' }}>
                      <FaRegLightbulb 
                        style={{ fontSize: 'clamp(16px, 2.5vw, 20px)' }} 
                        color={colors.info} 
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
                Optimization Tips
              </SectionTitle>
              {timeManagement?.optimizationTips.map((tip: string, index: number) => (
                <Alert 
                  key={index}
                  severity="info"
                  icon={<FiZap style={{ fontSize: 'clamp(16px, 2.5vw, 20px)' }} />}
                  sx={{ 
                    marginBottom: 'clamp(0.5rem, 1vw, 1rem)',
                    borderRadius: '12px',
                    background: 'linear-gradient(135deg, #eff6ff, #dbeafe)',
                    fontSize: 'clamp(0.75rem, 2vw, 0.875rem)'
                  }}
                >
                  <Typography 
                    variant="body2"
                    sx={{ fontSize: 'clamp(0.75rem, 2vw, 0.875rem)' }}
                  >
                    {tip}
                  </Typography>
                </Alert>
              ))}
            </Grid>
          </Grid>

          <Box sx={{ marginTop: 'clamp(1rem, 2vw, 2rem)' }}>
            <Alert 
              severity={timeManagement?.timeAnalysis.hasOverdueDeadlines ? "warning" : "success"}
              icon={false}
              sx={{ 
                borderRadius: '12px',
                '& .MuiAlert-message': {
                  width: '100%',
                  textAlign: 'center',
                  padding: 0
                }
              }}
            >
              <Typography 
                variant="subtitle1" 
                fontWeight="bold" 
                sx={{ 
                  width: '100%', 
                  textAlign: 'center',
                  fontSize: 'clamp(0.875rem, 2.5vw, 1rem)'
                }}
              >
                Deadline Status
              </Typography>
              <Typography 
                variant="body2" 
                sx={{ 
                  width: '100%', 
                  textAlign: 'center',
                  fontSize: 'clamp(0.75rem, 2vw, 0.875rem)'
                }}
              >              
                {timeManagement?.timeAnalysis.hasOverdueDeadlines 
                  ? "You have overdue deadlines. Consider prioritizing urgent tasks."
                  : "Great! You're on track with all your deadlines."
                }
              </Typography>
            </Alert>
          </Box>
        </CardContent>
      )}

      {/* Consistency Tab */}
      {activeTab === 3 && (
        <CardContent sx={{ padding: '16px' }}>
          <SectionTitle style={{
            marginBottom: '1vh',
            fontSize: 'clamp(1rem, 2vw, 1.5rem)'
          }}>
            <FiActivity 
              style={{ fontSize: 'clamp(18px, 3vw, 24px)' }} 
              color={colors.primary} 
            />
            Consistency Analysis
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
            {consistency?.summary}
          </Typography>

          <ChartContainer>
            <Typography 
              variant="h6" 
              fontWeight="bold" 
              sx={{ 
                marginBottom: '0.5vh',
                fontSize: 'clamp(0.9rem, 2.5vw, 1.25rem)'
              }}
            >
              Consistency Trend Over Time
            </Typography>
            <ResponsiveContainer width="100%" height={getResponsiveHeight()}>
              <LineChart data={consistencyTrendData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" stroke={colors.gray[200]} />
                <XAxis 
                  dataKey="week" 
                  tick={{ fontSize: 'clamp(10, 2vw, 12)' }}
                />
                <YAxis />
                <RechartsTooltip content={<CustomTooltip />} />
                <Legend 
                  wrapperStyle={{
                    fontSize: 'clamp(0.75rem, 2vw, 1rem)'
                  }}
                />
                <Line type="monotone" dataKey="streak" stroke={colors.warning} strokeWidth={3} dot={{ fill: colors.warning, strokeWidth: 2, r: 6 }} />
                <Line type="monotone" dataKey="goals" stroke={colors.success} strokeWidth={3} dot={{ fill: colors.success, strokeWidth: 2, r: 6 }} />
              </LineChart>
            </ResponsiveContainer>
          </ChartContainer>

          {/* Consistency Metrics */}
          <StatsGrid>
            <MetricCard alignItems gradient={`linear-gradient(135deg, ${colors.warning}, #d97706)`}>
              <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px) !important' }}>
                <Box 
                  display="flex" 
                  alignItems="center" 
                  sx={{ 
                    gap: 'clamp(0.5rem, 1vw, 1rem)',
                    marginTop: 'clamp(1vh, 2vw, 2vh)'
                  }}
                >
                  <IoMdFlame 
                    style={{ fontSize: 'clamp(36px, 6vw, 48px)' }} 
                    color="red" 
                  />
                  <Box display="flex" flexDirection="column" alignItems="flex-start">
                    <Typography 
                      variant="h3" 
                      fontWeight="bold" 
                      color="red" 
                      lineHeight={1}
                      sx={{ fontSize: 'clamp(1.75rem, 5vw, 3rem)' }}
                    >
                      {consistency?.metrics.currentLoginStreak}
                    </Typography>
                    <Typography 
                      variant="body1" 
                      color="red" 
                      sx={{ 
                        marginBottom: 0.5,
                        fontSize: 'clamp(0.8rem, 2vw, 1rem)'
                      }}
                    >
                      Current Login Streak
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </MetricCard>

            <MetricCard alignItems>
              <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px) !important' }}>
                <Box 
                  display="flex" 
                  alignItems="center" 
                  sx={{ 
                    gap: 'clamp(0.5rem, 1vw, 1rem)',
                    marginTop: 'clamp(1vh, 2vw, 1vh)'
                  }}
                >
                  <FiTarget 
                    style={{ fontSize: 'clamp(36px, 6vw, 48px)' }} 
                    color={colors.success} 
                  />
                  <Box display="flex" flexDirection="column" alignItems="flex-start">
                    <Typography 
                      variant="h3" 
                      fontWeight="bold" 
                      color={colors.success} 
                      lineHeight={1}
                      sx={{ fontSize: 'clamp(1.75rem, 5vw, 3rem)' }}
                    >
                      {consistency?.metrics.goalCompletionRate}%
                    </Typography>
                    <Typography 
                      variant="body1" 
                      color={colors.success} 
                      sx={{ 
                        marginBottom: 0.5,
                        fontSize: 'clamp(0.8rem, 2vw, 1rem)'
                      }}
                    >
                      Goal Completion
                    </Typography>
                    <Typography 
                      variant="caption" 
                      color={colors.success}
                      sx={{ fontSize: 'clamp(0.7rem, 1.5vw, 0.75rem)' }}
                    >
                      {consistency?.metrics.goalsCompletedThisWeek}/{consistency?.metrics.totalGoalsThisWeek} this week
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </MetricCard>
          </StatsGrid>

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
                Consistency Insights
              </SectionTitle>
              {consistency?.consistencyInsights.map((insight: string, index: number) => (
                <InsightCard key={index} variant="success">
                  <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px)' }}>
                    <Box display="flex" alignItems="center" sx={{ gap: 'clamp(0.5rem, 1vw, 1rem)' }}>
                      <FiActivity 
                        style={{ fontSize: 'clamp(16px, 2.5vw, 20px)' }} 
                        color={colors.success} 
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
                Improvement Suggestions
              </SectionTitle>
              {consistency?.improvementSuggestions.map((suggestion: string, index: number) => (
                <Alert 
                  key={index}
                  severity="info"
                  icon={<FiTarget style={{ fontSize: 'clamp(16px, 2.5vw, 20px)' }} />}
                  sx={{ 
                    marginBottom: 'clamp(0.5rem, 1vw, 1rem)',
                    borderRadius: '12px',
                    background: 'linear-gradient(135deg, #eff6ff, #dbeafe)'
                  }}
                >
                  <Typography 
                    variant="body2"
                    sx={{ fontSize: 'clamp(0.75rem, 2vw, 0.875rem)' }}
                  >
                    {suggestion}
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

export default TimeConsistencyTab;