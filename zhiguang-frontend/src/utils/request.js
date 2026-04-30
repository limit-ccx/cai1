import axios from 'axios'
import { ElMessage } from 'element-plus'

function resolveApiBaseURL () {
  const raw = import.meta.env.VITE_API_BASE_URL
  if (raw != null && String(raw).trim() !== '') {
    return String(raw).trim().replace(/\/+$/, '')
  }
  return import.meta.env.DEV ? '' : 'http://127.0.0.1:8080'
}

function resolveTimeoutMs () {
  const n = Number(import.meta.env.VITE_API_TIMEOUT)
  if (Number.isFinite(n) && n > 0) return n
  return 30000
}

const request = axios.create({
  baseURL: resolveApiBaseURL(),
  timeout: resolveTimeoutMs()
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (
      payload &&
      typeof payload === 'object' &&
      typeof payload.code === 'number'
    ) {
      if (payload.code !== 200) {
        const err = new Error(payload.message || '请求失败')
        err.businessCode = payload.code
        err.response = response
        return Promise.reject(err)
      }
      return payload.data
    }
    return payload
  },
  (error) => {
    const status = error.response?.status
    const raw = error.response?.data
    const message =
      (typeof raw === 'object' && raw?.message) ||
      error.message ||
      '请求失败'

    if (status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
      return Promise.reject(error)
    }

    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request