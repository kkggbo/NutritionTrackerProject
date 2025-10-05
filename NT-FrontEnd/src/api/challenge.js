// 导入request.js请求工具
import request from '@/utils/request'

/**
 * 获取所有挑战（包含用户挑战状态）
 */
export function getAllChallengesService() {
  return request({
    url: '/user/challenge/list',
    method: 'get',
  })
}

/**
 * 获取用户进行中的挑战
 */
export function getUserOngoingChallengesService() {
  return request({
    url: '/user/challenge/ongoingList',
    method: 'get',
  })
}

/**
 * 开始挑战
 * @param {Number} challengeId 挑战ID
 */
export function startChallengeService(challengeId) {
  return request({
    url: `/user/challenge/start`,
    method: 'post',
    params: { challengeId },
  })
}

/**
 * 终止挑战
 * @param {Number} userChallengeId 用户挑战实例ID
 */
export function terminateChallengeService(userChallengeId) {
  return request({
    url: `/user/challenge/terminate`,
    method: 'post',
    params: { userChallengeId },
  })
}
