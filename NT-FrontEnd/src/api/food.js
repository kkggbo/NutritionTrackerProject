// 导入request.js请求工具
import request from '@/utils/request'

// 获取食物详细信息
export function foodDetailService(foodId) {
  return request({
    url: '/food/detail/' + foodId,
    method: 'get',
  });
}

export function fetchFoodsService(page = 1, size = 10, name = '') {

  return request({
    url: '/food/list',
    method: 'get',
    params: { page, size, name }
  })
}

export function intakeSingleFoodService(mealType, foodId, weight) {
  return request({
    url: '/food/intake',
    method: 'post',
    data: { mealType, foodId, weight }
  })
}

