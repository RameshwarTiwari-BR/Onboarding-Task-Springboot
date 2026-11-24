<template>
  <div class="dashboard-layout">
    <header class="navbar">
      <h1>Task and Project Management</h1>
      <button @click="handleLogout" :disabled="isLoggingOut" class="logout-btn">
        {{ isLoggingOut ? 'Logging out...' : 'Log Out' }}
      </button>
    </header>

    <main class="content">
      <!-- 1. Project List & Creator -->
      <ProjectView
        :selected-project-id="String(selectedProject?.id ?? '')"
        @select="handleSelectProject"
      />

      <ProjectMemberView
        v-if="selectedProject"
        :project-id="String(selectedProject.id)"
        :project-name="selectedProject.name"
        @member-added="handleMemberAdded"
      />

      <!-- 2. Task Board (Renders once a project is selected) -->
      <TaskManagerView
        v-if="selectedProject"
        :project-id="String(selectedProject.id)"
        :project-name="selectedProject.name"
        :new-member="latestMemberAdded"
      />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'
import { authService } from '../services/authServices'
import ProjectView from './ProjectView.vue'
import TaskManagerView from './TaskManagerView.vue'
import ProjectMemberView from './ProjectMemberView.vue'
import type { Project } from '@/types/project'
import type { ProjectMemberResponse } from '@/types/task'

const router = useRouter()
const isLoggingOut = ref(false)
const selectedProject = ref<Project | null>(null)

const handleSelectProject = (project: Project | null) => {
  selectedProject.value = project
}

const latestMemberAdded = ref<ProjectMemberResponse | null>(null)

const handleMemberAdded = (member: ProjectMemberResponse) => {
  latestMemberAdded.value = member
}

const handleLogout = async () => {
  isLoggingOut.value = true
  try {
    await authService.logout()
    toast.info('Logged out successfully.')
  } finally {
    isLoggingOut.value = false
    router.push('/')
  }
}
</script>

<style scoped>
.dashboard-layout {
  min-height: 100vh;
  width: 100vw;
  background-color: #f4f6f8;
  font-family:
    system-ui,
    -apple-system,
    sans-serif;
}
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}
.navbar h1 {
  margin: 0;
  font-size: 1.25rem;
  color: #1f2937;
}
.logout-btn {
  padding: 0.5rem 1rem;
  background-color: #ef4444;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}
.content {
  padding: 2rem;
  max-width: 1000px;
  margin: 0 auto;
  color: black;
}
</style>
