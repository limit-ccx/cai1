<template>
  <div class="home-page">
    <Header />
    
    <main class="main-content">
      <section class="hero-section">
        <div class="container">
          <div class="hero-content">
            <h1 class="hero-title">让知识发光发热</h1>
            <p class="hero-desc">知光平台是一个专注于知识分享的社区，在这里你可以分享见解、记录思考、与志同道合的人交流。</p>
            <div class="hero-actions">
              <router-link v-if="authStore.isLoggedIn" to="/create" class="btn-primary">开始创作</router-link>
              <router-link v-else to="/register" class="btn-primary">开始创作</router-link>
              <router-link to="/articles" class="btn-secondary">探索文章</router-link>
            </div>
          </div>
        </div>
      </section>

      <section class="content-section">
        <div class="container">
          <div class="content-layout">
            <aside class="sidebar-left">
              <div class="sidebar-card">
                <h3 class="sidebar-title">热门标签</h3>
                <div class="tag-cloud">
                  <span v-for="tag in popularTags" :key="tag.name" class="tag-item" @click="handleTagClick(tag.name)">
                    {{ tag.name }}
                  </span>
                </div>
              </div>
            </aside>

            <div class="main-area">
              <div class="section-header">
                <h2 class="section-title">推荐文章</h2>
                <router-link to="/articles" class="section-more">查看更多 →</router-link>
              </div>
              
              <div class="article-list" v-loading="loading">
                <ArticleCard
                  v-for="article in articles"
                  :key="article.id"
                  :article="article"
                  @like="handleLike"
                  @favorite="handleFavorite"
                />
                
                <div v-if="!loading && articles.length === 0" class="empty-state">
                  <div class="empty-icon">📝</div>
                  <p>还没有文章，成为第一个分享者吧！</p>
                  <router-link to="/create" class="btn-primary">写文章</router-link>
                </div>
              </div>
            </div>

            <aside class="sidebar-right">
              <div v-if="authStore.isLoggedIn && currentUser" class="sidebar-card author-card">
                <div class="author-avatar">
                  <img :src="currentUser?.avatar || defaultAvatar" alt="用户头像" />
                </div>
                <h3 class="author-name">{{ currentUser?.username }}</h3>
                <p class="author-bio">{{ currentUser?.bio || '这个人很懒，什么都没有写' }}</p>
                <div class="author-stats">
                  <div class="stat">
                    <span class="stat-value">{{ userStats.articleCount }}</span>
                    <span class="stat-label">文章</span>
                  </div>
                  <div class="stat">
                    <span class="stat-value">{{ userStats.followCount }}</span>
                    <span class="stat-label">关注</span>
                  </div>
                  <div class="stat">
                    <span class="stat-value">{{ userStats.fansCount }}</span>
                    <span class="stat-label">粉丝</span>
                  </div>
                </div>
                <router-link to="/create" class="btn-primary btn-full">写文章</router-link>
              </div>

              <div class="sidebar-card">
                <h3 class="sidebar-title">热门文章</h3>
                <ul class="hot-articles">
                  <li v-for="(item, index) in hotArticles" :key="item.id" class="hot-item">
                    <span class="hot-number">{{ index + 1 }}</span>
                    <router-link :to="`/article/${item.id}`" class="hot-title">{{ item.title }}</router-link>
                  </li>
                </ul>
              </div>
            </aside>
          </div>
        </div>
      </section>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import ArticleCard from '@/components/ArticleCard.vue'
import { useAuthStore } from '@/store/auth'
import request from '@/utils/request'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const articles = ref([])
const hotArticles = ref([])
const currentUser = computed(() => authStore.user)
const defaultAvatar = 'https://www.w3schools.com/w3images/avatar2.png'

const userStats = ref({
  articleCount: 0,
  followCount: 0,
  fansCount: 0
})

const popularTags = ref([
  { name: '前端' },
  { name: '后端' },
  { name: 'JavaScript' },
  { name: 'Vue' },
  { name: 'React' },
  { name: 'Python' },
  { name: '架构' },
  { name: 'DevOps' }
])

onMounted(() => {
  loadArticles()
  loadHotArticles()
  if (authStore.isLoggedIn) {
    loadUserProfile()
  }
})

const loadArticles = async () => {
  loading.value = true
  try {
    const page = await request({
      url: '/api/articles',
      method: 'get',
      params: { page: 0, size: 10 }
    })
    articles.value = Array.isArray(page?.list) ? page.list : []
  } catch (error) {
    console.error('加载文章失败:', error)
    articles.value = []
  } finally {
    loading.value = false
  }
}

