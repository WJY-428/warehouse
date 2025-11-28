import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)  // 用户信息

  // 登录
  const login = async (username, password) => {
    const res = await request.post('/user/login', { username, password })
    userInfo.value = res.data
    localStorage.setItem('user', JSON.stringify(res.data))
    return res
  }

  // 退出登录
  const logout = () => {
    userInfo.value = null
    localStorage.removeItem('user')
  }

  // 初始化用户信息（页面刷新后）
  const initUser = () => {
    const user = localStorage.getItem('user')
    if (user) userInfo.value = JSON.parse(user)
  }

  return { userInfo, login, logout, initUser }
})