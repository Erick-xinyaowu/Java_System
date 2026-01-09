import request from './request'

// ========== 响应数据类型 ==========

export interface DashboardOverview {
  resumeCompleteness: number
  totalSkills: number
  expertSkills: number
  educationCount: number
  workExperienceCount: number
  assessmentCount: number
  learningRecordCount: number
  totalLearningHours: number
  weeklyLearningHours: number
  reportCount: number
  lastUpdateTime?: string
}

export interface TrendData {
  date: string
  value: number
  type?: string
}

export interface CategoryData {
  name: string
  value: number
  percentage?: number
  color?: string
}

export interface RecentActivity {
  type: string
  title: string
  description?: string
  time: string
  icon?: string
}

export interface DistributionData {
  skillDistribution: CategoryData[]
  skillLevelDistribution: CategoryData[]
}

export interface AllDashboardData {
  overview: DashboardOverview
  trend: TrendData[]
  skillDistribution: CategoryData[]
  skillLevelDistribution: CategoryData[]
  activities: RecentActivity[]
}

// ========== API 响应类型 ==========

export interface ApiResponse<T> {
  success: boolean
  code: number
  message: string
  data: T
}

// ========== API 函数 ==========

/**
 * 获取仪表盘概览数据
 */
export function getDashboardOverview(): Promise<ApiResponse<DashboardOverview>> {
  return request.get('/dashboard/overview')
}

/**
 * 获取学习趋势数据
 * @param period 时间周期: 7d, 30d, 90d
 */
export function getLearningTrend(period: string = '7d'): Promise<ApiResponse<TrendData[]>> {
  return request.get('/dashboard/trend', { params: { period } })
}

/**
 * 获取技能分布数据
 */
export function getSkillDistribution(): Promise<ApiResponse<CategoryData[]>> {
  return request.get('/dashboard/skill-distribution')
}

/**
 * 获取技能等级分布数据
 */
export function getSkillLevelDistribution(): Promise<ApiResponse<CategoryData[]>> {
  return request.get('/dashboard/skill-level')
}

/**
 * 获取所有分布数据（技能分布 + 等级分布）
 */
export function getDistributionData(): Promise<ApiResponse<DistributionData>> {
  return request.get('/dashboard/distribution')
}

/**
 * 获取最近活动记录
 * @param limit 返回数量，默认10
 */
export function getRecentActivities(limit: number = 10): Promise<ApiResponse<RecentActivity[]>> {
  return request.get('/dashboard/activities', { params: { limit } })
}

/**
 * 获取所有仪表盘数据（一次性获取）
 */
export function getAllDashboardData(): Promise<ApiResponse<AllDashboardData>> {
  return request.get('/dashboard/all')
}
