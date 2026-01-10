<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, BarChart, LineChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { getDashboardOverview, getSkillDistribution, getLearningTrend } from '@/api/dashboard'
import type { DashboardOverview, CategoryData, TrendData } from '@/api/dashboard'
import BaseCard from '@/components/ui/BaseCard.vue'
import PageHeader from '@/components/ui/PageHeader.vue'

const router = useRouter()

use([
  CanvasRenderer,
  PieChart,
  BarChart,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const loading = ref(true)
const overview = ref<DashboardOverview | null>(null)
const skillData = ref<CategoryData[]>([])
const trendData = ref<TrendData[]>([])

// --- Data Fetching ---
onMounted(async () => {
  loading.value = true
  try {
    // Parallel fetching for performance
    const [overviewRes, skillRes, trendRes] = await Promise.all([
      getDashboardOverview(),
      getSkillDistribution(),
      getLearningTrend()
    ])

    if (overviewRes.success) overview.value = overviewRes.data
    if (skillRes.success) skillData.value = skillRes.data
    if (trendRes.success) trendData.value = trendRes.data

    // Mock data if empty (remove in prod)
    if (!overview.value) {
       overview.value = {
          resumeCompleteness: 85,
          totalSkills: 12,
          expertSkills: 4,
          educationCount: 1,
          workExperienceCount: 2,
          assessmentCount: 3,
          learningRecordCount: 10,
          totalLearningHours: 50,
          weeklyLearningHours: 8,
          reportCount: 2
       }
    }
    if (skillData.value.length === 0) {
      skillData.value = [
        { name: 'Java', value: 40 },
        { name: 'Spring Boot', value: 30 },
        { name: 'Vue', value: 20 },
        { name: 'MySQL', value: 10 }
      ]
    }
    if (trendData.value.length === 0) {
      trendData.value = [
        { date: 'Mon', value: 65 },
        { date: 'Tue', value: 70 },
        { date: 'Wed', value: 75 },
        { date: 'Thu', value: 72 },
        { date: 'Fri', value: 80 },
        { date: 'Sat', value: 85 },
        { date: 'Sun', value: 90 }
      ]
    }

  } catch (error) {
    console.error('Failed to load dashboard data', error)
  } finally {
    loading.value = false
  }
})

// --- Stats Formatting ---
const stats = computed(() => {
  if (!overview.value) {
    return [
      { label: 'Resume Score', value: '-', suffix: '%', icon: 'ScoreIcon', color: 'text-primary' },
      { label: 'Skills Identified', value: '-', suffix: '', icon: 'SkillIcon', color: 'text-warning' },
      { label: 'Expert Level', value: '-', suffix: '', icon: 'ExpertIcon', color: 'text-success' },
      { label: 'Experience', value: '-', suffix: 'yrs', icon: 'ExpIcon', color: 'text-info' }
    ]
  }
  return [
    { label: 'Resume Score', value: overview.value.resumeCompleteness, suffix: '%', icon: 'ScoreIcon', color: 'text-primary' },
    { label: 'Skills Identified', value: overview.value.totalSkills, suffix: '', icon: 'SkillIcon', color: 'text-warning' },
    { label: 'Expert Level', value: overview.value.expertSkills, suffix: '', icon: 'ExpertIcon', color: 'text-success' },
    { label: 'Experience', value: overview.value.workExperienceCount, suffix: 'yrs', icon: 'ExpIcon', color: 'text-info' }
  ]
})

// --- ECharts Options (Design System Alignment) ---
const chartPalette = ['#6366f1', '#8b5cf6', '#ec4899', '#f43f5e', '#10b981', '#f59e0b']

const skillOption = computed(() => ({
  color: chartPalette,
  tooltip: { 
    trigger: 'item',
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    borderColor: '#e2e8f0',
    textStyle: { color: '#1e293b' }
  },
  legend: {
    orient: 'vertical',
    right: 0,
    top: 'center',
    icon: 'circle',
    textStyle: { color: '#64748b' }
  },
  series: [
    {
      name: 'Skill Distribution',
      type: 'pie',
      radius: ['50%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 16, fontWeight: 'bold' }
      },
      data: skillData.value
    }
  ]
}))

const trendOption = computed(() => ({
  color: ['#6366f1'],
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    borderColor: '#e2e8f0',
    textStyle: { color: '#1e293b' }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: trendData.value.map(t => t.date),
    axisLine: { show: false },
    axisTick: { show: false },
    axisLabel: { color: '#94a3b8' }
  },
  yAxis: {
    type: 'value',
    splitLine: { 
      lineStyle: { type: 'dashed', color: '#f1f5f9' } 
    },
    axisLabel: { color: '#94a3b8' }
  },
  series: [
    {
      name: 'Growth Score',
      type: 'line',
      smooth: true,
      lineStyle: { width: 3 },
      showSymbol: false,
      areaStyle: {
        opacity: 0.2,
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: '#6366f1' },
            { offset: 1, color: 'rgba(99, 102, 241, 0.05)' }
          ]
        }
      },
      data: trendData.value.map(t => t.value)
    }
  ]
}))

