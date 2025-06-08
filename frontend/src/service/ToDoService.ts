import axios, { type AxiosResponse } from 'axios';
import type { Goal, CalendarMonth, EnrolledCourse, ProgressUpdateRequest } from '../types/ToDosTypes';

const API_URL = 'http://localhost:8080/api';

const getAuthHeader = (): { Authorization: string } => {
    const token = localStorage.getItem('token');
    return { Authorization: `Bearer ${token}` };
};

export const TodoService = {
    // Calendar related APIs
    getCalendarMonth: async (): Promise<CalendarMonth> => {
        try {
            console.log("Fetching calendar month data");
            const response: AxiosResponse<CalendarMonth> = await axios.get(
                `${API_URL}/calendar/month`,
                { headers: getAuthHeader() }
            );

            console.log("Calendar month data:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching calendar month:', error);
            throw error;
        }
    },

    // Goals related APIs
    getTodayGoals: async (): Promise<Goal[]> => {
        try {
            console.log("Fetching all of today's goals (completed and incomplete)");
            const response: AxiosResponse<Goal[]> = await axios.get(
                `${API_URL}/goals/today`,
                { headers: getAuthHeader() }
            );
            console.log("Today's goals response:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching today\'s goals:', error);
            throw error;
        }
    },

    getActiveTodayGoals: async (): Promise<Goal[]> => {
        try {
            console.log("Fetching active (incomplete) goals for today");
            const response: AxiosResponse<Goal[]> = await axios.get(
                `${API_URL}/goals/today/active`,
                { headers: getAuthHeader() }
            );
            console.log("Today's active goals response:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching today\'s active goals:', error);
            throw error;
        }
    },

    completeGoals: async (goalIds: number[]): Promise<Goal[]> => {
        try {
            console.log("Completing goals with IDs:", goalIds);

            const response: AxiosResponse<Goal[]> = await axios.post(
                `${API_URL}/goals/complete`,
                { completedGoalIds: goalIds },
                { headers: { ...getAuthHeader(), 'Content-Type': 'application/json' } }
            );

            console.log("Complete goals response:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error completing goals:', error);
            if (axios.isAxiosError(error)) {
                console.error('API error details:', error.response?.data);
            }
            throw error;
        }
    },

    createGoal: async (goal: Omit<Goal, 'id' | 'isCompleted'>): Promise<Goal> => {
        try {
            console.log("Creating new goal:", goal);
            const response: AxiosResponse<Goal> = await axios.post(
                `${API_URL}/goals`,
                goal,
                { headers: { ...getAuthHeader(), 'Content-Type': 'application/json' } }
            );
            console.log("Create goal response:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error creating goal:', error);
            throw error;
        }
    },

    // Enrolled Courses APIs
    getEnrolledCourses: async (): Promise<EnrolledCourse[]> => {
        try {
            console.log("Fetching enrolled courses");
            const response: AxiosResponse<EnrolledCourse[]> = await axios.get(
                `${API_URL}/enrolled-courses`,
                { headers: getAuthHeader() }
            );
            console.log("Enrolled courses response:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching enrolled courses:', error);
            throw error;
        }
    },

    updateProgress: async (request: ProgressUpdateRequest): Promise<EnrolledCourse> => {
        try {
            console.log("Updating course progress:", request);
            const response: AxiosResponse<EnrolledCourse> = await axios.put(
                `${API_URL}/enrolled-courses/progress`,
                request,
                { headers: { ...getAuthHeader(), 'Content-Type': 'application/json' } }
            );
            console.log("Update progress response:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error updating course progress:', error);
            if (axios.isAxiosError(error)) {
                console.error('API error details:', error.response?.data);
            }
            throw error;
        }
    }
};