export type EventType = 'DEADLINE' | 'MILESTONE' | 'REMINDER';
export type ResourceType = 'DOCUMENTATION' | 'COURSE' | 'VIDEO' | 'ARTICLE' | 'OTHER';

export interface CalendarEvent {
    id: number;
    eventDate: string;
    title: string;
    description: string;
    eventType: EventType;
    enrolledCourseId?: number;
}

export interface Goal {
    id: number;
    title: string;
    description: string;
    allocatedHours: number;
    resourceUrl?: string;
    resourceType: ResourceType;
    enrolledCourseId?: number;
    completed: boolean;
}

export interface CalendarMonth {
    year: number;
    month: number;
    days: Array<{
        date: string;
        events: CalendarEvent[];
    }>;
}

export interface EnrolledCourse {
    id: number;
    courseId: number;
    courseTitle: string;
    platform: string;
    url: string;
    enrollmentDate: string;
    targetCompletionDate: string;
    actualCompletionDate?: string;
    progressPercentage: number;
    status: string;
    hoursSpent: number;
    estimatedHours: number;
    category: string;
}

export interface ProgressUpdateRequest {
    enrolledCourseId: number;
    progressPercentage: number;
    additionalHoursSpent: number;
}

export interface CompleteGoalsRequest {
    completedGoalIds: number[];
    enrolledCourseId?: number;
    progressPercentage?: number;
    additionalHoursSpent?: number;
}