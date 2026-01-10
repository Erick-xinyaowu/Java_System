import request from './request'

/**
 * 会话消息
 */
export interface ChatMessage {
  id: number
  role: 'user' | 'assistant'
  content: string
  createdAt: string
}

/**
 * 会话信息
 */
export interface ChatSession {
  id: number
  title: string
  createdAt: string
  updatedAt: string
  messages?: ChatMessage[]
}

/**
 * 发送消息请求
 */
export interface SendMessageRequest {
  sessionId?: number | null
  content: string
}

/**
 * 获取用户的所有会话列表
 */
export const getSessions = () => {
  return request.get<ChatSession[]>('/chat/sessions')
}

/**
 * 获取会话详情（包含消息）
 */
export const getSessionDetail = (sessionId: number) => {
  return request.get<ChatSession>(`/chat/sessions/${sessionId}`)
}

/**
 * 创建新会话
 */
export const createSession = () => {
  return request.post<ChatSession>('/chat/sessions')
}

/**
 * 发送消息并获取 AI 回复
 */
export const sendMessage = (data: SendMessageRequest) => {
  return request.post<ChatMessage>('/chat/message', data)
}

/**
 * 更新会话标题
 */
export const updateSessionTitle = (sessionId: number, title: string) => {
  return request.put(`/chat/sessions/${sessionId}/title`, { title })
}

/**
 * 删除会话
 */
export const deleteSession = (sessionId: number) => {
  return request.delete(`/chat/sessions/${sessionId}`)
}

/**
 * 清空所有会话
 */
export const clearAllSessions = () => {
  return request.delete('/chat/sessions')
}
