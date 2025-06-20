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

  return (
    <>
      {activeTab === 0 && (
        <CardContent sx={{ padding: '32px' }}>
          <SectionTitle>
            <IoBarChartSharp size={24} color={colors.primary} />
            Learning Overview
          </SectionTitle>
          <Typography textAlign="left" variant="body1" color="text.secondary" sx={{ marginBottom: 4 }}>
            {overviewData?.summary}
          </Typography>

          <Grid container spacing={4}>
            <Grid size={{ xs: 12, md: 6 }}>
              <ChartContainer>
                <Typography variant="h6" fontWeight="bold" sx={{ marginBottom: 2 }}>
                  Course Completion
                </Typography>
                <ResponsiveContainer width="100%" height={250}>
                  <PieChart>
                    <Pie
                      data={completionData}
                      cx="50%"
                      cy="50%"
                      innerRadius={60}
                      outerRadius={100}
                      paddingAngle={5}
                      dataKey="value"
                    >
                      {completionData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip content={<CustomTooltip />} />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </ChartContainer>
            </Grid>

            <Grid size={{ xs: 12, md: 6 }}>
              <ChartContainer>
                <Typography variant="h6" fontWeight="bold" >
                  Performance Metrics
                </Typography>
                <ResponsiveContainer width="100%" height={250}>
                  <RadialBarChart cx="50%" cy="50%" innerRadius="20%" outerRadius="90%" data={[
                    { name: 'Completion', value: overviewData?.performance.completionRate || 0, fill: colors.success },
                    { name: 'Average Score', value: overviewData?.performance.averageScore || 0, fill: colors.primary },
                    { name: 'Points Progress', value: Math.min((overviewData?.performance.totalPoints || 0) / 20, 100), fill: colors.warning }
                  ]}>
                    <RadialBar dataKey="value" cornerRadius={10} fill="#8884d8" />
                    <Tooltip content={<CustomTooltip />} />
                    <Legend />
                  </RadialBarChart>
                </ResponsiveContainer>
              </ChartContainer>
            </Grid>
          </Grid>

          <SectionContainer>
            <SectionTitle>Progress Tracking</SectionTitle>
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
                    <Typography variant="caption" color="text.secondary" sx={{ fontSize: '0.7rem', marginTop: 0.5 }}>
                      Live API data
                    </Typography>
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
              <SectionTitle style={{ color: colors.success }}>
                <FiCheckCircle size={20} />
                Strengths
              </SectionTitle>
              {overviewData?.strengths.map((strength: string, index: number) => (
                <InsightCard key={index} variant="success">
                  <CardContent sx={{ padding: '16px' }}>
                    <Box display="flex" alignItems="center" gap={2}>
                      <FiCheckCircle size={20} color={colors.success} />
                      <Typography variant="body2">{strength}</Typography>
                    </Box>
                  </CardContent>
                </InsightCard>
              ))}
            </Grid>
            
            <Grid size={{ xs: 12, md: 6 }}>
              <SectionTitle style={{ color: colors.warning }}>
                <FiAlertCircle size={20} />
                Areas for Improvement
              </SectionTitle>
              {overviewData?.weaknesses.map((weakness: string, index: number) => (
                <InsightCard key={index} variant="warning">
                  <CardContent sx={{ padding: '16px' }}>
                    <Box display="flex" alignItems="center" gap={2}>
                      <FiAlertCircle size={20} color={colors.warning} />
                      <Typography variant="body2">{weakness}</Typography>
                    </Box>
                  </CardContent>
                </InsightCard>
              ))}
            </Grid>
          </Grid>
        </CardContent>
      )}

      {activeTab === 1 && (
        <CardContent sx={{ padding: '32px' }}>
          <SectionTitle>
            <LuBrainCircuit size={24} color={colors.primary} />
            Skills Analysis
          </SectionTitle>
          <Typography textAlign="left" variant="body1" color="text.secondary" sx={{ marginBottom: 4 }}>
            {skillAnalysis?.summary}
          </Typography>

          {/* Skills Performance Chart */}
          <ChartContainer>
            <Typography variant="h6" fontWeight="bold" sx={{ marginBottom: 2 }}>
              Skills Performance Overview
            </Typography>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={skillsChartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" stroke={colors.gray[200]} />
                <XAxis dataKey="skill" tick={{ fontSize: 12 }} />
                <YAxis domain={[0, 100]} />
                <Tooltip content={<CustomTooltip />} />
                <Bar dataKey="score" fill={colors.primary} radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </ChartContainer>

          {/* Skill Distribution */}
          <Grid container spacing={4} sx={{ marginBottom: 4 }}>
            <Grid size={{ xs: 12, md: 6 }}>
              <ChartContainer>
                <Typography variant="h6" fontWeight="bold" sx={{ marginBottom: 2 }}>
                  Skill Level Distribution
                </Typography>
                <ResponsiveContainer width="100%" height={250}>
                  <PieChart>
                    <Pie
                      data={skillDistributionData}
                      cx="50%"
                      cy="50%"
                      outerRadius={80}
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

            <Grid size={{ xs: 12, md: 6 }}>
              <SectionTitle>Individual Skill Scores</SectionTitle>
              <Box display="flex" flexDirection="column" gap={2}>
                {skillsChartData.length > 0 ? (
                  skillsChartData.map((skill, index) => (
                    <MetricCard key={index}>
                      <CardContent sx={{ padding: '16px' }}>
                        <Box display="flex" justifyContent="space-between" alignItems="center" sx={{ marginBottom: 1 }}>
                          <Typography variant="subtitle1" fontWeight="bold">
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
                    <CardContent sx={{ padding: '16px' }}>
                      <Typography variant="body2" color="text.secondary" textAlign="center">
                        No skill scores available yet. Complete some courses to see your progress!
                      </Typography>
                    </CardContent>
                  </MetricCard>
                )}
              </Box>
            </Grid>
          </Grid>

          {/* Skill Gaps Section */}
          <SectionContainer>
            <SectionTitle>
              <MdOutlineSocialDistance size={24} />
              Skill Gaps
            </SectionTitle>
            {hasSkillGaps ? (
              <Box display="flex" gap={1} flexWrap="wrap">
                {skillAnalysis?.skillGaps.map((gap: string, index: number) => (
                  <Chip
                    key={index}
                    label={gap}
                    icon={<MdOutlineSocialDistance size={20} />}
                    sx={{
                      background: `linear-gradient(45deg, ${colors.gray[400]}, ${colors.gray[500]})`,
                      color: 'white',
                      fontSize: '1rem',
                      padding: '2.5vh 0.5rem',
                      height: '4vh',
                      
                      '&:hover': {
                        background: `linear-gradient(45deg, ${colors.gray[500]}, ${colors.gray[600]})`,
                      }
                    }}
                  />
                ))}
              </Box>
            ) : (
              <MetricCard>
                <CardContent sx={{ padding: '16px' }}>
                  <Box display="flex" alignItems="center" gap={2}>
                    <FiCheckCircle size={20} color={colors.success} />
                    <Typography variant="body2" color="text.secondary">
                      Great! No skill gaps identified. You're working on all the skills in your profile.
                    </Typography>
                  </Box>
                </CardContent>
              </MetricCard>
            )}
          </SectionContainer>

          {/* Strong vs Weak Skills */}
          <Grid container spacing={3}>
            <Grid size={{ xs: 12, md: 6 }}>
              <SectionTitle style={{ color: colors.success }}>
                <IoMdTrophy size={20} />
                Strong Skills
              </SectionTitle>
              {skillAnalysis?.strongSkills && skillAnalysis.strongSkills.length > 0 ? (
                skillAnalysis.strongSkills.map((skill: StrongSkill, index: number) => (
                  <MetricCard key={index} gradient="linear-gradient(135deg, #ecfdf5, #d1fae5)">
                    <CardContent>
                      <Box display="flex" alignItems="center" gap={2} sx={{ marginBottom: 2 }}>
                        <IoMdTrophy size={24} color={colors.success} />
                        <Typography variant="h6" fontWeight="bold">
                          {skill.skill}
                        </Typography>
                        <Chip 
                          label={`${skill.averageScore}%`} 
                          size="small" 
                          sx={{ background: colors.success, color: 'white' }}
                        />
                      </Box>
                      <Typography textAlign="left" variant="body2" color="text.secondary" sx={{ marginBottom: 1 }}>
                        {skill.reason}
                      </Typography>
                      <Typography textAlign="left" variant="caption" color="text.secondary">
                        {skill.coursesCompleted} courses completed
                      </Typography>
                    </CardContent>
                  </MetricCard>
                ))
              ) : (
                <MetricCard>
                  <CardContent sx={{ padding: '16px' }}>
                    <Typography variant="body2" color="text.secondary" textAlign="center">
                      Complete more courses to identify your strong skills!
                    </Typography>
                  </CardContent>
                </MetricCard>
              )}
            </Grid>

            <Grid size={{ xs: 12, md: 6 }}>
              <SectionTitle style={{ color: colors.warning }}>
                <FiAlertCircle size={20} />
                Skills Needing Attention
              </SectionTitle>
              {hasWeakSkills ? (
                skillAnalysis?.weakSkills.map((skill: WeakSkill, index: number) => (
                  <MetricCard key={index} gradient="linear-gradient(135deg, #fffbeb, #fef3c7)">
                    <CardContent>
                      <Box display="flex" alignItems="center" gap={2} sx={{ marginBottom: 2 }}>
                        <FiAlertCircle size={24} color={colors.warning} />
                        <Typography variant="h6" fontWeight="bold" color="black">
                          {skill.skill}
                        </Typography>
                        <Chip 
                          label={`${skill.averageScore}%`} 
                          size="small" 
                          sx={{ background: colors.warning, color: 'black' }}
                        />
                      </Box>
                      <Typography variant="body2" color="text.secondary" sx={{ marginBottom: 1 }}>
                        {skill.reason}
                      </Typography>
                      <Alert severity="info" sx={{ marginTop: 2 }}>
                        <Typography variant="caption">
                          <strong>Improvement tip:</strong> {skill.improvement}
                        </Typography>
                      </Alert>
                    </CardContent>
                  </MetricCard>
                ))
              ) : (
                <MetricCard gradient="linear-gradient(135deg, #ecfdf5, #d1fae5)">
                  <CardContent sx={{ padding: '16px' }}>
                    <Box display="flex" alignItems="center" gap={2}>
                      <FiCheckCircle size={20} color={colors.success} />
                      <Typography variant="body2" color="text.secondary">
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
            <SectionTitle>
              <FaRegLightbulb size={20} />
              Recommended Skills to Learn
            </SectionTitle>
            {skillAnalysis?.recommendedSkills && skillAnalysis.recommendedSkills.length > 0 ? (
              <Box display="flex" gap={1} flexWrap="wrap">
                {skillAnalysis.recommendedSkills.map((skill: string, index: number) => (
                  <Chip
                    key={index}
                    label={skill}
                    icon={<FaRegLightbulb size={16} />}
                    sx={{
                      background: `linear-gradient(45deg, ${colors.primary}, ${colors.secondary})`,
                      color: 'white',
                      fontSize: '1rem',
                      padding: '2.5vh 0.5rem',
                      height: '4vh',
                      '&:hover': {
                        background: `linear-gradient(45deg, ${colors.secondary}, ${colors.primary})`,
                      }
                    }}
                  />
                ))}
              </Box>
            ) : (
              <MetricCard>
                <CardContent sx={{ padding: '16px' }}>
                  <Typography variant="body2" color="text.secondary" textAlign="center">
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