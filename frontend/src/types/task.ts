export type TaskStatus =  'TO_DO' | 'IN_PROGRESS' | 'OVERDUE' | 'COMPLETED';


export interface TaskResponse {
    id: number; 
    title: string;
    description?: string;
    status: TaskStatus;
    assigneeId: number;
    createdAt?: string; 
    updatedAt?: string;
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
}

export interface TaskRequest {
  title: string;
  description: string;
  dueDate: string;
}


export interface TaskStatusRequest {
  status: TaskStatus;
}