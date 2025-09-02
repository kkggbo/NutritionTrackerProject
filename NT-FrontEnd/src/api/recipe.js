// 导入request.js请求工具
import request from '@/utils/request'

export function fetchRecipeListService(data) {

  return request({
    url: '/recipe/query',
    method: 'post',
    data: data
  })
}

export function fetchRecipeDetailService(id) {

    return request({
    url: `/recipe/${id}`,
    method: 'get'
  })
}