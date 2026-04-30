<template>
  <article class="article-card" @click="goToDetail">
    <div class="card-header">
      <div class="author-info">
        <img :src="article.author?.avatar || defaultAvatar" alt="头像" class="author-avatar" />
        <div class="author-meta">
          <span class="author-name">{{ article.author?.username || '匿名用户' }}</span>
          <span class="publish-time">{{ formatTime(article.createdAt) }}</span>
        </div>
      </div>
      <div class="tags" v-if="article.tags?.length">
        <span v-for="tag in article.tags.slice(0, 2)" :key="tag" class="tag">{{ tag }}</span>
      </div>
    </div>

    <h3 class="card-title">{{ article.title }}</h3>
    <p class="card-summary">{{ article.summary || article.content?.substring(0, 120) + '...' }}</p>

    <div class="card-footer">
      <div class="stats">
        <span class="stat-item">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
            <circle cx="12" cy="12" r="3"/>
          </svg>
          {{ article.viewCount || 0 }}
        </span>
        <span class="stat-item" :class="{ active: article.isLiked }" @click.stop="handleLike">
          <svg width="16" height="16" viewBox="0 0 24 24" :fill="article.isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
          </svg>
          {{ article.likeCount || 0 }}
        </span>
        <span class="stat-item" :class="{ active: article.isFavorited }" @click.stop="handleFavorite">
          <svg width="16" height="16" viewBox="0 0 24 24" :fill="article.isFavorited ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
          </svg>
          {{ article.favoriteCount || 0 }}
        </span>
      </div>
      <span class="read-more">阅读全文 →</span>
    </div>
  </article>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  article: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['like', 'favorite'])

const router = useRouter()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const goToDetail = () => {
  router.push(`/article/${props.article.id}`)
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

const handleLike = () => {
  emit('like', props.article.id)
}

const handleFavorite = () => {
  emit('favorite', props.article.id)
}
</script>

<style lang="scss" scoped>
.article-card {
  @include card;
  padding: 20px;
  cursor: pointer;
  transition: all var(--transition-normal);

  &:hover {
    transform: translateY(-2px);
  }
}

.card-header {
  @include flex-between;
  margin-bottom: 12px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.author-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.author-name {
  font-size: 13px;
  color: var(--color-primary);
  font-weight: 500;
}

.publish-time {
  font-size: 12px;
  color: var(--color-text-muted);
}

.tags {
  display: flex;
  gap: 8px;
}

.tag {
  padding: 2px 8px;
  font-size: 12px;
  color: var(--color-primary);
  background: rgba(74, 144, 217, 0.1);
  border-radius: var(--radius-full);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 8px;
  line-height: 1.4;
  @include text-ellipsis(1);
}

.card-summary {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin-bottom: 16px;
  @include text-ellipsis(2);
}

.card-footer {
  @include flex-between;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
}

.stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--color-text-muted);
  transition: color var(--transition-fast);

  svg {
    flex-shrink: 0;
  }

  &.active {
    color: var(--color-accent);
  }

  &:not(.active):hover {
    color: var(--color-text-secondary);
    cursor: pointer;
  }
}

.read-more {
  font-size: 13px;
  color: var(--color-primary);
  opacity: 0;
  transform: translateX(-10px);
  transition: all var(--transition-fast);
}

.article-card:hover .read-more {
  opacity: 1;
  transform: translateX(0);
}
</style>