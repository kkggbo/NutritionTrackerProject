<template>
  <div class="container">
    <h2 class="title">🍎 食物库</h2>

    <el-input
      v-model="searchName"
      placeholder="🔍 输入食物名称进行搜索"
      clearable
      class="search-box"
      @clear="onSearch"
      @keyup.enter="onSearch"
    >
      <template #append>
        <el-button @click="onSearch">搜索</el-button>
      </template>
    </el-input>

    <el-scrollbar
      style="height: 500px;"
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
      <el-button type="primary" @click="goBack">返回</el-button>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { fetchFoodsService } from '@/api/food'
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

const loadMore = async () => {
  if (loading.value || noMore.value) return
  loading.value = true
  try {
    const res = await fetchFoodsService(page.value, size, searchName.value)
    const data = res.data || []
    if (data.length < size) noMore.value = true
    foods.value.push(...data)
    page.value++
  } catch (e) {
    console.error('加载失败', e)
  } finally {
    loading.value = false
  }
}

const onSearch = () => {
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

.el-button{
  width:  90%;
  height: 60px;
  position: fixed;
  bottom: 20px;
  left: 20px;
  z-index: 1000;
}
</style>
