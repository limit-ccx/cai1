<template>
  <div id="app">
    <router-view v-slot="{ Component }">
      <transition name="page" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
</script>

<style lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  min-height: 100vh;
  font-family: var(--font-family);
  background-color: var(--color-bg);
  color: var(--color-text);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ===== 页面过渡动画 ===== */
.page-enter-active {
  transition: opacity 0.4s cubic-bezier(0.4, 0, 0.2, 1),
              transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-leave-active {
  transition: opacity 0.25s cubic-bezier(0.4, 0, 0.2, 1),
              transform 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-enter-from {
  opacity: 0;
  transform: translateY(12px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

a {
  text-decoration: none;
  color: inherit;
}

ul, ol {
  list-style: none;
}

button {
  cursor: pointer;
  border: none;
  outline: none;
  background: none;
  font-family: inherit;
}

input, textarea {
  font-family: inherit;
  outline: none;
}

img {
  max-width: 100%;
  display: block;
}

/* ===== 选中文字高亮 ===== */
::selection {
  background: rgba(124, 107, 255, 0.3);
  color: var(--color-text);
}

/* ===== 全局微交互 ===== */

/* 按钮点击反馈 */
button:not(:disabled):active {
  transform: scale(0.97);
}

/* 链接下划线动画 */
a:not(.btn-primary):not(.btn-secondary):not(.btn-text) {
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    width: 0;
    height: 1px;
    background: linear-gradient(90deg, var(--color-primary), var(--color-secondary));
    transition: width var(--transition-normal);
  }

  &:hover::after {
    width: 100%;
  }
}

/* 输入框聚焦发光 */
input:focus,
textarea:focus {
  transition: all var(--transition-fast);
}

/* 卡片悬浮抬升效果 */
.card-hover {
  transition: all var(--transition-normal);

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
  }
}

/* 图片悬浮缩放 */
.img-hover {
  overflow: hidden;

  img {
    transition: transform var(--transition-slow);
  }

  &:hover img {
    transform: scale(1.05);
  }
}

/* 加载旋转动画 */
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.spin {
  animation: spin 0.8s linear infinite;
}

/* 脉冲发光 */
@keyframes pulse-glow {
  0%, 100% {
    box-shadow: 0 0 5px rgba(124, 107, 255, 0.2);
  }
  50% {
    box-shadow: 0 0 20px rgba(124, 107, 255, 0.5);
  }
}

.pulse-glow {
  animation: pulse-glow 2s ease-in-out infinite;
}

/* 渐变文字 */
.gradient-text {
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 玻璃拟态面板 */
.glass-panel {
  @include glass;
  border-radius: var(--radius-lg);
}

/* 减少动画偏好 */
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
</style>
