<template>
    <el-row class="form-container" justify="center" align="middle">
        <el-col :xs="22" :sm="18" :md="12" :lg="8">
            <el-card shadow="hover">
                <h2 class="title">请完善以下资料，助您实现营养目标</h2>
                <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" size="large">
                    <el-form-item label="性别" prop="gender">
                        <el-radio-group v-model="form.gender">
                            <el-radio v-model="form.gender" :value="1">男</el-radio>
                            <el-radio v-model="form.gender" :value="2">女</el-radio>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item label="年龄" prop="age">
                        <el-input-number v-model="form.age" placeholder="请输入年龄"
                            style="width: 100%" />
                    </el-form-item>

                    <el-form-item label="身高" prop="height">
                        <el-input v-model="form.height" placeholder="请输入身高（cm）" type="number" />
                    </el-form-item>

                    <el-form-item label="体重" prop="weight">
                        <el-input v-model="form.weight" placeholder="请输入体重（kg）" type="number" />
                    </el-form-item>

                    <el-form-item label="我的目标" prop="goal">
                        <el-select v-model="form.goal" placeholder="请选择目标">
                            <el-option label="减脂" value="减脂" />
                            <el-option label="增肌" value="增肌" />
                        </el-select>
                    </el-form-item>

                    <el-form-item label="活动强度" prop="activityLevel">
                        <el-select v-model="form.activityLevel" placeholder="请选择活动强度" style="width: 240px">
                            <el-option label="久坐少动（基本不运动）" :value="1.2" />
                            <el-option label="轻度活动（偶尔运动）" :value="1.375" />
                            <el-option label="中度活动（每周锻炼3~5次）" :value="1.55" />
                            <el-option label="高强度活动（每天锻炼）" :value="1.725" />
                            <el-option label="极高强度活动（高强度训练）" :value="1.9" />
                        </el-select>

                        <el-button text type="primary" @click="dialogVisible = true" style="margin-left: 10px">
                            查看详细说明
                        </el-button>
                    </el-form-item>

                    <el-form-item>
                        <div style="width: 100%; display: flex; justify-content: space-between; align-items: center;">
                            <div>
                                <el-button type="primary" @click="submitForm">提交</el-button>
                                <el-button @click="resetForm">重置</el-button>
                            </div>
                            <el-button type="info" size="small" @click="skipSetting">暂不设置</el-button>
                        </div>
                    </el-form-item>
                </el-form>
            </el-card>
        </el-col>
    </el-row>

    <!-- 活动强度说明弹窗 -->
    <el-dialog v-model="dialogVisible" title="活动强度等级说明" width="600px">
        <div style="white-space: pre-wrap; line-height: 1.8;">
            🧍‍♂️ <strong>1. 久坐少动（Sedentary）</strong><br>
            活动系数：1.2 <br>
            日常几乎不运动 <br>
            久坐办公、上学、在家等 <br>
            每天仅进行非常轻微的日常活动（如走路去厕所、做饭） <br>
            🟡 适合典型办公室职员、学生、居家人士<br>
            <br>
            🚶‍♂️ <strong>2. 轻度活动（Lightly Active）</strong><br>
            活动系数：1.375 <br>
            每周运动 1~3 天，强度较低 <br>
            每天约有 30 分钟的轻体力活动或散步 <br>
            教师、收银员、护理员等适量站立工作者 <br>
            🟡 适合轻运动人群、上下班步行者<br>
            <br>
            🚴‍♀️ <strong>3. 中度活动（Moderately Active）</strong><br>
            活动系数：1.55 <br>
            每周锻炼 3~5 天，中等强度 <br>
            有一定出汗和呼吸加快的运动习惯（如跑步、健身、骑车、打球等） <br>
            快递员、服务员、清洁工等体力劳动者 <br>
            🟢 健身爱好者、经常锻炼的人<br>
            <br>
            🏋️‍♀️ <strong>4. 高强度活动（Very Active）</strong><br>
            活动系数：1.725 <br>
            每周锻炼 6~7 天，强度较高 <br>
            重量训练、高强度间歇训练、长期有氧训练 <br>
            建筑工人、体能教练、专业健身者 <br>
            🟢 推荐给体力劳动者或训练频繁的人群<br>
            <br>
            🏃‍♂️ <strong>5. 极高强度活动（Super Active）</strong><br>
            活动系数：1.9 <br>
            每天多次训练，或进行高强度体力劳动 <br>
            训练+体力工作组合 <br>
            特种兵、马拉松运动员、健美运动员备赛期 <br>
            🔴 仅限极个别高体能人群<br>
        </div>
        <template #footer>
            <el-button @click="dialogVisible = false">关闭</el-button>
        </template>
    </el-dialog>

</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

// 引入路由
const router = useRouter()

const dialogVisible = ref(false)

// 表单数据
const form = ref({
  age: 10,
  height: '',
  weight: '',
  gender: '',
  goal: '',
  activityLevel: '',
})

// 表单引用
const formRef = ref(null)

// 校验规则
const rules = {
    gender: [
        {
            required: true,
            message: '请选择您的性别哦 😊',
            trigger: 'change'
        }
    ],
    age: [
        {
            required: true,
            message: '请输入您的年龄 😊',
            trigger: 'blur'
        },
        {
            type: 'number',
            min: 10,
            max: 90,
            message: '年龄需在 10 到 90 岁之间，请确认一下哦 💡',
            trigger: 'blur'
        }
    ],
    height: [
        {
            required: true,
            message: '请输入您的身高（cm），方便为您提供建议 🧍',
            trigger: 'blur'
        }
    ],
    weight: [
        {
            required: true,
            message: '请输入您的体重（kg），我们为您保密 😄',
            trigger: 'blur'
        }
    ],
    goal: [
        {
            required: true,
            message: '请选择您的健身目标 🏋️',
            trigger: 'change'
        }
    ],
    activityLevel: [{ required: true, message: '请选择活动强度', trigger: 'change' }],
}

import { ElMessage } from 'element-plus'
import { setUserInfoService } from '@/api/user.js'
// 提交函数
const submitForm = () => {
    formRef.value.validate(async (valid) => {
        if (valid) {
            // console.log(form.value)
            let result = await setUserInfoService(form.value)
            ElMessage.success('提交成功！🎉')
            router.push("/")
        } else {
            ElMessage.warning('请完整填写表单')
        }
    })
}

// 重置函数
const resetForm = () => {
    formRef.value.resetFields()
}

// 暂不设置按钮函数（TODO）
const skipSetting = () => {
  // 跳转首页或其他页面
  router.push('/')
  // 或你也可以用 alert 或 emit 告知上层不设置
  alert("你可以随时在个人中心设置信息")
}
</script>

<style scoped>
.form-container {
    min-height: 100vh;
    padding-top: 5vh;
    background-color: #f4f6f8;
}

.title {
    text-align: center;
    margin-bottom: 20px;
}
</style>
