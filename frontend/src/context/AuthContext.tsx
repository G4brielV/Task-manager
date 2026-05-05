import React, { createContext, useState, useContext, useEffect, type ReactNode } from 'react';
import { authService } from '../services/authService';
import type { LoginRequest, RegisterRequest } from '../types/auth';

// Contexto de autenticação para gerenciar o estado do token JWT
interface AuthContextData {
  isAuthenticated: boolean;
  token: string | null;
  loading: boolean;
  signIn: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  signOut: () => void;
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

interface AuthProviderProps {
  children: ReactNode;
}

// Gerencia o estado de autenticação e token JWT, persistindo-o no localStorage
export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  // Verifica se ja existe token
  useEffect(() => {
    const storedToken = localStorage.getItem('@taskmanager:token');
    if (storedToken) {
      setToken(storedToken);
    }
    setLoading(false);
  }, []);

  const signIn = async (data: LoginRequest) => {
    const jwtToken = await authService.login(data);
    localStorage.setItem('@taskmanager:token', jwtToken);
    setToken(jwtToken);
  };

  const register = async (data: RegisterRequest) => {
    await authService.register(data);
  };

  const signOut = () => {
    localStorage.removeItem('@taskmanager:token');
    setToken(null);
  };

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated: !!token,
        token,
        loading,
        signIn,
        register,
        signOut,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside an AuthProvider');
  }
  return context;
};