<template>

    <el-card class="meal-detail-card">
        <!-- 餐次类型标题 -->
        <h2 class="meal-type" style="margin-bottom: 10px">{{ mealNameMap[mealType] }}</h2>

        <el-row :gutter="10" class="macro-summary" direction="vertical">
            <el-col :span="24">总热量: {{ totalCalories }} kcal</el-col>
            <el-col :span="24">碳水: {{ totalCarbs }} g</el-col>
            <el-col :span="24">蛋白质: {{ totalProtein }} g</el-col>
            <el-col :span="24">脂肪: {{ totalFat }} g</el-col>
        </el-row>

        <!-- 下半部分：食物列表 -->
        <el-table :data="foods" class="custom-food-table" style="margin-top: 20px" empty-text="这一餐还什么都没吃呢">
            <el-table-column prop="name" label="食物" min-width="50" />
            <el-table-column prop="weight" label="质量 (g)" width="90">
                <template #default="scope">
                    <el-input-number v-model="scope.row.weight" :min="1" @change="updateTotal" style="width: 60px"
                        :controls="false" />
                </template>
            </el-table-column>
            <el-table-column prop="caloriesPer100g" label="热量 (kcal)" min-width="50" />
            <el-table-column prop="carbsPer100g" label="碳水 (g)" min-width="50" />
            <el-table-column prop="proteinPer100g" label="蛋白 (g)" min-width="50" />
            <el-table-column prop="fatPer100g" label="脂肪 (g)" min-width="50" />
            <el-table-column label="" width="50">
                <template #default="scope">
                    <el-button type="danger" size="small" :icon="Delete" circle @click="removeFood(scope.$index)" />
                </template>
            </el-table-column>
        </el-table>
        
        <!-- 添加食物按钮 -->
  <div style="margin-top: 16px; text-align: center;">
    <el-button type="primary" :icon="Plus" @click="goToAddFood" circle style="width: 48px; height: 48px;">
    </el-button>
  </div>

        <!-- 页面底部按钮区域 -->
        <div class="footer-buttons">
            <el-button type="primary" @click="submitChanges">保存更改</el-button>
            <el-button type="primary" @click="goBack">返回</el-button>
        </div>

    </el-card>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { Delete, Plus } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
const route = useRoute();

const router = useRouter();

const mealType = ref(route.query.mealType || '')

// 当前显示的食物列表
const foods = ref('');
// 初始化时克隆的原始列表，用于检查哪些食物需要被修改
const originalFoods = ref([])
// 被删除的id集合
const deletedFoodIds = ref([]) 

const today = new Date().toISOString().split('T')[0];

const totalCalories = ref(0);
const totalCarbs = ref(0);
const totalProtein = ref(0);
const totalFat = ref(0);

const mealNameMap = {
    1: '早餐信息',
    2: '中餐信息',
    3: '晚餐信息',
    4: '加餐信息'
};

const updateTotal = () => {
    totalCalories.value = 0;
    totalCarbs.value = 0;
    totalProtein.value = 0;
    totalFat.value = 0;

    for (const item of foods.value) {
        totalCalories.value += (item.caloriesPer100g * item.weight / 100);
        totalCarbs.value += (item.carbsPer100g * item.weight / 100);
        totalProtein.value += (item.proteinPer100g * item.weight / 100);
        totalFat.value += (item.fatPer100g * item.weight / 100);
    }

    totalCalories.value = Math.round(totalCalories.value);
    totalCarbs.value = Math.round(totalCarbs.value * 10) / 10;
    totalProtein.value = Math.round(totalProtein.value * 10) / 10;
    totalFat.value = Math.round(totalFat.value * 10) / 10;
};

const removeFood = (index) => {
    const removed = foods.value.splice(index, 1)[0]; // 删除并获取被删元素
    if (removed && removed.id) {
        deletedFoodIds.value.push({ id: removed.id });
    }
    updateTotal();
};

const goToAddFood = () => {
    router.push({ path: '/foodList', query: { mealType: mealType.value } });
};

watch(foods, updateTotal, { deep: true });

const goBack = () => {
    router.back()
}

import { fetchMealService, updateMealService } from '@/api/food'
const fetchMealData = async () => {
    try {
        const res = await fetchMealService(mealType.value, today);
        foods.value = res.data;
        originalFoods.value = JSON.parse(JSON.stringify(res.data)) // 深拷贝
    } catch (err) {
        ElMessage.warning("获取数据失败");
    }
};

// 获取被修改食物的内容
const getModifiedFoods = () => {
  // 1. 找出 weight 被修改的食物
  const modified = foods.value.filter(curr => {
    const orig = originalFoods.value.find(o => o.id === curr.id)
    return orig && orig.weight !== curr.weight
  }).map(item => ({
    id: item.id,
    weight: item.weight
  }))

  // 2. 把被删除的食物添加进去（weight = 0）
const deleted = deletedFoodIds.value.map(item => ({
  id: item.id,
  weight: 0
}))


  // 3. 合并两个数组
  return [...modified, ...deleted]
}


// 提交更改
const submitChanges = () => {
  const modified = getModifiedFoods()
  updateMealService(mealType.value, today, modified)
  ElMessage.success('修改成功')
}


fetchMealData()
updateTotal()
</script>

<style scoped>
.meal-detail-card {
    height: 100vh;
    margin: 2%;
}

.meal-type {
    font-size: 24px;
    margin: 0px;
}

.macro-summary {
    padding: 10px 12px;
    font-size: 18px;
    background-color: #ffeeb8;
    border-radius: 8px;
    margin-bottom: 12px;
    line-height: 3;
    font-weight: bold;
}


.footer-buttons {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 60px;
    display: flex;
    gap: 8px;
    /* 按钮间距 */
    padding: 8px;
    background-color: #ffffff;
    /* 给个背景色避免被遮住 */
    box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
    /* 可选：阴影提升感 */
    box-sizing: border-box;
    z-index: 1000;
}

.footer-buttons .el-button {
    flex: 1;
    /* 每个按钮等宽 */
}

.goBack-wrapper {
    margin-bottom: 10px;
}

.custom-food-table .el-table__cell {
  padding: 6px 10px;
}

</style>
