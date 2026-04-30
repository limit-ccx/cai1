<template>
  <div class="register-page">
    <div class="register-left">
      <div class="brand-content">
        <router-link to="/" class="brand-logo">
          <span class="logo-icon">知</span>
          <span class="logo-text">知光平台</span>
        </router-link>
        <h1 class="brand-slogan">开启你的知识之旅</h1>
        <p class="brand-desc">加入知光，与千万开发者一起分享知识、记录成长、互相启发。</p>
        <div class="features">
          <div class="feature-item">
            <span class="feature-icon">🚀</span>
            <span>快速成长路径</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🎯</span>
            <span>精准知识匹配</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🌟</span>
            <span>高质量内容社区</span>
          </div>
        </div>
      </div>
    </div>

    <div class="register-right">
      <div class="register-card">
        <div class="register-header">
          <h2 class="register-title">加入知光</h2>
          <p class="register-subtitle">创建账号，开始你的知识之旅</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="register-form"
          @submit.prevent="handleRegister"
        >
          <el-form-item prop="username">
            <label class="input-label">用户名</label>
            <el-input
              v-model="form.username"
              placeholder="请输入用户名（3-20个字符）"
              size="large"
            />
          </el-form-item>

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
              placeholder="请输入密码（至少6位）"
              size="large"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <label class="input-label">确认密码</label>
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              size="large"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <button type="submit" class="btn-primary btn-full" :loading="loading">
              注 册
            </button>
          </el-form-item>
        </el-form>

        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="link">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名3-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authStore.register({
      username: form.username,
      email: form.email,
      password: form.password
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    // 错误已在 request 拦截器中提示
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  display: flex;
}

.register-left {
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

.register-right {
  width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);
  padding: 60px;
}

.register-card {
  width: 100%;
  max-width: 380px;
}

.register-header {
  margin-bottom: 40px;
}

.register-title {
  font-size: 28px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 8px;
}

.register-subtitle {
  font-size: 15px;
  color: var(--color-text-secondary);
}

.register-form {
  :deep(.el-form-item) {
    margin-bottom: 20px;
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

.register-footer {
  text-align: center;
  font-size: 14px;
  color: var(--color-text-muted);
  margin-top: 24px;
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
  .register-left {
    display: none;
  }

  .register-right {
    width: 100%;
    min-height: 100vh;
  }
}

@media (max-width: 480px) {
  .register-right {
    padding: 40px 24px;
  }
}
</style>