import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import SignupView from '@/views/SignupView.vue'
import DashboardView from '@/views/DashboardView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Login',
      component: LoginView,
      meta: { requiresGuest: true },
    },
    {
      path: '/signup',
      name: 'Signup',
      component: SignupView,
      meta: { requiresGuest: true },
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: DashboardView,
      meta: { requiresAuth: true }, // Protected route
    },
  ],
})

function isTokenValid(token: string | null): boolean {
  if (!token) return false;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    // exp is in seconds; Date.now() is in milliseconds
    return payload.exp * 1000 > Date.now();
  } catch {
    return false;
  }
}

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('access_token');
  const isAuthenticated = isTokenValid(token);

  // Clean up stale token immediately if expired
  if (token && !isAuthenticated) {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
  }

  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } });
  } else if (to.meta.requiresGuest && isAuthenticated) {
    next({ name: 'Dashboard' });
  } else {
    next();
  }
});


export default router
