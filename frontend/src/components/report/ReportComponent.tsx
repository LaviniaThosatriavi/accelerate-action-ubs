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
} from '@mui/material';
import {
  FiTarget,
  FiClock,
  FiBook,
  FiZap,
  FiActivity,
  FiUsers,
} from 'react-icons/fi';
import { LuBrainCircuit } from "react-icons/lu";
import { IoBarChartSharp } from "react-icons/io5";
import { FaRegLightbulb } from "react-icons/fa";
import {
  IoMdFlame,
  IoMdMedal,
  IoMdTrophy,
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
  IconWrapper,
  StatsGrid,
  LoadingContainer,
  colors,
} from '../../styles/ReportStyles';

import OverviewTab from './OverviewTab';
import TimeConsistencyTab from './TimeConsistencyTab';
import CompetitiveTab from './CompetitiveTab';

const ReportComponent: React.FC = () => {
  const [activeTab, setActiveTab] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // State for all report data
  const [quickInsights, setQuickInsights] = useState<QuickInsights | null>(null);
  const [overviewData, setOverviewData] = useState<OverviewData | null>(null);
  const [skillAnalysis, setSkillAnalysis] = useState<SkillAnalysis | null>(null);
  const [timeManagement, setTimeManagement] = useState<TimeManagement | null>(null);
  const [consistency, setConsistency] = useState<ConsistencyData | null>(null);
  const [competitive, setCompetitive] = useState<CompetitiveData | null>(null);

  // Helper function to create authenticated fetch options
  const createFetchOptions = (): RequestInit => {
    const token = localStorage.getItem('token') || localStorage.getItem('authToken') || sessionStorage.getItem('token');
    
    const options: RequestInit = {
      method: 'GET',
      credentials: 'include', // Include cookies if your backend uses them
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      }
    };

    // Add authorization header if token exists
    if (token) {
      (options.headers as Record<string, string>)['Authorization'] = `Bearer ${token}`;
    }

    return options;
  };

  // Enhanced fetch function with error handling
  const fetchWithAuth = useCallback(async (url: string) => {
    try {
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

      return await response.json();
    } catch (error) {
      console.error(`Error fetching ${url}:`, error);
      throw error;
    }
  }, []);

  // Fetch data on component mount
  useEffect(() => {
    const fetchReportData = async () => {
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
          competitiveData
        ] = await Promise.all([
          fetchWithAuth('/api/reports/quick-insights'),
          fetchWithAuth('/api/reports/overview'),
          fetchWithAuth('/api/reports/skills'),
          fetchWithAuth('/api/reports/time-management'),
          fetchWithAuth('/api/reports/consistency'),
          fetchWithAuth('/api/reports/competitive')
        ]);

        setQuickInsights(quickInsightsData);
        setOverviewData(overviewData);
        setSkillAnalysis(skillsData);
        setTimeManagement(timeData);
        setConsistency(consistencyData);
        setCompetitive(competitiveData);
      } catch (error) {
        console.error('Error fetching report data:', error);
        setError(error instanceof Error ? error.message : 'Failed to load report data');
      } finally {
        setLoading(false);
      }
    };

    fetchReportData();
  }, [fetchWithAuth]);

  // Helper functions
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

  const handleTabChange = (_: React.SyntheticEvent, newValue: number) => {
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

  // Error state
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

  // Loading state
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
      {/* Header Section */}
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
          
          {/* Quick Metrics Grid */}
          <StatsGrid>
            <MetricCard gradient={`linear-gradient(135deg, ${colors.primary}, ${colors.secondary})`}>
              <CardContent>
                <IconWrapper bgColor="rgba(255,255,255,0.2)" color="black">
                  <FiTarget size={24} color="black" />
                </IconWrapper>
                <Typography variant="h3" fontWeight="bold" color="black">
                  {quickInsights?.completionRate || 0}%
                </Typography>
                <Typography variant="body2" color="black">
                  Completion Rate
                </Typography>
              </CardContent>
            </MetricCard>
            
            <MetricCard gradient={`linear-gradient(135deg, ${colors.success}, #059669)`}>
              <CardContent>
                <IconWrapper bgColor="rgba(255,255,255,0.2)">
                  <IoMdTrophy size={24} />
                </IconWrapper>
                <Typography variant="h3" fontWeight="bold" color="white">
                  {quickInsights?.averageScore || 0}
                </Typography>
                <Typography variant="body2" color="rgba(255,255,255,0.8)">
                  Average Score
                </Typography>
              </CardContent>
            </MetricCard>

            <MetricCard gradient={`linear-gradient(135deg, ${colors.warning}, #d97706)`}>
              <CardContent>
                <IconWrapper bgColor="rgba(255,255,255,0.2)">
                  <FiBook size={24} />
                </IconWrapper>
                <Typography variant="h3" fontWeight="bold" color="white">
                  {overviewData?.performance.totalCourses || 0}
                </Typography>
                <Typography variant="body2" color="rgba(255,255,255,0.8)">
                  Total Courses
                </Typography>
              </CardContent>
            </MetricCard>

            <MetricCard gradient={`linear-gradient(135deg, ${colors.info}, #1d4ed8)`}>
              <CardContent>
                <IconWrapper bgColor="rgba(255,255,255,0.2)">
                  <FiClock size={24} />
                </IconWrapper>
                <Typography variant="h3" fontWeight="bold" color="white">
                  {timeManagement?.timeAnalysis.actualHoursThisWeek || 0}h
                </Typography>
                <Typography variant="body2" color="rgba(255,255,255,0.8)">
                  This Week
                </Typography>
              </CardContent>
            </MetricCard>
          </StatsGrid>

          {/* Top Insights */}
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

      {/* Detailed Analysis Tabs */}
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

        {/* Tab Content */}
        <OverviewTab
          overviewData={overviewData}
          skillAnalysis={skillAnalysis}
          activeTab={activeTab}
          customTooltip={CustomTooltip}
        />

        <TimeConsistencyTab
          timeManagement={timeManagement}
          consistency={consistency}
          activeTab={activeTab}
          customTooltip={CustomTooltip}
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