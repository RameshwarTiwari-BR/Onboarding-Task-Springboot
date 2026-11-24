const API_BASE = '/api'
const V1 = `${API_BASE}/v1`
const AUTH_V1 = `${V1}/auth`

export const API_ENDPOINTS = {
  // Health
  HEALTH_V1: '/actuator/health',

  // Auth
  AUTH: {
    USERS_V1: `${V1}/users`,
    SIGNUP_V1: `${AUTH_V1}/signup`,
    LOGIN_V1: `${AUTH_V1}/login`,
    LOGOUT_V1: `${AUTH_V1}/logout`,
    REFRESH_V1 : `${AUTH_V1}/refresh`
  },

  // Task & Project CRUD
  TASKS_V1: `${V1}/tasks`,
  PROJECTS_V1: `${V1}/projects`,
} as const
