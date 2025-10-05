<template>
  <div class="challenge-page">
    <el-container>
      <el-header>
        <h2 style="text-align: center;">挑战中心</h2>
      </el-header>

      <el-main>
        <el-row :gutter="20">
          <el-col :span="24" v-for="challenge in challenges" :key="challenge.id">
            <el-card class="challenge-card" shadow="hover">
              <div class="challenge-info">
                <h3>{{ challenge.title }}</h3>
                <p>{{ challenge.description }}</p>
                <p>持续时间：{{ formatDuration(challenge.durationSeconds) }}</p>
                <p>奖励积分：{{ challenge.rewardPoints }}</p>
              </div>

              <div class="challenge-action">
                <!-- 正在进行中 -->
                <template v-if="challenge.userChallengeStatus === 'ONGOING'">
                  <el-tag type="success">进行中</el-tag>
                  <div class="countdown">
                    剩余时间：{{ getRemainingTime(challenge.userChallengeEndTime) }}
                  </div>
                  <el-button type="danger" @click="terminateChallenge(challenge.userChallengeId)">停止挑战</el-button>
                </template>

                <!-- 未开始 -->
                <template v-else>
                  <el-tag type="info">未开始</el-tag>
                  <el-button type="primary" @click="startChallenge(challenge.id)">开始挑战</el-button>
                </template>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
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
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { 
  getAllChallengesService, 
  getUserOngoingChallengesService, 
  startChallengeService, 
  terminateChallengeService 
} from '@/api/challenge';
import { useRouter } from 'vue-router'
const router = useRouter()

const challenges = ref([]);

// 获取所有挑战 + 合并用户进行中状态
const fetchChallenges = async () => {
  try {
    const [allRes, ongoingRes] = await Promise.all([
      getAllChallengesService(),
      getUserOngoingChallengesService()
    ]);

    const allChallenges = allRes.data;
    const ongoingChallenges = ongoingRes.data;

    // 合并状态
    challenges.value = allChallenges.map(c => {
      const ongoing = ongoingChallenges.find(oc => oc.challengeId === c.id);
      if (ongoing) {
        return {
          ...c,
          userChallengeStatus: 'ONGOING',
          userChallengeEndTime: ongoing.endTime,
          userChallengeId: ongoing.id
        }
      } else {
        return {
          ...c,
          userChallengeStatus: null,
          userChallengeEndTime: null,
          userChallengeId: null
        }
      }
    });

  } catch (err) {
    console.error(err);
    ElMessage.error('获取挑战失败');
  }
};

// 开始挑战
const startChallenge = async (challengeId) => {
  try {
    await startChallengeService(challengeId);
    ElMessage.success('挑战已开始');
    fetchChallenges(); // 刷新页面
  } catch (err) {
    console.error(err);
    ElMessage.error('开始挑战失败');
  }
};

// 终止挑战
const terminateChallenge = async (userChallengeId) => {
  try {
    await terminateChallengeService(userChallengeId);
    ElMessage.success('挑战已终止');
    fetchChallenges(); // 刷新页面
  } catch (err) {
    console.error(err);
    ElMessage.error('终止挑战失败');
  }
};

// 格式化持续时间（秒 -> 天/小时/分钟）
const formatDuration = (seconds) => {
  if (!seconds) return '';
  if (seconds >= 86400) return `${Math.floor(seconds / 86400)}天`;
  if (seconds >= 3600) return `${Math.floor(seconds / 3600)}小时`;
  return `${Math.floor(seconds / 60)}分钟`;
};

// 计算剩余时间
const getRemainingTime = (endTime) => {
  if (!endTime) return '';
  const diff = new Date(endTime).getTime() - Date.now();
  if (diff <= 0) return '已结束';
  const h = Math.floor(diff / 1000 / 3600);
  const m = Math.floor((diff / 1000 % 3600) / 60);
  const s = Math.floor(diff / 1000 % 60);
  return `${h}时${m}分${s}秒`;
};

// 页面加载时获取数据
onMounted(() => {
  fetchChallenges();
  // 每秒刷新倒计时
  setInterval(() => {
    challenges.value = [...challenges.value];
  }, 1000);
});

// 底部导航栏切换
const tabs = [
  { name: 'diary', label: '营养日记', icon: '📔', path: '/' },
  { name: 'recipe', label: '食谱', icon: '🥗', path: '/recipeList' },
  { name: 'profile', label: '个人中心', icon: '👤', path: '/userCenter' },
  { name: 'challenge', label: '挑战', icon: '⚔️', path: '/challenge' }
]
</script>

<style scoped>
.challenge-page {
  padding: 16px;
}

.challenge-card {
  margin-bottom: 16px;
}

.challenge-info {
  margin-bottom: 12px;
}

.challenge-action {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.countdown {
  font-weight: bold;
  color: #f56c6c;
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
