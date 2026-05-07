import React, { useEffect, useState, useCallback } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Box,
  Container,
  Button,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Pagination,
  CircularProgress,
  Fab,
  Alert,
} from '@mui/material';
import TaskCard from '../components/TaskCard';
import TaskFormDialog from '../components/TaskFormDialog';
import ConfirmDialog from '../components/ConfirmDialog';
import type { TaskResponse, GetTasksParams, TaskStatus, TaskRequest } from '../types/task';
import { taskService } from '../services/taskService';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const statusOptions: Array<'ALL' | TaskStatus> = ['ALL', 'TO_DO', 'IN_PROGRESS', 'OVERDUE', 'COMPLETED'];

const statusLabels: Record<string, string> = {
  ALL: 'Todos',
  TO_DO: 'A fazer',
  IN_PROGRESS: 'Em progresso',
  OVERDUE: 'Atrasado',
  COMPLETED: 'Concluído',
};

const TaskboardPage: React.FC = () => {
  const { signOut } = useAuth();
  const navigate = useNavigate();

  const [tasks, setTasks] = useState<TaskResponse[]>([]);
  const [page, setPage] = useState<number>(1);
  const [size] = useState<number>(10);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [search, setSearch] = useState<string>('');
  const [status, setStatus] = useState<'ALL' | TaskStatus>('ALL');
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<TaskResponse | null>(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [toDelete, setToDelete] = useState<TaskResponse | null>(null);

  const fetchTasks = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params: GetTasksParams = {
        status: status === 'ALL' ? undefined : status,
        search: search || undefined,
        page: page - 1, 
        size,
        sort: 'created_date,desc',
      };
      const res = await taskService.getTasks(params);
      setTasks(res.content);
      setTotalPages(res.totalPages);
    } catch (err) {
      console.error('Error fetching tasks:', err);
      setError('Erro ao carregar tarefas.');
    } finally {
      setLoading(false);
    }
  }, [page, size, status, search]);

  // Debounce fetch when filters change
  useEffect(() => {
    const timeout = setTimeout(() => fetchTasks(), 300);
    return () => clearTimeout(timeout);
  }, [fetchTasks]);

  const handleOpenCreate = () => {
    setEditing(null);
    setDialogOpen(true);
  };

  const handleSubmit = async (data: TaskRequest) => {
    try {
      if (editing) {
        await taskService.updateTask(editing.id, data);
      } else {
        await taskService.createTask(data);
      }
      await fetchTasks();
    } catch (err) {
      console.error('Error saving task:', err);
      setError('Erro ao salvar tarefa.');
    }
  };

  const handleEdit = (task: TaskResponse) => {
    setEditing(task);
    setDialogOpen(true);
  };

  const handleDelete = (task: TaskResponse) => {
    setToDelete(task);
    setConfirmOpen(true);
  };

  const confirmDelete = async () => {
    if (!toDelete) return;
    try {
      await taskService.deleteTask(toDelete.id);
      setConfirmOpen(false);
      setToDelete(null);
      await fetchTasks();
    } catch (err) {
      console.error('Error deleting task:', err);
      setError('Erro ao excluir tarefa.');
    }
  };

  const handleChangeStatus = async (id: number, newStatus: TaskStatus) => {
    try {
      await taskService.updateTaskStatus(id, { status: newStatus });
      await fetchTasks();
    } catch (err: any) {
      console.error('Error changing status:', err);
      const backendMsg = err?.response?.data?.message;
      setError(backendMsg || 'Erro ao alterar status.');
    }
  };

  const handleLogout = () => {
    signOut();
    navigate('/login');
  };

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar position="sticky">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700 }}>
            🗂️ Task Manager
          </Typography>
          <Button color="inherit" onClick={handleLogout} id="logout-button">
            Sair
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="lg" sx={{ mt: 4, pb: 6 }}>
        {/* Filters bar */}
        <Box
          sx={{
            display: 'flex',
            gap: 2,
            mb: 3,
            alignItems: 'center',
            flexWrap: 'wrap',
          }}
        >
          <FormControl size="small" sx={{ minWidth: 160 }}>
            <InputLabel id="status-filter-label">Status</InputLabel>
            <Select
              labelId="status-filter-label"
              id="status-filter"
              label="Status"
              value={status}
              onChange={(e) => {
                setPage(1);
                setStatus(e.target.value as 'ALL' | TaskStatus);
              }}
            >
              {statusOptions.map((s) => (
                <MenuItem key={s} value={s}>
                  {statusLabels[s] ?? s}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <TextField
            id="search-field"
            size="small"
            placeholder="Buscar por título ou descrição..."
            value={search}
            onChange={(e) => {
              setPage(1);
              setSearch(e.target.value);
            }}
            sx={{ flex: 1, minWidth: 220 }}
          />

          <Button
            id="create-task-button"
            variant="contained"
            onClick={handleOpenCreate}
            size="medium"
          >
            + Nova tarefa
          </Button>
        </Box>

        {/* Error alert */}
        {error && (
          <Alert
            severity="error"
            sx={{ mb: 2 }}
            onClose={() => setError(null)}
          >
            {error}
          </Alert>
        )}

        {/* Loading state */}
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
            <CircularProgress />
          </Box>
        ) : tasks.length === 0 ? (
          /* Empty state */
          <Box sx={{ textAlign: 'center', mt: 8, color: 'text.secondary' }}>
            <Typography variant="h6" sx={{ mb: 1 }}>
              Nenhuma tarefa encontrada
            </Typography>
            <Typography variant="body2">
              Clique em "+ Nova tarefa" para criar sua primeira tarefa.
            </Typography>
          </Box>
        ) : (
          /* Task grid */
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
              gap: 2.5,
            }}
          >
            {tasks.map((t) => (
              <TaskCard
                key={t.id}
                task={t}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onChangeStatus={handleChangeStatus}
              />
            ))}
          </Box>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
            <Pagination
              count={totalPages}
              page={page}
              onChange={(_, value) => setPage(value)}
              color="primary"
              shape="rounded"
            />
          </Box>
        )}
      </Container>

      {/* Floating action button */}
      <Fab
        color="primary"
        aria-label="criar tarefa"
        sx={{ position: 'fixed', right: 28, bottom: 28 }}
        onClick={handleOpenCreate}
        id="fab-create-task"
      >
        +
      </Fab>

      {/* Create/Edit dialog */}
      <TaskFormDialog
        open={dialogOpen}
        initial={editing}
        onClose={() => setDialogOpen(false)}
        onSubmit={handleSubmit}
      />

      {/* Delete confirmation dialog */}
      <ConfirmDialog
        open={confirmOpen}
        title="Excluir tarefa"
        description="Tem certeza que deseja excluir esta tarefa? Esta ação não pode ser desfeita."
        onCancel={() => setConfirmOpen(false)}
        onConfirm={confirmDelete}
      />
    </Box>
  );
};

export default TaskboardPage;
