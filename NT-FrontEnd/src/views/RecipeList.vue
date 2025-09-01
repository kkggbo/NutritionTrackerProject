<template>
    <div class="recipe-list-page p-4">
        <!-- 搜索框 -->
        <div class="search-bar flex justify-center mb-2">
            <el-input v-model="searchKeyword" placeholder="请输入食谱名称" clearable class="w-full md:w-1/2"
                @keyup.enter.native="handleSearch">
                <template #append>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                </template>
            </el-input>
        </div>

        <!-- 更多筛选条件按钮 -->
        <div class="filter-btn-container">
            <el-button type="primary" @click="showFilterDialog = true">
                更多筛选条件
            </el-button>
        </div>

        <!-- 筛选弹窗 -->
        <el-dialog title="筛选条件" v-model="showFilterDialog" :append-to-body="true">
            <div class="mb-4">
                <p class="font-semibold mb-2">膳食类型</p>
                <el-radio-group v-model="filter.mealType">
                    <el-radio label="">不限</el-radio>
                    <el-radio label="早餐">早餐</el-radio>
                    <el-radio label="午餐">午餐</el-radio>
                    <el-radio label="晚餐">晚餐</el-radio>
                    <el-radio label="甜点">甜点</el-radio>
                </el-radio-group>
            </div>

            <div class="mb-4">
                <p class="font-semibold mb-2">卡路里范围 (kcal)</p>
                <el-slider v-model="filter.calories" :min="0" :max="2000" range show-stops></el-slider>
                <div class="flex justify-between text-xs mt-1">
                    <span>{{ filter.calories[0] }}</span>
                    <span> - </span>
                    <span>{{ filter.calories[1] }}</span>
                </div>
            </div>

            <div class="mb-4">
                <p class="font-semibold mb-2">烹饪时长范围 (分钟)</p>
                <el-slider v-model="filter.cookTime" :min="0" :max="120" range show-stops></el-slider>
                <div class="flex justify-between text-xs mt-1">
                    <span>{{ filter.cookTime[0] }}</span>
                    <span> - </span>
                    <span>{{ filter.cookTime[1] }}</span>
                </div>
            </div>

            <template #footer>
                <el-button @click="resetFilter">重置</el-button>
                <el-button type="primary" @click="applyFilter">应用</el-button>
            </template>
        </el-dialog>

        <!-- 食谱卡片列表 -->
        <el-row :gutter="12">
            <el-col v-for="recipe in recipes" :key="recipe.id" :xs="24" :sm="12" :md="8" :lg="6">
                <el-card shadow="hover" class="mb-4 cursor-pointer recipe-card" @click="goDetail(recipe.id)">
                    <img :src="recipe.coverImg" class="card-img" alt="封面" />
                    <div class="mt-2">
                        <h3 class="text-lg font-bold truncate">{{ recipe.name }}</h3>
                        <p class="text-sm text-gray-500 truncate">{{ recipe.description }}</p>
                        <div class="flex justify-between text-xs mt-2 text-gray-600">
                            <span>时长: {{ recipe.cookTime }} 分钟</span>
                            <span>热量: {{ recipe.totalCalories }} kcal</span>
                        </div>
                        <span class="tag">{{ recipe.mealType }}</span>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 下滑加载提示 -->
        <div v-if="loading" class="text-center py-4 text-gray-500">正在加载更多...</div>
        <div v-else-if="noMore" class="text-center py-4 text-gray-400">没有更多了</div>
    </div>

    <!-- 底部导航栏 -->
    <nav class="bottom-nav">
        <button v-for="tab in tabs" :key="tab.name" class="nav-item" @click="router.push({ path: tab.path })">
            <span class="icon">{{ tab.icon }}</span>
            <span class="label">{{ tab.label }}</span>
        </button>
    </nav>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { fetchRecipeListService } from "../api/recipe";

// 页面数据
const searchKeyword = ref("");
const router = useRouter();
const recipes = ref([]);
const page = ref(1);
const loading = ref(false);
const noMore = ref(false);
const filter = ref({
    name: searchKeyword.value,
    mealType: "",
    calories: [0, 2000],
    cookTime: [0, 120],
    pageNo: page,
    pageSize: 8
});

// 筛选弹窗控制
const showFilterDialog = ref(false);

// 加载食谱
const loadRecipes = async () => {
    if (loading.value || noMore.value) return;
    loading.value = true;
    try {
        // 调用后端接口，可将筛选条件传过去
        const params = {
            name: filter.value.name,
            mealType: filter.value.mealType,
            cookTimeMin: filter.value.cookTime[0],
            cookTimeMax: filter.value.cookTime[1],
            caloriesMin: filter.value.calories[0],
            caloriesMax: filter.value.calories[1],
            pageNo: filter.value.pageNo,
            pageSize: filter.value.pageSize
        };
        const res = await fetchRecipeListService(params);
        if (res.code === 1) {
            const records = res.data;
            if (records && records.length > 0) {
                recipes.value.push(...records);
                page.value++;
            } else {
                noMore.value = true;
            }
        } else {
            console.error("加载失败：", res.msg);
        }
    } catch (err) {
        console.error("请求出错：", err);
    } finally {
        loading.value = false;
    }
};

// 搜索
const handleSearch = () => {
    recipes.value = [];
    page.value = 1;
    noMore.value = false;
    loadRecipes();
};

// 筛选弹窗操作
const resetFilter = () => {
    filter.value = { mealType: "", calories: [0, 2000], cookTime: [0, 120] };
};
const applyFilter = () => {
    showFilterDialog.value = false;
    recipes.value = [];
    page.value = 1;
    noMore.value = false;
    loadRecipes();
};

// 跳转详情页
const goDetail = (id) => {
    ElMessage.info(`跳转到食谱详情页: ${id}`);
};

// 滚动加载
const onScroll = () => {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    const windowHeight = window.innerHeight;
    const docHeight = document.documentElement.scrollHeight;
    if (scrollTop + windowHeight >= docHeight - 50) {
        loadRecipes();
    }
};

onMounted(() => {
    loadRecipes();
    window.addEventListener("scroll", onScroll);
});

// 底部导航栏
const tabs = [
    { name: "diary", label: "营养日记", icon: "📔", path: "/" },
    { name: "recipe", label: "食谱", icon: "🥗", path: "/recipeList" },
    { name: "profile", label: "个人中心", icon: "👤", path: "/userCenter" },
    { name: "settings", label: "设置（TODO）", icon: "⚙️", path: "/" }
];
</script>

<style scoped>
.filter-btn-container {
    text-align: right;
    /* 按钮靠右 */
    margin-top: 8px;
    margin-bottom: 8px;
}

.el-row {
    margin-left: 0 !important;
    margin-right: 0 !important;
}

.recipe-card {
    position: relative;
    border-radius: 12px;
    overflow: hidden;
}

.card-img {
    width: 100%;
    height: 180px;
    object-fit: cover;
    border-radius: 8px;
}

.tag {
    position: absolute;
    top: 10px;
    right: 10px;
    background: #409eff;
    color: white;
    font-size: 12px;
    padding: 2px 6px;
    border-radius: 6px;
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
    z-index: 1000;
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

/* 给主内容区域留出底部高度 */
.recipe-list-page {
    padding-bottom: 70px;
}
</style>
