<template>
  <div class="project-card">
    <h3>Projects (Click a project to view tasks)</h3>

    <!-- Create Project Form -->
    <form @submit.prevent="handleCreateProject" class="create-form">
      <input
        v-model.trim="projectName"
        type="text"
        placeholder="Enter project name..."
        required
        :disabled="isSubmitting"
      />
      <button type="submit" :disabled="isSubmitting || !projectName">
        {{ isSubmitting ? 'Creating...' : '+ Create Project' }}
      </button>
    </form>

    <!-- Project List -->
    <p v-if="isLoading" class="muted">Loading projects...</p>
    <p v-else-if="projects.length === 0" class="muted">No projects yet. Create one above.</p>

    <ul v-else class="project-list">
      <li
        v-for="project in projects"
        :key="project.id"
        :class="['project-item', { active: String(project.id) === selectedProjectId }]"
        @click="emit('select', project)"
      >
        <!-- Inline Edit Mode -->
        <template v-if="editingId === String(project.id)">
          <input
            v-model.trim="editName"
            type="text"
            class="edit-input"
            @click.stop
            @keyup.enter="saveEditProject(String(project.id))"
            @keyup.esc="cancelEdit"
            :disabled="isSavingEdit"
          />
          <div class="actions" @click.stop>
            <button
              type="button"
              class="save-btn"
              :disabled="isSavingEdit || !editName"
              @click="saveEditProject(String(project.id))"
            >
              {{ isSavingEdit ? 'Saving...' : 'Save' }}
            </button>
            <button type="button" class="cancel-btn" :disabled="isSavingEdit" @click="cancelEdit">
              Cancel
            </button>
          </div>
        </template>

        <!-- Normal View Mode -->
        <template v-else>
          <span>{{ project.name }}</span>

          <div class="actions">
            <button type="button" class="edit-btn" @click.stop="startEdit(project)">Edit</button>
            <button
              type="button"
              class="delete-btn"
              :disabled="deletingId === String(project.id)"
              @click.stop="handleDeleteProject(String(project.id))"
            >
              {{ deletingId === String(project.id) ? 'Deleting...' : 'Delete' }}
            </button>
            <span class="arrow">→</span>
          </div>
        </template>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { toast } from 'vue3-toastify'
import { projectService } from '../services/projectServices'
import type { Project } from '@/types/project'

const props = defineProps<{
  selectedProjectId?: string
}>()

const emit = defineEmits<{
  (e: 'select', project: Project | null): void
}>()

const projects = ref<Project[]>([])
const projectName = ref('')
const isLoading = ref(false)
const isSubmitting = ref(false)
const deletingId = ref<string | null>(null)

// Inline editing state
const editingId = ref<string | null>(null)
const editName = ref('')
const isSavingEdit = ref(false)

const fetchProjects = async () => {
  isLoading.value = true
  try {
    projects.value = await projectService.getAll()
    if (projects.value.length > 0) {
      emit('select', projects.value[0])
    }
  } catch {
    toast.error('Failed to load projects.')
  } finally {
    isLoading.value = false
  }
}

const handleCreateProject = async () => {
  if (!projectName.value) return

  isSubmitting.value = true
  try {
    const newProject = await projectService.create({ name: projectName.value })
    projects.value.unshift(newProject)
    emit('select', newProject)
    projectName.value = ''
    toast.success('Project created!')
  } catch (err) {
    const msg = axios.isAxiosError(err)
      ? err.response?.data?.message || 'Could not create project.'
      : 'Something went wrong.'
    toast.error(msg)
  } finally {
    isSubmitting.value = false
  }
}

const startEdit = (project: Project) => {
  editingId.value = String(project.id)
  editName.value = project.name
}

const cancelEdit = () => {
  editingId.value = null
  editName.value = ''
}

const saveEditProject = async (id: string) => {
  if (!editName.value) return

  isSavingEdit.value = true
  try {
    const updated = await projectService.update(id, { name: editName.value })

    // Update item in local list
    const idx = projects.value.findIndex((p) => String(p.id) === id)
    if (idx !== -1) {
      projects.value[idx] = { ...projects.value[idx], ...updated, name: editName.value }
    }

    // Update parent selectedProject header if this project is currently active
    if (props.selectedProjectId === id && idx !== -1) {
      emit('select', projects.value[idx])
    }

    toast.success('Project updated!')
    cancelEdit()
  } catch (error) {
    const msg = axios.isAxiosError(error)
      ? error.response?.data?.message || 'Could not update project.'
      : 'Something went wrong.'
    toast.error(msg)
  } finally {
    isSavingEdit.value = false
  }
}

const handleDeleteProject = async (id: string) => {
  deletingId.value = id
  try {
    await projectService.delete(id)
    projects.value = projects.value.filter((p) => String(p.id) !== id)

    if (props.selectedProjectId === id) {
      emit('select', projects.value[0] ?? null)
    }

    toast.success('Project deleted!')
  } catch (error) {
    const msg = axios.isAxiosError(error)
      ? error.response?.data?.message || 'Could not delete project.'
      : 'Something went wrong.'
    toast.error(msg)
  } finally {
    deletingId.value = null
  }
}

onMounted(fetchProjects)
</script>

<style scoped>
.project-card {
  background: #ffffff;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
h3 {
  margin: 0 0 1rem;
  color: #1f2937;
}
.create-form {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1.25rem;
}
input {
  flex: 1;
  padding: 0.65rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.95rem;
}
.edit-input {
  padding: 0.35rem 0.6rem;
  margin-right: 0.75rem;
}
button[type='submit'] {
  padding: 0.65rem 1.25rem;
  background-color: #42b883;
  color: #ffffff;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}
.project-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.project-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #1f2937;
  cursor: pointer;
  transition: all 0.15s ease;
}
.project-item:hover {
  background-color: #f9fafb;
}
.project-item.active {
  border-color: #42b883;
  background-color: #ecfdf5;
  color: #065f46;
  font-weight: 600;
}
.actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.edit-btn,
.save-btn,
.cancel-btn,
.delete-btn {
  padding: 0.3rem 0.65rem;
  border: none;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
}
.edit-btn {
  background-color: #e0f2fe;
  color: #0369a1;
}
.edit-btn:hover {
  background-color: #bae6fd;
}
.save-btn {
  background-color: #42b883;
  color: #ffffff;
}
.cancel-btn {
  background-color: #e5e7eb;
  color: #374151;
}
.delete-btn {
  background-color: #fee2e2;
  color: #dc2626;
}
.delete-btn:hover {
  background-color: #fecaca;
}
button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.muted {
  color: #6b7280;
  font-size: 0.9rem;
}
</style>
