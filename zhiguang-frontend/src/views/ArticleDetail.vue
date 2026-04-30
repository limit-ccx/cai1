<template>
  <div class="article-detail-page">
    <Header />

    <main class="main-content" v-loading="loading">
      <div class="container" v-if="article">
        <div class="article-layout">
          <article class="article-main">
            <header class="article-header">
              <div class="tags" v-if="article.tags?.length">
                <span v-for="tag in article.tags" :key="tag" class="tag" @click="goToTag(tag)">{{ tag }}</span>
              </div>
              <h1 class="article-title">{{ article.title }}</h1>
              <div class="article-meta">
                <div class="author-info">
                  <img :src="article.author?.avatar || defaultAvatar" alt="头像" class="author-avatar" />
                  <div class="author-details">
                    <span class="author-name">{{ article.author?.username }}</span>
                    <span class="meta-info">
                      {{ formatDate(article.createdAt) }} · {{ article.viewCount }} 阅读
                    </span>
                  </div>
                </div>
              </div>
            </header>

            <div class="article-cover" v-if="article.cover">
              <img :src="article.cover" alt="文章封面" />
            </div>

            <div class="article-content" v-html="article.content"></div>

            <footer class="article-footer">
              <div class="actions">
                <button class="action-btn" :class="{ active: article.isLiked }" @click="handleLike">
                  <svg width="20" height="20" viewBox="0 0 24 24" :fill="article.isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                  </svg>
                  <span>{{ article.likeCount || 0 }} 赞</span>
                </button>
                <button class="action-btn" :class="{ active: article.isFavorited }" @click="handleFavorite">
                  <svg width="20" height="20" viewBox="0 0 24 24" :fill="article.isFavorited ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                    <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                  </svg>
                  <span>{{ article.favoriteCount || 0 }} 收藏</span>
                </button>
                <button class="action-btn" @click="handleShare">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="18" cy="5" r="3"/>
                    <circle cx="6" cy="12" r="3"/>
                    <circle cx="18" cy="19" r="3"/>
                    <line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/>
                    <line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/>
                  </svg>
                  <span>分享</span>
                </button>
              </div>
            </footer>

            <section class="comments-section">
              <h3 class="section-title">评论 ({{ comments.length }})</h3>

              <div class="comment-form" v-if="authStore.isLoggedIn">
                <img :src="currentUserAvatar" alt="我的头像" class="comment-avatar" />
                <div class="comment-input-wrapper">
                  <textarea
                    v-model="commentContent"
                    placeholder="写下你的评论..."
                    class="comment-input"
                    rows="3"
                  ></textarea>
                  <button class="btn-primary" @click="submitComment" :disabled="!commentContent.trim() || submitting">
                    {{ submitting ? '发布中...' : '发布评论' }}
                  </button>
                </div>
              </div>

              <div v-else class="login-tip">
                <router-link to="/login" class="link">登录</router-link>后参与评论
              </div>

              <div class="comments-list" v-if="comments.length > 0">
                <div v-for="comment in comments" :key="comment.id" class="comment-item">
                  <img :src="comment.user?.avatar || defaultAvatar" alt="头像" class="comment-avatar" />
                  <div class="comment-body">
                    <div class="comment-header">
                      <span class="comment-author">{{ comment.user?.username }}</span>
                      <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                    </div>
                    <p class="comment-content">{{ comment.content }}</p>
                    <div class="comment-actions" v-if="authStore.isLoggedIn">
                      <button class="comment-action-btn" @click="handleReply(comment)">回复</button>
                      <button v-if="comment.user?.id === currentUserId" class="comment-action-btn danger" @click="deleteComment(comment.id)">删除</button>
                    </div>
                  </div>
                </div>
              </div>

              <div v-else class="no-comments">
                暂无评论，来抢沙发吧！
              </div>
            </section>
          </article>

          <aside class="article-sidebar">
            <div class="sidebar-card author-card">
              <h4 class="sidebar-title">作者</h4>
              <div class="author-info">
                <img :src="article.author?.avatar || defaultAvatar" alt="头像" class="author-avatar-lg" />
                <div class="author-details">
                  <span class="author-name">{{ article.author?.username }}</span>
                  <p class="author-bio">{{ article.author?.bio || '暂无简介' }}</p>
                </div>
              </div>
              <button 
                class="btn-secondary btn-full" 
                :disabled="!authStore.isLoggedIn || article.author?.id === currentUserId"
                @click="handleFollow">
                {{ isFollowing ? '已关注' : '+ 关注' }}
              </button>
            </div>

            <div class="sidebar-card">
              <h4 class="sidebar-title">相关推荐</h4>
              <ul class="related-articles">
                <li v-for="item in relatedArticles" :key="item.id">
                  <router-link :to="`/article/${item.id}`" class="related-link">
                    {{ item.title }}
                  </router-link>
                </li>
                <li v-if="relatedArticles.length === 0" class="no-related">
                  暂无相关推荐
                </li>
              </ul>
            </div>
          </aside>
        </div>
      </div>

      <div v-if="!loading && !article" class="not-found">
        <h2>文章不存在或已被删除</h2>
        <router-link to="/articles" class="btn-primary">返回文章列表</router-link>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const article = ref(null)
