import axios from 'axios'
import { ElMessage } from 'element-plus'

const TOKEN_KEY = 'token'
const USER_KEY = 'user'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截：统一带上 JWT
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(TOKEN_KEY)
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截：统一处理 Result<T> 与错误
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端统一返回 { code, message, data }
    if (res && typeof res.code !== 'undefined' && res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  async (error) => {
    const status = error.response?.status
    const message = error.response?.data?.message

    if (status === 401) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
      ElMessage.error(message || '登录已过期，请重新登录')
      // 动态引入，避免 request ← router ← store ← request 的循环依赖
      const { default: router } = await import('@/router')
      if (router.currentRoute.value.path !== '/login') {
        router.replace('/login')
      }
    } else {
      ElMessage.error(message || error.message || '网络异常，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default request
