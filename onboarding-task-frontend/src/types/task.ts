export type TaskStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH';
export type ProjectRole = 'OWNER' | 'ADMIN' | 'MEMBER';

export interface TaskResponse {
  id: string;
  title: string;
  description: string | null;
  status: TaskStatus;
  priority: TaskPriority;
  projectId: string;
  createdAt: string;
  dueDate: string | null;
  assignedTo: string | null;
  createdBy: string;
}

export interface TaskRequest {
  title: string;
  description?: string;
  status?: TaskStatus;
  priority?: TaskPriority;
  dueDate?: string | null;
  assignedTo?: string | null;
}

export interface AddProjectMemberRequest {
  email: string;
  role: ProjectRole;
}

export interface ProjectMemberResponse {
  memberId: string;
  userId: string;
  email: string;
  firstName: string;
  lastName: string;
  role: ProjectRole;
  joinedAt: string;
}