const comments = ref([])
const commentContent = ref('')
const submitting = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const relatedArticles = ref([])
const isFollowing = ref(false)
const isFavoriting = ref(false)

const currentUserAvatar = computed(() => authStore.user?.avatar || defaultAvatar)
const currentUserId = computed(() => authStore.user?.id)

onMounted(() => {
  loadArticle()
  loadComments()
})

const loadArticle = async () => {
  loading.value = true
  try {
    const data = await request({
      url: `/api/articles/${route.params.id}`,
      method: 'get'
    })
    article.value = data

    if (authStore.isLoggedIn) {
      try {
        const [likeStatus, followStatus] = await Promise.all([
          request({
            url: `/api/articles/${route.params.id}/like/status`,
            method: 'get'
          }),
          request({
            url: `/api/users/${data.author?.id}/follow/status`,
            method: 'get'
          })
        ])
        article.value.isLiked = likeStatus.isLiked
        article.value.isFavorited = likeStatus.isFavorited
        article.value.likeCount = likeStatus.likeCount
        article.value.favoriteCount = likeStatus.favoriteCount
        isFollowing.value = followStatus.isFollowing
      } catch (e) {
        console.error('获取状态失败', e)
      }
    }

    loadRelatedArticles()
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
  } finally {
    loading.value = false
  }
}

const loadRelatedArticles = async () => {
  try {
    const list = await request({
      url: '/api/articles/hot',
      method: 'get',
      params: { limit: 5 }
    })
    relatedArticles.value = (Array.isArray(list) ? list : []).filter(a => a.id !== article.value?.id).slice(0, 5)
  } catch (error) {
    console.error('加载相关推荐失败:', error)
  }
}

const loadComments = async () => {
  try {
    const list = await request({
      url: `/api/comments/article/${route.params.id}/all`,
      method: 'get'
    })
    comments.value = Array.isArray(list) ? list : []
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

const isLiking = ref(false)

const handleLike = async () => {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  if (isLiking.value) {
    ElMessage.warning('操作过于频繁，请稍后再试')
    return
  }
  isLiking.value = true
  try {
    const data = await request({
      url: `/api/articles/${article.value.id}/like`,
      method: 'post'
    })
    article.value.isLiked = data.isLiked
    article.value.likeCount = data.likeCount
  } catch (error) {
    const message = error.response?.data?.message || '操作失败'
    ElMessage.warning(message)
  } finally {
    setTimeout(() => {
      isLiking.value = false
    }, 1000)
  }
}

const handleFollow = async () => {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  if (isFollowing.value) {
    ElMessage.info('已关注该作者')
    return
  }
  try {
    const data = await request({
      url: `/api/users/${article.value.author?.id}/follow`,
      method: 'post'
    })
    isFollowing.value = data.isFollowing
    ElMessage.success('关注成功')
  } catch (error) {
    const message = error.response?.data?.message || '关注失败'
    ElMessage.warning(message)
  }
}

const handleFavorite = async () => {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  if (isFavoriting.value) {
    ElMessage.warning('操作过于频繁，请稍后再试')
    return
  }
  isFavoriting.value = true
  try {
    const data = await request({
      url: `/api/articles/${article.value.id}/favorite`,
      method: 'post'
    })
    article.value.isFavorited = data.isFavorited
    article.value.favoriteCount = data.favoriteCount
  } catch (error) {
    const message = error.response?.data?.message || '操作失败'
    ElMessage.warning(message)
  } finally {
    setTimeout(() => {
      isFavoriting.value = false
    }, 1000)
  }
}

const handleShare = () => {
  navigator.clipboard.writeText(window.location.href)
  ElMessage.success('链接已复制到剪贴板')
}

const submitComment = async () => {
  if (!commentContent.value.trim()) return

  submitting.value = true
  try {
    await request({
      url: '/api/comments',
      method: 'post',
      data: {
        articleId: article.value.id,
        content: commentContent.value
      }
    })
    ElMessage.success('评论发布成功')
    commentContent.value = ''
    loadComments()
  } catch (error) {
    ElMessage.error('评论发布失败')
  } finally {
    submitting.value = false
  }
}

const handleReply = (comment) => {
  commentContent.value = `@${comment.user?.username} `
  ElMessage.info('请在输入框回复')
}

const deleteComment = async (commentId) => {
  try {
    await request({
      url: `/api/comments/${commentId}`,
      method: 'delete'
    })
    ElMessage.success('评论已删除')
    loadComments()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const goToTag = (tag) => {
  router.push({ path: '/articles', query: { tag } })
}
</script>

<style lang="scss" scoped>
.article-detail-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 40px 0;
}

.article-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 32px;
}

.article-header {
  margin-bottom: 32px;
}

.tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.tag {
  padding: 4px 12px;
  font-size: 13px;
  color: var(--color-primary);
  background: rgba(74, 144, 217, 0.1);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--transition-fast);

  &:hover {
    background: var(--color-primary);
    color: white;
  }
}

.article-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-text);
  line-height: 1.3;
  margin-bottom: 20px;
}

