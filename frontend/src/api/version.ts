import request from './request'

// 版本列表项
export interface ResumeVersionVO {
  id: number
  resumeId: number
  versionNumber: number
  fileName: string
  fileSize: number
  uploadTime: string
  versionNote: string | null
  candidateName: string | null
  hasAnalysis: boolean
}

// 版本详情
export interface ResumeVersionDetailVO {
  id: number
  resumeId: number
  versionNumber: number
  fileName: string
  fileSize: number
  rawText: string
  parsedData: string | null
  analysisReport: string | null
  analysisMetadata: string | null
  uploadTime: string
  versionNote: string | null
  candidateName: string | null
  hasAnalysis: boolean
}

// 解析结果
export interface ParseResultVO {
  fileName: string
  fileSize: number
  rawText: string
  parsedJson: string
  versionId: number | null
  candidateName: string
  phone: string
  email: string
  address: string
  targetPosition: string
  summary: string
  skills: Array<{
    name: string
    level: number
    category: string
    years: number
  }>
  educations: Array<{
    school: string
    degree: string
    major: string
    startDate: string
    endDate: string
    gpa: number | null
    description: string
  }>
  workExperiences: Array<{
    company: string
    position: string
    startDate: string
    endDate: string | null
    description: string
    achievements: string
  }>
}

// 获取版本列表
export function getResumeVersions() {
  return request.get<ResumeVersionVO[]>('/resume/versions')
}

// 获取版本详情
export function getResumeVersionDetail(id: number) {
  return request.get<ResumeVersionDetailVO>(`/resume/versions/${id}`)
}

// 删除版本
export function deleteResumeVersion(id: number) {
  return request.delete(`/resume/versions/${id}`)
}

// 上传并解析简历
export function uploadAndParseResume(file: File, versionNote?: string) {
  const formData = new FormData()
  formData.append('file', file)
  if (versionNote) {
    formData.append('versionNote', versionNote)
  }
  return request.post<ParseResultVO>('/resume/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 保存解析结果到简历
export function saveParseResult(parseResult: ParseResultVO) {
  return request.post('/resume/save-parse-result', parseResult)
}
