<template>
  <div class="page-container">
    <div class="app-container">
      <!-- 中间可滚动内容 -->
      <div class="page-content">
        <!-- 总结区域 -->
        <el-button class="summary-card" @click="goToSummaryDetails" plain>
          <div class="summary-left">
            <svg viewBox="0 0 140 140" class="calorie-progress-circle" :width="size" :height="size">
              <circle class="bg-circle" cx="70" cy="70" :r="radius" :stroke-width="12" fill="none" />
              <circle class="progress-circle" cx="70" cy="70" :r="radius" :stroke-width="12" fill="none"
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

        <!-- 当前页面内容 -->
        <component :is="currentView" />
      </div>

      <!-- 底部导航 -->
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
import { computed, ref } from 'vue'

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

const size = 180
const radius = 64
const circumference = 2 * Math.PI * radius
const progressPercent = computed(() => Math.min(totalCalories / goalCalories, 1))
const dashOffset = computed(() => circumference * (1 - progressPercent.value))

const progressColor = computed(() => {
  const ratio = totalCalories / goalCalories
  if (totalCalories === 0) return '#ffffff'
  if (ratio <= 1) return '#67c23a'
  const excessRatio = Math.min((ratio - 1) * 5, 1)
  const red = Math.round(103 + (255 - 103) * excessRatio)
  const green = Math.round(195 * (1 - excessRatio))
  const blue = 58 * (1 - excessRatio)
  return `rgb(${red},${green},${blue})`
})

const remainingCalories = computed(() => Math.max(goalCalories - totalCalories, 0))

function goToMeal(mealName) {
  console.log('查看', mealName)
}
function goToSummaryDetails() {
  console.log('进入总结详情页')
}

const Diary = { template: '<div style="margin-top: 16px;">营养日记内容</div>' }
const Profile = { template: '<div style="margin-top: 16px;">个人中心内容</div>' }
const Settings = { template: '<div style="margin-top: 16px;">设置页面内容</div>' }

const tabs = [
  { name: 'diary', label: '营养日记', icon: '📔', component: Diary },
  { name: 'profile', label: '个人中心', icon: '👤', component: Profile },
  { name: 'settings', label: '设置', icon: '⚙️', component: Settings }
]

const currentView = computed(() => {
  const tab = tabs.find(t => t.name === activeTab.value)
  return tab ? tab.component : null
})
</script>

<style scoped>
.page-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f8f8f8;
}

.app-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
  padding-bottom: 70px; /* 给底部导航留空间 */
}

/* 卡片区域样式 */
.summary-card {
  display: flex;
  gap: 24px;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 12px;
  margin-bottom: 16px;
  align-items: center;
  width: 100%;
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
  font-size: 14px;
  font-weight: 700;
  fill: #333;
}
.calorie-text-line2 {
  font-size: 12px;
  font-weight: 500;
  fill: #666;
}

.summary-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-width: 0;
}

.macro-item {
  display: flex;
  flex-direction: column;
}
.progress-label {
  font-size: 14px;
  margin-bottom: 6px;
}

/* 四餐 */
.meals-card {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}
.meal-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  padding: 12px;
  background: #fff;
  border: 1px solid #ffd2d2;
}
.meal-calories {
  margin-top: 4px;
  font-size: 14px;
  color: #999;
}

/* 底部导航栏 */
.bottom-nav {
  display: flex;
  height: 56px;
  background: #fff;
  border-top: 1px solid #ddd;
  position: sticky;
  bottom: 0;
  z-index: 100;
}
.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-size: 12px;
  color: #666;
  background: none;
  border: none;
  cursor: pointer;
}
.nav-item .icon {
  font-size: 20px;
}
.nav-item.active {
  color: #409eff;
  font-weight: bold;
}
</style>
