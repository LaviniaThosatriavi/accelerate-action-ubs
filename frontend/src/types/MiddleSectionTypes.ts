export interface EnrollmentStats {
    totalCourses: number;
    completedCourses: number;
    inProgressCourses: number;
    notStartedCourses: number;
    totalHoursSpent: number;
    overallProgress: number;
}

export interface ProgressUpdateData {
    enrolledCourseId: number;
    progressPercentage: number;
    additionalHoursSpent: number;
}

export interface CourseScore {
    id: number;
    score: number;
    percentage: number;
    completionDate: string;
    pointsEarned: number;
    courseId: number;
    maxScore: number;
    notes: string | null;
    enrolledCourse: {
        id: number;
    } | null;
    user: {
        id: number;
        username: string;
        password: string;
        accountNonExpired: boolean;
        accountNonLocked: boolean;
    };
}

export interface CourseScoreRequest {
    enrolledCourseId: number;
    score: number;
    completionDate?: string;
}