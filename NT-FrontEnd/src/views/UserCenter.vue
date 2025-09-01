<template>
  <div class="profile-page p-4">
    <!-- 用户资料展示卡片 -->
    <el-card shadow="hover" class="profile-card">
      <h2 class="title">个人中心</h2>
      <el-descriptions column="1" border>
        <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ user.genderText }}</el-descriptions-item>
        <el-descriptions-item label="身高">{{ user.height }} cm</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ user.age }} 岁</el-descriptions-item>
        <el-descriptions-item label="目标">{{ user.goalText }}</el-descriptions-item>
        <el-descriptions-item label="体重">{{ user.weight }} kg</el-descriptions-item>
        <el-descriptions-item label="活动水平">{{ user.activityText }}</el-descriptions-item>
      </el-descriptions>

      <div class="mt-4">
        <el-button type="primary" @click="openEdit" class="full-width" style="margin-top: 4%;">编辑资料</el-button>
      </div>
    </el-card>

    <!-- 编辑资料对话框 -->
    <el-dialog title="编辑个人资料" v-model="editVisible" width="70%" :close-on-click-modal="false"
      :modal-append-to-body="true">
      <el-form :model="editForm" label-width="50%" class="edit-form">
        <el-form-item label="性别">
          <el-select v-model="editForm.genderText" placeholder="选择性别" class="full-width">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="editForm.age" :min="1" class="full-width" />
        </el-form-item>
        <el-form-item label="身高 (cm)">
          <el-input-number v-model="editForm.height" :min="100" :max="250" class="full-width" />
        </el-form-item>
        <el-form-item label="体重 (kg)">
          <el-input-number v-model="editForm.weight" :min="30" :max="200" class="full-width" />
        </el-form-item>
        <el-form-item label="目标">
          <el-select v-model="editForm.goalText" placeholder="选择目标" class="full-width">
            <el-option label="增肌" value="增肌" />
            <el-option label="减脂" value="减脂" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动水平">
          <el-select v-model="editForm.activityText" placeholder="选择活动水平" class="full-width">
            <el-option label="久坐少动" value="久坐少动" />
            <el-option label="轻度活动" value="轻度活动" />
            <el-option label="中度活动" value="中度活动" />
            <el-option label="高强度活动" value="高强度活动" />
            <el-option label="极高强度活动" value="极高强度活动" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button class="full-width" @click="editVisible = false">取消</el-button>
        <el-button type="primary" class="full-width mt-2" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfoService, updateUserInfoService } from '../api/user'
import { ElMessage } from 'element-plus'
const router = useRouter()

// 响应式对象
const user = ref({
  username: '',
  age: 0,
  weight: 0,
  height: 0,
  goalText: '',
  activityText: ''
})

// 映射表
const genderMap = { '1': '男', '2': '女' }
const goalMap = { '1': '增肌', '2': '减脂' }
const activityMap = { '1.2': '久坐少动', '1.375': '轻度活动', '1.55': '中度活动', '1.725': '高强度活动', '1.9': '极高强度活动' }

const genderReverseMap = { '男': '1', '女': '2' }
const goalReverseMap = { '增肌': '1', '减脂': '2' }
const activityReverseMap = {
  '久坐少动': '1.2',
  '轻度活动': '1.375',
  '中度活动': '1.55',
  '高强度活动': '1.725',
  '极高强度活动': '1.9'
}

const editVisible = ref(false)
const editForm = ref({ ...user.value })

// 获取用户信息
const getUserInfo = async () => {
  try {
    let result = await getUserInfoService()
    console.log('获取用户信息成功', result)
    const data = result.data

    if (data) {
      user.value = {
        ...data,
        genderText: genderMap[data.gender] || '未知',
        goalText: goalMap[data.goal] || '未知',
        activityText: activityMap[data.activityLevel] || data.activityLevel
      }
    } else {
      console.warn(result.msg || '获取日记信息失败')
    }
  } catch (error) {
    console.error('请求失败', error)
  }
}

// 提交修改用户信息
const saveProfile = async () => {
  // 构造提交数据
  const submitData = {
    username: editForm.value.username,
    age: editForm.value.age,
    weight: editForm.value.weight,
    height: editForm.value.height,
    gender: genderReverseMap[editForm.value.gender] || 1,
    goal: goalReverseMap[editForm.value.goal] || 1,
    activityLevel: activityReverseMap[editForm.value.activityText] || 1.2
  }
  console.log('提交修改用户信息:', submitData)
  try {
    const res = await updateUserInfoService(submitData)
    if (res.code === 1) {
      ElMessage.success('信息更新成功')
      // 更新本地显示
      user.value = { ...editForm.value }
      editVisible.value = false
    } else {
      console.warn('更新失败:', res.msg)
    }
  } catch (err) {
    console.error('提交失败', err)
  }
}

const openEdit = () => {
  editVisible.value = true
  editForm.value = { ...user.value } // 每次打开时拷贝最新信息
}


// 底部导航栏切换
const tabs = [
  { name: 'diary', label: '营养日记', icon: '📔', path: '/' },
  { name: 'recipe', label: '食谱', icon: '🥗', path: '/recipeList' },
  { name: 'profile', label: '个人中心', icon: '👤', path: '/userCenter' },
  { name: 'settings', label: '设置（TODO）', icon: '⚙️', path: '/' }
]


getUserInfo()
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  border-radius: 12px;
  padding-left: 2.5rem;
  padding-right: 2.5rem;
  height: calc(100vh - 56px);
}

.title {
  font-size: 1.6rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.full-width {
  width: 100%;
  margin: 0;
  align-items: center,
}

.mt-2 {
  margin-top: 0.5rem;
}

.el-form {
  align-items: center
}

.el-form-item {
  width: 75%;
}


/* 响应式优化，竖屏下卡片和对话框宽度占满屏幕 */
@media (max-width: 768px) {
  .profile-page {
    padding: 1rem;
  }

  .profile-card {
    padding: 1rem;
  }
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
  /* 确保在最上层 */
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
  padding-bottom: 70px;
  /* 要大于 bottom-nav 的高度 */
}
</style>
