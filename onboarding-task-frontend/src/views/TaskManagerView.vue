<template>
  <div class="task-panel">
    <h3>Tasks for {{ projectName }}</h3>

    <!-- Create Task Form -->
    <form @submit.prevent="handleCreateTask" class="task-form">
      <div class="row">
        <input
          v-model.trim="form.title"
          type="text"
          maxlength="200"
          placeholder="Task title (required)"
          required
        />
        <select v-model="form.priority">
          <option value="LOW">Low Priority</option>
          <option value="MEDIUM">Medium Priority</option>
          <option value="HIGH">High Priority</option>
        </select>
        <select v-model="form.status">
          <option value="PENDING">Pending</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="COMPLETED">Done</option>
        </select>
      </div>

      <textarea
        v-model.trim="form.description"
        rows="2"
        placeholder="Description (optional)"
      ></textarea>

      <div class="row">
        <select v-model="form.assignedTo">
          <option :value="null">Unassigned</option>
          <option v-for="member in members" :key="member.memberId" :value="member.userId">
            {{ member.firstName }} {{ member.lastName }} ({{ member.role }})
          </option>
        </select>

        <input v-model="dueDateInput" type="datetime-local" title="Due Date" />

        <button type="submit" :disabled="isSubmitting || !form.title">
          {{ isSubmitting ? 'Adding...' : '+ Add Task' }}
        </button>
      </div>
    </form>

    <!-- Task List -->
    <p v-if="isLoading" class="muted">Loading tasks...</p>
    <p v-else-if="tasks.length === 0" class="muted">No tasks in this project yet.</p>

    <div v-else class="task-list">
      <div v-for="task in tasks" :key="task.id" class="task-item">
        <!-- INLINE EDIT MODE -->
        <form
          v-if="editingTaskId === task.id"
          @submit.prevent="saveTaskEdit(task.id)"
          class="edit-task-form"
        >
          <div class="row">
            <input
              v-model.trim="editForm.title"
              type="text"
              maxlength="200"
              placeholder="Task title"
              required
            />
            <select v-model="editForm.priority">
              <option value="LOW">Low Priority</option>
              <option value="MEDIUM">Medium Priority</option>
              <option value="HIGH">High Priority</option>
            </select>
            <select v-model="editForm.status">
              <option value="PENDING">Pending</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Done</option>
            </select>
          </div>

          <textarea
            v-model.trim="editForm.description"
            rows="2"
            placeholder="Description (optional)"
          ></textarea>

          <div class="row">
            <select v-model="editForm.assignedTo">
              <option :value="null">Unassigned</option>
              <option v-for="member in members" :key="member.memberId" :value="member.userId">
                {{ member.firstName }} {{ member.lastName }} ({{ member.role }})
              </option>
            </select>

            <input v-model="editDueDateInput" type="datetime-local" title="Due Date" />

            <div class="edit-actions">
              <button type="submit" class="save-btn" :disabled="isUpdating || !editForm.title">
                {{ isUpdating ? 'Saving...' : 'Save' }}
              </button>
              <button type="button" class="cancel-btn" :disabled="isUpdating" @click="cancelEdit">
                Cancel
              </button>
            </div>
          </div>
        </form>

        <!-- NORMAL VIEW MODE -->
        <template v-else>
          <div class="task-header">
            <strong>{{ task.title }}</strong>
            <div class="badges">
              <span :class="['badge', 'priority', task.priority.toLowerCase()]">
                {{ task.priority }}
              </span>

              <!-- Quick Status Dropdown right on the card -->
              <select
                :value="task.status"
                @change="
                  handleQuickStatusChange(
                    task,
                    ($event.target as HTMLSelectElement).value as TaskStatus,
                  )
                "
                class="status-select"
                title="Change Status"
              >
                <option value="PENDING">Pending</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="COMPLETED">Done</option>
              </select>
            </div>
          </div>

          <p v-if="task.description" class="desc">{{ task.description }}</p>

          <div class="task-meta">
            <span>Assignee: {{ getAssigneeName(task.assignedTo) }}</span>
            <span v-if="task.dueDate">Due: {{ new Date(task.dueDate).toLocaleString() }}</span>
            <div class="card-actions">
              <button type="button" @click="startEdit(task)" class="edit-btn">Edit</button>
              <button type="button" @click="handleDelete(task.id)" class="delete-btn">
                Delete
              </button>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import axios from 'axios'
import { toast } from 'vue3-toastify'
import { taskService } from '@/services/taskService'
import type { TaskRequest, TaskResponse, TaskStatus, ProjectMemberResponse } from '@/types/task'

const props = defineProps<{
  projectId: string
  projectName: string
  newMember?: ProjectMemberResponse | null
}>()

const tasks = ref<TaskResponse[]>([])
const members = ref<ProjectMemberResponse[]>([])
const isLoading = ref(false)
const isSubmitting = ref(false)
const dueDateInput = ref('')

// Create form state
const form = reactive<TaskRequest>({
  title: '',
  description: '',
  status: 'PENDING',
  priority: 'MEDIUM',
  dueDate: null,
  assignedTo: null,
})

// Edit form state
const editingTaskId = ref<string | null>(null)
const isUpdating = ref(false)
const editDueDateInput = ref('')
const editForm = reactive<TaskRequest>({
  title: '',
  description: '',
  status: 'PENDING',
  priority: 'MEDIUM',
  dueDate: null,
  assignedTo: null,
})

