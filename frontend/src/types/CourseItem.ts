// Define the EnrolledCourse interface
export interface EnrolledCourse {
    id: number;
    courseId: number;
    courseTitle: string;
    platform: string;
    url: string;
    enrollmentDate: string;
    targetCompletionDate: string;
    actualCompletionDate: string | null;
    progressPercentage: number;
    status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';
    hoursSpent: number;
    estimatedHours: number;
    category: string;
}

export interface CourseReference {
    category: string;
    courseraUrl: string;
    freeCodeCampPlaylistUrl: string;
    suggestedVideos: string[];
}

// Define the ProgressUpdate interface for tracking changes
export interface ProgressUpdate {
    percentage: number;
    hours: number;
}

// Define the EnrollmentStats interface
export interface EnrollmentStats {
    totalCourses: number;
    completedCourses: number;
    inProgressCourses: number;
    notStartedCourses: number;
    totalHoursSpent: number;
    overallProgress: number;
}

// Define the CourseReference interface for course recommendations
export interface CourseReference {
    category: string;
    courseraUrl: string;
    freeCodeCampPlaylistUrl: string;
    suggestedVideos: string[];
}

// Define the ExternalCourseEnrollment interface for enrolling in external courses
export interface ExternalCourseEnrollment {
    courseName: string;
    platform: string;
    courseUrl: string;
    estimatedHours: number;
    targetCompletionDate: string;
}