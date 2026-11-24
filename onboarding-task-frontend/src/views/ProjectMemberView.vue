<template>
  <div class="members-card">
    <h3>Team Members — {{ projectName }}</h3>

    <!-- Add Member Form with User Dropdown -->
    <form @submit.prevent="handleAddMember" class="add-member-form">
      <select
        v-model="form.email"
        required
        :disabled="isSubmitting || availableUsers.length === 0"
        class="user-select"
      >
        <option value="" disabled>
          {{ availableUsers.length === 0 ? 'All users already added' : 'Select a user to add...' }}
        </option>
        <option v-for="user in availableUsers" :key="user.id" :value="user.email">
          {{ user.firstName }} {{ user.lastName }} ({{ user.email }})
        </option>
      </select>

      <select v-model="form.role" :disabled="isSubmitting">
        <option value="MEMBER">Member</option>
        <option value="ADMIN">Admin</option>
        <option value="OWNER">Owner</option>
      </select>

      <button type="submit" :disabled="isSubmitting || !form.email">
        {{ isSubmitting ? 'Adding...' : '+ Add Member' }}
      </button>
    </form>

    <!-- Current Project Members List -->
    <p v-if="isLoading" class="muted">Loading team members...</p>
    <p v-else-if="members.length === 0" class="muted">No members assigned yet.</p>

    <ul v-else class="member-list">
      <li v-for="member in members" :key="member.memberId" class="member-item">
        <div class="member-info">
          <strong>{{ member.firstName }} {{ member.lastName }}</strong>
          <span class="email">{{ member.email }}</span>
        </div>
        <span class="role-badge">{{ member.role }}</span>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import axios from 'axios'
import { toast } from 'vue3-toastify'
import { taskService } from '@/services/taskService'
import type { UserResponse } from '@/types/auth'
import type { ProjectMemberResponse, AddProjectMemberRequest } from '@/types/task'

const props = defineProps<{
  projectId: string
  projectName: string
}>()

const allUsers = ref<UserResponse[]>([])
const members = ref<ProjectMemberResponse[]>([])
const isLoading = ref(false)
const isSubmitting = ref(false)
const newMemberAdded = ref(false)

const form = reactive<AddProjectMemberRequest>({
  email: '',
  role: 'MEMBER',
})

// Automatically filter out DISABLED users and users already in this project
const availableUsers = computed(() => {
  const existingEmails = new Set(members.value.map((m) => m.email))
  return allUsers.value.filter(
    (user) => user.status === 'ACTIVE' && !existingEmails.has(user.email),
  )
})

const fetchAllUsers = async () => {
  try {
    allUsers.value = await taskService.getAllUsers()
  } catch {
    toast.error('Failed to load users list.')
  }
}

const fetchMembers = async (projectId: string) => {
  if (!projectId) return
  isLoading.value = true
  try {
    members.value = await taskService.getProjectMembers(projectId)
    form.email = '' // Reset dropdown when switching projects
  } catch {
    toast.error('Failed to load project members.')
  } finally {
    isLoading.value = false
  }
}

onMounted(fetchAllUsers)
watch(() => props.projectId, fetchMembers, { immediate: true })

const emit = defineEmits<{
  (e: 'memberAdded', member: ProjectMemberResponse): void
}>()

const handleAddMember = async () => {
  if (!form.email) return

  isSubmitting.value = true
  newMemberAdded.value = true
  try {
    const newMember = await taskService.addProjectMember(props.projectId, {
      email: form.email,
      role: form.role,
    })
    members.value.push(newMember) // availableUsers updates automatically!
    emit('memberAdded', newMember)
    form.email = ''
    form.role = 'MEMBER'
    toast.success('Member added to project!')
  } catch (err) {
    const msg = axios.isAxiosError(err)
      ? err.response?.data?.message || 'Could not add member.'
      : 'Something went wrong.'
    toast.error(msg)
  } finally {
    isSubmitting.value = false
    newMemberAdded.value = false
  }
}
</script>

<style scoped>
.members-card {
  background: #ffffff;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-top: 1.5rem;
}
h3 {
  margin: 0 0 1rem;
  color: #1f2937;
}
.add-member-form {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1.25rem;
  flex-wrap: wrap;
}
.user-select {
  flex: 2;
}
select {
  padding: 0.65rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background-color: #ffffff;
  font-size: 0.9rem;
}
button[type='submit'] {
  padding: 0.65rem 1.25rem;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}
button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.member-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.member-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  margin-bottom: 0.5rem;
}
.member-info {
  display: flex;
  flex-direction: column;
}
.email {
  font-size: 0.85rem;
  color: #6b7280;
}
.role-badge {
  font-size: 0.75rem;
  font-weight: 600;
  padding: 0.25rem 0.6rem;
  border-radius: 4px;
  background-color: #eef2ff;
  color: #4338ca;
}
.muted {
  color: #6b7280;
  font-size: 0.9rem;
}
</style>
