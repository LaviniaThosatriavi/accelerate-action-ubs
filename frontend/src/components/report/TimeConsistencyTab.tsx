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
          maxWidth: 320,
          padding: 2,
          borderRadius: '12px',
          boxShadow: '0 8px 32px rgba(0,0,0,0.12)',
        }
      }}
    >
      <Paper elevation={0}>
        <Typography variant="subtitle1" fontWeight="bold" sx={{ marginBottom: 1, color: colors.primary }}>
          {title}
        </Typography>
        <Typography variant="body2" sx={{ marginBottom: 2, color: 'text.secondary' }}>
          {description}
        </Typography>
        <Box sx={{ 
          backgroundColor: colors.gray[50], 
          padding: 1.5, 
          borderRadius: '8px',
          border: `1px solid ${colors.gray[200]}`
        }}>
          <Typography variant="caption" fontWeight="bold" sx={{ display: 'block', marginBottom: 0.5 }}>
            How it's calculated:
          </Typography>
          <Typography variant="caption" sx={{ color: 'text.secondary', lineHeight: 1.4 }}>
            {calculation}
          </Typography>
        </Box>
      </Paper>
    </Popover>
  );
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
        <CardContent sx={{ padding: '32px' }}>
          <SectionTitle>
            <FiClock size={24} color={colors.primary} />
            Time Management Analysis
          </SectionTitle>
          <Typography textAlign="left" variant="body1" color="text.secondary" sx={{ marginBottom: 4 }}>
            {timeManagement?.summary}
          </Typography>

          <ChartContainer>
            <Typography variant="h6" fontWeight="bold" sx={{ marginBottom: 2 }}>
              Weekly Hours Comparison
            </Typography>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={timeData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" stroke={colors.gray[200]} />
                <XAxis dataKey="name" />
                <YAxis />
                <RechartsTooltip content={<CustomTooltip />} />
                <Legend />
                <Bar dataKey="hours" fill={colors.primary} radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </ChartContainer>

          <StatsGrid>
            <MetricCard gradient={`linear-gradient(135deg, ${colors.primary}, ${colors.secondary})`}>
              <CardContent sx={{ textAlign: 'center' }}>
                <IoMdTimer size={38} color="black" />
                <Typography variant="h5" fontWeight="bold" sx={{ marginTop: 0.4 }}>
                  {utilizationRate}%
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Utilization Rate
                </Typography>
              </CardContent>
            </MetricCard>

            <MetricCard>
              <CardContent sx={{ textAlign: 'center' }}>
                <FiClock size={32} color={colors.success} />
                <Typography variant="h5" fontWeight="bold" sx={{ marginTop: 1 }}>
                  {actualHoursThisWeek}h / {timeManagement?.timeAnalysis.plannedHoursPerWeek}h
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  This Week's Progress
                </Typography>
              </CardContent>
            </MetricCard>

            <MetricCard>
              <CardContent sx={{ textAlign: 'center', position: 'relative' }}>
                <FiTarget size={32} color={colors.warning} />
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 0.5 }}>
                  <Typography variant="h5" fontWeight="bold" sx={{ marginTop: 1 }}>
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
                      <FiInfo size={16} color={colors.info} />
                    </IconButton>
                  </Tooltip>
                </Box>
                <Typography variant="caption" color="text.secondary">
                  Optimal Weekly Target
                </Typography>
              </CardContent>
            </MetricCard>

            <MetricCard>
              <CardContent sx={{ textAlign: 'center', position: 'relative' }}>
                <FiActivity size={32} color={colors.info} />
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 0.5 }}>
                  <Typography variant="h5" fontWeight="bold" sx={{ marginTop: 1 }}>
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
                      <FiInfo size={16} color={colors.info} />
                    </IconButton>
                  </Tooltip>
                </Box>
                <Typography variant="caption" color="text.secondary">
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

          <Grid container spacing={3}>
            <Grid size={{ xs: 12, md: 6 }}>
              <SectionTitle>Time Insights</SectionTitle>
              {timeManagement?.timeInsights.map((insight: string, index: number) => (
                <InsightCard key={index} variant="info">
                  <CardContent>
                    <Box display="flex" alignItems="center" gap={2}>
                      <FaRegLightbulb size={20} color={colors.info} />
                      <Typography variant="body2">{insight}</Typography>
                    </Box>
                  </CardContent>
                </InsightCard>
              ))}
            </Grid>

            <Grid size={{ xs: 12, md: 6 }}>
              <SectionTitle>Optimization Tips</SectionTitle>
              {timeManagement?.optimizationTips.map((tip: string, index: number) => (
                <Alert 
                  key={index}
                  severity="info"
                  icon={<FiZap />}
                  sx={{ 
                    marginBottom: 2,
                    borderRadius: '12px',
                    background: 'linear-gradient(135deg, #eff6ff, #dbeafe)'
                  }}
                >
                  <Typography variant="body2">{tip}</Typography>
                </Alert>
              ))}
            </Grid>
          </Grid>

          <Box sx={{ marginTop: 4 }}>
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
              <Typography variant="subtitle1" fontWeight="bold" sx={{ width: '100%', textAlign: 'center' }}>
                Deadline Status
              </Typography>
              <Typography variant="body2" sx={{ width: '100%', textAlign: 'center' }}>              
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
        <CardContent sx={{ padding: '32px' }}>
          <SectionTitle>
            <FiActivity size={24} color={colors.primary} />
            Consistency Analysis
          </SectionTitle>
          <Typography textAlign="left" variant="body1" color="text.secondary" sx={{ marginBottom: 4 }}>
            {consistency?.summary}
          </Typography>

          <ChartContainer>
            <Typography variant="h6" fontWeight="bold" sx={{ marginBottom: 2 }}>
              Consistency Trend Over Time
            </Typography>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={consistencyTrendData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" stroke={colors.gray[200]} />
                <XAxis dataKey="week" />
                <YAxis />
                <RechartsTooltip content={<CustomTooltip />} />
                <Legend />
                <Line type="monotone" dataKey="streak" stroke={colors.warning} strokeWidth={3} dot={{ fill: colors.warning, strokeWidth: 2, r: 6 }} />
                <Line type="monotone" dataKey="goals" stroke={colors.success} strokeWidth={3} dot={{ fill: colors.success, strokeWidth: 2, r: 6 }} />
              </LineChart>
            </ResponsiveContainer>
          </ChartContainer>

          {/* Consistency Metrics */}
          <StatsGrid>
            <MetricCard alignItems gradient={`linear-gradient(135deg, ${colors.warning}, #d97706)`}>
              <CardContent sx={{ padding: '16px !important' }}>
                <Box display="flex" alignItems="center" gap={2} marginTop="2vh">
                  <IoMdFlame size={48} color="red" />
                  <Box display="flex" flexDirection="column" alignItems="flex-start">
                    <Typography variant="h3" fontWeight="bold" color="red" lineHeight={1}>
                      {consistency?.metrics.currentLoginStreak}
                    </Typography>
                    <Typography variant="body1" color="red" sx={{ marginBottom: 0.5 }}>
                      Current Login Streak
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </MetricCard>

            <MetricCard alignItems>
              <CardContent sx={{ padding: '16px !important' }}>
                <Box display="flex" alignItems="center" gap={2} marginTop="1vh">
                  <FiTarget size={48} color={colors.success} />
                  <Box display="flex" flexDirection="column" alignItems="flex-start">
                    <Typography variant="h3" fontWeight="bold" color={colors.success} lineHeight={1}>
                      {consistency?.metrics.goalCompletionRate}%
                    </Typography>
                    <Typography variant="body1" color={colors.success} sx={{ marginBottom: 0.5 }}>
                      Goal Completion
                    </Typography>
                    <Typography variant="caption" color={colors.success}>
                      {consistency?.metrics.goalsCompletedThisWeek}/{consistency?.metrics.totalGoalsThisWeek} this week
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </MetricCard>
          </StatsGrid>

          <Grid container spacing={3}>
            <Grid size={{ xs: 12, md: 6 }}>
              <SectionTitle>Consistency Insights</SectionTitle>
              {consistency?.consistencyInsights.map((insight: string, index: number) => (
                <InsightCard key={index} variant="success">
                  <CardContent>
                    <Box display="flex" alignItems="center" gap={2}>
                      <FiActivity size={20} color={colors.success} />
                      <Typography variant="body2">{insight}</Typography>
                    </Box>
                  </CardContent>
                </InsightCard>
              ))}
            </Grid>

            <Grid size={{ xs: 12, md: 6 }}>
              <SectionTitle>Improvement Suggestions</SectionTitle>
              {consistency?.improvementSuggestions.map((suggestion: string, index: number) => (
                <Alert 
                  key={index}
                  severity="info"
                  icon={<FiTarget />}
                  sx={{ 
                    marginBottom: 2,
                    borderRadius: '12px',
                    background: 'linear-gradient(135deg, #eff6ff, #dbeafe)'
                  }}
                >
                  <Typography variant="body2">{suggestion}</Typography>
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