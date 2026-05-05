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
  return iso.substring(0, 10);
};

const TaskFormDialog: React.FC<Props> = ({ open, initial, onClose, onSubmit }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [touchedTitle, setTouchedTitle] = useState(false);
  const [touchedDueDate, setTouchedDueDate] = useState(false);

  useEffect(() => {
    setTouchedTitle(false);
    setTouchedDueDate(false);
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

  const isTitleValid = title.trim().length > 0;
  const isDueDateValid = dueDate.length > 0;
  const isFormValid = isTitleValid && isDueDateValid;

  const handleSave = async () => {
    setTouchedTitle(true);
    setTouchedDueDate(true);
    if (!isFormValid) return;

    setSubmitting(true);
    try {
      const payload: TaskRequest = {
        title: title.trim(),
        description,
        dueDate,  // "yyyy-MM-dd"
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
            error={touchedTitle && !isTitleValid}
            helperText={touchedTitle && !isTitleValid ? 'O título é obrigatório' : ''}
            fullWidth
            margin="normal"
            required
          />
          <TextField label="Descrição" value={description} onChange={(e) => setDescription(e.target.value)} fullWidth margin="normal" multiline rows={3} />
          <Typography variant="subtitle2" sx={{ mt: 2, mb: 0.5 }}>Data de vencimento *</Typography>
          <TextField
            type="date"
            value={dueDate}
            onChange={(e) => setDueDate(e.target.value)}
            onBlur={() => setTouchedDueDate(true)}
            error={touchedDueDate && !isDueDateValid}
            helperText={touchedDueDate && !isDueDateValid ? 'A data de vencimento é obrigatória' : ''}
            fullWidth
            margin="normal"
            required
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={submitting}>Cancelar</Button>
        <Button onClick={handleSave} variant="contained" disabled={submitting || !isFormValid}>{submitting ? 'Salvando...' : 'Salvar'}</Button>
      </DialogActions>
    </Dialog>
  );
};

export default TaskFormDialog;
