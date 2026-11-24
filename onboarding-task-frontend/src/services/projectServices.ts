import apiClient from '@/api/axios';
import { API_ENDPOINTS } from '@/constants/apiEndpoints';
import type { CreateProjectRequest, Project } from '@/types/project';

export const projectService = {
  async getAll(): Promise<Project[]> {
    const { data } = await apiClient.get<Project[]>(API_ENDPOINTS.PROJECTS_V1);
    return data;
  },

  async create(payload: CreateProjectRequest): Promise<Project> {
    const { data } = await apiClient.post<Project>(API_ENDPOINTS.PROJECTS_V1, payload);
    return data;
  },

  async delete(id: number | string): Promise<void> {
    await apiClient.delete(`${API_ENDPOINTS.PROJECTS_V1}/${id}`);
  },

async update(id: string, payload: CreateProjectRequest): Promise<Project> {
  const { data } = await apiClient.put<Project>(
    `${API_ENDPOINTS.PROJECTS_V1}/${id}`,
    payload
  );
  return data;
},

};
