<template>
  <div class="profile-page">
    <Header />

    <main class="main-content">
      <div class="container">
        <div class="profile-header">
          <div class="profile-avatar">
            <img :src="currentAvatar" alt="头像" />
          </div>
          <div class="profile-info">
            <h1 class="profile-name">{{ userInfo.username }}</h1>
            <p class="profile-bio">{{ userInfo.bio || '这个人很懒，什么都没有写' }}</p>
            <div class="profile-stats">
              <div class="stat-item">
                <span class="stat-value">{{ userInfo.articleCount || 0 }}</span>
                <span class="stat-label">文章</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ userInfo.followCount || 0 }}</span>
                <span class="stat-label">关注</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ userInfo.fansCount || 0 }}</span>
                <span class="stat-label">粉丝</span>
              </div>
            </div>
          </div>
          <div class="profile-actions">
            <button class="btn-secondary" @click="activeTab = 'settings'">编辑资料</button>
          </div>
        </div>

        <div class="profile-tabs">
          <span
            class="tab-item"
            :class="{ active: activeTab === 'articles' }"
            @click="activeTab = 'articles'"
          >
            我的文章
          </span>
          <span
            class="tab-item"
            :class="{ active: activeTab === 'favorites' }"
            @click="activeTab = 'favorites'"
          >
            我的收藏
          </span>
          <span
            class="tab-item"
            :class="{ active: activeTab === 'settings' }"
            @click="activeTab = 'settings'"
          >
            设置
          </span>
        </div>

        <div class="profile-content">
          <div v-if="activeTab === 'articles'" class="tab-panel">
            <div class="article-list" v-loading="loading">
              <ArticleCard
                v-for="article in myArticles"
                :key="article.id"
                :article="article"
              />
              <div v-if="!loading && myArticles.length === 0" class="empty-state">
                <p>还没有发布文章</p>
                <router-link to="/create" class="btn-primary">写文章</router-link>
              </div>
            </div>
          </div>

          <div v-if="activeTab === 'favorites'" class="tab-panel">
            <div class="article-list" v-loading="loading">
              <ArticleCard
                v-for="article in myFavorites"
                :key="article.id"
                :article="article"
              />
              <div v-if="!loading && myFavorites.length === 0" class="empty-state">
                <p>还没有收藏文章</p>
                <router-link to="/articles" class="btn-primary">浏览文章</router-link>
              </div>
            </div>
          </div>

          <div v-if="activeTab === 'settings'" class="tab-panel">
            <div class="settings-form">
              <div class="form-group avatar-select-group">
                <label class="form-label">选择头像</label>
                <div class="avatar-grid">
                  <div
                    v-for="avatar in presetAvatars"
                    :key="avatar"
                    class="avatar-option"
                    :class="{ selected: editForm.avatar === avatar }"
                    @click="selectAvatar(avatar)"
                  >
                    <img :src="avatar" :alt="avatar" />
                    <div class="avatar-check" v-if="editForm.avatar === avatar">✓</div>
                  </div>
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">用户名</label>
                <input v-model="editForm.username" type="text" class="input-field" />
              </div>
              <div class="form-group">
                <label class="form-label">邮箱</label>
                <input v-model="editForm.email" type="email" class="input-field" disabled />
              </div>
              <div class="form-group">
                <label class="form-label">个人简介</label>
                <textarea v-model="editForm.bio" class="textarea-field" rows="4" placeholder="介绍一下自己..."></textarea>
              </div>
              <div class="form-actions">
                <button class="btn-primary" @click="saveProfile" :disabled="saving">
                  {{ saving ? '保存中...' : '保存修改' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import ArticleCard from '@/components/ArticleCard.vue'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { getMyFavorites } from '@/api/article'

const authStore = useAuthStore()

const loading = ref(false)
const activeTab = ref('articles')
const defaultAvatar = 'https://www.w3schools.com/w3images/avatar2.png'
const saving = ref(false)

const presetAvatars = [
  'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix',
  'https://api.dicebear.com/7.x/avataaars/svg?seed=Aneka',
  'https://api.dicebear.com/7.x/avataaars/svg?seed=Mimi',
  'https://api.dicebear.com/7.x/avataaars/svg?seed=Luna',
  'https://api.dicebear.com/7.x/avataaars/svg?seed=Charlie',
  'https://api.dicebear.com/7.x/avataaars/svg?seed=Muffin',
  'https://api.dicebear.com/7.x/avataaars/svg?seed=Coco',
  'https://api.dicebear.com/7.x/avataaars/svg?seed=Bella',
  'https://api.dicebear.com/7.x/avataaars/svg?seed=Rocky',
  'https://api.dicebear.com/7.x/avataaars/svg?seed=Daisy',
  'https://api.dicebear.com/7.x/avataaars/svg?seed=Ginger',
  'https://api.dicebear.com/7.x/avataaars/svg?seed=Shadow'
]

const selectAvatar = (avatar) => {
  editForm.avatar = avatar
}

const userInfo = reactive({
  username: '',
  email: '',
  bio: '',
  avatar: '',
  articleCount: 0,
  followCount: 0,
  fansCount: 0
})

const editForm = reactive({
  username: '',
  email: '',
  bio: '',
  avatar: ''
})

const myArticles = ref([])
const myFavorites = ref([])

const currentAvatar = computed(() => userInfo.avatar || defaultAvatar)

onMounted(() => {
  if (!authStore.isLoggedIn) {
    return
  }
  loadProfile()
  loadMyArticles()
})

watch(activeTab, (tab) => {
  if (tab === 'articles') {
    loadMyArticles()
  } else if (tab === 'favorites') {
    loadMyFavorites()
  }
})

const loadProfile = async () => {
  try {
    const response = await request({
      url: '/api/users/profile',
      method: 'get'
    })
    
    const data = response
    Object.assign(userInfo, {
      username: data.user?.username || '',
      email: data.user?.email || '',
      bio: data.user?.bio || '',
      avatar: data.user?.avatar || '',
      articleCount: data.articleCount || 0,
      followCount: data.followCount || 0,
      fansCount: data.fansCount || 0
    })
    
    Object.assign(editForm, {
      username: userInfo.username,
      email: userInfo.email,
      bio: userInfo.bio,
      avatar: userInfo.avatar
    })
  } catch (error) {
    console.error('加载个人信息失败:', error)
  }
}

const loadMyArticles = async () => {
  if (!authStore.isLoggedIn) return

  loading.value = true
  try {
    const response = await request({
      url: '/api/users/profile',
      method: 'get'
    })
    
    userInfo.articleCount = response.articleCount || 0
    userInfo.followCount = response.followCount || 0
    userInfo.fansCount = response.fansCount || 0
    
    const userId = response.user?.id || authStore.user?.id
    if (!userId) {
      throw new Error('无法获取用户信息')
    }
    const page = await request({
      url: `/api/articles/user/${userId}`,
      method: 'get',
      params: { page: 0, size: 20 }
    })
    myArticles.value = Array.isArray(page?.list) ? page.list : []
  } catch (error) {
    console.error('加载我的文章失败:', error)
    myArticles.value = []
  } finally {
    loading.value = false
  }
}

const loadMyFavorites = async () => {
  if (!authStore.isLoggedIn) return

  loading.value = true
  try {
    const favorites = await getMyFavorites()
    myFavorites.value = Array.isArray(favorites) ? favorites : []
  } catch (error) {
    console.error('加载收藏失败:', error)
    myFavorites.value = []
  } finally {
    loading.value = false
  }
}

const saveProfile = async () => {
  saving.value = true
  try {
    await request({
      url: '/api/users/profile',
      method: 'put',
      data: {
        username: editForm.username,
        bio: editForm.bio,
        avatar: editForm.avatar
      }
    })

    userInfo.username = editForm.username
    userInfo.bio = editForm.bio
    userInfo.avatar = editForm.avatar

    authStore.user.username = editForm.username
    authStore.user.bio = editForm.bio
    authStore.user.avatar = editForm.avatar

    ElMessage.success('个人资料更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    saving.value = false
  }
}
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 40px 0;
}

.profile-header {
  display: flex;
  gap: 24px;
  align-items: flex-start;
  margin-bottom: 40px;
  padding-bottom: 32px;
  border-bottom: 1px solid var(--color-border);
}

.profile-avatar {
  position: relative;
  width: 120px;
  height: 120px;
  flex-shrink: 0;

  img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    object-fit: cover;
  }

  .avatar-edit-btn {
    position: absolute;
    bottom: 0;
    right: 0;
    width: 36px;
    height: 36px;
    background: var(--color-primary);
    color: white;
    border-radius: 50%;
    @include flex-center;
    transition: background var(--transition-fast);

    &:hover {
      background: var(--color-primary-dark);
    }
  }
}

.profile-info {
  flex: 1;
}

.profile-name {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 8px;
}

.profile-bio {
  font-size: 15px;
  color: var(--color-text-secondary);
  margin-bottom: 16px;
}

.profile-stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text);
}

