<template>
  <div class="page-container">
    <!-- 总结区域按钮 -->
    <el-button class="summary-card" @click="goToSummaryDetails" plain>
      <div class="summary-left">
        <svg viewBox="0 0 140 140" class="calorie-progress-circle" :width="size" :height="size">
          <circle class="bg-circle" cx="70" cy="70" :r="radius" :stroke-width="12" fill="none" />
          <circle
            class="progress-circle"
            cx="70"
            cy="70"
            :r="radius"
            :stroke-width="12"
            fill="none"
            :stroke="progressColor"
            :stroke-dasharray="circumference"
            :stroke-dashoffset="dashOffset"
            stroke-linecap="round"
            transform="rotate(-90 70 70)"
          />
          <text x="70" y="62" text-anchor="middle" dominant-baseline="middle" class="calorie-text-line1">
            已食用 {{ totalCalories }} kcal
          </text>
          <text x="70" y="85" text-anchor="middle" dominant-baseline="middle" class="calorie-text-line2">
            剩余 {{ remainingCalories }} kcal
          </text>
        </svg>
      </div>

      <div class="summary-right">
        <div class="macro-item" v-for="macro in macros" :key="macro.label">
          <div class="progress-label">
            {{ macro.label }}：{{ macro.value }} / {{ macro.goal }} g
          </div>
          <el-progress
            :percentage="Math.min((macro.value / macro.goal) * 100, 150)"
            :color="macro.color"
            status="success"
            :show-text="false"
            :stroke-width="14"
          />
        </div>
      </div>
    </el-button>

    <!-- 四餐区域 -->
    <div class="meals-card">
      <el-button v-for="meal in meals" :key="meal.name" class="meal-card" @click="goToMeal(meal.index)" plain>
        <span>{{ meal.name }}</span>
        <div class="meal-calories">{{ meal.calories }} kcal</div>
      </el-button>
    </div>
  </div>

  <!-- 底部导航栏 -->
<nav class="bottom-nav">
  <button
    v-for="tab in tabs"
    :key="tab.name"
    class="nav-item"
    @click="router.push({ path: tab.path })"
  >
    <span class="icon">{{ tab.icon }}</span>
    <span class="label">{{ tab.label }}</span>
  </button>
</nav>
</template>


<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useTokenStore } from '@/stores/token.js'
import { getUserDiaryService } from '@/api/user'
const router = useRouter()

const tokenStore = useTokenStore()

// 响应式数据
const totalCalories = ref(0)
const goalCalories = ref(0)

const macros = reactive([
  { label: '碳水', value: 0, goal: 0, color: "#409EFF" },
  { label: '蛋白质', value: 0, goal: 0, color: "#67C23A" },
  { label: '脂肪', value: 0, goal: 0, color: "#E6A23C" }
])

const meals = reactive([
  { name: '早餐', calories: 0, index: 1},
  { name: '中餐', calories: 0, index: 2},
  { name: '晚餐', calories: 0, index: 3},
  { name: '加餐', calories: 0, index: 4}
])

const activeTab = ref('diary')

// 圆环计算
const size = 180
const radius = 64
const circumference = 2 * Math.PI * radius

const progressPercent = computed(() =>
  Math.min(totalCalories.value / goalCalories.value, 1)
)

const dashOffset = computed(() =>
  circumference * (1 - progressPercent.value)
)

const progressColor = computed(() => {
  const ratio = totalCalories.value / goalCalories.value
  if (totalCalories.value === 0) return '#ffffff'
  if (ratio <= 1) return '#67c23a'
  const excessRatio = Math.min((ratio - 1) * 5, 1)
  const red = Math.round(103 + (255 - 103) * excessRatio)
  const green = Math.round(195 * (1 - excessRatio))
  const blue = Math.round(58 * (1 - excessRatio))
  return `rgb(${red},${green},${blue})`
})

const remainingCalories = computed(() =>
  Math.max(goalCalories.value - totalCalories.value, 0)
)

// 事件
function goToMeal(mealIndex) {
  console.log('当前餐编号：' + mealIndex)
  router.push(
    {
      path: '/mealdetail',
      query: { mealType: mealIndex }
    }
  )
}

