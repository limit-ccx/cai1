import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, register as apiRegister, getProfile as apiGetProfile } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const token = ref(localStorage.getItem('token') || '')

  const isLoggedIn = computed(() => !!token.value)

  const setSession = (nextToken, nextUser = null) => {
    token.value = nextToken || ''
    user.value = nextUser

    if (token.value) {
      localStorage.setItem('token', token.value)
    } else {
      localStorage.removeItem('token')
    }

    if (nextUser) {
      localStorage.setItem('user', JSON.stringify(nextUser))
    } else {
      localStorage.removeItem('user')
    }
  }

  const login = async (email, password) => {
    try {
      const data = await apiLogin({ email, password })
      setSession(data.token, data.user)
      return data
    } catch (error) {
      throw error
    }
  }

  const register = async (data) => {
    try {
      const response = await apiRegister(data)
      return response
    } catch (error) {
      throw error
    }
  }

  const logout = () => {
    setSession('', null)
  }

  const fetchProfile = async () => {
    try {
      const profile = await apiGetProfile()
      if (profile.user) {
        setSession(token.value, profile.user)
        return profile
      } else {
        setSession(token.value, profile)
        return profile
      }
    } catch (error) {
      logout()
      throw error
    }
  }

  return {
    user,
    token,
    isLoggedIn,
    login,
    register,
    logout,
    fetchProfile
  }
})