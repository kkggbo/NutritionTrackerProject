// 导入request.js请求工具
import request from '@/utils/request'

// 导入token
import { useTokenStore } from '@/stores/token.js'

// 提供收集用户信息接口的函数
export function setUserInfoService(form) {

    // 在前端将性别转换为数字
    // const genderMap = {
    //     男: 1,
    //     女: 2
    // }

        const goalMap = {
        增肌: 1,
        减脂: 2
    }

    const payload = {
        gender: Number(form.gender),
        age: Number(form.age),
        height: Number(form.height),
        weight: Number(form.weight),
        goal: goalMap[form.goal],
        activityLevel: Number(form.activityLevel)
    }

    return request({
        url: '/user/profile/set',
        method: 'post',
        data: payload
    })
}

 /**
 * 查询用户日记信息
 */
export const getUserDiaryService = () => { 
    return request({
        url: '/user/diary',
        method: 'get',
    })
}
