<template>
  <div class="login-page">
    <div class="login-left">
      <div class="brand-content">
        <router-link to="/" class="brand-logo">
          <span class="logo-icon">知</span>
          <span class="logo-text">知光平台</span>
        </router-link>
        <h1 class="brand-slogan">让知识发光发热</h1>
        <p class="brand-desc">知光是一个专注于知识分享的社区，在这里你可以分享见解、记录思考、与志同道合的人交流。</p>
        <div class="features">
          <div class="feature-item">
            <span class="feature-icon">📚</span>
            <span>海量知识资源</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">💡</span>
            <span>深度思考分享</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🤝</span>
            <span>志同道合的伙伴</span>
          </div>
        </div>
      </div>
    </div>

    <div class="login-right">
      <div class="login-card">
        <div class="login-header">
          <h2 class="login-title">欢迎回来</h2>
          <p class="login-subtitle">登录以继续探索知识的世界</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="email">
            <label class="input-label">邮箱</label>
            <el-input
              v-model="form.email"
              type="email"
              placeholder="请输入邮箱地址"
              size="large"
            />
          </el-form-item>

          <el-form-item prop="password">
            <label class="input-label">密码</label>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
            />
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <a href="#" class="forgot-link">忘记密码？</a>
          </div>

          <el-form-item>
            <button type="submit" class="btn-primary btn-full" :loading="loading">
              登 录
            </button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="link">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({
  email: '',
  password: ''
})

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authStore.login(form.email, form.password)
    await authStore.fetchProfile()
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/')
  } catch {
    // 错误已在 request 拦截器中提示
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, var(--color-secondary) 0%, var(--color-primary-dark) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(76, 110, 245, 0.15) 0%, transparent 50%);
    animation: pulse 15s infinite;
  }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

.brand-content {
  position: relative;
  z-index: 1;
  max-width: 480px;
}

.brand-logo {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 48px;

  .logo-icon {
    width: 48px;
    height: 48px;
    background: linear-gradient(135deg, var(--color-accent), var(--color-accent-dark));
    color: white;
    border-radius: var(--radius-lg);
    @include flex-center;
    font-size: 24px;
    font-weight: 600;
  }

  .logo-text {
    font-size: 28px;
    font-weight: 600;
    color: white;
  }
}

.brand-slogan {
  font-size: 42px;
  font-weight: 700;
  color: white;
  line-height: 1.2;
  margin-bottom: 20px;
}

.brand-desc {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.7;
  margin-bottom: 48px;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);

  .feature-icon {
    font-size: 24px;
  }
}

.login-right {
  width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);
  padding: 60px;
}

.login-card {
  width: 100%;
  max-width: 380px;
}

.login-header {
  margin-bottom: 40px;
}

.login-title {
  font-size: 28px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 15px;
  color: var(--color-text-secondary);
}

.login-form {
  :deep(.el-form-item) {
    margin-bottom: 24px;
  }

  :deep(.el-input__wrapper) {
    padding: 12px 16px;
    border-radius: var(--radius-md);
    background: #2D3748;
    box-shadow: 0 0 0 1px #4A5568;

    &:hover {
      box-shadow: 0 0 0 1px var(--color-accent);
    }

    &.is-focus {
      box-shadow: 0 0 0 2px rgba(76, 110, 245, 0.3);
    }

    .el-input__inner {
      color: #E5EEFF;

      &::placeholder {
        color: #8A9BB5;
      }
    }
  }
}

.input-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
  margin-bottom: 8px;
}

.form-options {
  @include flex-between;
  margin-bottom: 32px;
}

.forgot-link {
  font-size: 14px;
  color: var(--color-accent);

  &:hover {
    text-decoration: underline;
  }
}

.btn-full {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--radius-md);
  background: var(--color-accent);
  color: white;
  transition: all 0.2s ease;

  &:hover:not(:disabled) {
    background: var(--color-accent-dark);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(76, 110, 245, 0.4);
  }

  &:active:not(:disabled) {
    transform: translateY(0);
  }
}

.login-footer {
  text-align: center;
  font-size: 14px;
  color: var(--color-text-muted);
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--color-border);

  .link {
    color: var(--color-accent);
    font-weight: 500;
    margin-left: 4px;

    &:hover {
      text-decoration: underline;
    }
  }
}

@media (max-width: 1024px) {
  .login-left {
    display: none;
  }

  .login-right {
    width: 100%;
    min-height: 100vh;
  }
}

@media (max-width: 480px) {
  .login-right {
    padding: 40px 24px;
  }
}
</style>