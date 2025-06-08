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

export function fetchFavoritesService(page = 1, size = 10) {
  return request({
    url: '/food/favorite/list',
    method: 'get',
    params: { page, size}
  })
}

export function fetchRecentService(limit = 20) {
  return request({
    url: '/food/recent/list',
    method: 'get',
    params: { limit }
  })
}


export function intakeSingleFoodService(mealType, foodId, weight) {
  return request({
    url: '/food/intake',
    method: 'post',
    data: { mealType, foodId, weight }
  })
}

export function addFoodService(food) {
  return request({
    url: '/food/add',
    method: 'post',
    data: food
  })
}

export function fetchMealService(mealType, date) {
  
  return request({
    url: '/food/meal',
    method: 'get',
    params: {
      mealType,
      date
    }
  })
}

export function updateMealService(mealType, date, foods) {
  
  return request({
    url: '/food/meal/update',
    method: 'post',
    data: {
      mealType,
      date,
      foods
    }
  })
}

export function checkFavoriteStatusService(foodId) {
  
  return request({
    url: `/food/favorite/status/${foodId}`,
    method: 'get',
  })
}

export function addOrRemoveFavoriteService(data) {
  return request({
    url: '/food/favorite',
    method: 'post',
    data,
  })
}



export function getFoodTagsService(foodId) {
  return request({
    url: '/food/tag/list',
    method: 'get',
    params: { foodId }
  })
}