// --- Actions ---
const handleUploadResume = () => {
  router.push('/resume')
}
</script>

<template>
  <div class="dashboard-page">
    <!-- Hero Section with Greeting -->
    <div class="dashboard-hero">
      <div class="hero-content">
        <div class="greeting-section">
          <span class="greeting-badge">
            <span class="badge-dot"></span>
            Dashboard Overview
          </span>
          <h1 class="hero-title">Welcome back! 👋</h1>
          <p class="hero-subtitle">Track your career growth and skill development progress.</p>
        </div>
        <div class="hero-actions">
          <button class="btn-primary" @click="handleUploadResume">
            <span class="btn-icon">📄</span>
            Upload New Resume
          </button>
          <button class="btn-secondary" @click="router.push('/ai-chat')">
            <span class="btn-icon">💬</span>
            AI Advisor
          </button>
        </div>
      </div>
      <div class="hero-visual">
        <div class="floating-card card-1">
          <span class="card-emoji">🎯</span>
          <span class="card-label">Skills</span>
        </div>
        <div class="floating-card card-2">
          <span class="card-emoji">📈</span>
          <span class="card-label">Growth</span>
        </div>
        <div class="floating-card card-3">
          <span class="card-emoji">🏆</span>
          <span class="card-label">Goals</span>
        </div>
      </div>
    </div>

    <!-- KPI Cards -->
    <div class="kpi-grid">
      <div 
        v-for="(item, index) in stats" 
        :key="index"
        class="kpi-card"
        :class="{ 'loading-skeleton': loading }"
        :style="{ '--delay': index * 0.1 + 's' }"
      >
        <div class="kpi-header">
          <div class="kpi-icon-wrapper" :class="'color-' + index">
            <span class="kpi-emoji">{{ ['📊', '⚡', '🌟', '💼'][index] }}</span>
          </div>
          <div class="kpi-trend up">
            <span class="trend-icon">↑</span>
            <span class="trend-value">12%</span>
          </div>
        </div>
        <div class="kpi-body">
          <h4 class="kpi-value">
            {{ item.value }}<span class="kpi-suffix">{{ item.suffix }}</span>
          </h4>
          <p class="kpi-label">{{ item.label }}</p>
        </div>
        <div class="kpi-footer">
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: (item.value || 0) + '%' }"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="charts-grid">
      <!-- Skill Distribution -->
      <BaseCard class="chart-card">
        <template #header>
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <span class="chart-icon">🎨</span>
              <div>
                <h3 class="chart-title">Technical Skill Breakdown</h3>
                <p class="chart-desc">Distribution of your core competencies</p>
              </div>
            </div>
            <div class="chart-actions">
              <button class="chart-btn active">Week</button>
              <button class="chart-btn">Month</button>
              <button class="chart-btn">Year</button>
            </div>
          </div>
        </template>
        <div class="chart-container">
          <VChart class="chart" :option="skillOption" autoresize />
        </div>
      </BaseCard>

      <!-- Learning Trend -->
      <BaseCard class="chart-card">
        <template #header>
          <div class="chart-header">
            <div class="chart-title-wrapper">
              <span class="chart-icon">📈</span>
              <div>
                <h3 class="chart-title">Capability Growth Trend</h3>
                <p class="chart-desc">Your progress over time</p>
              </div>
            </div>
          </div>
        </template>
        <div class="chart-container">
          <VChart class="chart" :option="trendOption" autoresize />
        </div>
      </BaseCard>
    </div>
    
    <!-- Activity & Recommendations -->
    <div class="bottom-grid">
      <!-- Recent Activity -->
      <BaseCard class="activity-card">
        <template #header>
          <div class="section-header">
            <span class="section-icon">⚡</span>
            <h3>Recent Activity</h3>
          </div>
        </template>
        <div class="activity-list">
          <div class="activity-item">
            <div class="activity-icon blue">📄</div>
            <div class="activity-content">
              <span class="activity-title">Resume uploaded</span>
              <span class="activity-time">2 hours ago</span>
            </div>
          </div>
          <div class="activity-item">
            <div class="activity-icon green">✅</div>
            <div class="activity-content">
              <span class="activity-title">Profile completed</span>
              <span class="activity-time">Yesterday</span>
            </div>
          </div>
          <div class="activity-item">
            <div class="activity-icon purple">💬</div>
            <div class="activity-content">
              <span class="activity-title">AI consultation</span>
              <span class="activity-time">3 days ago</span>
            </div>
          </div>
        </div>
      </BaseCard>

      <!-- Recommendations -->
      <BaseCard class="recommendation-card">
        <template #header>
          <div class="section-header">
            <span class="section-icon">💡</span>
            <h3>Recommended Actions</h3>
          </div>
        </template>
        <div class="recommendation-list">
          <div class="rec-item" @click="router.push('/profile')">
            <div class="rec-icon">👤</div>
            <div class="rec-content">
              <span class="rec-title">Complete your profile</span>
              <span class="rec-desc">Add more details for better AI recommendations</span>
            </div>
            <span class="rec-arrow">→</span>
          </div>
          <div class="rec-item" @click="router.push('/ai-chat')">
            <div class="rec-icon">🤖</div>
            <div class="rec-content">
              <span class="rec-title">Get career advice</span>
              <span class="rec-desc">Chat with AI for personalized guidance</span>
            </div>
            <span class="rec-arrow">→</span>
          </div>
        </div>
      </BaseCard>
    </div>
  </div>
