<template>
  <footer class="footer">
    <div class="footer-container container">
      <div class="footer-main">
        <div class="footer-brand">
          <div class="footer-logo">
            <span class="logo-icon">知</span>
            <span class="logo-text">知光平台</span>
          </div>
          <p class="footer-desc">一个专注于知识分享与交流的平台，让知识发光发热。</p>
        </div>

        <div class="footer-links">
          <div class="footer-column">
            <h4 class="footer-title">关于</h4>
            <ul class="footer-list">
              <li><a href="#">关于我们</a></li>
              <li><a href="#">加入团队</a></li>
              <li><a href="#">联系方式</a></li>
            </ul>
          </div>

          <div class="footer-column">
            <h4 class="footer-title">帮助</h4>
            <ul class="footer-list">
              <li><a href="#">使用指南</a></li>
              <li><a href="#">常见问题</a></li>
              <li><a href="#">建议反馈</a></li>
            </ul>
          </div>

          <div class="footer-column">
            <h4 class="footer-title">关注</h4>
            <ul class="footer-list">
              <li><a href="#">微信公众号</a></li>
              <li><a href="#">微博</a></li>
              <li><a href="#">知乎</a></li>
            </ul>
          </div>
        </div>
      </div>

      <div class="footer-bottom">
        <p class="copyright">© 2024 知光平台. All rights reserved.</p>
        <div class="footer-legal">
          <a href="#">隐私政策</a>
          <a href="#">用户协议</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>

<style lang="scss" scoped>
.footer {
  background: var(--color-secondary);
  color: #A0AEC0;
  margin-top: auto;
}

.footer-container {
  padding-top: 48px;
  padding-bottom: 24px;
}

.footer-main {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 48px;
  padding-bottom: 32px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-brand {
  .footer-logo {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 16px;

    .logo-icon {
      width: 36px;
      height: 36px;
      background: linear-gradient(135deg, var(--color-primary), #6B9BD2);
      color: white;
      border-radius: var(--radius-md);
      @include flex-center;
      font-size: 18px;
      font-weight: 600;
    }

    .logo-text {
      font-size: 20px;
      font-weight: 600;
      color: white;
    }
  }

  .footer-desc {
    font-size: 14px;
    line-height: 1.6;
    color: #A0AEC0;
  }
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.footer-title {
  font-size: 14px;
  font-weight: 600;
  color: white;
  margin-bottom: 16px;
}

.footer-list {
  li {
    margin-bottom: 8px;
  }

  a {
    font-size: 14px;
    color: #A0AEC0;
    transition: color var(--transition-fast);

    &:hover {
      color: white;
    }
  }
}

.footer-bottom {
  @include flex-between;
  padding-top: 24px;
  font-size: 13px;
}

.copyright {
  color: #718096;
}

.footer-legal {
  display: flex;
  gap: 24px;

  a {
    color: #718096;
    transition: color var(--transition-fast);

    &:hover {
      color: white;
    }
  }
}

@media (max-width: 768px) {
  .footer-main {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .footer-links {
    grid-template-columns: repeat(2, 1fr);
  }

  .footer-bottom {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
}
</style>