const loadHotArticles = async () => {
  try {
    const list = await request({
      url: '/api/articles/hot',
      method: 'get',
      params: { limit: 5 }
    })
    hotArticles.value = Array.isArray(list) ? list : []
  } catch (error) {
    console.error('加载热门文章失败:', error)
    hotArticles.value = []
  }
}

const loadUserProfile = async () => {
  try {
    if (!authStore.user) {
      await authStore.fetchProfile()
    }
    const response = await request({
      url: '/api/users/profile',
      method: 'get'
    })
    userStats.value = {
      articleCount: response.articleCount || 0,
      followCount: response.followCount || 0,
      fansCount: response.fansCount || 0
    }
    if (response.user) {
      authStore.user = { ...authStore.user, ...response.user }
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

const handleTagClick = (tag) => {
  router.push({ path: '/articles', query: { tag } })
}

const handleLike = async (id) => {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  try {
    const data = await request({
      url: `/api/articles/${id}/like`,
      method: 'post'
    })
    const article = articles.value.find(a => a.id === id)
    if (article) {
      article.isLiked = data.isLiked
      article.likeCount = data.likeCount
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const handleFavorite = async (id) => {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  try {
    const data = await request({
      url: `/api/articles/${id}/favorite`,
      method: 'post'
    })
    const article = articles.value.find(a => a.id === id)
    if (article) {
      article.isFavorited = data.isFavorited
      article.favoriteCount = data.favoriteCount
    }
  } catch (error) {
    console.error('收藏失败:', error)
  }
}
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.hero-section {
  background: linear-gradient(135deg, var(--color-secondary) 0%, var(--color-primary-dark) 50%, #2D3A4D 100%);
  padding: 80px 0;
  color: white;
}

.hero-content {
  max-width: 600px;
  margin: 0 auto;
  text-align: center;
}

.hero-title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
  line-height: 1.2;
}

.hero-desc {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 32px;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.content-section {
  flex: 1;
  padding: 40px 0;
}

.content-layout {
  display: grid;
  grid-template-columns: 240px 1fr 280px;
  gap: 24px;
}

.sidebar-card {
  @include card;
  padding: 20px;
  margin-bottom: 20px;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border);
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  background: transparent;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-full);
  font-size: 13px;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all var(--transition-fast);

  &:hover {
    border-color: var(--color-accent);
    color: var(--color-accent);
    background: rgba(76, 110, 245, 0.1);
  }
}

.section-header {
  @include flex-between;
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text);
}

.section-more {
  font-size: 14px;
  color: var(--color-primary);

  &:hover {
    text-decoration: underline;
  }
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

  .empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }

  p {
    font-size: 16px;
    color: var(--color-text-secondary);
    margin-bottom: 20px;
  }
}

.author-card {
  text-align: center;
  border: 1px solid var(--color-border);

  .author-avatar {
    width: 80px;
    height: 80px;
    margin: 0 auto 12px;
    border-radius: 50%;
    overflow: hidden;
    border: 3px solid var(--color-accent);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .author-name {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 4px;
  }

  .author-bio {
    font-size: 14px;
    color: var(--color-text-muted);
    margin-bottom: 16px;
  }

  .author-stats {
    display: flex;
    justify-content: center;
    gap: 24px;
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid var(--color-border);

    .stat {
      text-align: center;
    }

    .stat-value {
      display: block;
      font-size: 18px;
      font-weight: 600;
      color: var(--color-accent);
    }

    .stat-label {
      font-size: 12px;
      color: var(--color-text-muted);
    }
  }
}

.btn-full {
  width: 100%;
}

.hot-articles {
  .hot-item {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    padding: 12px 0;
    border-bottom: 1px solid var(--color-border);

    &:last-child {
      border-bottom: none;
    }
  }

  .hot-number {
    flex-shrink: 0;
    width: 20px;
    height: 20px;
    background: var(--color-primary);
    color: white;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 600;
    @include flex-center;
  }

  .hot-title {
    font-size: 14px;
    color: var(--color-text);
    line-height: 1.4;
    transition: color var(--transition-fast);

    &:hover {
      color: var(--color-primary);
    }
  }
}

@media (max-width: 1199px) {
  .content-layout {
    grid-template-columns: 200px 1fr;
  }

  .sidebar-right {
    display: none;
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 48px 0;
  }

  .hero-title {
    font-size: 28px;
  }

  .hero-desc {
    font-size: 16px;
  }

  .content-layout {
    grid-template-columns: 1fr;
  }

  .sidebar-left {
    order: 2;
  }

  .main-area {
    order: 1;
  }
}
</style>