import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',  // 代理前缀
  timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 携带token（登录后存储到localStorage）
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(res)
    }
    return res
  },
  (error) => {
    ElMessage.error(error.message || '服务器错误')
    return Promise.reject(error)
  }
)

export default request