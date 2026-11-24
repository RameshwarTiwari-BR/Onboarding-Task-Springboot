<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { authService } from '../services/authServices';
import type { LoginRequest } from '@/types/auth';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

const router = useRouter();
const errorMessage = ref<string>('');
const isLoading = ref<boolean>(false);

const form = reactive<LoginRequest>({
  email: '',
  password: '',
});

const handleLogin = async () => {
  errorMessage.value = '';
  isLoading.value = true;

  try {
    await authService.login(form);
    toast.success("Login successfully!")
    router.push('/dashboard');

  } catch (err) {
    if (axios.isAxiosError(err)) {
      errorMessage.value = err.response?.data?.message || 'Invalid email or password.';
    } else {
      errorMessage.value = 'Something went wrong. Please try again.';
    }
  } finally {
    isLoading.value = false;
  }
};
</script>


<template>
  <div class="auth-container">
    <div class="auth-card">
      <h2>Welcome Back</h2>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="email">Email</label>
          <input
            id="email"
            v-model.trim="form.email"
            type="email"
            placeholder="you@example.com"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="••••••••"
            required
          />
        </div>

        <p v-if="errorMessage" class="error-msg">{{ errorMessage }}</p>

        <button type="submit" class="submit-btn">Log In</button>
      </form>

      <p class="toggle-text">
        Don't have an account?
        <RouterLink to="/signup" class="link-btn">Sign Up</RouterLink>
      </p>
    </div>
  </div>
</template>

<!-- <script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter() // Like useNavigate() in React Router
const errorMessage = ref('')

const form = reactive({
  email: '',
  password: '',
})

const handleLogin = async () => {
  errorMessage.value = ''
  try {
    // Replace with your API call
    console.log('Logging in:', form)
    router.push('/dashboard') // Redirect after login
  } catch (err) {
    errorMessage.value = 'Invalid email or password.'
  }
}
</script> -->

<style scoped>
/* You can move these shared styles to a global CSS file later */
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f4f6f8;
  font-family:
    system-ui,
    -apple-system,
    sans-serif;
}
.auth-card {
  background: #ffffff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 380px;
}
h2 {
  margin: 0 0 1.5rem;
  text-align: center;
  color: #1f2937;
}
.form-group {
  margin-bottom: 1rem;
}
label {
  display: block;
  margin-bottom: 0.4rem;
  font-size: 0.9rem;
  font-weight: 500;
  color: #374151;
}
input {
  width: 100%;
  padding: 0.65rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
  box-sizing: border-box;
}
input:focus {
  outline: none;
  border-color: #42b883;
}
.submit-btn {
  width: 100%;
  padding: 0.75rem;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  margin-top: 0.5rem;
}
.submit-btn:hover {
  background-color: #369e6f;
}
.toggle-text {
  margin-top: 1.25rem;
  text-align: center;
  font-size: 0.9rem;
  color: #4b5563;
}
.link-btn {
  color: #42b883;
  font-weight: 600;
  text-decoration: none;
  margin-left: 0.25rem;
}
.link-btn:hover {
  text-decoration: underline;
}
.error-msg {
  color: #dc2626;
  font-size: 0.875rem;
  margin: 0.5rem 0;
}
</style>
