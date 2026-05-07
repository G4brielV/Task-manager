import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  Button,
  CardActions,
  FormControl,
  Select,
  MenuItem,
  type SelectChangeEvent,
} from '@mui/material';
import { type TaskResponse, type TaskStatus, STATUS_LABELS, ALLOWED_TRANSITIONS } from '../types/task';
import StatusChip from './StatusChip';

interface Props {
  task: TaskResponse;
  onEdit: (task: TaskResponse) => void;
  onDelete: (task: TaskResponse) => void;
  onChangeStatus: (id: number, status: TaskStatus) => void;
}

const TaskCard: React.FC<Props> = ({ task, onEdit, onDelete, onChangeStatus }) => {
  const due = task.dueDate
    ? new Date(task.dueDate + 'T00:00:00').toLocaleDateString()
    : '—';

  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const dueObj = task.dueDate ? new Date(task.dueDate + 'T00:00:00') : null;
  if (dueObj) dueObj.setHours(0, 0, 0, 0);
  
  let allowedNext = ALLOWED_TRANSITIONS[task.status] ?? [];

  if (dueObj && dueObj < today) {
    allowedNext = allowedNext.filter((s) => s !== 'TO_DO' && s !== 'IN_PROGRESS');
  }

  const handleStatusChange = (e: SelectChangeEvent<string>) => {
    const newStatus = e.target.value as TaskStatus;
    if (newStatus !== task.status) {
      onChangeStatus(task.id, newStatus);
    }
  };

  return (
    <Card variant="outlined" sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent sx={{ flexGrow: 1 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
          <Typography variant="h6" sx={{ flex: 1, mr: 1 }}>{task.title}</Typography>
          <StatusChip status={task.status} />
        </Box>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>{task.description}</Typography>
        <Typography variant="caption" color="text.secondary">Prazo: {due}</Typography>
      </CardContent>

      <CardActions sx={{ px: 2, pb: 1.5, flexWrap: 'wrap', gap: 1 }}>
        <Button size="small" onClick={() => onEdit(task)}>Editar</Button>
        <Button size="small" color="error" onClick={() => onDelete(task)}>Excluir</Button>
        <Box sx={{ flexGrow: 1 }} />

        {/* Status transition dropdown */}
        {allowedNext.length > 0 && (
          <FormControl size="small" sx={{ minWidth: 140 }}>
            <Select
              value=""
              displayEmpty
              onChange={handleStatusChange}
              renderValue={() => 'Mover para...'}
              sx={{
                fontSize: '0.8125rem',
                '& .MuiSelect-select': { py: 0.5 },
              }}
            >
              {allowedNext.map((s) => (
                <MenuItem key={s} value={s}>
                  {STATUS_LABELS[s]}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        )}
      </CardActions>
    </Card>
  );
};

export default TaskCard;
