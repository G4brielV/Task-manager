import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Alert,
  Link,
  CircularProgress,
  InputAdornment,
  IconButton,
} from '@mui/material';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import type { AxiosError } from 'axios';

const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const { signIn } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      await signIn({ email, password });
      navigate('/taskboard');
    } catch (err) {
      const axiosErr = err as AxiosError<{ message?: string }>;
      if (axiosErr.response) {
        const status = axiosErr.response.status;
        if (status === 401 || status === 403) {
          setError('E-mail ou senha inválidos.');
        } else if (axiosErr.response.data?.message) {
          setError(axiosErr.response.data.message);
        } else {
          setError(`Erro do servidor (${status}).`);
        }
      } else if (axiosErr.request) {
        setError('Não foi possível conectar ao servidor. Verifique se o backend está rodando.');
      } else {
        setError('Erro inesperado. Tente novamente.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #e3f0ff 0%, #f0f2f5 50%, #e8eaf6 100%)',
      }}
    >
      <Card
        sx={{
          width: 420,
          maxWidth: '92vw',
          px: 1,
          py: 2,
        }}
        elevation={6}
      >
        <CardContent>
          <Typography variant="h4" align="center" sx={{ mb: 0.5 }}>
            🗂️
          </Typography>
          <Typography variant="h5" align="center" sx={{ mb: 3 }}>
            Entrar
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <form onSubmit={handleSubmit}>
            <TextField
              id="login-email"
              label="Email"
              type="email"
              fullWidth
              margin="normal"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              autoComplete="email"
              autoFocus
            />
            <TextField
              id="login-password"
              label="Senha"
              type={showPassword ? 'text' : 'password'}
              fullWidth
              margin="normal"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              autoComplete="current-password"
              slotProps={{
                input: {
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={() => setShowPassword(!showPassword)}
                        edge="end"
                        size="small"
                      >
                        {showPassword ? '🙈' : '👁️'}
                      </IconButton>
                    </InputAdornment>
                  ),
                },
              }}
            />

            <Button
              id="login-submit"
              type="submit"
              variant="contained"
              fullWidth
              disabled={loading || !email || !password}
              sx={{ mt: 2, py: 1.3 }}
              size="large"
            >
              {loading ? <CircularProgress size={24} color="inherit" /> : 'Entrar'}
            </Button>
          </form>

          <Box sx={{ mt: 2.5, textAlign: 'center' }}>
            <Link component={RouterLink} to="/register" underline="hover">
              Não possui conta? Cadastre-se
            </Link>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default LoginPage;
