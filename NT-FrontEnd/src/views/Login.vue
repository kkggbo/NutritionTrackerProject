<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

// 引入路由
const router = useRouter()

// 控制注册与登录表单的显示，默认显示登录
const isRegister = ref(false)

// 定义数据模型
const registerData = ref({
    username: '',
    password: '',
    confirmPassword: ''
})

// 校验密码函数
const checkRePassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== registerData.value.password) {
        callback(new Error('两次输入密码不一致!'))
    } else {
        callback()
    }
}

// 校验用户名函数
const checkUserName = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请输入用户名'))
    } else if (value.length < 5 || value.length > 16) {
        callback(new Error('用户名长度在 5 到 16 个字符'))
    } else {
        callback()
    }
}

// 定义表单校验规则
const rules = ref({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 5, max: 16, message: '用户名长度在 5 到 16 个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 5, max: 16, message: '密码长度在 5 到 16 个字符', trigger: 'blur' }
    ],
    confirmPassword: [
        { validator: checkRePassword, trigger: 'blur' }
    ]
})

const form = ref(null)

// 导入注册和登录的函数
import { registerService, loginService } from '@/api/login.js'
import { useTokenStore } from '@/stores/token.js'
const tokenStore = useTokenStore()
// 调用后台接口完成注册
const register = async () => {
    form.value.validate(async (valid) => {
        if (valid) {
            let data = {
                username: registerData.value.username,
                password: registerData.value.password
            }
            let result = await registerService(data)
            ElMessage.success('注册成功')
            // 把得到的token存储到pinia中
            tokenStore.setToken(result.data)
            // 跳转到用户信息收集页
            router.push('/userInfo')
        } else {
            // 校验失败
            ElMessage.warning('请输入有效的用户名或密码')
        }
    })
}

// 登录函数
const login = async() => {
    // 调用接口完成登录
    let result = await loginService(registerData.value)
    ElMessage.success('登录成功')
    // 把得到的token存储到pinia中
    tokenStore.setToken(result.data)
    router.push('/')
}

// 定义一个函数，登录与注册页面切换时清空数据模型
const clearData = () => {
    registerData.value.username = ''
    registerData.value.password = ''
    registerData.value.confirmPassword = ''
}
</script>

<template>
    <el-row class="login-page" justify="center" align="middle">
        <el-col :xs="22" :sm="18" :md="12" :lg="8" class="form">
            <!-- 注册表单 -->
            <el-form ref="form" size="large" autocomplete="off" v-if="isRegister" :model="registerData" :rules="rules">
                <el-form-item>
                    <h1 class="title">欢迎加入 👋</h1>

                    <p class="subtitle">请填写注册信息</p>
                </el-form-item>
                <el-form-item prop="username">
                    <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="registerData.username" />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input :prefix-icon="Lock" type="password" placeholder="请输入密码" v-model="registerData.password" />
                </el-form-item>
                <el-form-item prop="confirmPassword">
                    <el-input :prefix-icon="Lock" type="password" placeholder="请再次输入密码"
                        v-model="registerData.confirmPassword" />
                </el-form-item>
                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space block @click="register">
                        注册
                    </el-button>
                </el-form-item>
                <el-form-item class="link">
                    <el-link type="info" :underline="false" @click="isRegister = false; clearData()">
                        ← 返回登录
                    </el-link>
                </el-form-item>
            </el-form>

            <!-- 登录表单 -->
            <el-form ref="form" size="large" autocomplete="off" v-else :model="registerData" :rules="rules">
                <el-form-item>
                    <h1 class="title">欢迎使用健康饮食小助手 😊</h1>
                    <p class="subtitle">请登录您的账户</p>
                </el-form-item>
                <el-form-item prop="username">
                    <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="registerData.username" />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input name="password" :prefix-icon="Lock" type="password" placeholder="请输入密码"
                        v-model="registerData.password" />
                </el-form-item>
                <el-form-item>
                    <div class="form-options">
                        <el-checkbox>记住我</el-checkbox>
                        <el-link type="primary" :underline="false">忘记密码？</el-link>
                    </div>
                </el-form-item>
                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space block @click="login">
                        登录
                    </el-button>
                </el-form-item>
                <el-form-item class="link">
                    <el-link type="info" :underline="false" @click="isRegister = true; ; clearData()">
                        注册 →
                    </el-link>
                </el-form-item>
            </el-form>
        </el-col>
    </el-row>
</template>

<style lang="scss" scoped>
.login-page {
    height: 100vh;
    background: #f0f2f5;
    padding: 1rem;
}

.form {
    background-color: #fff;
    border-radius: 12px;
    padding: 2rem;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    user-select: none;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .title {
        text-align: center;
        font-size: 1.8rem;
        margin: 0 auto 1rem;
        font-weight: bold;
    }

    .subtitle {
        text-align: center;
        font-size: 1rem;
        color: #666;
        margin-bottom: 1.5rem;
    }

    .button {
        width: 100%;
    }

    .form-options {
        display: flex;
        justify-content: space-between;
        width: 100%;
        align-items: center;

        .el-link {
            margin-left: auto;
        }
    }

    .link {
        text-align: center;
    }
}

/* 响应式调整：小屏幕更紧凑 */
@media (max-width: 768px) {
    .form {
        padding: 1.5rem 1rem;

        .title {
            font-size: 1.5rem;
        }
    }
}
</style>
