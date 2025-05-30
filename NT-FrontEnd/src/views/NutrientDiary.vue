<template>
  <div class="page-container">
    <!-- 总结区域按钮 -->
    <el-button class="summary-card" @click="goToSummaryDetails" plain>
      <div class="summary-left">
        <svg viewBox="0 0 140 140" class="calorie-progress-circle" :width="size" :height="size">
          <circle class="bg-circle" cx="70" cy="70" :r="radius" :stroke-width=12 fill="none" />
          <circle class="progress-circle" cx="70" cy="70" :r="radius" :stroke-width=12 fill="none"
            :stroke="progressColor" :stroke-dasharray="circumference" :stroke-dashoffset="dashOffset"
            stroke-linecap="round" transform="rotate(-90 70 70)" />
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
      <div class="progress-label">{{ macro.label }}：{{ macro.value }} / {{ macro.goal }} g</div>
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
      <el-button v-for="meal in meals" :key="meal.name" class="meal-card" @click="goToMeal(meal.name)" plain>
        <span>{{ meal.name }}</span>
        <div class="meal-calories">{{ meal.calories }} kcal</div>
      </el-button>
    </div>

    <!-- 标签页
    <el-tabs v-model="activeTab" class="tab-bar" tab-position="bottom">
      <el-tab-pane label="营养日记" name="diary"></el-tab-pane>
      <el-tab-pane label="个人中心" name="profile"></el-tab-pane>
    </el-tabs> -->
      <div class="app-container">
    <div class="page-content">
      <component :is="currentView" />
    </div>

    <nav class="bottom-nav">
      <button
        v-for="tab in tabs"
        :key="tab.name"
        :class="['nav-item', { active: activeTab === tab.name }]"
        @click="activeTab = tab.name"
      >
        <span class="icon">{{ tab.icon }}</span>
        <span class="label">{{ tab.label }}</span>
      </button>
    </nav>
  </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { computed, ref, defineAsyncComponent } from 'vue'
import { useTokenStore } from '@/stores/token.js'

const tokenStore = useTokenStore()

const totalCalories = 1600
const goalCalories = 1800

const macros = [
  { label: '碳水', value: 160, goal: 250, color: "#409EFF" },
  { label: '蛋白质', value: 80, goal: 120 },
  { label: '脂肪', value: 45, goal: 60, color: "#E6A23C" }
]

const meals = [
  { name: '早餐', calories: 380 },
  { name: '中餐', calories: 580 },
  { name: '晚餐', calories: 350 },
  { name: '加餐', calories: 40 }
]

const activeTab = ref('diary')

// 圆环相关计算
const size = 180
const radius = 64
const circumference = 2 * Math.PI * radius

const progressPercent = computed(() => Math.min(totalCalories / goalCalories, 1))
const dashOffset = computed(() => circumference * (1 - progressPercent.value))

const progressColor = computed(() => {
  const ratio = totalCalories / goalCalories
  if (totalCalories === 0) {
    return '#ffffff' // 白色空心
  } else if (ratio <= 1) {
    return '#67c23a' // 绿色
  } else {
    // 超标红色渐变，越高越红
    const excessRatio = Math.min((ratio - 1) * 5, 1)
    const red = Math.round(103 + (255 - 103) * excessRatio)
    const green = Math.round(195 * (1 - excessRatio))
    const blue = 58 * (1 - excessRatio)
    return `rgb(${red},${green},${blue})`
  }
})

const remainingCalories = computed(() => Math.max(goalCalories - totalCalories, 0))

function goToMeal(mealName) {
  console.log('查看', mealName)
}

function goToSummaryDetails() {
  console.log('进入总结详情页')
}

// 模拟页面组件（你可以替换成真实页面组件）
const Diary = {
  template: '<div>营养日记内容</div>',
}
const Profile = {
  template: '<div>个人中心内容</div>',
}
const Settings = {
  template: '<div>设置页面内容</div>',
}

const tabs = [
  { name: 'diary', label: '营养日记', icon: '📔', component: Diary },
  { name: 'profile', label: '个人中心', icon: '👤', component: Profile },
  { name: 'settings', label: '设置', icon: '⚙️', component: Settings },
]

const currentView = computed(() => {
  const tab = tabs.find(t => t.name === activeTab.value)
  return tab ? tab.component : null
})

// 发起请求获取当前页面数据
import { getUserDiaryService } from '@/api/user'
const userDiaryInfo  = async()=>{ 
  let result = await getUserDiaryService()
  // TODO 拿到后端数据后为前端模型赋值
}

// 调用方法获取数据
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
  margin-bottom: 12px;
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
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
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

/* 底部导航栏 */
.bottom-nav {
  display: flex;
  border-top: 1px solid #ddd;
  background: #fff;
  height: 56px;
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
  color: #409eff; /* 选中颜色 */
  font-weight: 600;
}
</style>
