<template>
  <div class="recipe-detail">
    <!-- 图片（后端暂时没有封面图，这里先用占位图或以后扩展） -->
    <el-image
      :src="recipe.coverImg || 'https://via.placeholder.com/600x300'"
      fit="cover"
      class="cover-img"
    />

    <!-- 标题和描述 -->
    <h2 class="title">{{ recipe.name }}</h2>
    <p class="description">{{ recipe.description }}</p>

    <!-- 基本信息 -->
    <div class="info-box">
      <p><strong>所需时间:</strong> {{ recipe.cookTime }} 分钟</p>
      <p><strong>总热量:</strong> {{ recipe.totalCalories }} kcal</p>
    </div>

    <!-- 营养成分 -->
    <div class="nutrition-box">
      <div class="nutrition-item">
        <p class="nutrition-label">蛋白质</p>
        <p class="nutrition-value">{{ recipe.totalProtein }} g</p>
      </div>
      <div class="nutrition-item">
        <p class="nutrition-label">碳水</p>
        <p class="nutrition-value">{{ recipe.totalCarbs }} g</p>
      </div>
      <div class="nutrition-item">
        <p class="nutrition-label">脂肪</p>
        <p class="nutrition-value">{{ recipe.totalFat }} g</p>
      </div>
    </div>

    <!-- 原材料 -->
    <div class="section">
      <h3>原材料</h3>
      <ul>
        <li v-for="(item, index) in recipe.ingredients" :key="index">
          {{ item.name }} - {{ item.weight }} g
        </li>
      </ul>
    </div>

    <!-- 制作步骤 -->
    <div class="section">
      <h3>制作步骤</h3>
      <ol>
        <li v-for="(step, index) in recipe.steps" :key="index">
          {{ step }}
        </li>
      </ol>
    </div>

    <!-- 收藏 / 点赞 -->
    <div class="actions">
      <el-button type="primary" @click="toggleFavorite">
        {{ isFavorite ? "取消收藏" : "收藏" }}
      </el-button>
      <el-button type="success" @click="toggleLike">
        👍 {{ likes }}
      </el-button>
    </div>

    <!-- 评论 -->
    <div class="comments section">
      <h3>评论</h3>
      <el-input
        v-model="newComment"
        placeholder="写下你的评论..."
        clearable
        class="comment-input"
      />
      <el-button type="primary" @click="addComment" class="comment-btn">提交</el-button>
      <ul>
        <li v-for="(comment, index) in comments" :key="index">
          {{ comment }}
        </li>
      </ul>
    </div>
        <!-- 底部返回按钮 -->
    <div class="fixed-footer">
      <el-button class="back-btn" type="primary" @click="goBack">返回</el-button>
    </div>
  </div>

  
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { fetchRecipeDetailService } from "@/api/recipe"; 
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'

// 从url获取食物id
const router = useRouter()
const route = useRoute()
const recipeId = route.params.id

// 模拟后端返回的 RecipeVO 数据
const recipe = ref({});

// 收藏 / 点赞 / 评论逻辑
const isFavorite = ref(false);
const likes = ref(12);
const comments = ref(["很喜欢这道菜！", "简单易做"]);
const newComment = ref("");

const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value;
  ElMessage.success(isFavorite.value ? "已收藏" : "取消收藏");
};

const toggleLike = () => {
  likes.value++;
  ElMessage.success("点赞成功！");
};

const addComment = () => {
  if (!newComment.value.trim()) return ElMessage.warning("请输入评论内容");
  comments.value.push(newComment.value.trim());
  newComment.value = "";
  ElMessage.success("评论成功！");
};

// 获取真实数据
onMounted(async () => {
  try {
    const res = await fetchRecipeDetailService(recipeId);
    console.log("获取食谱详情成功：", res);

    if (res && res.data && res.code === 1) {
      // 按后端约定解构数据
      recipe.value = res.data;
    } else {
      console.warn("获取食谱详情失败：", res?.data?.msg || "未知错误");
      recipe.value = {
        id: null,
        name: "未找到食谱",
        description: "请稍后重试",
        cookTime: 0,
        mealType: "",
        ingredients: [],
        steps: [],
        totalCalories: 0,
        totalProtein: 0,
        totalFat: 0,
        totalCarbs: 0,
      };
    }
  } catch (error) {
    console.error("请求食谱详情出错：", error);
    recipe.value = {
      id: null,
      name: "加载失败",
      description: "网络异常，请检查网络后重试。",
      cookTime: 0,
      mealType: "",
      ingredients: [],
      steps: [],
      totalCalories: 0,
      totalProtein: 0,
      totalFat: 0,
      totalCarbs: 0,
    };
  }
});

// 返回上一个页面
const goBack = () => {
  router.back() // 或 router.go(-1)
}

</script>

<style scoped>
.recipe-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  padding-bottom: 80px;
}

.fixed-footer {
  position: fixed;
  bottom: 20px;   /* 距离底部 20px */
  left: 0;
  right: 0;
  display: flex;
  justify-content: center; /* 居中显示 */
  z-index: 2000; /* 确保在最上层 */
}

.back-btn {
  width: 200px;   /* 加宽 */
  height: 50px;   /* 加高 */
  font-size: 18px; /* 字体更大 */
  font-weight: bold;
}

.cover-img {
  width: 100%;
  height: 300px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 10px;
}

.description {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.info-box {
  margin-bottom: 20px;
}

.nutrition-box {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.nutrition-item {
  flex: 1;
  border: 1px solid #ddd;
  padding: 10px;
  margin: 0 5px;
  border-radius: 6px;
  text-align: center;
}

.nutrition-label {
  font-weight: bold;
  margin-bottom: 5px;
}

.section {
  margin-bottom: 20px;
}

.section h3 {
  font-size: 20px;
  margin-bottom: 10px;
}

.actions {
  margin: 20px 0;
  display: flex;
  gap: 10px;
}

.comments ul {
  margin-top: 10px;
}

.comment-input {
  width: calc(100% - 100px);
  display: inline-block;
}

.comment-btn {
  margin-left: 10px;
}
</style>
