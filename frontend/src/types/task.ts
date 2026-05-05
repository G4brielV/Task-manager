export type TaskStatus =  'TO_DO' | 'IN_PROGRESS' | 'OVERDUE' | 'COMPLETED';

export const STATUS_LABELS: Record<TaskStatus, string> = {
  TO_DO: 'A fazer',
  IN_PROGRESS: 'Em progresso',
  COMPLETED: 'Concluído',
  OVERDUE: 'Atrasado',
};


export interface TaskResponse {
    id: number; 
    title: string;
    description?: string;
    status: TaskStatus;
    assigneeId: number;
    createdAt?: string; 
  dueDate?: string;
}

export interface GetTasksParams {
  status?: TaskStatus;
  search?: string;
  page?: number;
  size?: number;
  sort?: string;
}

export interface Page<T> {
  content: T[];          
  totalElements: number; 
  totalPages: number;    
  size: number;          
  number: number;        
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface TaskRequest {
  title: string;
  description: string;
  dueDate?: string | null;
}


export interface TaskStatusRequest {
  status: TaskStatus;
}