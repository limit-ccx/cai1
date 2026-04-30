<template>
  <el-dialog
    v-model="visible"
    title=""
    width="900px"
    :close-on-click-modal="false"
    destroy-on-close
    class="ai-polish-dialog"
    align-center
  >
    <!-- 自定义标题 -->
    <template #header>
      <div class="dialog-header">
        <div class="dialog-title">
          <div class="title-icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L2 7l10 5 10-5-10-5z"/>
              <path d="M2 17l10 5 10-5"/>
              <path d="M2 12l10 5 10-5"/>
            </svg>
          </div>
          <div class="title-text">
            <h3>AI 润色结果</h3>
            <span class="title-meta">
              {{ modeLabel }} · {{ model }}
            </span>
          </div>
        </div>
      </div>
    </template>

    <!-- 对比区域 -->
    <div class="compare-container">
      <div class="compare-pane original-pane">
        <div class="pane-header">
          <div class="pane-badge original-badge">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
            </svg>
            原文
          </div>
          <span class="word-count">{{ originalContent.length }} 字</span>
        </div>
        <div class="pane-content">
          <div class="content-text">{{ originalContent }}</div>
        </div>
      </div>

      <div class="compare-divider">
        <div class="divider-line"></div>
        <div class="divider-arrow">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14M12 5l7 7-7 7"/>
          </svg>
        </div>
        <div class="divider-line"></div>
      </div>

      <div class="compare-pane polished-pane">
        <div class="pane-header">
          <div class="pane-badge polished-badge">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>
            润色后
          </div>
          <span class="word-count">{{ polishedContent.length }} 字</span>
        </div>
        <div class="pane-content">
          <div class="content-text polished-text">{{ polishedContent }}</div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <div class="footer-tip">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="16" x2="12" y2="12"/>
            <line x1="12" y1="8" x2="12.01" y2="8"/>
          </svg>
          采纳后将替换编辑器中的内容
        </div>
        <div class="footer-actions">
          <button type="button" class="footer-btn btn-reject" @click="handleReject">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
            放弃
          </button>
          <button type="button" class="footer-btn btn-accept" @click="handleAccept">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="20 6 9 17 4 12"/>
            </svg>
            采纳润色结果
          </button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  originalContent: {
    type: String,
    default: ''
  },
  polishedContent: {
    type: String,
    default: ''
  },
  mode: {
    type: String,
    default: 'polish'
  },
  model: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'accept', 'reject'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const MODE_LABELS = {
  polish:  '智能润色',
  formal:  '正式化',
  casual:  '轻松化',
  concise: '精简提炼',
  expand:  '扩写丰富'
}

const modeLabel = computed(() => MODE_LABELS[props.mode] || '智能润色')

const handleAccept = () => {
  emit('accept', props.polishedContent)
  visible.value = false
}

const handleReject = () => {
  emit('reject')
  visible.value = false
}
</script>

<style lang="scss" scoped>
.ai-polish-dialog {
  :deep(.el-dialog) {
    background: var(--color-card) !important;
    border: 1px solid var(--color-card-border) !important;
    border-radius: var(--radius-xl) !important;
    box-shadow: 0 24px 80px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(255, 255, 255, 0.05) !important;
    overflow: hidden;
  }

  :deep(.el-dialog__header) {
    padding: 0 !important;
    margin: 0 !important;
    border: none !important;
  }

  :deep(.el-dialog__body) {
    padding: 0 !important;
  }

  :deep(.el-dialog__footer) {
    padding: 0 !important;
    border: none !important;
  }
}

.dialog-header {
  padding: 24px 28px 16px;
  border-bottom: 1px solid var(--color-divider);
}

.dialog-title {
  display: flex;
  align-items: center;
  gap: 14px;
}

.title-icon {
  width: 44px;
  height: 44px;
  @include flex-center;
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
  border-radius: var(--radius-md);
  color: #fff;
  box-shadow: 0 4px 16px rgba(124, 107, 255, 0.35);
}

.title-text {
  h3 {
    font-size: 17px;
    font-weight: 700;
    color: var(--color-text);
    margin-bottom: 2px;
  }

  .title-meta {
    font-size: 12px;
    color: var(--color-text-muted);
    font-weight: 400;
  }
}

.compare-container {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 0;
  max-height: 480px;
}

.compare-pane {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.pane-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: var(--color-bg-elevated);
  border-bottom: 1px solid var(--color-divider);
  flex-shrink: 0;
}

.pane-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 600;

  &.original-badge {
    color: var(--color-text-muted);
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid var(--color-border);
  }

  &.polished-badge {
    color: var(--color-primary-light);
    background: rgba(124, 107, 255, 0.12);
    border: 1px solid rgba(124, 107, 255, 0.25);
  }
}

.word-count {
  font-size: 12px;
  color: var(--color-text-muted);
  font-weight: 500;
}

.pane-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: var(--color-bg);
}

.content-text {
  font-size: 14px;
  line-height: 1.85;
  white-space: pre-wrap;
  word-break: break-word;
  color: var(--color-text-secondary);

  &.polished-text {
    color: var(--color-text);
  }
}

.compare-divider {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 48px;
  background: var(--color-bg-elevated);
  border-left: 1px solid var(--color-divider);
  border-right: 1px solid var(--color-divider);
  flex-shrink: 0;
  padding: 16px 0;

  .divider-line {
    flex: 1;
    width: 1px;
    background: linear-gradient(to bottom, transparent, var(--color-border), transparent);
  }

  .divider-arrow {
    width: 32px;
    height: 32px;
    @include flex-center;
    background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
    border-radius: var(--radius-full);
    color: #fff;
    margin: 12px 0;
    box-shadow: 0 2px 12px rgba(124, 107, 255, 0.4);
  }
}

.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 28px;
  border-top: 1px solid var(--color-divider);
  background: var(--color-bg-elevated);
}

.footer-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--color-text-muted);

  svg {
    color: var(--color-text-muted);
  }
}

.footer-actions {
  display: flex;
  gap: 10px;
}

.footer-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 18px;
  height: 38px;
  border-radius: var(--radius-md);
  font-size: 13px;
  font-weight: 600;
  transition: all var(--transition-fast);
  cursor: pointer;
  border: none;

  &.btn-reject {
    background: transparent;
    color: var(--color-text-secondary);
    border: 1px solid var(--color-border);

    &:hover {
      background: rgba(255, 255, 255, 0.04);
      border-color: var(--color-border-hover);
      color: var(--color-text);
    }
  }

  &.btn-accept {
    background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
    color: #fff;
    box-shadow: 0 2px 12px rgba(124, 107, 255, 0.35);

    &:hover {
      box-shadow: 0 4px 20px rgba(124, 107, 255, 0.5);
      transform: translateY(-1px);
    }

    &:active {
      transform: translateY(0);
    }
  }
}
</style>
