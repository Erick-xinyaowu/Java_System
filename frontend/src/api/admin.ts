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
