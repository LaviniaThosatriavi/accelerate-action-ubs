import React, { useState, useEffect, useCallback } from 'react';
import {
  Typography,
  Box,
  Chip,
  Avatar,
  CircularProgress,
  Tabs,
  Tab,
  Alert,
  CardContent,
  Grid,
  IconButton,
} from '@mui/material';
import {
  FiClock,
  FiZap,
  FiActivity,
  FiUsers,
  FiRefreshCw,
} from 'react-icons/fi';
import { LuBrainCircuit } from "react-icons/lu";
import { IoBarChartSharp } from "react-icons/io5";
import { FaRegLightbulb } from "react-icons/fa";
import {
  IoMdFlame,
  IoMdMedal,
} from 'react-icons/io';

import type {
  QuickInsights,
  OverviewData,
  SkillAnalysis,
  TimeManagement,
  ConsistencyData,
  CompetitiveData,
} from '../../types/ReportTypes';
import {
  ReportContainer,
  HeaderCard,
  MetricCard,
  StatsGrid,
  LoadingContainer,
  colors,
} from '../../styles/ReportStyles';

import OverviewTab from './OverviewTab';
import TimeConsistencyTab from './TimeConsistencyTab';
import CompetitiveTab from './CompetitiveTab';

const ReportComponent: React.FC = () => {
  const [activeTab, setActiveTab] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  
  // State for all report data
  const [quickInsights, setQuickInsights] = useState<QuickInsights | null>(null);
  const [overviewData, setOverviewData] = useState<OverviewData | null>(null);
  const [skillAnalysis, setSkillAnalysis] = useState<SkillAnalysis | null>(null);
  const [timeManagement, setTimeManagement] = useState<TimeManagement | null>(null);
  const [consistency, setConsistency] = useState<ConsistencyData | null>(null);
  const [competitive, setCompetitive] = useState<CompetitiveData | null>(null);
  const [actualHoursThisWeek, setActualHoursThisWeek] = useState<number>(0);

  // Helper function to create authenticated fetch options
  const createFetchOptions = (): RequestInit => {
    const token = localStorage.getItem('token');
    
    const options: RequestInit = {
      method: 'GET',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      }
    };

    if (token) {
      (options.headers as Record<string, string>)['Authorization'] = `Bearer ${token}`;
    } else {
      console.warn('No authorization token found');
    }

    return options;
  };

  // Enhanced fetch function with error handling
  const fetchWithAuth = useCallback(async (url: string): Promise<unknown> => {
    try {
      const token = localStorage.getItem('token');
      
      if (!token) {
        throw new Error('No authorization token found');
      }

      const response = await fetch(url, createFetchOptions());
      
      if (!response.ok) {
        if (response.status === 401) {
          throw new Error('Unauthorized - Please log in again');
        } else if (response.status === 403) {
          throw new Error('Forbidden - You don\'t have permission to access this resource');
        } else if (response.status === 404) {
          throw new Error('Resource not found');
        } else {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
      }

      const data: unknown = await response.json();
      return data;
    } catch (error) {
      console.error(`❌ Error fetching ${url}:`, error);
      throw error;
    }
  }, []);

  // Refresh hours data function
  const refreshHours = useCallback(async (): Promise<void> => {
    try {
      const hours = await fetchWithAuth('/api/enrolled-courses/total-hours-this-week');
      setActualHoursThisWeek(hours as number);
    } catch (error) {
      console.error('❌ Refresh failed:', error);
    }
  }, [fetchWithAuth]);

  // Fetch data on component mount
  useEffect(() => {
    const fetchReportData = async (): Promise<void> => {
      try {
        setLoading(true);
        setError(null);
        
        // Fetch all data with proper authentication
        const [
          quickInsightsData,
          overviewData,
          skillsData,
          timeData,
          consistencyData,
          competitiveData,
          actualHours
        ] = await Promise.all([
          fetchWithAuth('/api/reports/quick-insights'),
          fetchWithAuth('/api/reports/overview'),
          fetchWithAuth('/api/reports/skills'),
          fetchWithAuth('/api/reports/time-management'),
          fetchWithAuth('/api/reports/consistency'),
          fetchWithAuth('/api/reports/competitive'),
          fetchWithAuth('/api/enrolled-courses/total-hours-this-week')
        ]);
        
        setQuickInsights(quickInsightsData as QuickInsights);
        setOverviewData(overviewData as OverviewData);
        setSkillAnalysis(skillsData as SkillAnalysis);
        setTimeManagement(timeData as TimeManagement);
        setConsistency(consistencyData as ConsistencyData);
        setCompetitive(competitiveData as CompetitiveData);
        setActualHoursThisWeek(actualHours as number);
        
      } catch (error) {
        console.error('❌ Error fetching report data:', error);
        setError(error instanceof Error ? error.message : 'Failed to load report data');
      } finally {
        setLoading(false);
      }
    };

    fetchReportData();
  }, [fetchWithAuth]);

  const getBadgeColor = (level: string): string => {
    switch (level?.toUpperCase()) {
      case 'BRONZE': return '#CD7F32';
      case 'SILVER': return '#C0C0C0';
      case 'GOLD': return '#FFD700';
      case 'PLATINUM': return '#E5E4E2';
      case 'MASTER': return '#8B00FF';
      default: return colors.primary;
    }
  };

  const handleTabChange = (_: React.SyntheticEvent, newValue: number): void => {
    setActiveTab(newValue);
  };

  // Custom tooltip component
  const CustomTooltip = ({ active, payload, label }: {
    active?: boolean;
    payload?: Array<{
      name: string;
      value: number;
      color: string;
    }>;
    label?: string;
  }) => {
    if (active && payload && payload.length) {
      return (
        <div style={{
          background: 'white',
          padding: '12px',
          border: '1px solid #e2e8f0',
          borderRadius: '8px',
          boxShadow: '0 4px 12px rgba(0,0,0,0.15)'
        }}>
          <p style={{ margin: 0, fontWeight: 'bold' }}>{label}</p>
          {payload.map((entry, index: number) => (
            <p key={index} style={{ margin: '4px 0', color: entry.color }}>
              {entry.name}: {entry.value}
            </p>
          ))}
        </div>
      );
    }
    return null;
  };

  if (error) {
    return (
      <ReportContainer>
        <Alert 
          severity="error" 
          sx={{ 
            margin: 4,
            borderRadius: '12px' 
          }}
        >
          <Typography variant="h6" fontWeight="bold">
            Failed to Load Report
          </Typography>
          <Typography variant="body2">
            {error}
          </Typography>
          <Box sx={{ marginTop: 2 }}>
            <Typography variant="body2" color="text.secondary">
              Please check your internet connection and try refreshing the page.
            </Typography>
          </Box>
        </Alert>
      </ReportContainer>
    );
  }

  if (loading) {
    return (
      <ReportContainer>
        <LoadingContainer>
          <CircularProgress size={60} sx={{ color: colors.primary }} />
          <Typography variant="h6" color="text.secondary">
            Loading your learning analytics...
          </Typography>
        </LoadingContainer>
      </ReportContainer>
    );
  }

  return (
    <ReportContainer>
      <HeaderCard>
        <CardContent sx={{ padding: '32px' }}>
          <Box display="flex" alignItems="center" justifyContent="space-between" sx={{ marginBottom: 3 }}>
            <Box display="flex" alignItems="center" gap={2}>
              <Avatar
                sx={{
                  width: 64,
                  height: 64,
                  background: `linear-gradient(135deg, ${getBadgeColor(quickInsights?.currentBadgeLevel || 'SILVER')}, ${colors.secondary})`,
                  fontSize: '24px',
                  fontWeight: 'bold'
                }}
              >
                {quickInsights?.username?.[0]?.toUpperCase() || 'U'}
              </Avatar>
              <Box>
                <Typography variant="h4" fontWeight="bold" color="primary">
                  Welcome back, {quickInsights?.username || 'User'}!
                </Typography>
                <Typography variant="h6" color="text.secondary">
                  Learning Performance Dashboard
                </Typography>
              </Box>
            </Box>
            <Box display="flex" alignItems="center" gap={2}>
              <IconButton
                onClick={refreshHours}
                sx={{
                  background: 'rgba(255,255,255,0.1)',
                  '&:hover': {
                    background: 'rgba(255,255,255,0.2)'
                  }
                }}
                title="Refresh hours data"
              >
                <FiRefreshCw size={20} />
              </IconButton>
              <Chip
                icon={<IoMdFlame size={16} />}
                label={`${quickInsights?.loginStreak || 0} Day Streak`}
                sx={{
                  background: `linear-gradient(45deg, ${colors.warning}, #ee5a52)`,
                  color: 'white',
                  fontSize: '16px',
                  padding: '8px 16px',
                  height: '40px'
                }}
              />
              <Chip
                icon={<IoMdMedal size={16} />}
                label={quickInsights?.currentBadgeLevel || 'SILVER'}
                sx={{
                  background: getBadgeColor(quickInsights?.currentBadgeLevel || 'SILVER'),
                  color: 'white',
                  fontSize: '14px'
                }}
              />
            </Box>
          </Box>
          
          <StatsGrid>
            <MetricCard center gradient={`linear-gradient(135deg, ${colors.primary}, ${colors.secondary})`}>
              <CardContent>
                <Typography variant="h3" fontWeight="bold" color="black" marginBottom="0.5vh">
                  {quickInsights?.completionRate || 0}%
                </Typography>
                <Typography variant="body2" color="black">
                  Completion Rate
                </Typography>
              </CardContent>
            </MetricCard>
            
            <MetricCard center gradient={`linear-gradient(135deg, ${colors.success}, #059669)`}>
              <CardContent>
                <Typography variant="h3" fontWeight="bold" color="black">
                  {quickInsights?.averageScore || 0}%
                </Typography>
                <Typography variant="body2" color="black">
                  Average Score
                </Typography>
              </CardContent>
            </MetricCard>

            <MetricCard center gradient={`linear-gradient(135deg, ${colors.warning}, #d97706)`}>
              <CardContent>
                <Typography variant="h3" fontWeight="bold" color="black">
                  {overviewData?.performance.totalCourses || 0}
                </Typography>
                <Typography variant="body2" color="black">
                  Total Courses
                </Typography>
              </CardContent>
            </MetricCard>

            <MetricCard center gradient={`linear-gradient(135deg, ${colors.info}, #1d4ed8)`}>
              <CardContent>
                <Typography variant="h3" fontWeight="bold" color="black">
                  {actualHoursThisWeek}h
                </Typography>
                <Typography variant="body2" color="black">
                  Hours This Week
                </Typography>
              </CardContent>
            </MetricCard>
          </StatsGrid>

          <Grid container spacing={3} sx={{ marginTop: 2 }}>
            <Grid size={{ xs: 12, md: 6 }}>
              <Alert 
                severity="success" 
                icon={<FiZap />}
                sx={{ 
                  borderRadius: '12px',
                  background: 'linear-gradient(135deg, #ecfdf5, #d1fae5)',
                  border: 'none'
                }}
              >
                <Typography variant="subtitle1" fontWeight="bold">
                  Top Strength
                </Typography>
                <Typography variant="body2">
                  {quickInsights?.topStrength}
                </Typography>
              </Alert>
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
              <Alert 
                severity="info" 
                icon={<FaRegLightbulb  />}
                sx={{ 
                  borderRadius: '12px',
                  background: 'linear-gradient(135deg, #eff6ff, #dbeafe)',
                  border: 'none'
                }}
              >
                <Typography variant="subtitle1" fontWeight="bold">
                  Top Recommendation
                </Typography>
                <Typography variant="body2">
                  {quickInsights?.topRecommendation}
                </Typography>
              </Alert>
            </Grid>
          </Grid>
        </CardContent>
      </HeaderCard>

      <MetricCard>
        <Tabs
          value={activeTab}
          onChange={handleTabChange}
          variant="scrollable"
          scrollButtons="auto"
          sx={{
            background: `linear-gradient(135deg, ${colors.primary}, ${colors.secondary})`,
            '& .MuiTab-root': {
              color: 'rgba(255,255,255,0.7)',
              fontWeight: 'bold',
              '&.Mui-selected': {
                color: 'white',
              }
            },
            '& .MuiTabs-indicator': {
              backgroundColor: 'white',
              height: '3px',
              borderRadius: '2px'
            }
          }}
        >
          <Tab icon={<IoBarChartSharp size={20} />} label="Overview" />
          <Tab icon={<LuBrainCircuit size={20} />} label="Skills Analysis" />
          <Tab icon={<FiClock size={20} />} label="Time Management" />
          <Tab icon={<FiActivity size={20} />} label="Consistency" />
          <Tab icon={<FiUsers size={20} />} label="Competitive" />
        </Tabs>

        <OverviewTab
          overviewData={overviewData}
          skillAnalysis={skillAnalysis}
          activeTab={activeTab}
          customTooltip={CustomTooltip}
          actualHoursThisWeek={actualHoursThisWeek}
        />

        <TimeConsistencyTab
          timeManagement={timeManagement}
          consistency={consistency}
          activeTab={activeTab}
          customTooltip={CustomTooltip}
          actualHoursThisWeek={actualHoursThisWeek}
        />

        <CompetitiveTab
          competitive={competitive}
          activeTab={activeTab}
          customTooltip={CustomTooltip}
          getBadgeColor={getBadgeColor}
        />
      </MetricCard>
    </ReportContainer>
  );
};

export default ReportComponent;