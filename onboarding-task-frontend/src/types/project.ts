export interface CreateProjectRequest {
  name: string;
}

export interface Project {
  id: number | string;
  name: string;
  createdAt?: string;
}
