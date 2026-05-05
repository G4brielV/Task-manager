import { api } from './api';
import type { LoginRequest, LoginResponse, RegisterRequest } from '../types/auth';

export const authService = {

    login: async (data: LoginRequest): Promise<string> => {
        const response = await api.post<LoginResponse>('/auth/login', data);
        return response.data.token;
    },

    register: async (data: RegisterRequest): Promise<void> => {
        await api.post('/auth/register', data);
    },
};