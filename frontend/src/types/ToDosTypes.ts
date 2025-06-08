export type EventType = 'DEADLINE' | 'MILESTONE' | 'REMINDER';
export type ResourceType = 'DOCUMENTATION' | 'COURSE' | 'VIDEO' | 'ARTICLE' | 'OTHER';

export interface CalendarEvent {
    id?: number;
    eventDate: string;
    title: string;
    description: string;
    eventType: string;
    enrolledCourseId: number;
    courseTitle: string;
    completed?: boolean;
    dueTime?: string;
}

export interface Goal {
    id: number;
    title: string;
    description: string;
    allocatedHours: number;
    resourceUrl?: string;
    resourceType: ResourceType;
    enrolledCourseId?: number;
    courseName?: string;
    goalDate?: string;
    isCompleted: boolean;
    isRecommended?: boolean;
    completedAt?: string;
}

export interface CalendarMonth {
    year: number;
    month: number;
    firstDay: string;
    lastDay: string;
    events: Record<string, CalendarEvent[]>;
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