<script setup lang="ts">
import { useRouter } from 'vue-router'
import {
  Document,
  DataAnalysis,
  Reading,
  TrendCharts
} from '@element-plus/icons-vue'

const router = useRouter()

const features = [
  {
    icon: Document,
    title: '简历管理',
    description: '上传和管理你的简历，智能解析技能和经验',
    path: '/resume',
    color: '#409EFF'
  },
  {
    icon: DataAnalysis,
    title: '职业测评',
    description: 'MBTI、霍兰德等专业测评，发现你的职业倾向',
    path: '/career-assessment',
    color: '#67C23A'
  },
  {
    icon: Reading,
    title: '职业报告',
    description: 'AI生成个性化职业规划建议和发展路径',
    path: '/career-report',
    color: '#E6A23C'
  },
  {
    icon: TrendCharts,
    title: '学业分析',
    description: '可视化学习数据，追踪学业进度和成长',
    path: '/dashboard',
    color: '#F56C6C'
  }
]

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">
          <span class="gradient-text">Career Planner</span>
        </h1>
        <h2 class="hero-subtitle">智能职业规划与学业分析系统</h2>
        <p class="hero-description">
          基于AI技术，帮助你发现职业潜力，规划学业路径，实现职业理想
        </p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="navigateTo('/career-assessment')">
            开始测评
          </el-button>
          <el-button size="large" @click="navigateTo('/dashboard')">
            查看仪表盘
          </el-button>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <h3 class="section-title">核心功能</h3>
      <div class="features-grid">
        <el-card
          v-for="feature in features"
          :key="feature.path"
          class="feature-card"
          shadow="hover"
          @click="navigateTo(feature.path)"
        >
          <div class="feature-icon" :style="{ backgroundColor: feature.color + '20' }">
            <el-icon :size="32" :style="{ color: feature.color }">
              <component :is="feature.icon" />
            </el-icon>
          </div>
          <h4 class="feature-title">{{ feature.title }}</h4>
          <p class="feature-description">{{ feature.description }}</p>
        </el-card>
      </div>
    </section>

    <!-- Stats Section -->
    <section class="stats-section">
      <div class="stats-grid">
        <div class="stat-item">
          <span class="stat-number">10,000+</span>
          <span class="stat-label">用户使用</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">50+</span>
          <span class="stat-label">职业测评</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">98%</span>
          <span class="stat-label">满意度</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">24/7</span>
          <span class="stat-label">AI服务</span>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
}

.hero-section {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: 60px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
}

.hero-content {
  max-width: 800px;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
}

.gradient-text {
  background: linear-gradient(90deg, #fff, #e0e0ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 28px;
  font-weight: 500;
  margin-bottom: 16px;
  opacity: 0.95;
}

.hero-description {
  font-size: 16px;
  opacity: 0.85;
  margin-bottom: 32px;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.features-section {
  padding: 60px 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  text-align: center;
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 40px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.feature-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  text-align: center;
  padding: 20px;
}

.feature-card:hover {
  transform: translateY(-4px);
}

.feature-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.feature-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.feature-description {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.stats-section {
  padding: 60px 40px;
  background: #001529;
  color: white;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 40px;
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  color: #1890ff;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}
</style>
