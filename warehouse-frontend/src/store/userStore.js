import { ref } from 'vue'
import request from '@/utils/request'

const TOKEN_KEY = 'token'
const USER_KEY = 'user'

function readUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  } catch (e) {
    return null
  }
}

const token = ref(localStorage.getItem(TOKEN_KEY) || '')
const userInfo = ref(readUser())

export function useUserStore() {
  const login = async (form) => {
    const res = await request.post('/user/login', form)
    const { token: newToken, user } = res.data
    token.value = newToken
    userInfo.value = user
    localStorage.setItem(TOKEN_KEY, newToken)
    localStorage.setItem(USER_KEY, JSON.stringify(user))
    return res.data
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  const initUser = () => {
    token.value = localStorage.getItem(TOKEN_KEY) || ''
    userInfo.value = readUser()
    return token.value
  }

  const isLogin = () => Boolean(token.value || localStorage.getItem(TOKEN_KEY))

  return { token, userInfo, login, logout, initUser, isLogin }
}
