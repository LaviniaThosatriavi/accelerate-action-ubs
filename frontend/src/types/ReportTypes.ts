export interface QuickInsights {
    username: string;
    completionRate: number;
    averageScore: number;
    loginStreak: number;
    currentBadgeLevel: string;
    topStrength: string;
    topRecommendation: string;
}

export interface OverviewData {
    reportType: string;
    summary: string;
    performance: {
        totalCourses: number;
        completedCourses: number;
        completionRate: number;
        averageScore: number;
        totalPoints: number;
        loginStreak: number;
        hoursThisWeek: number;
        targetHoursPerWeek: number;
        currentBadgeLevel: string;
    };
    strengths: string[];
    weaknesses: string[];
    recommendations: string[];
}

export interface SkillData {
    skill: string;
    averageScore: number;
    coursesCompleted: number;
    reason: string;
}

export interface WeakSkillData {
    skill: string;
    averageScore: number;
    coursesAttempted: number;
    reason: string;
    improvement: string;
}

export interface SkillAnalysis {
    reportType: string;
    summary: string;
    strongSkills: SkillData[];
    weakSkills: WeakSkillData[];
    skillGaps: string[];
    recommendedSkills: string[];
    skillScores: Record<string, number>;
}

export interface TimeAnalysis {
    plannedHoursPerWeek: number;
    timeUtilizationRate: number;
    recommendation: string;
    optimalHoursPerWeek: number;
    learningPace: string;
    hasOverdueDeadlines: boolean;
}

export interface TimeManagement {
    reportType: string;
    summary: string;
    timeAnalysis: TimeAnalysis;
    timeInsights: string[];
    optimizationTips: string[];
}

export interface ConsistencyMetrics {
    currentLoginStreak: number;
    longestLoginStreak: number;
    goalCompletionRate: number;
    goalsCompletedThisWeek: number;
    totalGoalsThisWeek: number;
    consistencyLevel: string;
    averageStudySessionsPerWeek: number;
}

export interface ConsistencyData {
    reportType: string;
    summary: string;
    metrics: ConsistencyMetrics;
    consistencyInsights: string[];
    improvementSuggestions: string[];
}

export interface CompetitiveMetrics {
    currentRank: number;
    totalUsers: number;
    percentile: string;
    pointsThisWeek: number;
    pointsToNextRank: number;
    trendDirection: string;
    badgeLevel: string;
    pointsToNextBadge: number;
}

export interface CompetitiveData {
    reportType: string;
    summary: string;
    metrics: CompetitiveMetrics;
    competitiveInsights: string[];
    motivationalMessages: string[];
}

export interface ReportData {
    quickInsights: QuickInsights | null;
    overview: OverviewData | null;
    skills: SkillAnalysis | null;
    timeManagement: TimeManagement | null;
    consistency: ConsistencyData | null;
    competitive: CompetitiveData | null;
}

export interface TabPanelProps {
    children?: React.ReactNode;
    index: number;
    value: number;
}

export interface WeakSkill {
    skill: string;
    averageScore: number;
    reason: string;
    improvement: string;
}

export interface StrongSkill {
    skill: string;
    averageScore: number;
    reason: string;
    coursesCompleted: number;
}