import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
// import { request } from '@/api/request' // TODO: 启用实际API调用时取消注释

export interface UserInfo {
  id: number
  username: string
  email: string
  role: string
  createdAt: string
}

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  email: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const userInfo = ref<UserInfo | null>(null)

  const isLoggedIn = computed(() => !!token.value)

  // 登录
  async function login(params: LoginParams) {
    try {
      // TODO: 调用实际的登录API
      // const res = await request.post('/user/login', params)
      
      // 模拟登录
      const mockResponse = {
        token: 'mock-jwt-token-' + Date.now(),
        user: {
          id: 1,
          username: params.username,
          email: params.username + '@example.com',
          role: 'USER',
          createdAt: new Date().toISOString()
        }
      }
      
      token.value = mockResponse.token
      userInfo.value = mockResponse.user
      localStorage.setItem('token', mockResponse.token)
      
      return mockResponse
    } catch (error) {
      throw error
    }
  }

  // 注册
  async function register(_params: RegisterParams) {
    try {
      // TODO: 调用实际的注册API
      // const res = await request.post('/user/register', _params)
      
      // 模拟注册
      return { message: '注册成功' }
    } catch (error) {
      throw error
    }
  }

  // 获取用户信息
  async function getUserInfo() {
    try {
      // TODO: 调用实际的获取用户信息API
      // const res = await request.get('/user/info')
      // userInfo.value = res.data
      
      return userInfo.value
    } catch (error) {
      throw error
    }
  }

  // 登出
  function logout() {
    token.value = null
    userInfo.value = null
    localStorage.removeItem('token')
  }

  // 初始化时尝试获取用户信息
  function init() {
    if (token.value) {
      getUserInfo().catch(() => {
        logout()
      })
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    register,
    getUserInfo,
    logout,
    init
  }
})
