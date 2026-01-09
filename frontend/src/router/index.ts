import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

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
    component: () => import('@/pages/ResumePage.vue'),
    meta: { title: '简历管理', requiresAuth: true }
  },
  {
    path: '/career-assessment',
    name: 'CareerAssessment',
    component: () => import('@/pages/CareerAssessmentPage.vue'),
    meta: { title: '职业测评', requiresAuth: true }
  },
  {
    path: '/career-report',
    name: 'CareerReport',
    component: () => import('@/pages/CareerReportPage.vue'),
    meta: { title: '职业报告', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title || 'Career Planner'} - 智能职业规划系统`

  // 检查登录状态
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