</template>

<style scoped lang="scss">
.dashboard-page {
  max-width: 1600px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Hero Section */
.dashboard-hero {
  background: var(--gradient-primary);
  border-radius: var(--radius-2xl);
  padding: 40px;
  margin-bottom: 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
  box-shadow: var(--shadow-colored);
  
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -20%;
    width: 60%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
    pointer-events: none;
  }
}

.hero-content {
  position: relative;
  z-index: 1;
}

.greeting-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
  padding: 8px 16px;
  border-radius: var(--radius-full);
  font-size: 13px;
  font-weight: 500;
  color: rgba(255,255,255,0.9);
  margin-bottom: 16px;
}

.badge-dot {
  width: 8px;
  height: 8px;
  background: #10b981;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.2); }
}

.hero-title {
  font-size: 32px;
  font-weight: 800;
  color: white;
  margin: 0 0 8px;
  letter-spacing: -0.02em;
}

.hero-subtitle {
  font-size: 16px;
  color: rgba(255,255,255,0.8);
  margin: 0 0 24px;
}

.hero-actions {
  display: flex;
  gap: 12px;
}

.btn-primary, .btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: var(--radius-lg);
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all var(--transition-normal);
  border: none;
}

.btn-primary {
  background: white;
  color: var(--color-primary-600);
  box-shadow: 0 4px 14px rgba(0,0,0,0.1);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(0,0,0,0.15);
  }
}

.btn-secondary {
  background: rgba(255,255,255,0.15);
  color: white;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.2);
  
  &:hover {
    background: rgba(255,255,255,0.25);
  }
}

.btn-icon {
  font-size: 16px;
}

.hero-visual {
  position: relative;
  width: 200px;
  height: 150px;
}

.floating-card {
  position: absolute;
  background: white;
  border-radius: var(--radius-lg);
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: var(--shadow-lg);
  animation: float 3s ease-in-out infinite;
  
  &.card-1 {
    top: 0;
    right: 20px;
    animation-delay: 0s;
  }
  
  &.card-2 {
    top: 50px;
    right: 80px;
    animation-delay: 0.5s;
  }
  
  &.card-3 {
    top: 100px;
    right: 0;
    animation-delay: 1s;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.card-emoji {
  font-size: 20px;
}

.card-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-neutral-700);
}

/* KPI Grid */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 32px;
  
  @media (max-width: 1200px) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (max-width: 640px) {
    grid-template-columns: 1fr;
  }
}