.stat-label {
  font-size: 13px;
  color: var(--color-text-muted);
}

.profile-actions {
  flex-shrink: 0;
}

.profile-tabs {
  display: flex;
  gap: 32px;
  margin-bottom: 24px;
}

.tab-item {
  font-size: 16px;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding-bottom: 8px;
  border-bottom: 2px solid transparent;
  transition: all var(--transition-fast);

  &:hover {
    color: var(--color-text);
  }

  &.active {
    color: var(--color-primary);
    border-bottom-color: var(--color-primary);
  }
}

.tab-panel {
  min-height: 300px;
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  @include card;

  p {
    font-size: 16px;
    color: var(--color-text-secondary);
    margin-bottom: 16px;
  }
}

.settings-form {
  max-width: 500px;
}

.form-group {
  margin-bottom: 24px;
}

.avatar-select-group {
  .avatar-grid {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    gap: 12px;
    margin-top: 12px;
  }

  .avatar-option {
    position: relative;
    aspect-ratio: 1;
    border-radius: 50%;
    overflow: hidden;
    cursor: pointer;
    border: 3px solid transparent;
    transition: all 0.2s ease;

    &:hover {
      transform: scale(1.08);
      border-color: var(--color-primary);
    }

    &.selected {
      border-color: var(--color-primary);
      box-shadow: 0 0 0 2px rgba(74, 144, 217, 0.3);
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .avatar-check {
      position: absolute;
      bottom: 0;
      right: 0;
      width: 24px;
      height: 24px;
      background: var(--color-primary);
      color: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      font-weight: bold;
      border: 2px solid white;
    }
  }
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
  color: var(--color-text);
}

.input-field {
  width: 100%;
  height: 44px;
  padding: 0 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 14px;

  &:focus {
    border-color: var(--color-primary);
    box-shadow: 0 0 0 2px rgba(74, 144, 217, 0.1);
  }

  &:disabled {
    background: #f5f5f5;
    color: var(--color-text-muted);
  }
}

.textarea-field {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 14px;
  resize: vertical;

  &:focus {
    border-color: var(--color-primary);
    box-shadow: 0 0 0 2px rgba(74, 144, 217, 0.1);
  }
}

.form-actions {
  padding-top: 16px;
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .profile-stats {
    justify-content: center;
  }

  .profile-actions {
    width: 100%;
  }

  .avatar-select-group {
    .avatar-grid {
      grid-template-columns: repeat(4, 1fr);
    }
  }
}
</style>