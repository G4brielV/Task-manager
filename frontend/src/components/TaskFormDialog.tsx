import React, { useEffect, useState } from 'react';
import { Dialog, DialogTitle, DialogContent, TextField, DialogActions, Button, Box, Typography } from '@mui/material';
import type { TaskRequest, TaskResponse } from '../types/task';

interface Props {
  open: boolean;
  initial?: TaskResponse | null;
  onClose: () => void;
  onSubmit: (data: TaskRequest) => Promise<void> | void;
}

const toInputValue = (iso?: string) => {
  if (!iso) return '';
  const d = new Date(iso);
  const pad = (n: number) => n.toString().padStart(2, '0');
  const yyyy = d.getFullYear();
  const mm = pad(d.getMonth() + 1);
  const dd = pad(d.getDate());
  const hh = pad(d.getHours());
  const min = pad(d.getMinutes());
  return `${yyyy}-${mm}-${dd}T${hh}:${min}`;
};

const formatForBackend = (localValue: string) => {
  if (!localValue) return undefined;
  // if contains minutes only (YYYY-MM-DDTHH:mm) append seconds
  if (localValue.length === 16) return `${localValue}:00`;
  return localValue;
};

const TaskFormDialog: React.FC<Props> = ({ open, initial, onClose, onSubmit }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [touchedTitle, setTouchedTitle] = useState(false);

  useEffect(() => {
    setTouchedTitle(false);
    if (initial) {
      setTitle(initial.title || '');
      setDescription(initial.description || '');
      setDueDate(toInputValue(initial.dueDate));
    } else {
      setTitle('');
      setDescription('');
      setDueDate('');
    }
  }, [initial, open]);

  const handleSave = async () => {
    setSubmitting(true);
    try {
      const formatted = formatForBackend(dueDate) ?? null;
      const payload: TaskRequest = {
        title,
        description,
        dueDate: formatted,
      };
      await onSubmit(payload);
      onClose();
    } catch (err) {
      console.error('Erro salvar tarefa', err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>{initial ? 'Editar tarefa' : 'Criar tarefa'}</DialogTitle>
      <DialogContent>
        <Box sx={{ mt: 1 }}>
          <TextField 
            label="Título" 
            value={title} 
            onChange={(e) => setTitle(e.target.value)} 
            onBlur={() => setTouchedTitle(true)}
            error={touchedTitle && !title}
            helperText={touchedTitle && !title ? 'O título é obrigatório' : ''}
            fullWidth 
            margin="normal" 
            required 
          />
          <TextField label="Descrição" value={description} onChange={(e) => setDescription(e.target.value)} fullWidth margin="normal" multiline rows={3} />
          <Typography variant="subtitle2" sx={{ mt: 2, mb: 0.5 }}>Data e hora</Typography>
          <TextField
            type="datetime-local"
            value={dueDate}
            onChange={(e) => setDueDate(e.target.value)}
            fullWidth
            margin="normal"
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={submitting}>Cancelar</Button>
        <Button onClick={handleSave} variant="contained" disabled={submitting || !title}>{submitting ? 'Salvando...' : 'Salvar'}</Button>
      </DialogActions>
    </Dialog>
  );
};

export default TaskFormDialog;
