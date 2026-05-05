import React from 'react';
import { Chip } from '@mui/material';
import { type TaskStatus, STATUS_LABELS } from '../types/task';

interface Props {
  status: TaskStatus;
  onClick?: () => void;
}

const StatusChip: React.FC<Props> = ({ status, onClick }) => {
  let color: 'default' | 'primary' | 'success' | 'warning' | 'error' = 'default';
  let label = STATUS_LABELS[status] || status;

  switch (status) {
    case 'TO_DO':
      color = 'default';
      break;
    case 'IN_PROGRESS':
      color = 'primary';
      break;
    case 'COMPLETED':
      color = 'success';
      break;
    case 'OVERDUE':
      color = 'error';
      break;
  }

  return <Chip label={label} color={color} size="small" onClick={onClick} sx={{ cursor: onClick ? 'pointer' : 'default' }} />;
};

export default StatusChip;
