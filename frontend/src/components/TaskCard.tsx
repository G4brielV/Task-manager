import React from 'react';
import { Card, CardContent, Typography, Box, Button, CardActions } from '@mui/material';
import { type TaskResponse, type TaskStatus, STATUS_LABELS } from '../types/task';
import StatusChip from './StatusChip';

interface Props {
  task: TaskResponse;
  onEdit: (task: TaskResponse) => void;
  onDelete: (task: TaskResponse) => void;
  onChangeStatus: (id: number, status: TaskStatus) => void;
}

const nextStatus = (s: TaskStatus): TaskStatus => {
  switch (s) {
    case 'TO_DO':
      return 'IN_PROGRESS';
    case 'IN_PROGRESS':
      return 'COMPLETED';
    case 'COMPLETED':
      return 'TO_DO';
    case 'OVERDUE':
      return 'IN_PROGRESS';
  }
};

const TaskCard: React.FC<Props> = ({ task, onEdit, onDelete, onChangeStatus }) => {
  const due = task.dueDate ? new Date(task.dueDate).toLocaleString() : '—';

  return (
    <Card variant="outlined" sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent sx={{ flexGrow: 1 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6">{task.title}</Typography>
          <StatusChip status={task.status} />
        </Box>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 1, mb: 1 }}>{task.description}</Typography>
        <Typography variant="caption" color="text.secondary">Prazo: {due}</Typography>
      </CardContent>

      <CardActions>
        <Button size="small" onClick={() => onEdit(task)}>Editar</Button>
        <Button size="small" color="error" onClick={() => onDelete(task)}>Excluir</Button>
        <Box sx={{ flexGrow: 1 }} />
        {task.status !== 'COMPLETED' && (
          <Button size="small" onClick={() => onChangeStatus(task.id, nextStatus(task.status))}>
            Mover p/ {STATUS_LABELS[nextStatus(task.status)]}
          </Button>
        )}
      </CardActions>
    </Card>
  );
};

export default TaskCard;
