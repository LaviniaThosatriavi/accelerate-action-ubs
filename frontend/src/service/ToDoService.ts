// src/services/TodoService.ts
import axios, { type AxiosResponse } from 'axios';
import type { Goal, CalendarEvent, CalendarMonth, EnrolledCourse, ProgressUpdateRequest, CompleteGoalsRequest } from '../types/ToDosTypes';

const API_URL = 'http://localhost:8080/api';

const getAuthHeader = (): { Authorization: string } => {
    const token = localStorage.getItem('token');
    return { Authorization: `Bearer ${token}` };
};

export const TodoService = {
    // Calendar related APIs
    getCalendarMonth: async (year: number, month: number): Promise<CalendarMonth> => {
        try {
            const response: AxiosResponse<CalendarMonth> = await axios.get(
                `${API_URL}/calendar/month?year=${year}&month=${month}`,
                { headers: getAuthHeader() }
            );
            return response.data;
        } catch (error) {
            console.error('Error fetching calendar month:', error);
            throw error;
        }
    },

    createCalendarEvent: async (event: Omit<CalendarEvent, 'id'>): Promise<CalendarEvent> => {
        try {
            const response: AxiosResponse<CalendarEvent> = await axios.post(
                `${API_URL}/calendar/event`,
                event,
                { headers: { ...getAuthHeader(), 'Content-Type': 'application/json' } }
            );
            return response.data;
        } catch (error) {
            console.error('Error creating calendar event:', error);
            throw error;
        }
    },

    updateCalendarEvent: async (id: number, event: Partial<CalendarEvent>): Promise<CalendarEvent> => {
        try {
            const response: AxiosResponse<CalendarEvent> = await axios.put(
                `${API_URL}/calendar/event/${id}`,
                event,
                { headers: { ...getAuthHeader(), 'Content-Type': 'application/json' } }
            );
            return response.data;
        } catch (error) {
            console.error('Error updating calendar event:', error);
            throw error;
        }
    },

    deleteCalendarEvent: async (id: number): Promise<void> => {
        try {
            await axios.delete(
                `${API_URL}/calendar/event/${id}`,
                { headers: getAuthHeader() }
            );
        } catch (error) {
            console.error('Error deleting calendar event:', error);
            throw error;
        }
    },

    // Goals related APIs
    getTodayGoals: async (): Promise<Goal[]> => {
        try {
            console.log("Fetching today's goals");
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

    getWeeklyGoals: async (startDate: string, endDate: string): Promise<Goal[]> => {
        try {
            console.log(`Fetching weekly goals from ${startDate} to ${endDate}`);
            const response: AxiosResponse<Goal[]> = await axios.get(
                `${API_URL}/goals/week?startDate=${startDate}&endDate=${endDate}`,
                { headers: getAuthHeader() }
            );
            console.log("Weekly goals response:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching weekly goals:', error);
            throw error;
        }
    },

    getGoalById: async (id: number): Promise<Goal> => {
        try {
            console.log(`Fetching goal with ID: ${id}`);
            const response: AxiosResponse<Goal> = await axios.get(
                `${API_URL}/goals/${id}`,
                { headers: getAuthHeader() }
            );
            console.log("Goal details response:", response.data);
            return response.data;
        } catch (error) {
            console.error(`Error fetching goal with ID ${id}:`, error);
            throw error;
        }
    },

    createGoal: async (goal: Omit<Goal, 'id' | 'completed'>): Promise<Goal> => {
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

    completeGoals: async (goalIds: number[]): Promise<Goal[]> => {
        try {
            console.log("Completing goals with IDs:", goalIds);

            // Using the simplified API endpoint format
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

    deleteGoal: async (id: number): Promise<void> => {
        try {
            console.log(`Deleting goal with ID: ${id}`);
            await axios.delete(
                `${API_URL}/goals/${id}`,
                { headers: getAuthHeader() }
            );
            console.log(`Goal with ID ${id} deleted successfully`);
        } catch (error) {
            console.error(`Error deleting goal with ID ${id}:`, error);
            throw error;
        }
    },

    // Enrolled Courses APIs
    getEnrolledCourses: async (): Promise<EnrolledCourse[]> => {
        try {
            const response: AxiosResponse<EnrolledCourse[]> = await axios.get(
                `${API_URL}/enrolled-courses`,
                { headers: getAuthHeader() }
            );
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