export interface AchievementProfile {
    userId: number;
    username: string;
    totalPoints: number;
    currentBadgeLevel: string;
    pointsToNextLevel: number;
    nextBadgeLevel: string;
    loginStreak: number;
    completedCourses: number;
    averageScore: number;
}

export interface Badge {
    level: string;
    minPoints: number;
    maxPoints: number | null;
    description: string;
    iconUrl: string;
}

export interface LeaderboardUser {
    userId: number;
    username: string;
    points: number;
    rank: number;
    badgeLevel: string;
    isCurrentUser: boolean;
}

export interface LeaderboardData {
    topUsers: LeaderboardUser[];
    currentUser: LeaderboardUser | null;
    period: string;
}

export interface LeaderboardResponse {
    topUsers: LeaderboardUser[];
    currentUser: LeaderboardUser | null;
    period: string;
}