<template>
  <header class="header" :class="{ 'header-scrolled': isScrolled }">
    <div class="header-container container">
      <router-link to="/" class="logo">
        <span class="logo-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <path d="M12 2L2 7l10 5 10-5-10-5z"/>
            <path d="M2 17l10 5 10-5"/>
            <path d="M2 12l10 5 10-5"/>
          </svg>
        </span>
        <span class="logo-text">知光</span>
      </router-link>

      <nav class="nav" :class="{ 'nav-mobile-open': mobileMenuOpen }">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          @click="mobileMenuOpen = false"
        >
          {{ item.label }}
        </router-link>
      </nav>

      <div class="header-actions">
        <div class="search-box">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索文章..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch" aria-label="搜索">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"/>
              <path d="m21 21-4.35-4.35"/>
            </svg>
          </button>
        </div>

        <template v-if="authStore.isLoggedIn">
          <router-link to="/create" class="write-btn btn-primary">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 5v14M5 12h14"/>
            </svg>
            <span>写文章</span>
          </router-link>
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-avatar">
              <img :src="userAvatar" alt="用户头像" />
              <span class="avatar-status"></span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">{{ authStore.user?.username || '用户' }}</el-dropdown-item>
                <el-dropdown-item command="articles">我的文章</el-dropdown-item>
                <el-dropdown-item command="settings">设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <template v-else>
          <router-link to="/login" class="btn-text">登录</router-link>
          <router-link to="/register" class="btn-primary">注册</router-link>
        </template>

        <button class="mobile-menu-btn" @click="mobileMenuOpen = !mobileMenuOpen" aria-label="菜单">
          <svg v-if="!mobileMenuOpen" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 12h18M3 6h18M3 18h18"/>
          </svg>
          <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
          </svg>
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const searchQuery = ref('')
const mobileMenuOpen = ref(false)
const isScrolled = ref(false)
const defaultAvatar = 'https://www.w3schools.com/w3images/avatar2.png'

const navItems = [
  { path: '/', label: '首页' },
  { path: '/articles', label: '文章' },
  { path: '/about', label: '关于' }
]

const userAvatar = computed(() => {
  return authStore.user?.avatar || defaultAvatar
})

watch(() => authStore.isLoggedIn, async (isLoggedIn) => {
  if (isLoggedIn && !authStore.user) {
    try {
      await authStore.fetchProfile()
    } catch {
      // 失败时由 store 清理登录态
    }
  }
}, { immediate: true })

const handleScroll = () => {
  isScrolled.value = window.scrollY > 10
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ path: '/articles', query: { search: searchQuery.value } })
    mobileMenuOpen.value = false
  }
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
    case 'articles':
    case 'settings':
      router.push('/profile')
      break
    case 'logout':
      authStore.logout()
      ElMessage.success('已退出登录')
      router.push('/')
      break
  }
}
</script>

<style lang="scss" scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: var(--header-height);
  background: transparent;
  transition: all var(--transition-normal);

  &.header-scrolled {
    @include glass;
    box-shadow: var(--shadow-md);
  }
}

.header-container {
  height: 100%;
  @include flex-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.02em;

  .logo-icon {
    width: 36px;
    height: 36px;
    background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
    color: #fff;
    border-radius: var(--radius-md);
    @include flex-center;
    box-shadow: 0 2px 12px rgba(124, 107, 255, 0.35);
    transition: all var(--transition-fast);

    svg {
      transition: transform var(--transition-normal);
    }
  }

  &:hover .logo-icon {
    box-shadow: 0 4px 20px rgba(124, 107, 255, 0.5);

    svg {
      transform: rotate(15deg) scale(1.1);
    }
  }

  .logo-text {
    background: linear-gradient(135deg, var(--color-text), var(--color-primary-light));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

.nav {
  display: flex;
  gap: 8px;

  .nav-item {
    padding: 8px 16px;
    font-size: 14px;
    font-weight: 500;
    color: var(--color-text-secondary);
    border-radius: var(--radius-md);
    transition: all var(--transition-fast);
    position: relative;

    &:hover {
      color: var(--color-text);
      background: rgba(255, 255, 255, 0.04);
    }

    &.router-link-active {
      color: var(--color-primary-light);
      background: rgba(124, 107, 255, 0.1);

      &::after {
        content: '';
        position: absolute;
        bottom: 4px;
        left: 50%;
        transform: translateX(-50%);
        width: 16px;
        height: 2px;
        background: linear-gradient(90deg, var(--color-primary), var(--color-secondary));
        border-radius: 1px;
      }
    }
  }
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;

  .search-input {
    @include input-base;
    width: 200px;
    height: 36px;
    padding-right: 40px;
    font-size: 13px;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(255, 255, 255, 0.06);

    &:focus {
      background: rgba(255, 255, 255, 0.06);
      width: 260px;
    }
  }

  .search-btn {
    position: absolute;
    right: 6px;
    width: 28px;
    height: 28px;
    @include flex-center;
    color: var(--color-text-muted);
    border-radius: var(--radius-sm);
    transition: all var(--transition-fast);
    cursor: pointer;

    &:hover {
      color: var(--color-primary-light);
      background: rgba(124, 107, 255, 0.1);
    }
  }
}

.write-btn {
  height: 36px;
  padding: 0 16px;
  font-size: 13px;
  font-weight: 500;

  svg {
    transition: transform var(--transition-fast);
  }

  &:hover svg {
    transform: rotate(90deg);
  }
}

.user-avatar {
  position: relative;
  width: 36px;
  height: 36px;
  border-radius: var(--radius-full);
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all var(--transition-fast);

  &:hover {
    border-color: var(--color-primary);
    box-shadow: 0 0 12px rgba(124, 107, 255, 0.3);
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .avatar-status {
    position: absolute;
    bottom: 0;
    right: 0;
    width: 10px;
    height: 10px;
    background: var(--color-success);
    border: 2px solid var(--color-card);
    border-radius: var(--radius-full);
  }
}

.mobile-menu-btn {
  display: none;
  width: 40px;
  height: 40px;
  @include flex-center;
  color: var(--color-text);
  border-radius: var(--radius-md);
  transition: background var(--transition-fast);

  &:hover {
    background: rgba(255, 255, 255, 0.06);
  }
}

@media (max-width: 768px) {
  .nav {
    position: fixed;
    top: var(--header-height);
    left: 16px;
    right: 16px;
    background: var(--color-card);
    border: 1px solid var(--color-card-border);
    border-radius: var(--radius-lg);
    flex-direction: column;
    padding: 12px;
    gap: 4px;
    box-shadow: var(--shadow-lg);
    transform: translateY(-10px);
    opacity: 0;
    visibility: hidden;
    transition: all var(--transition-normal);

    &.nav-mobile-open {
      transform: translateY(0);
      opacity: 1;
      visibility: visible;
    }

    .nav-item {
      padding: 10px 16px;
      border-radius: var(--radius-md);
    }
  }

  .search-box {
    display: none;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .write-btn span {
    display: none;
  }
}
</style>
