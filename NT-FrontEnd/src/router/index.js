// 创建router路由器

import { createRouter, createWebHistory } from 'vue-router'

// 导入组件
import LoginView from '@/views/Login.vue'
import UserInfoForm from '@/views/UserInfoForm.vue'
import NutrientDiary from '@/views/NutrientDiary.vue'
import FoodDetail from '@/views/FoodDetail.vue'
import FoodList from '@/views/FoodList.vue'
import MealDetail from '@/views/MealDetail.vue'


// 定义路由关系
const routes = [
    { path: '/login', component: LoginView },
    { path: '/userInfo', component: UserInfoForm },
    { path: '/', component: NutrientDiary },
    { path: '/foodDetail', component: FoodDetail },
    {
        path: '/foodDetail/:foodId',
        name: 'FoodDetail',
        component: () => import('@/views/FoodDetail.vue')
    },
    { path: '/foodList', component: FoodList},
    { path: '/mealDetail', component: MealDetail }
]

// 创建路由器
const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

// 导出路由
export default router