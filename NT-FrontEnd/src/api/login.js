// 导入request.js请求工具
import request from '@/utils/request'

// 提供注册接口的函数
export function registerService(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export function loginService(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}