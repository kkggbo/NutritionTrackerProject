// 创建router路由器

import { createRouter, createWebHistory } from 'vue-router'

// 导入组件
import LoginView from '@/views/Login.vue'
import UserInfoForm from '@/views/UserInfoForm.vue'
import NutrientDiary from '@/views/NutrientDiary.vue'

// 定义路由关系
const routes = [
    { path: '/login', component: LoginView },
    { path: '/userInfo', component: UserInfoForm },
    { path: '/', component: NutrientDiary }
]

// 创建路由器
const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

// 导出路由
export default router