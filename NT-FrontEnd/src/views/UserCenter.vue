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
        <el-descriptions-item label="当前积分">{{ points }} 分</el-descriptions-item>
      </el-descriptions>

      <div class="mt-4 flex-col">
        <el-button type="primary" @click="openEdit" class="full-width" style="margin-top: 4%;">编辑资料</el-button>
      </div>

            <div class="mt-4 flex-col">
        <el-button type="success" @click="openRewardDialog" class="full-width" style="margin-top: 4%;">积分兑换</el-button>
      </div>
    </el-card>

    <!-- 编辑资料对话框 -->
    <el-dialog title="编辑个人资料" v-model="editVisible" width="70%" :close-on-click-modal="false" :modal-append-to-body="true">
      <el-form :model="editForm" class="edit-form">
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
        <el-button type="primary" class="full-width mt-2" style="margin-top: 10px;" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>

    <!-- 积分兑换对话框 -->
    <el-dialog title="积分兑换中心" v-model="rewardDialogVisible" width="80%">
      <el-row :gutter="20">
        <el-col v-for="gift in giftList" :key="gift.id" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card shadow="hover" class="gift-card">
            <el-image :src="gift.imageUrl" alt="gift" class="gift-img" />
            <h3>{{ gift.name }}</h3>
            <p>{{ gift.description }}</p>
            <p>所需积分：<b>{{ gift.requiredPoints }}</b></p>
            <p>库存：{{ gift.stock }}</p>
            <el-input-number
              v-model="gift.selectedCount"
              :min="0"
              :max="gift.stock"
              size="small"
              class="mt-1"
            />
          </el-card>
        </el-col>
      </el-row>

      <template #footer>
        <el-button @click="rewardDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="exchangeGifts">确认兑换</el-button>
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
import { getUserInfoService, updateUserInfoService, getPointsService } from '../api/user'
import { listGiftsService, exchangeGiftsService } from '../api/reward'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 响应式数据
const user = ref({})
const points = ref(0)
const editVisible = ref(false)
const editForm = ref({})
const rewardDialogVisible = ref(false)
const giftList = ref([])

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

// 获取用户信息
const getUserInfo = async () => {
  try {
    const result = await getUserInfoService()
    const data = result.data
    if (data) {
      user.value = {
        ...data,
        genderText: genderMap[data.gender] || '未知',
        goalText: goalMap[data.goal] || '未知',
        activityText: activityMap[data.activityLevel] || data.activityLevel
      }
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

// 获取用户积分
const getPoints = async () => {
  try {
    const res = await getPointsService()
    points.value = res.data || 0
  } catch (error) {
    console.error('获取积分失败', error)
  }
}

// 打开编辑
const openEdit = () => {
  editVisible.value = true
  editForm.value = { ...user.value }
}

// 保存资料
const saveProfile = async () => {
  const submitData = {
    username: editForm.value.username,
    age: editForm.value.age,
    weight: editForm.value.weight,
    height: editForm.value.height,
    gender: genderReverseMap[editForm.value.genderText] || 1,
    goal: goalReverseMap[editForm.value.goalText] || 1,
    activityLevel: activityReverseMap[editForm.value.activityText] || 1.2
  }
  try {
    const res = await updateUserInfoService(submitData)
    if (res.code === 1) {
      ElMessage.success('信息更新成功')
      user.value = { ...editForm.value }
      editVisible.value = false
    } else {
      ElMessage.error(res.msg || '更新失败')
    }
  } catch (err) {
    console.error('提交失败', err)
  }
}

// 打开积分兑换对话框
const openRewardDialog = async () => {
  rewardDialogVisible.value = true
  try {
    const res = await listGiftsService()
    console.log('res', res)
    if (res.code === 1 && Array.isArray(res.data)) {
      giftList.value = res.data.map(g => ({ ...g, selectedCount: 0 }))
    }
  } catch (error) {
    console.error('获取礼品列表失败', error)
  }
}

// 兑换礼品
const exchangeGifts = async () => {
  const selected = giftList.value.filter(g => g.selectedCount > 0)
  if (selected.length === 0) {
    ElMessage.warning('请至少选择一个礼品')
    return
  }

  let totalPoints = 0
  for (const g of selected) {
    if (g.selectedCount > g.stock) {
      ElMessage.warning(`${g.name} 库存不足`)
      return
    }
    totalPoints += g.requiredPoints * g.selectedCount
  }
  console.log('totalPoints', totalPoints)
  console.log('currentPoints', points.value)

  if (totalPoints > points.value) {
    ElMessage.warning('积分不足，无法兑换')
    return
  }

  try {
    for (const g of selected) {
      const res = await exchangeGiftsService({ giftId: g.id, count: g.selectedCount })
      if (res.code !== 1) {
        ElMessage.error(`${g.name} 兑换失败：${res.msg}`)
        return
      }
    }
    ElMessage.success('兑换成功！')
    rewardDialogVisible.value = false
    getPoints() // 刷新积分
  } catch (error) {
    console.error('兑换失败', error)
  }
}

// 初始化
getUserInfo()
getPoints()

// 底部导航栏
const tabs = [
  { name: 'diary', label: '营养日记', icon: '📔', path: '/' },
  { name: 'recipe', label: '食谱', icon: '🥗', path: '/recipeList' },
  { name: 'profile', label: '个人中心', icon: '👤', path: '/userCenter' },
  { name: 'challenge', label: '挑战', icon: '⚔️', path: "/challenge" }
]
</script>

<style scoped>

.edit-form {
  width: 60%;
  margin: 0 auto; /* 表单整体水平居中 */
}

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

.full-width {
  width: 100%;
  margin: 0; 
  align-items: center;
}

.gift-card {
  text-align: center;
  border-radius: 10px;
  margin-bottom: 1rem;
}

.gift-img {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 10px;
}

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
  margin-bottom: 2px;
}
</style>