.kpi-card {
  background: white;
  border-radius: var(--radius-xl);
  padding: 24px;
  border: 1px solid var(--color-neutral-100);
  transition: all var(--transition-normal);
  animation: fadeInUp 0.5s ease-out backwards;
  animation-delay: var(--delay);
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
    border-color: transparent;
  }
}

.kpi-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.kpi-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  
  &.color-0 { background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%); }
  &.color-1 { background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%); }
  &.color-2 { background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%); }
  &.color-3 { background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%); }
}

.kpi-emoji {
  font-size: 22px;
}

.kpi-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: var(--radius-full);
  
  &.up {
    background: var(--color-success-light);
    color: var(--color-success-dark);
  }
}

.kpi-body {
  margin-bottom: 16px;
}

.kpi-value {
  font-size: 36px;
  font-weight: 800;
  color: var(--color-neutral-900);
  margin: 0;
  line-height: 1;
  letter-spacing: -0.02em;
}

.kpi-suffix {
  font-size: 16px;
  font-weight: 500;
  color: var(--color-neutral-400);
  margin-left: 4px;
}

.kpi-label {
  font-size: 14px;
  color: var(--color-neutral-500);
  margin: 8px 0 0;
}

.kpi-footer {
  margin-top: auto;
}

.progress-bar {
  height: 6px;
  background: var(--color-neutral-100);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: var(--gradient-primary);
  border-radius: var(--radius-full);
  transition: width 1s ease-out;
}

/* Charts Grid */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 32px;
  
  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
  }
}

.chart-card {
  :deep(.card-header) {
    padding-bottom: 0;
  }
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.chart-title-wrapper {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.chart-icon {
  font-size: 24px;
  background: var(--color-neutral-100);
  width: 44px;
  height: 44px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-neutral-900);
  margin: 0;
}

.chart-desc {
  font-size: 13px;
  color: var(--color-neutral-500);
  margin: 4px 0 0;
}

.chart-actions {
  display: flex;
  gap: 4px;
  background: var(--color-neutral-100);
  padding: 4px;
  border-radius: var(--radius-md);
}

.chart-btn {
  padding: 6px 12px;
  border: none;
  background: transparent;
  border-radius: var(--radius-sm);
  font-size: 13px;
  font-weight: 500;
  color: var(--color-neutral-500);
  cursor: pointer;
  transition: all var(--transition-fast);
  
  &:hover {
    color: var(--color-neutral-700);
  }
  
  &.active {
    background: white;
    color: var(--color-neutral-900);
    box-shadow: var(--shadow-sm);
  }
}

.chart-container {
  height: 300px;
  width: 100%;
  padding-top: 16px;
}

.chart {
  height: 100%;
  width: 100%;
}

/* Bottom Grid */
.bottom-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  
  @media (max-width: 900px) {
    grid-template-columns: 1fr;
  }
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  
  h3 {
    font-size: 16px;
    font-weight: 700;
    color: var(--color-neutral-900);
    margin: 0;
  }
}

.section-icon {
  font-size: 20px;
}

/* Activity List */
.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: var(--radius-lg);
  background: var(--color-neutral-50);
  transition: all var(--transition-fast);
  
  &:hover {
    background: var(--color-neutral-100);
  }
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  
  &.blue { background: var(--color-info-light); }
  &.green { background: var(--color-success-light); }
  &.purple { background: #ede9fe; }
}

.activity-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.activity-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-neutral-800);
}

.activity-time {
  font-size: 12px;
  color: var(--color-neutral-500);
}

/* Recommendation List */
.recommendation-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rec-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-neutral-200);
  cursor: pointer;
  transition: all var(--transition-normal);
  
  &:hover {
    border-color: var(--color-primary-200);
    background: var(--color-primary-50);
    transform: translateX(4px);
    
    .rec-arrow {
      transform: translateX(4px);
      color: var(--color-primary-600);
    }
  }
}

.rec-icon {
  font-size: 24px;
  width: 48px;
  height: 48px;
  background: var(--color-neutral-100);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
}

.rec-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.rec-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-neutral-800);
}

.rec-desc {
  font-size: 13px;
  color: var(--color-neutral-500);
}

.rec-arrow {
  font-size: 18px;
  color: var(--color-neutral-400);
  transition: all var(--transition-normal);
}
</style>
