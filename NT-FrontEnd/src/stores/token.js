// 定义store
import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * params: 1. 唯一标识，2. 配置项
 * return: 函数
 */
export const useTokenStore = defineStore('token', ()=>{
    // 定义状态内容

    //响应式变量
    const token = ref('')

    // 定义一个函数用于修改token的值
    const setToken = (newToken) => {
        token.value = newToken
    }

    // 移除token的值
        const removeToken = () => {
        token.value = ''
    }

    return {token, setToken, removeToken}
});