// 导入request.js请求工具
import request from '@/utils/request'

export const listGiftsService = () => {
  return request.post('/reward/list')
}

export const exchangeGiftsService = (data) => {
  return request.post('/reward/exchange', data)
}
