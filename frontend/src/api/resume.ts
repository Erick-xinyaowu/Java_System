import request from './request'

// ========== 请求参数类型 ==========

export interface ResumeDTO {
  title?: string
  realName?: string
  targetPosition?: string
  expectedSalary?: string
  workCity?: string
  education?: string
  school?: string
  major?: string
  graduationYear?: number
  workExperience?: number
  selfIntroduction?: string
}

export interface SkillDTO {
  name: string
  level: number
  category?: string
}

export interface EducationDTO {
  school: string
  major: string
  degree: string
  startDate: string
  endDate?: string
  description?: string
}

export interface WorkExperienceDTO {
  company: string
  position: string
  startDate: string
  endDate?: string
  description?: string
}

// ========== 响应数据类型 ==========

export interface Skill {
  id: number
  resumeId: number
  name: string
  level: number
  category?: string
}

export interface Education {
  id: number
  resumeId: number
  school: string
  major: string
  degree: string
  startDate: string
  endDate?: string
  description?: string
}

export interface WorkExperience {
  id: number
  resumeId: number
  company: string
  position: string
  startDate: string
  endDate?: string
  description?: string
}

export interface Resume {
  id: number
  userId: number
  title?: string
  realName?: string         // 后端字段名
  targetPosition?: string
  expectedSalary?: string
  workCity?: string
  education?: string
  school?: string
  major?: string
  graduationYear?: number
  workExperience?: number
  selfIntroduction?: string  // 后端字段名
  createdAt?: string
  updatedAt?: string
}

export interface ResumeDetail extends Resume {
  skills: Skill[]
  educations: Education[]
  workExperiences: WorkExperience[]
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
 * 创建简历
 */
export function createResume(data: ResumeDTO): Promise<ApiResponse<Resume>> {
  return request.post('/resume', data)
}

/**
 * 获取当前用户的简历详情
 */
export function getMyResume(): Promise<ApiResponse<ResumeDetail>> {
  return request.get('/resume')  // 后端使用 GET /api/resume 获取当前用户简历
}

/**
 * 获取简历详情
 */
export function getResumeById(id: number): Promise<ApiResponse<ResumeDetail>> {
  return request.get(`/resume/${id}`)
}

/**
 * 更新简历基本信息
 */
export function updateResume(id: number, data: ResumeDTO): Promise<ApiResponse<Resume>> {
  return request.put(`/resume/${id}`, data)
}

/**
 * 删除简历
 */
export function deleteResume(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/resume/${id}`)
}

// ========== 技能相关 ==========

/**
 * 添加技能
 */
export function addSkill(resumeId: number, data: SkillDTO): Promise<ApiResponse<Skill>> {
  return request.post(`/resume/${resumeId}/skill`, data)
}

/**
 * 更新技能
 */
export function updateSkill(resumeId: number, skillId: number, data: SkillDTO): Promise<ApiResponse<Skill>> {
  return request.put(`/resume/${resumeId}/skill/${skillId}`, data)
}

/**
 * 删除技能
 */
export function deleteSkill(resumeId: number, skillId: number): Promise<ApiResponse<null>> {
  return request.delete(`/resume/${resumeId}/skill/${skillId}`)
}

// ========== 教育经历相关 ==========

/**
 * 添加教育经历
 */
export function addEducation(resumeId: number, data: EducationDTO): Promise<ApiResponse<Education>> {
  return request.post(`/resume/${resumeId}/education`, data)
}

/**
 * 更新教育经历
 */
export function updateEducation(resumeId: number, eduId: number, data: EducationDTO): Promise<ApiResponse<Education>> {
  return request.put(`/resume/${resumeId}/education/${eduId}`, data)
}

/**
 * 删除教育经历
 */
export function deleteEducation(resumeId: number, eduId: number): Promise<ApiResponse<null>> {
  return request.delete(`/resume/${resumeId}/education/${eduId}`)
}

// ========== 工作经历相关 ==========

/**
 * 添加工作经历
 */
export function addWorkExperience(resumeId: number, data: WorkExperienceDTO): Promise<ApiResponse<WorkExperience>> {
  return request.post(`/resume/${resumeId}/work`, data)
}

/**
 * 更新工作经历
 */
export function updateWorkExperience(resumeId: number, workId: number, data: WorkExperienceDTO): Promise<ApiResponse<WorkExperience>> {
  return request.put(`/resume/${resumeId}/work/${workId}`, data)
}

/**
 * 删除工作经历
 */
export function deleteWorkExperience(resumeId: number, workId: number): Promise<ApiResponse<null>> {
  return request.delete(`/resume/${resumeId}/work/${workId}`)
}

// ========== 简历上传解析 ==========

/**
 * 上传并解析简历文件
 */
export function uploadResume(file: File): Promise<ApiResponse<ResumeDetail>> {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/resume/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 60000  // 上传和解析可能需要较长时间
  })
}
