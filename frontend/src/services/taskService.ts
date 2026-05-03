import { api } from './api';
import type { TaskResponse, TaskRequest, TaskStatusRequest, GetTasksParams, Page } from '../types/task';

export const taskService = {
  

    getTasks: async (params?: GetTasksParams): Promise<Page<TaskResponse>> => {
        const response = await api.get<Page<TaskResponse>>('/tasks', { 
            params: params 
        });
        return response.data;
    },

    getTaskById: async (id: number): Promise<TaskResponse> => {
        const response = await api.get<TaskResponse>(`/tasks/${id}`);
        return response.data;
    },

    createTask: async (data: TaskRequest): Promise<TaskResponse> => {
        const response = await api.post<TaskResponse>('/tasks', data);
        return response.data;
    },

    updateTask: async (id: number, data: TaskRequest): Promise<TaskResponse> => {
        const response = await api.put<TaskResponse>(`/tasks/${id}`, data);
        return response.data;
    },

    updateTaskStatus: async (id: number, data: TaskStatusRequest): Promise<TaskResponse> => {
        const response = await api.patch<TaskResponse>(`/tasks/${id}/status`, data);
        return response.data;
    },

    deleteTask: async (id: number): Promise<void> => {
        await api.delete(`/tasks/${id}`);
    }
};