function goToSummaryDetails() {
  console.log('进入总结详情页')
}

// 获取用户日记数据
const userDiaryInfo = async () => {
  try {
    let result = await getUserDiaryService()
    const data = result.data
    console.log('获取日记信息成功', data)

    if (data) {

      // 更新热量数据
      totalCalories.value = Number(data.totalCalories ?? 0)
      goalCalories.value = Number(data.goalCalories ?? 0)

      // 更新宏量营养素数组
      macros.splice(0, macros.length, ...(data.macros.map(item => ({
        label: getMacroLabel(item.label),
        value: item.value,
        goal: item.goal,
      }))))

      // 更新每餐摄入数据
      meals.splice(0, meals.length, ...(data.meals.map((item, idx) => ({
        name: getMealName(item.name),
        calories: item.calories,
        index: idx + 1
      }))))
    } else {
      console.warn(result.msg || '获取日记信息失败')
    }
  } catch (error) {
    console.error('请求失败', error)
  }
}

// 辅助函数：转换 label 数字为文字
const getMacroLabel = (label) => {
  switch (label) {
    case 1: return '碳水'
    case 2: return '蛋白质'
    case 3: return '脂肪'
    default: return '未知'
  }
}

// 辅助函数：转换餐次编号为文字
const getMealName = (name) => {
  switch (name) {
    case 1: return '早餐'
    case 2: return '中餐'
    case 3: return '晚餐'
    case 4: return '加餐'
    default: return '其他'
  }
}

// 底部导航栏切换
const tabs = [
  { name: 'diary', label: '营养日记', icon: '📔', path: '/' },
  { name: 'recipe', label: '食谱', icon: '🥗', path: '/recipeList' },
  { name: 'profile', label: '个人中心', icon: '👤', path: '/userCenter' },
  { name: 'settings', label: '设置（TODO）', icon: '⚙️', path: '/' }
]

userDiaryInfo()
</script>


<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: 8px;
  box-sizing: border-box;
} 

.summary-card {
  flex: 8;
  display: flex;
  flex-direction: row;
  padding: 16px;
  width: 100%;
  border-radius: 12px;
  margin-bottom: 16px;
  text-align: center;
  background-color: #f5f5f5;
  gap: 24px;
  align-items: center;
}

.summary-left {
  width: 240px;
  height: 240px;
  flex-shrink: 0;
}

.calorie-progress-circle {
  width: 100%;
  height: 100%;
}

.bg-circle {
  stroke: #ddd;
}

.progress-circle {
  transition: stroke-dashoffset 0.6s ease, stroke 0.6s ease;
}

.calorie-text-line1 {
  font-weight: 700;
  font-size: 14px;
  fill: #333;
}

.calorie-text-line2 {
  font-weight: 500;
  font-size: 12px;
  fill: #666;
}

.summary-right {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  gap: 20px;
  width: 200px;
  margin-left: 100px;
}

.macro-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 40px;
}

.progress-label {
  font-size: 14px;
  margin-bottom: 6px;
}

.meals-card {
  flex: 8;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 12px;
  margin-bottom: 60px;
  width: 95%;
  align-self: center;
}

.meal-card {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 12px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid rgb(255, 87, 87);
  margin: 0;
}

.meal-calories {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}

.tab-bar {
  flex: 1;
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 4px 0;
  border-top: 5px solid #ddd;
}

.tab-btn {
  flex: 1;
  margin: 0 4px;
}

/* 底部导航栏固定 */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  border-top: 1px solid #ddd;
  background: #fff;
  height: 56px;
  z-index: 1000; /* 确保在最上层 */
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-size: 12px;
  color: #666;
  border: none;
  background: none;
  cursor: pointer;
  transition: color 0.3s ease;
}

.nav-item .icon {
  font-size: 20px;
  line-height: 1;
  margin-bottom: 2px;
}

.nav-item.active {
  color: #409eff;
  font-weight: 600;
}

/* 关键：给主内容区域留出底部高度，避免被导航栏挡住 */
.recipe-list-page {
  padding-bottom: 70px; /* 要大于 bottom-nav 的高度 */
}
</style>
