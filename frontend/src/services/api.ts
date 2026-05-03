import axios from 'axios';

const API_URL = 'http://10.0.2.2:3000'; 

export const api = axios.create({
  baseURL: API_URL, 
  timeout: 10000, 
  
  headers: {
    'Content-Type': 'application/json',
  }
});

// Injetar o JWT
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('@taskmanager:token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
  
    return config;
}, (error) => {
    return Promise.reject(error);
});

// Tratar 401 (Token expirado/inválido)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Limpa os dados do armazenamento
      localStorage.removeItem('@taskmanager:token');
      window.location.href = '/'; 
    }
    return Promise.reject(error);
  }
);