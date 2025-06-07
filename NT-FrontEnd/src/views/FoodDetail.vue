<template>
    <div class="page-container">
        <!-- 返回按钮 -->
        <div class="goBack-wrapper">
            <el-button type="primary" :icon="ArrowLeft" @click="goBack" class="goBack">
                返回
            </el-button>
        </div>

        <el-card class="food-card" shadow="hover">
            <template #header>
                <div class="card-header">食物详情</div>
            </template>

            <div class="info-item"><strong>食物名称：</strong>{{ food.name }}</div>
            <div class="info-item"><strong>每 100g 热量：</strong>{{ food.caloriesPer100g }} kcal</div>
            <div class="info-item last"><strong>每 100g 碳水：</strong>{{ food.carbsPer100g }} g</div>
            <div class="info-item"><strong>每 100g 蛋白质：</strong>{{ food.proteinPer100g }} g</div>
            <div class="info-item"><strong>每 100g 脂肪：</strong>{{ food.fatPer100g }} g</div>

            <!-- TODO 标签区域 -->
            <!-- <div class="tags-area">
                <el-tag class="tag" type="success">高蛋白</el-tag>
                <el-tag class="tag" type="warning">低脂肪</el-tag>
            </div> -->

            <el-form class="form-area" label-position="top">
                <el-form-item label="请输入食用克数">
                    <el-input v-model.number="weight" type="number" placeholder="如：100" />
                </el-form-item>
            </el-form>

            <div class="nutrition-area">
                <div><strong>实际热量：</strong>{{ actualCalories }} kcal</div>
                <div><strong>蛋白质：</strong>{{ actualProtein }} g</div>
                <div><strong>脂肪：</strong>{{ actualFat }} g</div>
                <div><strong>碳水：</strong>{{ actualCarbs }} g</div>
            </div>
        </el-card>

        <!-- 固定底部添加按钮 -->
        <div class="fixed-bottom">
            <div class="btn-wrapper">
                <el-button type="primary" class="add-button add-main" @click="addFood">
                    添加
                </el-button>
                  <el-button
    :icon="isFavorite ? Star : StarFilled"
    :type="isFavorite ? 'danger' : 'info'"
    class="add-button add-favorite favorite-button"
    @click="toggleFavorite"
  >
    {{ isFavorite ? '已收藏' : '收藏' }}
  </el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'

// 收藏状态
const isFavorite = ref(false) 

// 从url获取食物id
const route = useRoute()
const router = useRouter()
const foodId = route.params.foodId

// 获取当前餐类型
const mealType = ref(route.query.mealType || '')

const food = ref({ 
    foodId: 0,
    name: '',
    caloriesPer100g: 0,
    carbsPer100g: 0,
    proteinPer100g: 0,
    fatPer100g: 0,
 })
const weight = ref(100) // 默认输入克数


// 营养素字段
const actualCalories = computed(() => ((food.value.caloriesPer100g || 0) * weight.value / 100).toFixed(1))
const actualProtein = computed(() => ((food.value.proteinPer100g || 0) * weight.value / 100).toFixed(1))
const actualFat = computed(() => ((food.value.fatPer100g || 0) * weight.value / 100).toFixed(1))
const actualCarbs = computed(() => ((food.value.carbsPer100g || 0) * weight.value / 100).toFixed(1))

import {intakeSingleFoodService} from '@/api/food'
const addFood = async() => {
    let result =await intakeSingleFoodService(mealType.value, foodId, weight.value)
    if (result.code === 1) {
        ElMessage.success(`已添加 ${weight.value}g 的 ${food.value.name} 到这一餐`)
    }
}

const goBack = () => {
    router.back()
}

import { foodDetailService } from '@/api/food'

// 获取食物数据
const fetchFoodData = async () => {
  try {
    const res = await foodDetailService(foodId); // 调用服务方法
    food.value = res.data; // 成功时拿到就是 data 对象
  } catch (err) {
    ElMessage.warning("获取数据失败");
  }
};

import { checkFavoriteStatusService } from '@/api/food'
// 获取收藏状态
const fetchFavoriteStatus = async () => {
  try {
    const res = await checkFavoriteStatusService(foodId)
    if (res.code === 1) {
      isFavorite.value = res.data.favorite // true 或 false
    }
  } catch (e) {
    ElMessage.warning('收藏状态获取失败')
  }
}

import { addOrRemoveFavoriteService } from '@/api/food'
// 修改收藏状态
const toggleFavorite = async () => {
  try {
    const result = await addOrRemoveFavoriteService({
      foodId: foodId,
      favorite: !isFavorite.value, // 当前状态取反
    })
    if (result.code === 1) {
      isFavorite.value = !isFavorite.value
      ElMessage.success(isFavorite.value ? '收藏成功' : '取消收藏成功')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(fetchFoodData(), fetchFavoriteStatus(foodId))

</script>

<style scoped>
.page-container {
    min-height: 100vh;
    padding: 8px;
    background-color: #f9fafb;
    /* 类似 Tailwind 的 gray-50 */
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-start;
    position: relative;
}

.goBack-wrapper {
    width: 100%;
    max-width: calc(100% - 32px);

}

.goBack {
    margin-top: 16px;
    margin-left: 16px;
    width: 180px;
    height: 40px;
}

.food-card {
    width: 100%;
    max-width: calc(100% - 32px);
    font-size: 16px;
    margin: 16px;
}

.card-header {
    font-size: 18px;
    font-weight: 600;
    text-align: center;
    padding-bottom: 12px;
}

.info-item {
    margin-bottom: 12px;
}

.info-item.last {
    margin-bottom: 16px;
}

.tags-area {
    margin-bottom: 16px;
}

.tag {
    margin-right: 8px;
}

.form-area {
    margin-bottom: 12px;
}

.el-form-item {
    margin-bottom: 32px;
    margin-top: 64px;
}

.nutrition-area {
    margin-bottom: 16px;
    padding: 12px;
    background-color: #f0f0f0;
    /* 类似 Tailwind 的 gray-100 */
    border-radius: 6px;
}

.fixed-bottom {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    background-color: white;
    border-top: 1px solid #ddd;
    padding: 16px 0;
    z-index: 50;
}

.btn-wrapper {
    margin: 0 auto;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    margin-left: 16px;
    margin-right: 16px;
}

.add-button {
    font-size: 20px;
    border-radius: 10px;
    width: 85%;
    height: 56px;
    color: white;
}

.el-input {
    width: 100%;
    margin: 0 auto;
    height: 48px;
}

.add-main {
  width: 85%;  /* 主按钮宽 */
  background-color: #2f55ff9d;
  color: white;
}

.add-favorite {
  width: 15%; /* 收藏按钮较窄 */
  min-width: 48px; /* 设置最小宽度避免太小 */
  padding: 0;

}

.favorite-button :deep(.el-icon svg) {
  fill: white !important;
}
</style>
