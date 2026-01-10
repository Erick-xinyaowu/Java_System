import request from './request'

/**
 * 管理员统计数据
 */
export interface AdminStats {
  totalUsers: number
  totalResumes: number
  activeUsers: number
  systemHealth: number
  
  userGrowthDates: string[]
  userGrowthCounts: number[]
  
  processedResumes: number
  pendingResumes: number
  
  topSkillNames: string[]
  topSkillCounts: number[]
  
  hourlyLabels: string[]
  hourlyCounts: number[]
  
  majorNames: string[]
  majorCounts: number[]
  
  cpuUsage: number
  memoryUsage: number
  diskUsage: number
  networkUsage: number
  
  todayNewUsers: number
  todayNewResumes: number
  todayAnalysis: number
}

/**
 * 用户管理数据
 */
export interface UserManage {
  id: number
  username: string
  email: string
  phone: string
  nickname: string
  avatar: string
  gender: number
  birthday: string
  status: number
  createdAt: string
  updatedAt: string
}

/**
 * 创建/更新用户请求
 */
export interface UserManageRequest {
  id?: number
  username?: string
  password?: string
  email?: string
  phone?: string
  nickname?: string
  avatar?: string
  gender?: number
  birthday?: string
  status?: number
}

/**
 * 获取管理员控制台统计数据
 */
export const getAdminStats = () => {
  return request.get<AdminStats>('/admin/stats')
}

/**
 * 检查当前用户是否是管理员
 */
export const checkAdmin = () => {
  return request.get<boolean>('/admin/check')
}

// ==================== 用户管理 API ====================

/**
 * 获取所有用户
 */
export const getAllUsers = () => {
  return request.get<UserManage[]>('/admin/users')
}

/**
 * 搜索用户
 */
export const searchUsers = (keyword: string) => {
  return request.get<UserManage[]>('/admin/users/search', { params: { keyword } })
}

/**
 * 获取单个用户
 */
export const getUserById = (id: number) => {
  return request.get<UserManage>(`/admin/users/${id}`)
}

/**
 * 创建用户
 */
export const createUser = (data: UserManageRequest) => {
  return request.post<UserManage>('/admin/users', data)
}

/**
 * 更新用户
 */
export const updateUser = (id: number, data: UserManageRequest) => {
  return request.put<UserManage>(`/admin/users/${id}`, data)
}

/**
 * 删除用户
 */
export const deleteUser = (id: number) => {
  return request.delete(`/admin/users/${id}`)
}

/**
 * 更新用户状态
 */
export const updateUserStatus = (id: number, status: number) => {
  return request.put(`/admin/users/${id}/status`, { status })
}

/**
 * 重置用户密码
 */
export const resetUserPassword = (id: number, password: string) => {
  return request.put(`/admin/users/${id}/password`, { password })
}
