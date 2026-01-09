<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ArrowRight, DataAnalysis, Document, Compass, Reading } from '@element-plus/icons-vue'

const router = useRouter()

const features = [
  {
    icon: Document,
    title: '智能简历管理',
    desc: 'AI 解析简历，智能提取技能与经验，支持多版本管理',
    path: '/resume',
    color: '#4F46E5',
    bg: '#EEF2FF'
  },
  {
    icon: Compass,
    title: '专业职业测评',
    desc: 'MBTI、霍兰德等权威测评，精准定位职业方向',
    path: '/career-assessment',
    color: '#059669',
    bg: '#ECFDF5'
  },
  {
    icon: Reading,
    title: 'AI 职业报告',
    desc: '生成个性化职业规划报告，包含行业分析与发展建议',
    path: '/career-report',
    color: '#D97706',
    bg: '#FFFBEB'
  },
  {
    icon: DataAnalysis,
    title: '学业数据分析',
    desc: '可视化学业数据，追踪成长轨迹，智能推荐提升方案',
    path: '/dashboard',
    color: '#DC2626',
    bg: '#FEF2F2'
  }
]

function navigateTo(path: string) {
  router.push(path)
}
</script>

<template>
  <div class="home-page">
    <!-- 背景装饰 -->
    <div class="bg-shape bg-shape-1"></div>
    <div class="bg-shape bg-shape-2"></div>

    <!-- Hero 区域 -->
    <section class="hero">
      <div class="hero-inner">
        <div class="hero-content">
          <div class="badge">🚀 V1.0 正式发布</div>
          <h1 class="hero-title">
            规划未来，<br />
            <span class="gradient-text">成就无限可能</span>
          </h1>
          <p class="hero-desc">
            Career Planner 利用先进的人工智能技术，为您提供全方位的职业规划与学业分析服务。数据驱动决策，让每一步成长都清晰可见。
          </p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="navigateTo('/career-assessment')">
              开始测评 <el-icon><ArrowRight /></el-icon>
            </el-button>
            <el-button size="large" @click="navigateTo('/dashboard')">
              查看演示
            </el-button>
          </div>
          <div class="hero-stats">
            <div class="stat-item">
              <strong>10w+</strong>
              <span>用户信赖</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <strong>98%</strong>
              <span>满意度</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <strong>AI</strong>
              <span>核心驱动</span>
            </div>
          </div>
        </div>

        <div class="hero-visual">
          <div class="visual-card main-card">
            <div class="card-dots">
              <span class="dot dot-red"></span>
              <span class="dot dot-yellow"></span>
              <span class="dot dot-green"></span>
            </div>
            <div class="card-chart">
              <div class="bar" style="height: 40%"></div>
              <div class="bar" style="height: 70%"></div>
              <div class="bar" style="height: 55%"></div>
              <div class="bar" style="height: 85%"></div>
            </div>
            <div class="card-info">
              <div class="info-avatar"></div>
              <div class="info-lines">
                <div class="line line-short"></div>
                <div class="line line-long"></div>
              </div>
            </div>
          </div>
          <div class="visual-card float-card float-card-1">
            <el-icon :size="20" color="#4F46E5"><DataAnalysis /></el-icon>
            <span>精准分析</span>
          </div>
          <div class="visual-card float-card float-card-2">
            <el-icon :size="20" color="#059669"><Compass /></el-icon>
            <span>智能推荐</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 功能区域 -->
    <section class="features">
      <div class="features-inner">
        <div class="section-header">
          <h2 class="section-title">核心功能</h2>
          <p class="section-desc">从校园到职场，我们提供全方位的职业成长支持</p>
        </div>
        <div class="features-grid">
          <div
            v-for="item in features"
            :key="item.path"
            class="feature-card"
            @click="navigateTo(item.path)"
          >
            <div class="feature-icon" :style="{ background: item.bg, color: item.color }">
              <el-icon :size="28"><component :is="item.icon" /></el-icon>
            </div>
            <h3 class="feature-title">{{ item.title }}</h3>
            <p class="feature-desc">{{ item.desc }}</p>
            <span class="feature-link" :style="{ color: item.color }">
              了解更多 <el-icon><ArrowRight /></el-icon>
            </span>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA 区域 -->
    <section class="cta">
      <div class="cta-inner">
        <div class="cta-box">
          <h2>准备好开启您的职业生涯了吗？</h2>
          <p>立即注册，免费获取您的第一份 AI 职业规划报告</p>
          <el-button type="primary" size="large" round @click="navigateTo('/login')">
            免费开始
          </el-button>
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="footer">
      <p>© 2024 Career Planner. All rights reserved.</p>
    </footer>
  </div>
