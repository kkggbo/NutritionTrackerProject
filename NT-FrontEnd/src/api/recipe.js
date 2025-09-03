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

// ================= 点赞功能 =================
export function likeRecipeService(recipeId) {
  return request({
    url: `/recipe/like/${recipeId}`,
    method: 'post'
  });
}

export function unlikeRecipeService(recipeId) {
  return request({
    url: `/recipe/like/${recipeId}`,
    method: 'delete'
  });
}

export function getLikeCountService(recipeId) {
  return request({
    url: `/recipe/like/count/${recipeId}`,
    method: 'get'
  });
}

export function isLikedService(recipeId) {
  return request({
    url: `/recipe/like/status/${recipeId}`,
    method: 'get'
  });
}

// ================= 收藏功能 =================
export function favoriteRecipeService(recipeId) {
  return request({
    url: `/recipe/favorite/${recipeId}`,
    method: 'post'
  });
}

export function unfavoriteRecipeService(recipeId) {
  return request({
    url: `/recipe/favorite/${recipeId}`,
    method: 'delete'
  });
}

export function getFavoriteCountService(recipeId) {
  return request({
    url: `/recipe/favorite/count/${recipeId}`,
    method: 'get'
  });
}

export function isFavoritedService(recipeId) {
  return request({
    url: `/recipe/favorite/status/${recipeId}`,
    method: 'get'
  });
}

// ================= 评论功能 =================
export function addCommentService(recipeId, commentContent) {
  return request({
    url: `/recipe/comment/${recipeId}`,
    method: 'post',
    params: { commentContent }
  });
}

export function getCommentsService(recipeId) {
  return request({
    url: `/recipe/comment/${recipeId}`,
    method: 'get'
  });
}