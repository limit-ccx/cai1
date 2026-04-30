import request from '@/utils/request'

export const login = (data) => {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

export const register = (data) => {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

export const getProfile = () => {
  return request({
    url: '/api/users/profile',
    method: 'get'
  })
}

export const updateProfile = (data) => {
  return request({
    url: '/api/users/profile',
    method: 'put',
    data
  })
}