</template>

<style scoped>
.home-page {
  width: 100%;
  min-height: 100vh;
  background: #f8fafc;
  position: relative;
  overflow-x: hidden;
}

/* 背景装饰 */
.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.5;
  z-index: 0;
}

.bg-shape-1 {
  width: 600px;
  height: 600px;
  background: #c7d2fe;
  top: -200px;
  right: -200px;
}

.bg-shape-2 {
  width: 500px;
  height: 500px;
  background: #fae8ff;
  bottom: 0;
  left: -200px;
}

/* Hero */
.hero {
  position: relative;
  z-index: 1;
  padding: 80px 0 60px;
}

.hero-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 5%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.badge {
  display: inline-block;
  padding: 8px 16px;
  background: #eef2ff;
  color: #4F46E5;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 24px;
}

.hero-title {
  font-size: 52px;
  font-weight: 800;
  line-height: 1.15;
  color: #0f172a;
  margin-bottom: 24px;
}

.gradient-text {
  background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 18px;
  line-height: 1.7;
  color: #64748b;
  margin-bottom: 40px;
  max-width: 540px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 48px;
}

.hero-stats {
  display: flex;
  align-items: center;
  gap: 32px;
  padding-top: 32px;
  border-top: 1px solid #e2e8f0;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-item strong {
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
}

.stat-item span {
  font-size: 14px;
  color: #94a3b8;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: #e2e8f0;
}

/* Hero Visual */
.hero-visual {
  position: relative;
  height: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.visual-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.1);
}

.main-card {
  width: 360px;
  height: 440px;
  padding: 24px;
  transform: rotate(-3deg);
  transition: transform 0.4s;
}

.main-card:hover {
  transform: rotate(0deg) scale(1.02);
}

.card-dots {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.dot-red { background: #fecaca; }
.dot-yellow { background: #fde68a; }
.dot-green { background: #bbf7d0; }

.card-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 200px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f1f5f9;
  margin-bottom: 24px;
}

.bar {
  width: 50px;
  background: linear-gradient(to top, #c7d2fe, #818cf8);
  border-radius: 8px 8px 0 0;
}

.card-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.info-avatar {
  width: 48px;
  height: 48px;
  background: #f1f5f9;
  border-radius: 12px;
}

.info-lines {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.line {
  height: 10px;
  background: #f1f5f9;
  border-radius: 5px;
}

.line-short { width: 50%; }
.line-long { width: 80%; }

.float-card {
  position: absolute;
  padding: 14px 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  font-size: 14px;
  color: #334155;
  animation: float 5s ease-in-out infinite;
}

.float-card-1 {
  top: 40px;
  right: 0;
}

.float-card-2 {
  bottom: 60px;
  left: 0;
  animation-delay: 2s;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

/* Features */
.features {
  position: relative;
  z-index: 1;
  padding: 80px 0;
}

.features-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 5%;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-title {
  font-size: 36px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 16px;
}

.section-desc {
  font-size: 18px;
  color: #64748b;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 32px;
}

.feature-card {
  background: #fff;
  padding: 32px;
  border-radius: 20px;
  border: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.3s;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px -12px rgba(0, 0, 0, 0.08);
}

.feature-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.feature-title {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 12px;
}

.feature-desc {
  font-size: 15px;
  line-height: 1.6;
  color: #64748b;
  margin-bottom: 20px;
}

.feature-link {
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* CTA */
.cta {
  position: relative;
  z-index: 1;
  padding: 80px 0;
}

.cta-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 5%;
}

.cta-box {
  background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
  border-radius: 24px;
  padding: 64px;
  text-align: center;
  color: #fff;
}

.cta-box h2 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 16px;
}

.cta-box p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 32px;
}

.cta-box .el-button {
  background: #fff;
  color: #4F46E5;
  border: none;
  font-weight: 600;
  padding: 12px 32px;
  height: auto;
}

/* Footer */
.footer {
  text-align: center;
  padding: 40px 0;
  color: #94a3b8;
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 1024px) {
  .hero-inner {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .hero-content {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .hero-desc {
    max-width: 100%;
  }

  .hero-stats {
    justify-content: center;
  }

  .hero-visual {
    display: none;
  }

  .hero-title {
    font-size: 40px;
  }
}
</style>
