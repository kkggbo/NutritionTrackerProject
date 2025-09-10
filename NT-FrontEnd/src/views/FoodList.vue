<template>
  <div class="container">
    <h2 class="title">🍎 食物库</h2>

  <div class="search-container">
    <el-input
      v-model="searchName"
      placeholder="🔍 输入食物名称进行搜索"
      clearable
      class="search-box"
      @clear="onSearch"
      @keyup.enter="onSearch"
    />
    <el-button class="search-btn" type="primary" @click="onSearch">搜索</el-button>
    <el-button class="search-btn" type="success" @click="onAddFood">添加新食物</el-button>
  </div>

      <!-- 最近和收藏按钮区域 -->
    <div class="filter-buttons">
      azai<el-button type="info" @click="showRecent">📅 最近食用</el-button>
      <el-button type="warning" @click="showFavorites">⭐ 我的收藏</el-button>
    </div>

    <el-scrollbar
      style="height: 550px"
      v-infinite-scroll="loadMore"
      :infinite-scroll-disabled="loading || noMore"
      infinite-scroll-distance="10"
    >
      <el-card v-for="item in foods" :key="item.id" class="food-card" @click="onClick(item.id, mealType)">
        <div class="card-content">
          <span>{{ item.name }}</span>
          <span>{{ item.caloriesPer100g }} kcal</span>
        </div>
      </el-card>
      <div v-if="loading" class="status-text">加载中...</div>
      <div v-if="noMore" class="status-text no-more">没有更多啦~</div>
    </el-scrollbar>
  </div>

      <!-- 底部返回按钮 -->
    <div class="back-button">
      <el-button class="eback-button" type="primary" @click="goBack">返回</el-button>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { fetchFoodsService, fetchFavoritesService, fetchRecentService } from '@/api/food'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const foods = ref([])
const page = ref(1)
const size = 10
const loading = ref(false)
const noMore = ref(false)
const searchName = ref('')

// 获取当前餐类型
const mealType = ref(route.query.mealType || '')
// 列表类型。0表示默认，1表示收藏，2表示最近
const listType = ref(0) // 

const loadMore = async () => {
  if (loading.value || noMore.value) return
  loading.value = true
  try {
    let res
    if (listType.value === 1) {
      res = await fetchFavoritesService(page.value, size)
    } else if (listType.value === 2) {
      res = await fetchRecentService() // 不分页
    } else {
      res = await fetchFoodsService(page.value, size, searchName.value)
    }

    const data = res.data || []

    // 如果不是“最近食物”，执行分页判断
    if (listType.value !== 2) {
      if (data.length < size) noMore.value = true
      page.value++
    } else {
      noMore.value = true // 最近食物只加载一次
    }

    foods.value.push(...data)
  } catch (e) {
    console.error('加载失败', e)
    noMore.value = true
  } finally {
    loading.value = false
  }
}

const onSearch = () => {
  listType.value = 0
  foods.value = []
  page.value = 1
  noMore.value = false
  loadMore()
}

const onClick = (id, mealType) => {
  router.push (
    {
      path: `/fooddetail/` + id,
      query: { mealType: mealType}
    }
  )
}

const goBack = () => {
    router.back()
}

const onAddFood = () => {
  router.push('/addfood')
}

const resetAndLoad = (type) => {
  listType.value = type
  foods.value = []
  page.value = 1
  noMore.value = false
  loadMore()
}

const showFavorites = () => resetAndLoad(1)
const showRecent = () => resetAndLoad(2)
</script>

<style scoped>
.container {
  padding: 16px;
}

.title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 16px;
}

.search-box {
  margin-bottom: 16px;
  width: 80%;
  height: 50px;
}

.search-container {
  display: flex;
  gap: 8px; /* 按钮之间留间距 */
  align-items: center;
}

.search-btn {
  height: 50px;
  margin-bottom: 16px;
}

.food-card {
  margin-bottom: 12px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.card-content {
  display: flex;
  justify-content: space-between;
}

.status-text {
  text-align: center;
  margin: 8px 0;
  color: #888;
}

.status-text.no-more {
  color: #aaa;
}

.back-button {
  margin-top: 50px;
  text-align: center;
}

.eback-button{
  width:  90%;
  height: 60px;
  position: fixed;
  bottom: 20px;
  left: 20px;
  z-index: 1000;
}

.filter-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.filter-buttons .el-button {
  flex: 1;
  height: 40px;
}
</style>
