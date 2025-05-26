<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { ref } from 'vue'

// 控制注册与登录表单的显示，默认显示登录
const isRegister = ref(false)
</script>

<template>
    <el-row class="login-page" justify="center" align="middle">
        <el-col :xs="22" :sm="18" :md="12" :lg="8" class="form">
            <!-- 注册表单 -->
            <el-form ref="form" size="large" autocomplete="off" v-if="isRegister">
                <el-form-item>
                    <h1 class="title">欢迎加入 👋</h1>

                    <p class="subtitle">请填写注册信息</p>
                </el-form-item>
                <el-form-item>
                    <el-input :prefix-icon="User" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item>
                    <el-input :prefix-icon="Lock" type="password" placeholder="请输入密码" />
                </el-form-item>
                <el-form-item>
                    <el-input :prefix-icon="Lock" type="password" placeholder="请再次输入密码" />
                </el-form-item>
                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space block>
                        注册
                    </el-button>
                </el-form-item>
                <el-form-item class="link">
                    <el-link type="info" :underline="false" @click="isRegister = false">
                        ← 返回登录
                    </el-link>
                </el-form-item>
            </el-form>

            <!-- 登录表单 -->
            <el-form ref="form" size="large" autocomplete="off" v-else>
                <el-form-item>
                    <h1 class="title">欢迎使用健康饮食小助手 😊</h1>
                    <p class="subtitle">请登录您的账户</p>
                </el-form-item>
                <el-form-item>
                    <el-input :prefix-icon="User" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item>
                    <el-input name="password" :prefix-icon="Lock" type="password" placeholder="请输入密码" />
                </el-form-item>
                <el-form-item>
                    <div class="form-options">
                        <el-checkbox>记住我</el-checkbox>
                        <el-link type="primary" :underline="false">忘记密码？</el-link>
                    </div>
                </el-form-item>
                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space block>
                        登录
                    </el-button>
                </el-form-item>
                <el-form-item class="link">
                    <el-link type="info" :underline="false" @click="isRegister = true">
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
