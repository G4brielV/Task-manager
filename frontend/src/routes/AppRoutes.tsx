import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from '../pages/LoginPage';
import RegisterPage from '../pages/RegisterPage';
import TaskboardPage from '../pages/TaskboardPage';
import PrivateRoute from './PrivateRoute';
import { useAuth } from '../context/AuthContext';

const AppRoutes: React.FC = () => {
  const { isAuthenticated, loading } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/taskboard"
        element={
          <PrivateRoute>
            <TaskboardPage />
          </PrivateRoute>
        }
      />

      <Route
        path="/"
        element={
          loading ? null : isAuthenticated ? <Navigate to="/taskboard" replace /> : <Navigate to="/login" replace />
        }
      />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default AppRoutes;