// Helper: Convert Java Instant ISO string -> "YYYY-MM-DDTHH:mm" for <input type="datetime-local">
const toLocalDateTimeInput = (isoString: string | null): string => {
  if (!isoString) return ''
  const date = new Date(isoString)
  const offset = date.getTimezoneOffset()
  const localDate = new Date(date.getTime() - offset * 60 * 1000)
  return localDate.toISOString().slice(0, 16)
}

const cancelEdit = () => {
  editingTaskId.value = null
}

const loadProjectData = async (projectId: string) => {
  if (!projectId || projectId === 'undefined') {
    isLoading.value = false
    return
  }

  isLoading.value = true
  cancelEdit()

  try {
    const [tasksResult, membersResult] = await Promise.allSettled([
      taskService.getByProject(projectId),
      taskService.getProjectMembers(projectId),
    ])

    console.log(tasksResult)
    console.log(membersResult)

    if (tasksResult.status === 'fulfilled') {
      tasks.value = tasksResult.value
    } else {
      console.error('Tasks fetch failed:', tasksResult.reason)
      toast.error('Failed to load project tasks.')
    }

    if (membersResult.status === 'fulfilled') {
      members.value = membersResult.value
    } else {
      console.error('Members fetch failed:', membersResult.reason)
    }
  } finally {
    isLoading.value = false
  }
}

watch(() => props.projectId, loadProjectData, { immediate: true })

// Instantly add a newly-added project member to the assignee dropdown
watch(
  () => props.newMember,
  (member) => {
    if (member && !members.value.some((m) => m.userId === member.userId)) {
      members.value.push(member)
    }
  },
)

const handleCreateTask = async () => {
  isSubmitting.value = true
  try {
    const payload: TaskRequest = {
      ...form,
      dueDate: dueDateInput.value ? new Date(dueDateInput.value).toISOString() : null,
    }

    const created = await taskService.create(props.projectId, payload)
    tasks.value.unshift(created)

    form.title = ''
    form.description = ''
    form.status = 'PENDING'
    form.priority = 'MEDIUM'
    form.assignedTo = null
    dueDateInput.value = ''

    toast.success('Task created!')
  } catch (err) {
    const msg = axios.isAxiosError(err)
      ? err.response?.data?.message || 'Failed to create task.'
      : 'Something went wrong.'
    toast.error(msg)
  } finally {
    isSubmitting.value = false
  }
}

const startEdit = (task: TaskResponse) => {
  editingTaskId.value = task.id
  editForm.title = task.title
  editForm.description = task.description || ''
  editForm.status = task.status
  editForm.priority = task.priority
  editForm.assignedTo = task.assignedTo
  editDueDateInput.value = toLocalDateTimeInput(task.dueDate)
}

const saveTaskEdit = async (taskId: string) => {
  isUpdating.value = true
  try {
    const payload: TaskRequest = {
      ...editForm,
      dueDate: editDueDateInput.value ? new Date(editDueDateInput.value).toISOString() : null,
    }

    const updated = await taskService.update(taskId, payload)
    const idx = tasks.value.findIndex((t) => t.id === taskId)
    if (idx !== -1) {
      tasks.value[idx] = updated
    }

    toast.success('Task updated!')
    cancelEdit()
  } catch (err) {
    const msg = axios.isAxiosError(err)
      ? err.response?.data?.message || 'Failed to update task.'
      : 'Something went wrong.'
    toast.error(msg)
  } finally {
    isUpdating.value = false
  }
}

// Update status immediately without opening full edit mode
const handleQuickStatusChange = async (task: TaskResponse, newStatus: TaskStatus) => {
  try {
    const updated = await taskService.update(task.id, {
      title: task.title,
      description: task.description ?? undefined,
      status: newStatus,
      priority: task.priority,
      dueDate: task.dueDate,
      assignedTo: task.assignedTo,
    })
    const idx = tasks.value.findIndex((t) => t.id === task.id)
    if (idx !== -1) {
      tasks.value[idx] = updated
    }
    toast.success(`Marked as ${newStatus}`)
  } catch {
    toast.error('Failed to update status.')
  }
}

const handleDelete = async (taskId: string) => {
  try {
    await taskService.delete(taskId)
    tasks.value = tasks.value.filter((t) => t.id !== taskId)
    toast.info('Task deleted.')
  } catch {
    toast.error('Could not delete task.')
  }
}
const getAssigneeName = (userId: string | null): string => {
  if (!userId) return 'Unassigned'
  const member = members.value.find((m) => m.userId === userId)
  return member ? `${member?.firstName} ${member?.lastName}` : userId
}
</script>

<style scoped>
.task-panel {
  background: #ffffff;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-top: 1.5rem;
  color: black;
}
.task-form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}
.row {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}
input,
select,
textarea {
  padding: 0.6rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.9rem;
  font-family: inherit;
}
input[type='text'] {
  flex: 2;
}
button[type='submit'] {
  padding: 0.6rem 1.2rem;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}
.task-item {
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 1rem;
  margin-bottom: 0.75rem;
}
.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.badges {
  display: flex;
  gap: 0.4rem;
}
.badge {
  font-size: 0.75rem;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  background: #e5e7eb;
  font-weight: 600;
}
.desc {
  color: #4b5563;
  font-size: 0.9rem;
  margin: 0.5rem 0;
}
.task-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.8rem;
  color: #6b7280;
  margin-top: 0.5rem;
}
.delete-btn {
  background: none;
  border: none;
  color: #dc2626;
  cursor: pointer;
  font-weight: 600;
}
.muted {
  color: #6b7280;
  font-size: 0.9rem;
}
</style>
