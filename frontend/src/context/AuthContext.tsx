import React, { createContext, useState, useContext, useEffect, type ReactNode } from 'react';
import { authService } from '../services/authService';
import type { LoginRequest } from '../types/auth';

// Contexto vai fornecer para as telas
interface AuthContextData {
  isAuthenticated: boolean;
  token: string | null;
  signIn: (data: LoginRequest) => Promise<void>;
  signOut: () => void;
}

// Contexto
const AuthContext = createContext<AuthContextData>({} as AuthContextData);

// Tipagem das props do Provider (que vai envolver os filhos/rotas)
interface AuthProviderProps {
  children: ReactNode;
}

// Provider que gerencia o estado
export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [token, setToken] = useState<string | null>(null);

  // Ao abrir o app para buscar o token salvo
  useEffect(() => {
    const storagedToken = localStorage.getItem('@taskmanager:token');
    if (storagedToken) {
      setToken(storagedToken);
    }
  }, []);

  const signIn = async (data: LoginRequest) => {
    try {
      const jwtToken = await authService.login(data);
      localStorage.setItem('@taskmanager:token', jwtToken);
      setToken(jwtToken);
    } catch (error) {
      console.error('Erro ao realizar login:', error);
      throw error; 
    }
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
        signIn, 
        signOut 
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
};