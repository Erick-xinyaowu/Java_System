import request from './request'

// ========== 请求参数类型 ==========

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  email: string
}

export interface UpdateUserParams {
  nickname?: string
  email?: string
  phone?: string
  avatar?: string
  gender?: number
  birthday?: string
}

// ========== 响应数据类型 ==========

export interface LoginResponse {
  success: boolean
  code: number
  message: string
  data: {
    token: string
    userId: number
    username: string
    email: string
  }
}

export interface UserInfoResponse {
  success: boolean
  code: number
  message: string
  data: {
    id: number
    username: string
    email: string
    phone?: string
    nickname?: string
    avatar?: string
    gender?: number
    birthday?: string
    status?: number
    createdAt?: string
  }
}

// ========== API 函数 ==========

/**
 * 用户登录
 */
export function login(data: LoginParams): Promise<LoginResponse> {
  return request.post('/user/login', data)
}

/**
 * 用户注册
 */
export function register(data: RegisterParams): Promise<LoginResponse> {
  return request.post('/user/register', data)
}

/**
 * 获取当前用户信息
 */
export function getUserInfo(): Promise<UserInfoResponse> {
  return request.get('/user/info')
}

/**
 * 更新用户信息
 */
export function updateUserInfo(data: UpdateUserParams) {
  return request.put('/user/update', data)
}
