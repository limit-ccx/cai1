<template>
  <div class="about-page">
    <Header />

    <main class="main-content">
      <div class="container">
        <div class="about-header">
          <div class="brand-logo">
            <span class="logo-icon">知</span>
            <span class="logo-text">知光平台</span>
          </div>
          <h1 class="about-title">让知识发光发热</h1>
          <p class="about-desc">
            知光平台是一个专注于知识分享的社区，在这里你可以分享见解、记录思考、与志同道合的人交流。
          </p>
        </div>

        <div class="features-section">
          <h2 class="section-title">平台特点</h2>
          <div class="features-grid">
            <div class="feature-card">
              <div class="feature-icon">📚</div>
              <h3>海量知识资源</h3>
              <p>汇聚各领域专业内容，涵盖前端、后端、人工智能等多个技术方向</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">💡</div>
              <h3>深度思考分享</h3>
              <p>支持富文本编辑，让你的技术见解和经验得到充分表达</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">🤝</div>
              <h3>志同道合的伙伴</h3>
              <p>在这里找到与你兴趣相投的开发者，互相学习共同进步</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">🚀</div>
              <h3>快速成长路径</h3>
              <p>通过分享和交流，加速你的技术成长之旅</p>
            </div>
          </div>
        </div>

        <div class="tech-section">
          <h2 class="section-title">技术栈</h2>
          <div class="tech-list">
            <div class="tech-item">
              <h4>后端</h4>
              <p>Java 17 + Spring Boot 3.x + MySQL 8.0 + Redis</p>
            </div>
            <div class="tech-item">
              <h4>前端</h4>
              <p>Vue 3 + Vite + Element Plus + Pinia</p>
            </div>
            <div class="tech-item">
              <h4>安全</h4>
              <p>Spring Security + JWT Token</p>
            </div>
          </div>
        </div>

        <div class="contact-section">
          <h2 class="section-title">联系我们</h2>
          <p class="contact-desc">如果你有任何问题或建议，欢迎通过以下方式联系我们</p>
          <div class="contact-list">
            <div class="contact-item">
              <span class="contact-icon">📧</span>
              <span>contact@zhiguang.com</span>
            </div>
            <div class="contact-item">
              <span class="contact-icon">🌐</span>
              <span>www.zhiguang.com</span>
            </div>
          </div>
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
</script>

<style lang="scss" scoped>
.about-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 60px 0;
}

.about-header {
  text-align: center;
  margin-bottom: 80px;
}

.brand-logo {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 32px;

  .logo-icon {
    width: 56px;
    height: 56px;
    background: linear-gradient(135deg, var(--color-primary), #6B9BD2);
    color: white;
    border-radius: var(--radius-lg);
    @include flex-center;
    font-size: 28px;
    font-weight: 600;
  }

  .logo-text {
    font-size: 32px;
    font-weight: 600;
    color: var(--color-secondary);
  }
}

.about-title {
  font-size: 42px;
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: 16px;
}

.about-desc {
  font-size: 18px;
  color: var(--color-text-secondary);
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.6;
}

.features-section,
.tech-section,
.contact-section {
  margin-bottom: 60px;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text);
  text-align: center;
  margin-bottom: 40px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.feature-card {
  @include card;
  padding: 32px 24px;
  text-align: center;

  .feature-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }

  h3 {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 12px;
    color: var(--color-text);
  }

  p {
    font-size: 14px;
    color: var(--color-text-secondary);
    line-height: 1.6;
  }
}

.tech-list {
  max-width: 600px;
  margin: 0 auto;
}

.tech-item {
  padding: 20px 24px;
  background: var(--color-card);
  border-radius: var(--radius-md);
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm);

  h4 {
    font-size: 16px;
    font-weight: 600;
    color: var(--color-primary);
    margin-bottom: 8px;
  }

  p {
    font-size: 14px;
    color: var(--color-text-secondary);
  }
}

.contact-section {
  text-align: center;
}

.contact-desc {
  font-size: 16px;
  color: var(--color-text-secondary);
  margin-bottom: 24px;
}

.contact-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  color: var(--color-text);

  .contact-icon {
    font-size: 20px;
  }
}

@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .about-title {
    font-size: 32px;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }
}
</style>