.article-meta {
  @include flex-between;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text);
}

.meta-info {
  font-size: 13px;
  color: var(--color-text-muted);
}

.article-cover {
  margin-bottom: 32px;
  border-radius: var(--radius-lg);
  overflow: hidden;

  img {
    width: 100%;
    height: auto;
  }
}

.article-content {
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-text);

  :deep(p) {
    margin-bottom: 16px;
  }

  :deep(h2) {
    font-size: 24px;
    font-weight: 600;
    margin: 32px 0 16px;
    color: var(--color-text);
  }

  :deep(h3) {
    font-size: 18px;
    font-weight: 600;
    margin: 24px 0 12px;
    color: var(--color-text);
  }

  :deep(pre) {
    background: #f6f8fa;
    padding: 16px;
    border-radius: 8px;
    overflow-x: auto;
    margin: 16px 0;
  }

  :deep(code) {
    background: #f6f8fa;
    padding: 2px 6px;
    border-radius: 4px;
    font-family: 'Courier New', monospace;
  }
}

.article-footer {
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid var(--color-border);
}

.actions {
  display: flex;
  gap: 16px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  font-size: 14px;
  color: var(--color-text-muted);
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);

  &:hover {
    border-color: var(--color-accent);
    color: var(--color-accent);
  }

  &.active {
    color: white;
    background: var(--color-accent);
    border-color: var(--color-accent);
  }
}

.comments-section {
  margin-top: 48px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid var(--color-accent);
}

.comment-form {
  display: flex;
  gap: 16px;
  margin-bottom: 32px;
}

.login-tip {
  text-align: center;
  padding: 24px;
  background: var(--color-card);
  border-radius: var(--radius-md);
  margin-bottom: 24px;
  color: var(--color-text-muted);

  .link {
    color: var(--color-accent);
    font-weight: 500;
  }
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
}

.comment-input-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: flex-end;
}

.comment-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 14px;
  resize: vertical;
  background: var(--color-bg);
  color: var(--color-text);
  transition: border-color var(--transition-fast);

  &:focus {
    border-color: var(--color-accent);
  }

  &::placeholder {
    color: var(--color-text-muted);
  }
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 20px 0;
  border-bottom: 1px solid var(--color-border);
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.comment-author {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
}

.comment-time {
  font-size: 12px;
  color: var(--color-text-muted);
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: var(--color-text);
  margin-bottom: 8px;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.comment-action-btn {
  font-size: 13px;
  color: var(--color-text-muted);

  &:hover {
    color: var(--color-accent);
  }
}

.no-comments, .no-related {
  text-align: center;
  padding: 24px;
  color: var(--color-text-muted);
  font-size: 14px;
}

.article-sidebar {
  position: sticky;
  top: calc(var(--header-height) + 20px);
  height: fit-content;
}

.sidebar-card {
  @include card;
  padding: 20px;
  margin-bottom: 20px;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--color-text);
}

.author-card {
  .author-info {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    margin-bottom: 16px;
  }

  .author-avatar-lg {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    margin-bottom: 12px;
  }

  .author-bio {
    font-size: 13px;
    color: var(--color-text-secondary);
    margin-top: 4px;
  }
}

.btn-full {
  width: 100%;
}

.related-articles {
  li {
    padding: 12px 0;
    border-bottom: 1px solid var(--color-border);

    &:last-child {
      border-bottom: none;
    }
  }
}

.related-link {
  font-size: 14px;
  color: var(--color-text);
  line-height: 1.5;
  transition: color var(--transition-fast);

  &:hover {
    color: var(--color-primary);
  }
}

.not-found {
  text-align: center;
  padding: 80px 20px;

  h2 {
    font-size: 24px;
    color: var(--color-text);
    margin-bottom: 24px;
  }
}

@media (max-width: 1024px) {
  .article-layout {
    grid-template-columns: 1fr;
  }

  .article-sidebar {
    position: static;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .article-title {
    font-size: 24px;
  }

  .article-sidebar {
    grid-template-columns: 1fr;
  }
}
</style>