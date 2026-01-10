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
    <PageHeader 
      title="Dashboard" 
      description="Overview of your career progress and skill analysis."
    >
      <template #actions>
        <button class="action-btn secondary" @click="handleUploadResume">
          Upload New Resume
        </button>
      </template>
    </PageHeader>

    <!-- KPI Cards -->
    <div class="kpi-grid">
      <BaseCard 
        v-for="(item, index) in stats" 
        :key="index"
        class="kpi-card"
        :class="{ 'loading-skeleton': loading }"
      >
        <div class="kpi-content">
          <div class="kpi-icon-wrapper" :class="item.color.replace('text-', 'bg-') + '-light'">
             <!-- Placeholder Icons -->
             <span class="kpi-icon-letter" :class="item.color">{{ item.label[0] }}</span>
          </div>
          <div>
            <p class="kpi-label">{{ item.label }}</p>
            <h4 class="kpi-value">
              {{ item.value }}<span class="kpi-suffix">{{ item.suffix }}</span>
            </h4>
          </div>
        </div>
      </BaseCard>
    </div>

    <!-- Charts Section -->
    <div class="charts-grid">
      <!-- Skill Distribution -->
      <BaseCard title="Technical Skill Breakdown" header-border>
        <div class="chart-container">
          <VChart class="chart" :option="skillOption" autoresize />
        </div>
      </BaseCard>

      <!-- Learning Trend -->
      <BaseCard title="Capability Growth Trend" header-border>
        <div class="chart-container">
          <VChart class="chart" :option="trendOption" autoresize />
        </div>
      </BaseCard>
    </div>
    
    <!-- Recent Activity / Suggestions (Optional) -->
    <div class="mt-6">
       <BaseCard title="Recommended Actions" header-border>
          <div class="empty-state">
            <p>Complete your profile to get personalized AI recommendations.</p>
            <button class="link-btn" @click="router.push('/profile')">Go to Profile &rarr;</button>
          </div>
       </BaseCard>
    </div>
  </div>
</template>

<style scoped lang="scss">
.dashboard-page {
  max-width: 1600px;
  margin: 0 auto;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.kpi-card {
  transition: transform 0.2s;
  
  &:hover {
    transform: translateY(-2px);
  }
}

.kpi-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.kpi-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* Dynamic background classes helper */
.bg-primary-light { background-color: var(--color-primary-50); }
.bg-warning-light { background-color: #fffbeb; }
.bg-success-light { background-color: #ecfdf5; }
.bg-info-light { background-color: #eff6ff; }

.kpi-icon-letter {
  font-size: 24px;
  font-weight: 700;
}

.kpi-label {
  font-size: 14px;
  color: var(--color-neutral-500);
  margin-bottom: 4px;
}

.kpi-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-neutral-900);
  margin: 0;
  line-height: 1;
}

.kpi-suffix {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-neutral-400);
  margin-left: 4px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.chart {
  height: 100%;
  width: 100%;
}

/* Action Buttons */
.action-btn {
  padding: 8px 16px;
  border-radius: var(--radius-md);
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.secondary {
  background-color: var(--color-white);
  border: 1px solid var(--color-neutral-300);
  color: var(--color-neutral-700);
  
  &:hover {
    background-color: var(--color-neutral-50);
    border-color: var(--color-neutral-400);
    color: var(--color-neutral-900);
  }
}

.link-btn {
  background: none;
  border: none;
  color: var(--color-primary-600);
  cursor: pointer; 
  font-weight: 500;
  margin-top: 8px;
  
  &:hover {
    text-decoration: underline;
  }
}

.mt-6 { margin-top: 24px; }

.empty-state {
  text-align: center;
  padding: 32px;
  color: var(--color-neutral-500);
}
</style>
