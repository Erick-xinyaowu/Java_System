import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getUserInfo as getUserInfoApi } from '@/api/user'
import type { LoginParams, RegisterParams } from '@/api/user'

export interface UserInfo {
  id: number
  username: string
  email: string
  phone?: string
  nickname?: string
  avatar?: string
  school?: string
  major?: string
  intro?: string
  createdAt?: string
}

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string | null>(localStorage.getItem('token'))
  const userInfo = ref<UserInfo | null>(null)
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)

  // 设置 Token
  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 设置用户信息
  function setUserInfo(info: UserInfo) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 登录
  async function login(params: LoginParams) {
    const res = await loginApi(params)
    // 后端返回 code=200 表示成功，拦截器已添加 success=true
    if ((res.success || res.code === 200) && res.data) {
      setToken(res.data.token)
      setUserInfo({
        id: res.data.userId,
        username: res.data.username,
        email: res.data.email || '',
        avatar: res.data.avatar || ''
      })
    }
    return res
  }

  // 注册
  async function register(params: RegisterParams) {
    const res = await registerApi(params)
    // 后端返回 code=200 表示成功
    if ((res.success || res.code === 200) && res.data) {
      setToken(res.data.token)
      setUserInfo({
        id: res.data.userId,
        username: res.data.username,
        email: res.data.email || params.email,
        avatar: res.data.avatar || ''
      })
    }
    return res
  }

  // 获取用户信息
  async function fetchUserInfo() {
    if (!token.value) return null
    try {
      const res = await getUserInfoApi()
      if (res.success && res.data) {
        setUserInfo(res.data)
        return res.data
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // Token 可能已失效
      logout()
    }
    return null
  }

  // 初始化 - 从 localStorage 恢复用户信息
  function initFromStorage() {
    const storedUserInfo = localStorage.getItem('userInfo')
    if (storedUserInfo) {
      try {
        userInfo.value = JSON.parse(storedUserInfo)
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
  }

  // 登出
  function logout() {
    token.value = null
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 初始化调用
  initFromStorage()

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    login,
    register,
    fetchUserInfo,
    logout
  }
})
