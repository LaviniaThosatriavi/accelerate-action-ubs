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
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  RadialBarChart,
  RadialBar,
} from 'recharts';
import {
  FiCheckCircle,
  FiAlertCircle,
} from 'react-icons/fi';
import { IoBarChartSharp } from "react-icons/io5";
import { LuBrainCircuit } from "react-icons/lu";
import { FaRegLightbulb } from "react-icons/fa";
import {
  IoMdTrophy,
} from 'react-icons/io';
import { MdOutlineSocialDistance } from 'react-icons/md';

import type { OverviewData, SkillAnalysis, StrongSkill, WeakSkill } from '../../types/ReportTypes';
import {
  ChartContainer,
  ProgressBar,
  InsightCard,
  MetricCard,
  SkillBadge,
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

interface OverviewTabProps {
  overviewData: OverviewData | null;
  skillAnalysis: SkillAnalysis | null;
  activeTab: number;
  customTooltip: React.ComponentType<CustomTooltipProps>;
  actualHoursThisWeek: number; // Add the actual hours as a prop
}

interface SkillChartData {
  skill: string;
  score: number;
  level: 'high' | 'medium' | 'low';
}

interface CompletionData {
  name: string;
  value: number;
  color: string;
}

interface SkillDistributionData {
  name: string;
  value: number;
  fill: string;
}

const OverviewTab: React.FC<OverviewTabProps> = ({
  overviewData,
  skillAnalysis,
  activeTab,
  customTooltip: CustomTooltip,
  actualHoursThisWeek,
}) => {
  const getSkillLevel = (score: number): 'high' | 'medium' | 'low' => {
    if (score >= 80) return 'high';
    if (score >= 60) return 'medium';
    return 'low';
  };

  const displayHours = actualHoursThisWeek;
  const targetHours = overviewData?.performance.targetHoursPerWeek || 1;
  const hoursProgress = (displayHours / targetHours) * 100;

  // Prepare chart data with proper typing
  const skillsChartData: SkillChartData[] = Object.entries(skillAnalysis?.skillScores || {}).map(([skill, score]) => ({
    skill,
    score: typeof score === 'number' ? score : 0,
    level: getSkillLevel(typeof score === 'number' ? score : 0)
  }));

  const completionData: CompletionData[] = [
    { name: 'Completed', value: overviewData?.performance.completedCourses || 0, color: colors.success },
    { name: 'Remaining', value: (overviewData?.performance.totalCourses || 0) - (overviewData?.performance.completedCourses || 0), color: colors.gray[300] }
  ];

  const skillDistributionData: SkillDistributionData[] = [
    { name: 'High (80+)', value: skillsChartData.filter(s => s.level === 'high').length, fill: colors.success },
    { name: 'Medium (60-79)', value: skillsChartData.filter(s => s.level === 'medium').length, fill: colors.warning },
    { name: 'Low (<60)', value: skillsChartData.filter(s => s.level === 'low').length, fill: colors.error }
  ];

  const hasWeakSkills = skillAnalysis?.weakSkills && skillAnalysis.weakSkills.length > 0;
  const hasSkillGaps = skillAnalysis?.skillGaps && skillAnalysis.skillGaps.length > 0;

  const getResponsiveRadius = (minRadius: number, maxRadius: number) => {
    if (typeof window !== 'undefined') {
      const vw = window.innerWidth;
      const ratio = Math.min(Math.max((vw - 320) / (1200 - 320), 0), 1);
      return Math.round(minRadius + (maxRadius - minRadius) * ratio);
    }
    return maxRadius; 
  };

  const getResponsiveHeight = () => {
    if (typeof window !== 'undefined') {
      return window.innerWidth < 500 ? 180 : 250;
    }
    return 250; // fallback for SSR
  };

  return (
    <>
      {activeTab === 0 && (
        <CardContent sx={{ padding: '16px'  }}>
          <SectionTitle style={{
            marginBottom: '1vh',
            fontSize: 'clamp(1rem, 2vw, 1.5rem)'
          }}>
            <IoBarChartSharp color={colors.primary} />
            Learning Overview
          </SectionTitle>
          <Typography textAlign="left" variant="body1" color="text.secondary" sx={{ marginBottom: '1.5vh', fontSize: 'clamp(0.7rem, 2vw, 0.9rem)' }}>
            {overviewData?.summary}
          </Typography>

          <Grid 
            container 
            spacing={4}
            sx={{
              '@media (max-width: 600px)': {
                rowGap: '0', 
                '& > .MuiGrid-root': {
                  paddingTop: '0 !important', // Remove default Material-UI spacing
                }
              }
            }}
          >
            <Grid size={{ xs: 12, sm: 6 }}>
              <ChartContainer>
                <Typography variant="h6" fontWeight="bold" sx={{ marginBottom: '0.5vh', fontSize: 'clamp(0.9rem, 2.5vw, 1.25rem)' }}>
                  Course Completion
                </Typography>
                <ResponsiveContainer width="100%" height={getResponsiveHeight()}>
                  <PieChart>
                    <Pie
                      data={completionData}
                      cx="50%"
                      cy="50%"
                      innerRadius={getResponsiveRadius(35, 60)}
                      outerRadius={getResponsiveRadius(60, 100)}
                      paddingAngle={5}
                      dataKey="value"
                    >
                      {completionData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color}  />
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
              <ChartContainer>
                <Typography variant="h6" fontWeight="bold" sx={{fontSize: 'clamp(0.9rem, 2.5vw, 1.25rem)'}} >
                  Performance Metrics
                </Typography>
                <ResponsiveContainer width="100%" height={getResponsiveHeight()}>
                  <RadialBarChart cx="50%" cy="50%" innerRadius="20%" outerRadius="90%" data={[
                    { name: 'Completion', value: overviewData?.performance.completionRate || 0, fill: colors.success },
                    { name: 'Average Score', value: overviewData?.performance.averageScore || 0, fill: colors.primary },
                    { name: 'Points Progress', value: Math.min((overviewData?.performance.totalPoints || 0) / 20, 100), fill: colors.warning }
                  ]}>
                    <RadialBar dataKey="value" cornerRadius={10} fill="#8884d8" />
                    <Tooltip content={<CustomTooltip />} />
                    <Legend 
                      wrapperStyle={{
                        fontSize: 'clamp(0.75rem, 2vw, 1rem)'
                      }}
                    />
                  </RadialBarChart>
                </ResponsiveContainer>
              </ChartContainer>
            </Grid>
          </Grid>

          <SectionContainer>
            <SectionTitle style={{fontSize: 'clamp(1rem, 2vw, 1.5rem)'}}>Progress Tracking</SectionTitle>
            <Grid container spacing={3}>
              <Grid size={{ xs: 12, md: 4 }}>
                <MetricCard>
                  <CardContent>
                    <Box display="flex" justifyContent="space-between" sx={{ marginBottom: 1 }}>
                      <Typography variant="subtitle2">Average Score</Typography>
                      <Typography variant="subtitle2" fontWeight="bold">
                        {overviewData?.performance.averageScore}%
                      </Typography>
                    </Box>
                    <ProgressBar 
                      progress={overviewData?.performance.averageScore || 0} 
                      color={colors.success} 
                    />
                  </CardContent>
                </MetricCard>
              </Grid>
              
              <Grid size={{ xs: 12, md: 4 }}>
                <MetricCard>
                  <CardContent>
                    <Box display="flex" justifyContent="space-between" sx={{ marginBottom: 1 }}>
                      <Typography variant="subtitle2">Weekly Hours</Typography>
                      <Typography variant="subtitle2" fontWeight="bold">
                        {actualHoursThisWeek}/{targetHours}h
                      </Typography>
                    </Box>
                    <ProgressBar 
                      progress={hoursProgress} 
                      color={colors.warning} 
                    />
                  </CardContent>
                </MetricCard>
              </Grid>
              
              <Grid size={{ xs: 12, md: 4 }}>
                <MetricCard>
                  <CardContent>
                    <Box display="flex" justifyContent="space-between" sx={{ marginBottom: 1 }}>
                      <Typography variant="subtitle2">Completion Rate</Typography>
                      <Typography variant="subtitle2" fontWeight="bold">
                        {overviewData?.performance.completionRate}%
                      </Typography>
                    </Box>
                    <ProgressBar 
                      progress={overviewData?.performance.completionRate || 0} 
                      color={colors.primary} 
                    />
                  </CardContent>
                </MetricCard>
              </Grid>
            </Grid>
          </SectionContainer>

          {/* Strengths and Weaknesses */}
          <Grid container spacing={3}>
          <Grid size={{ xs: 12, md: 6 }}>
            <SectionTitle style={{ color: colors.success, fontSize: 'clamp(1rem, 2vw, 1.5rem)'}}>
              <FiCheckCircle />
              Strengths
            </SectionTitle>
            {overviewData?.strengths.map((strength: string, index: number) => (
              <InsightCard key={index} variant="success">
                <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px)' }}>
                  <Box display="flex" alignItems="center" sx={{ gap: '1.2vw', fontSize: 'clamp(0.8rem, 2vw, 1rem)'}}>
                    <FiCheckCircle 
                      color={colors.success} 
                      style={{ fontSize: 'clamp(15px, 2.5vw, 20px)' }}
                    />
                    <Typography 
                      variant="body2" 
                      sx={{ fontSize: 'clamp(0.8rem, 2vw, 1rem)' }}
                    >
                      {strength}
                    </Typography>
                  </Box>
                </CardContent>
              </InsightCard>
            ))}
          </Grid>
          
          <Grid size={{ xs: 12, md: 6 }}>
            <SectionTitle style={{ color: colors.warning, fontSize: 'clamp(1rem, 2vw, 1.5rem)'}}>
              <FiAlertCircle />
              Areas for Improvement
            </SectionTitle>
            {overviewData?.weaknesses.map((weakness: string, index: number) => (
              <InsightCard key={index} variant="warning">
                <CardContent sx={{ padding: 'clamp(10px,2vw, 16px)' }}>
                  <Box display="flex" alignItems="center" sx={{ gap: '1.2vw', fontSize: 'clamp(0.8rem, 2vw, 1rem)'}}>
                    <FiAlertCircle 
                      color={colors.warning} 
                      style={{ fontSize: 'clamp(15px, 2.5vw, 20px)' }}
                    />
                    <Typography 
                      variant="body2" 
                      sx={{ fontSize: 'clamp(0.8rem, 2vw, 1rem)' }}
                    >
                      {weakness}
                    </Typography>
                  </Box>
                </CardContent>
              </InsightCard>
            ))}
          </Grid>
          </Grid>
        </CardContent>
      )}

      {activeTab === 1 && (
  <CardContent sx={{ padding: '16px' }}>
    <SectionTitle style={{
      marginBottom: '1vh',
      fontSize: 'clamp(1rem, 2vw, 1.5rem)'
    }}>
      <LuBrainCircuit 
        style={{ fontSize: 'clamp(18px, 3vw, 24px)' }} 
        color={colors.primary} 
      />
      Skills Analysis
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
      {skillAnalysis?.summary}
    </Typography>

    {/* Skills Performance Chart */}
    <ChartContainer>
      <Typography 
        variant="h6" 
        fontWeight="bold" 
        sx={{ 
          marginBottom: '0.5vh', 
          fontSize: 'clamp(0.9rem, 2.5vw, 1.25rem)' 
        }}
      >
        Skills Performance Overview
      </Typography>
      <ResponsiveContainer width="100%" height={getResponsiveHeight()}>
        <BarChart data={skillsChartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke={colors.gray[200]} />
          <XAxis 
            dataKey="skill" 
            tick={{ fontSize: 'clamp(10, 2vw, 12)' }} 
          />
          <YAxis domain={[0, 100]} />
          <Tooltip content={<CustomTooltip />} />
          <Bar dataKey="score" fill={colors.primary} radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </ChartContainer>

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
            Skill Level Distribution
          </Typography>
          <ResponsiveContainer width="100%" height={getResponsiveHeight()}>
            <PieChart>
              <Pie
                data={skillDistributionData}
                cx="50%"
                cy="50%"
                outerRadius={getResponsiveRadius(50, 80)}
                dataKey="value"
                label={({ name, value }) => `${name}: ${value}`}
              >
                {skillDistributionData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.fill} />
                ))}
              </Pie>
              <Tooltip content={<CustomTooltip />} />
            </PieChart>
          </ResponsiveContainer>
        </ChartContainer>
      </Grid>

      <Grid size={{ xs: 12, sm: 6 }}>
        <SectionTitle style={{
          fontSize: 'clamp(1rem, 2vw, 1.5rem)',
          marginBottom: 'clamp(0.5rem, 1vw, 1rem)'
        }}>
          Individual Skill Scores
        </SectionTitle>
        <Box display="flex" flexDirection="column" sx={{ gap: 'clamp(0.5rem, 1vw, 1rem)' }}>
          {skillsChartData.length > 0 ? (
            skillsChartData.map((skill, index) => (
              <MetricCard key={index}>
                <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px)' }}>
                  <Box 
                    display="flex" 
                    justifyContent="space-between" 
                    alignItems="center" 
                    sx={{ marginBottom: 1 }}
                  >
                    <Typography 
                      variant="subtitle1" 
                      fontWeight="bold"
                      sx={{ fontSize: 'clamp(0.8rem, 2vw, 1rem)' }}
                    >
                      {skill.skill}
                    </Typography>
                    <SkillBadge level={skill.level}>
                      {skill.score}%
                    </SkillBadge>
                  </Box>
                  <ProgressBar 
                    progress={skill.score} 
                    color={skill.level === 'high' ? colors.success : skill.level === 'medium' ? colors.warning : colors.error} 
                  />
                </CardContent>
              </MetricCard>
            ))
          ) : (
            <MetricCard>
              <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px)' }}>
                <Typography 
                  variant="body2" 
                  color="text.secondary" 
                  textAlign="center"
                  sx={{ fontSize: 'clamp(0.75rem, 2vw, 0.875rem)' }}
                >
                  No skill scores available yet. Complete some courses to see your progress!
                </Typography>
              </CardContent>
            </MetricCard>
          )}
        </Box>
      </Grid>
    </Grid>

    {/* Skill Gaps Section */}
    <SectionContainer
      style={{
        marginBottom: 'clamp(1rem, 2vw, 2rem)'
      }}
    >
      <SectionTitle style={{
        fontSize: 'clamp(1rem, 2vw, 1.5rem)',
        marginBottom: 'clamp(0.5rem, 1vw, 1rem)'
      }}>
        <MdOutlineSocialDistance 
          style={{ fontSize: 'clamp(18px, 3vw, 24px)' }} 
        />
        Skill Gaps
      </SectionTitle>
      {hasSkillGaps ? (
        <Box display="flex" sx={{ gap: 'clamp(0.5rem, 1vw, 1rem)' }} flexWrap="wrap">
          {skillAnalysis?.skillGaps.map((gap: string, index: number) => (
            <Chip
              key={index}
              label={gap}
              icon={<MdOutlineSocialDistance 
                style={{ fontSize: 'clamp(16px, 2.5vw, 20px)' }} 
              />}
              sx={{
                background: `linear-gradient(45deg, ${colors.gray[400]}, ${colors.gray[500]})`,
                color: 'white',
                fontSize: 'clamp(0.7rem, 2vw, 0.8rem)',
                padding: 'clamp(0.5vh, 1vw, 1vh) clamp(0.5rem, 1vw, 0.5rem)',
                height: 'clamp(30px, 4vw, 40px)',
                '&:hover': {
                  background: `linear-gradient(45deg, ${colors.gray[500]}, ${colors.gray[600]})`,
                }
              }}
            />
          ))}
        </Box>
      ) : (
        <MetricCard>
          <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px)' }}>
            <Box display="flex" alignItems="center" sx={{ gap: 'clamp(0.5rem, 1vw, 1rem)' }}>
              <FiCheckCircle 
                style={{ fontSize: 'clamp(16px, 2.5vw, 20px)' }} 
                color={colors.success} 
              />
              <Typography 
                variant="body2" 
                color="text.secondary"
                sx={{ fontSize: 'clamp(0.75rem, 2vw, 0.875rem)' }}
              >
                Great! No skill gaps identified. You're working on all the skills in your profile.
              </Typography>
            </Box>
          </CardContent>
        </MetricCard>
      )}
    </SectionContainer>

    {/* Strong vs Weak Skills */}
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
          color: colors.success,
          fontSize: 'clamp(1rem, 2vw, 1.5rem)',
          marginBottom: 'clamp(0.5rem, 1vw, 1rem)'
        }}>
          <IoMdTrophy 
            style={{ fontSize: 'clamp(16px, 2.5vw, 20px)' }} 
          />
          Strong Skills
        </SectionTitle>
        {skillAnalysis?.strongSkills && skillAnalysis.strongSkills.length > 0 ? (
          skillAnalysis.strongSkills.map((skill: StrongSkill, index: number) => (
            <MetricCard key={index} gradient="linear-gradient(135deg, #ecfdf5, #d1fae5)">
              <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px)' }}>
                <Box 
                  display="flex" 
                  alignItems="center" 
                  sx={{ 
                    gap: 'clamp(0.5rem, 1vw, 1rem)',
                    marginBottom: 'clamp(0.5rem, 1vw, 1rem)' 
                  }}
                >
                  <IoMdTrophy 
                    style={{ fontSize: 'clamp(20px, 3vw, 24px)' }} 
                    color={colors.success} 
                  />
                  <Typography 
                    variant="h6" 
                    fontWeight="bold"
                    sx={{ fontSize: 'clamp(0.9rem, 2.5vw, 1.25rem)' }}
                  >
                    {skill.skill}
                  </Typography>
                  <Chip 
                    label={`${skill.averageScore}%`} 
                    size="small" 
                    sx={{ 
                      background: colors.success, 
                      color: 'white',
                      fontSize: 'clamp(0.7rem, 1.5vw, 0.8rem)'
                    }}
                  />
                </Box>
                <Typography 
                  textAlign="left" 
                  variant="body2" 
                  color="text.secondary" 
                  sx={{ 
                    marginBottom: 1,
                    fontSize: 'clamp(0.75rem, 2vw, 0.875rem)'
                  }}
                >
                  {skill.reason}
                </Typography>
                <Typography 
                  color="text.secondary"
                  textAlign="left"
                  sx={{ fontSize: 'clamp(0.7rem, 1.5vw, 0.75rem)' }}
                >
                  {skill.coursesCompleted} courses completed
                </Typography>
              </CardContent>
            </MetricCard>
          ))
        ) : (
          <MetricCard>
            <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px)' }}>
              <Typography 
                variant="body2" 
                color="text.secondary" 
                textAlign="center"
                sx={{ fontSize: 'clamp(0.75rem, 2vw, 0.875rem)' }}
              >
                Complete more courses to identify your strong skills!
              </Typography>
            </CardContent>
          </MetricCard>
        )}
      </Grid>

      <Grid size={{ xs: 12, md: 6 }}>
        <SectionTitle style={{ 
          color: colors.warning,
          fontSize: 'clamp(1rem, 2vw, 1.5rem)',
          marginBottom: 'clamp(0.5rem, 1vw, 1rem)'
        }}>
          <FiAlertCircle 
            style={{ fontSize: 'clamp(16px, 2.5vw, 20px)' }} 
          />
          Skills Needing Attention
        </SectionTitle>
        {hasWeakSkills ? (
          skillAnalysis?.weakSkills.map((skill: WeakSkill, index: number) => (
            <MetricCard key={index} gradient="linear-gradient(135deg, #fffbeb, #fef3c7)">
              <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px)' }}>
                <Box 
                  display="flex" 
                  alignItems="center" 
                  sx={{ 
                    gap: 'clamp(0.5rem, 1vw, 1rem)',
                    marginBottom: 'clamp(0.5rem, 1vw, 1rem)' 
                  }}
                >
                  <FiAlertCircle 
                    style={{ fontSize: 'clamp(20px, 3vw, 24px)' }} 
                    color={colors.warning} 
                  />
                  <Typography 
                    variant="h6" 
                    fontWeight="bold" 
                    color="black"
                    sx={{ fontSize: 'clamp(0.9rem, 2.5vw, 1.25rem)' }}
                  >
                    {skill.skill}
                  </Typography>
                  <Chip 
                    label={`${skill.averageScore}%`} 
                    size="small" 
                    sx={{ 
                      background: colors.warning, 
                      color: 'black',
                      fontSize: 'clamp(0.7rem, 1.5vw, 0.8rem)'
                    }}
                  />
                </Box>
                <Typography 
                  textAlign="left" 
                  variant="body2" 
                  color="text.secondary" 
                  sx={{ 
                    marginBottom: 1,
                    fontSize: 'clamp(0.75rem, 2vw, 0.875rem)'
                  }}
                >
                  {skill.reason}
                </Typography>
                <Alert severity="info" sx={{ marginTop: 2 }}>
                  <Typography 
                    variant="caption"
                    sx={{ fontSize: 'clamp(0.7rem, 1.5vw, 0.75rem)' }}
                  >
                    <strong>Improvement tip:</strong> {skill.improvement}
                  </Typography>
                </Alert>
              </CardContent>
            </MetricCard>
          ))
        ) : (
          <MetricCard gradient="linear-gradient(135deg, #ecfdf5, #d1fae5)">
            <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px)' }}>
              <Box display="flex" alignItems="center" sx={{ gap: 'clamp(0.5rem, 1vw, 1rem)' }}>
                <FiCheckCircle 
                  style={{ fontSize: 'clamp(16px, 2.5vw, 20px)' }} 
                  color={colors.success} 
                />
                <Typography 
                  variant="body2" 
                  color="text.secondary"
                  sx={{ fontSize: 'clamp(0.75rem, 2vw, 0.875rem)' }}
                >
                  Good job! No course scores below 65%. Keep up the excellent work!
                </Typography>
              </Box>
            </CardContent>
          </MetricCard>
        )}
      </Grid>
    </Grid>

    {/* Recommended Skills */}
    <SectionContainer>
      <SectionTitle style={{
        fontSize: 'clamp(1rem, 2vw, 1.5rem)',
        marginBottom: 'clamp(0.5rem, 1vw, 1rem)'
      }}>
        <FaRegLightbulb 
          style={{ fontSize: 'clamp(16px, 2.5vw, 20px)' }} 
        />
        Recommended Skills to Learn
      </SectionTitle>
      {skillAnalysis?.recommendedSkills && skillAnalysis.recommendedSkills.length > 0 ? (
        <Box display="flex" sx={{ gap: 'clamp(0.5rem, 1vw, 1rem)' }} flexWrap="wrap">
          {skillAnalysis.recommendedSkills.map((skill: string, index: number) => (
            <Chip
              key={index}
              label={skill}
              icon={<FaRegLightbulb 
                style={{ fontSize: 'clamp(12px, 2vw, 16px)' }} 
              />}
              sx={{
                background: `linear-gradient(45deg, ${colors.primary}, ${colors.secondary})`,
                color: 'white',
                fontSize: 'clamp(0.8rem, 2vw, 1rem)',
                padding: 'clamp(1vh, 2vw, 2.5vh) clamp(0.5rem, 1vw, 0.5rem)',
                height: 'clamp(32px, 4vw, 40px)',
                '&:hover': {
                  background: `linear-gradient(45deg, ${colors.secondary}, ${colors.primary})`,
                }
              }}
            />
          ))}
        </Box>
      ) : (
        <MetricCard>
          <CardContent sx={{ padding: 'clamp(10px, 2vw, 16px)' }}>
            <Typography 
              variant="body2" 
              color="text.secondary" 
              textAlign="center"
              sx={{ fontSize: 'clamp(0.75rem, 2vw, 0.875rem)' }}
            >
              No specific recommendations at this time. Keep exploring new skills!
            </Typography>
          </CardContent>
          </MetricCard>
      )}
    </SectionContainer>
  </CardContent>
)}
    </>
  );
};

export default OverviewTab;