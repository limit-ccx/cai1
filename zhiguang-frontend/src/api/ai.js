import request from '@/utils/request'

/**
 * AI 润色文章内容
 * @param {Object} data
 * @param {string} data.content  - 要润色的文本内容
 * @param {string} data.mode     - 润色模式：polish/formal/casual/concise/expand
 * @param {string} [data.title]  - 文章标题（可选）
 */
export const polishContent = (data) => {
  return request({
    url: '/api/ai/polish',
    method: 'post',
    data,
    timeout: 90000  // AI 接口需要更长超时（90s）
  })
}
