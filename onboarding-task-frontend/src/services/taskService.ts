import apiClient from '@/api/axios';
import { API_ENDPOINTS } from '@/constants/apiEndpoints';
import type {
  TaskRequest,
  TaskResponse,
  TaskStatus,
  ProjectMemberResponse,
  AddProjectMemberRequest,
} from '@/types/task';
import type { UserResponse } from '@/types/auth';


export const taskService = {
  // GET /api/v1/projects/{projectId}/tasks?status=...
  async getByProject(projectId: string, status?: TaskStatus): Promise<TaskResponse[]> {
    const { data } = await apiClient.get<TaskResponse[]>(
      `${API_ENDPOINTS.PROJECTS_V1}/${projectId}/tasks`,
      { params: status ? { status } : undefined }
    );
    return data;
  },

  // POST /api/v1/projects/{projectId}/tasks
  async create(projectId: string, payload: TaskRequest): Promise<TaskResponse> {
    const { data } = await apiClient.post<TaskResponse>(
      `${API_ENDPOINTS.PROJECTS_V1}/${projectId}/tasks`,
      payload
    );
    return data;
  },

  // PUT /api/v1/tasks/{taskId}
  async update(taskId: string, payload: TaskRequest): Promise<TaskResponse> {
    const { data } = await apiClient.put<TaskResponse>(
      `${API_ENDPOINTS.TASKS_V1}/${taskId}`,
      payload
    );
    return data;
  },

  // DELETE /api/v1/tasks/{taskId}
  async delete(taskId: string): Promise<void> {
    await apiClient.delete(`${API_ENDPOINTS.TASKS_V1}/${taskId}`);
  },

  // GET /api/v1/projects{projectId}/members (Matches your current OpenAPI path)
  async getProjectMembers(projectId: string): Promise<ProjectMemberResponse[]> {
    const { data } = await apiClient.get<ProjectMemberResponse[]>(
      `${API_ENDPOINTS.PROJECTS_V1}${projectId}/members`
    );
    return data;
  },

  // POST /api/v1/projects{projectId}/members
  async addProjectMember(
    projectId: string,
    payload: AddProjectMemberRequest
  ): Promise<ProjectMemberResponse> {
    const { data } = await apiClient.post<ProjectMemberResponse>(
      `${API_ENDPOINTS.PROJECTS_V1}${projectId}/members`,
      payload
    );
    return data;
  },


// Inside taskService:
async getAllUsers(): Promise<UserResponse[]> {
  const { data } = await apiClient.get<UserResponse[]>(API_ENDPOINTS.AUTH.USERS_V1);
  return data;
},

};
