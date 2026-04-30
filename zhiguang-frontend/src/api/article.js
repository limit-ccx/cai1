import request from '@/utils/request'

export const getArticles = (params) => {
  return request({
    url: '/api/articles',
    method: 'get',
    params
  })
}

export const getArticle = (id) => {
  return request({
    url: `/api/articles/${id}`,
    method: 'get'
  })
}

export const createArticle = (data) => {
  return request({
    url: '/api/articles',
    method: 'post',
    data
  })
}

export const updateArticle = (id, data) => {
  return request({
    url: `/api/articles/${id}`,
    method: 'put',
    data
  })
}

export const deleteArticle = (id) => {
  return request({
    url: `/api/articles/${id}`,
    method: 'delete'
  })
}

export const likeArticle = (id) => {
  return request({
    url: `/api/articles/${id}/like`,
    method: 'post'
  })
}

export const favoriteArticle = (id) => {
  return request({
    url: `/api/articles/${id}/favorite`,
    method: 'post'
  })
}

export const getLikeStatus = (id) => {
  return request({
    url: `/api/articles/${id}/like/status`,
    method: 'get'
  })
}

export const getMyFavorites = () => {
  return request({
    url: '/api/users/favorites',
    method: 'get'
  })
}