<template>
  <div class="article-list-page">
    <Header />
    
    <main class="main-content">
      <div class="container">
        <div class="page-header">
          <h1 class="page-title">{{ pageTitle }}</h1>
          <router-link to="/create" class="btn-primary">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 5v14M5 12h14"/>
            </svg>
            写文章
          </router-link>
        </div>

        <div class="filters">
          <div class="filter-tags">
            <span
              class="filter-tag"
              :class="{ active: !currentTag }"
              @click="handleTagChange(null)"
            >全部</span>
            <span
              v-for="tag in tags"
              :key="tag"
              class="filter-tag"
              :class="{ active: currentTag === tag }"
              @click="handleTagChange(tag)"
            >{{ tag }}</span>
          </div>

          <div class="filter-sort">
            <el-select v-model="sortBy" placeholder="排序方式" size="default" @change="loadArticles">
              <el-option label="最新发布" value="createdAt" />
              <el-option label="最多浏览" value="viewCount" />
              <el-option label="最多点赞" value="likeCount" />
            </el-select>
          </div>
        </div>

        <div class="article-grid" v-loading="loading">
          <ArticleCard
            v-for="article in articles"
            :key="article.id"
            :article="article"
            @like="handleLike"
            @favorite="handleFavorite"
          />

          <div v-if="!loading && articles.length === 0" class="empty-state">
            <div class="empty-icon">📚</div>
            <p>暂无文章</p>
          </div>
        </div>

        <div class="pagination" v-if="totalPages > 1">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import ArticleCard from '@/components/ArticleCard.vue'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const articles = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentTag = ref(null)
const sortBy = ref('createdAt')

const tags = ['前端', '后端', 'JavaScript', 'Vue', 'React', 'Python', '架构', 'DevOps']

const pageTitle = computed(() => {
  if (currentTag.value) return `#${currentTag.value}`
  if (route.query.search) return `搜索: ${route.query.search}`
  return '全部文章'
})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

onMounted(() => {
  if (route.query.tag) {
    currentTag.value = route.query.tag
  }
  if (route.query.search) {
    currentTag.value = null
  }
  loadArticles()
})

watch(() => route.query, (query) => {
  if (query.tag) {
    currentTag.value = query.tag
  } else if (query.search) {
    currentTag.value = null
  } else {
    currentTag.value = null
  }
  currentPage.value = 1
  loadArticles()
}, { deep: true })

const loadArticles = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sortBy: sortBy.value
    }

    let response
    if (route.query.search) {
      params.search = route.query.search
      response = await request({
        url: '/api/articles',
        method: 'get',
        params
      })
    } else if (currentTag.value) {
      params.tag = currentTag.value
      response = await request({
        url: '/api/articles',
        method: 'get',
        params
      })
    } else {
      response = await request({
        url: '/api/articles',
        method: 'get',
        params
      })
    }

    articles.value = Array.isArray(response?.list) ? response.list : []
    total.value = typeof response?.total === 'number' ? response.total : articles.value.length
  } catch (error) {
    console.error('加载文章失败:', error)
    articles.value = []
    ElMessage.error('加载文章失败')
  } finally {
    loading.value = false
  }
}

const handleTagChange = (tag) => {
  currentTag.value = tag
  currentPage.value = 1
  if (tag) {
    router.push({ query: { tag } })
  } else {
    router.push({ query: {} })
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadArticles()
  window.scrollTo({ top: 0, behavior: 'smooth' })
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
    ElMessage.error('操作失败')
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
    ElMessage.error('操作失败')
  }
}
</script>

<style lang="scss" scoped>
.article-list-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 40px 0;
}

.page-header {
  @include flex-between;
  margin-bottom: 24px;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: var(--color-text);
  }
}

.filters {
  @include flex-between;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--color-border);
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-tag {
  padding: 6px 16px;
  font-size: 14px;
  color: var(--color-text-secondary);
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--transition-fast);

  &:hover {
    border-color: var(--color-primary);
    color: var(--color-primary);
  }

  &.active {
    background: var(--color-primary);
    border-color: var(--color-primary);
    color: white;
  }
}

.filter-sort {
  :deep(.el-select) {
    width: 140px;
  }
}

.article-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  @include card;

  .empty-icon {
    font-size: 64px;
    margin-bottom: 16px;
  }

  p {
    font-size: 16px;
    color: var(--color-text-secondary);
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .filters {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
}
</style>