import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// API 统一响应格式
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
  success?: boolean
}

// 创建 axios 实例，连接后端服务
const request = axios.create({
  baseURL: 'http://localhost:8080/api',  // 后端服务地址
  timeout: 120000,  // 增加超时时间到2分钟，AI解析+生成报告可能耗时较长
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加 JWT Token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    // 打印请求日志（开发环境）
    console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`, config.data || '')
    return config
  },
  (error) => {
    console.error('[API Request Error]', error)
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一处理响应和错误
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 打印响应日志（开发环境）
    console.log(`[API Response] ${response.config.url}`, res)
    
    // 后端 ApiResponse 格式: { code: number, message: string, data: any, timestamp: number }
    // code=200 表示成功
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    // 添加 success 字段方便前端判断
    res.success = true
    return res
  },
  (error) => {
    console.error('[API Response Error]', error)
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          // Token 过期或无效
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          ElMessage.error('登录已过期，请重新登录')
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error(data?.message || '服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || `请求失败 (${status})`)
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default request
