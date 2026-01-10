import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { checkAdmin } from '@/api/admin'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/pages/HomePage.vue'),
    meta: { title: '首页', hideLayout: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/LoginPage.vue'),
    meta: { title: '登录', hideLayout: true }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/pages/DashboardPage.vue'),
    meta: { title: '数据仪表盘', requiresAuth: true }
  },
  {
    path: '/resume',
    name: 'Resume',
    component: () => import('@/pages/ResumeAnalysisPage.vue'),
    meta: { title: '智能简历分析', requiresAuth: true }
  },
  {
    path: '/ai-chat',
    name: 'AIChat',
    component: () => import('@/views/AIRecommendPage.vue'),
    meta: { title: 'AI 职业顾问', requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'AdminConsole',
    component: () => import('@/views/AdminConsolePage.vue'),
    meta: { title: '管理员控制台', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users',
    name: 'UserManage',
    component: () => import('@/views/UserManagePage.vue'),
    meta: { title: '用户管理', requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach(async (to, _from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title || 'Career Planner'} - 智能职业规划系统`

  // 检查登录状态
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  // 检查管理员权限
  if (to.meta.requiresAdmin && token) {
    try {
      const res = await checkAdmin()
      if (res.code !== 200 || !res.data) {
        // 不是管理员，静默跳转到仪表盘
        next({ name: 'Dashboard' })
        return
      }
    } catch (error) {
      // 检查失败，静默跳转
      next({ name: 'Dashboard' })
      return
    }
  }

  next()
})

export default router
