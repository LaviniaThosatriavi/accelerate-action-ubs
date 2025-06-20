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
        <CardContent sx={{ padding: '32px' }}>
          <SectionTitle>
            <FiUsers size={24} color={colors.primary} />
            Competitive Analysis
          </SectionTitle>
          <Typography textAlign="left" variant="body1" color="text.secondary" sx={{ marginBottom: 4 }}>
            {competitive?.summary}
          </Typography>

          <Grid container spacing={4} sx={{ marginBottom: 4 }}>
            <Grid size={{ xs: 12, md: 6 }}>
              <ChartContainer>
                <Typography variant="h6" fontWeight="bold" sx={{ marginBottom: 2 }}>
                  Your Position
                </Typography>
                <ResponsiveContainer width="100%" height={250}>
                  <PieChart>
                    <Pie
                      data={rankingPositionData}
                      cx="50%"
                      cy="50%"
                      innerRadius={60}
                      outerRadius={100}
                      dataKey="value"
                    >
                      {rankingPositionData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.fill} />
                      ))}
                    </Pie>
                    <Tooltip content={<CustomTooltip />} />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </ChartContainer>
            </Grid>

            <Grid size={{ xs: 12, md: 6 }}>
              <RankCard>
                <CardContent sx={{ textAlign: 'center', position: 'relative', zIndex: 1 }}>
                  <FiUsers size={64} color="black"/>
                  <Typography variant="h2" fontWeight="bold" color="red">
                    #{competitive?.metrics.currentRank}
                  </Typography>
                  <Typography variant="h6" color="black" sx={{ marginTop:0 }}>
                    Current Rank
                  </Typography>
                  <Typography variant="body2" color="black">
                    out of {competitive?.metrics.totalUsers.toLocaleString()} users
                  </Typography>
                  <Chip 
                    label={competitive?.metrics.percentile}
                    sx={{ 
                      marginTop: 0,
                      background: 'rgba(255,255,255,0.2)', 
                      color: 'black',
                      fontWeight: 'bold'
                    }}
                  />
                </CardContent>
              </RankCard>
            </Grid>
          </Grid>

          <StatsGrid>
            <MetricCard gradient={`linear-gradient(135deg, ${colors.success}, #059669)`}>
              <CardContent sx={{ textAlign: 'center' }}>
                  <FiStar size={36} color="blue" />
                  <Box>
                    <Typography variant="h4" fontWeight="bold" color="blue" sx={{ marginTop: 1 }}>
                      {competitive?.metrics.pointsThisWeek}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      Points This Week
                    </Typography>
                  </Box>
              </CardContent>
            </MetricCard>
            
            <MetricCard>
              <CardContent sx={{ textAlign: 'center' }}>
                <FiTrendingUp size={36} color={colors.success} />
                <Typography variant="h4" fontWeight="bold" sx={{ marginTop: 1 }}>
                  {competitive?.metrics.pointsToNextRank}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Points to Next Rank
                </Typography>
              </CardContent>
            </MetricCard>
            
            <MetricCard>
              <CardContent sx={{ textAlign: 'center' }}>
                <FiAward size={36} color={getBadgeColor(competitive?.metrics.badgeLevel || 'SILVER')} />
                <Typography variant="h4" fontWeight="bold" sx={{ marginTop: 1 }}>
                  {competitive?.metrics.pointsToNextBadge}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Points to Next Badge
                </Typography>
              </CardContent>
            </MetricCard>
          </StatsGrid>

          <SectionContainer>
            <SectionTitle>Progress to Next Rank</SectionTitle>
            <MetricCard>
              <CardContent>
                <Box display="flex" justifyContent="space-between" sx={{ marginBottom: 1 }}>
                  <Typography variant="body2" color="text.secondary">
                    Current Progress
                  </Typography>
                  <Typography variant="body2" fontWeight="bold">
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

          <Grid container spacing={3}>
            <Grid size={{ xs: 12, md: 6 }}>
              <SectionTitle>Competitive Insights</SectionTitle>
              {competitive?.competitiveInsights.map((insight: string, index: number) => (
                <InsightCard key={index} variant="warning">
                  <CardContent>
                    <Box display="flex" alignItems="center" gap={2}>
                      <IoMdTrophy size={20} color={colors.warning} />
                      <Typography variant="body2">{insight}</Typography>
                    </Box>
                  </CardContent>
                </InsightCard>
              ))}
            </Grid>

            <Grid size={{ xs: 12, md: 6 }}>
              <SectionTitle>Keep Going!</SectionTitle>
              {competitive?.motivationalMessages.map((message: string, index: number) => (
                <Alert 
                  key={index}
                  severity="success"
                  icon={<IoMdRocket />}
                  sx={{ 
                    marginBottom: 2,
                    borderRadius: '12px',
                    background: 'linear-gradient(135deg, #ecfdf5, #d1fae5)'
                  }}
                >
                  <Typography textAlign="left" variant="body2" fontWeight="500">
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