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

// 概览数据
const overview = ref<DashboardOverview | null>(null)

// 统计卡片数据（计算属性）
const stats = computed(() => {
  if (!overview.value) {
    return [
      { label: '简历完成度', value: '-', color: '#059669' },
      { label: '技能数量', value: '-', color: '#D97706' },
      { label: '精通技能', value: '-', color: '#4F46E5' },
      { label: '工作经历', value: '-', color: '#DC2626' }
    ]
  }
  return [
    { label: '简历完成度', value: `${overview.value.resumeCompleteness}%`, color: '#059669' },
    { label: '技能数量', value: overview.value.totalSkills, color: '#D97706' },
    { label: '精通技能', value: overview.value.expertSkills, color: '#4F46E5' },
    { label: '工作经历', value: overview.value.workExperienceCount, color: '#DC2626' }
  ]
})

// 技能分布数据
const skillData = ref<CategoryData[]>([])

// 技能饼图配置
const skillOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  series: [
    {
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold' }
      },
      data: skillData.value.length > 0 
        ? skillData.value.map((item, index) => ({
            value: item.value,
            name: item.name,
            itemStyle: { color: item.color || getColorByIndex(index) }
          }))
        : [{ value: 1, name: '暂无数据', itemStyle: { color: '#e5e7eb' } }]
    }
  ]
}))

// 学习趋势数据
const trendData = ref<TrendData[]>([])

// 学习趋势图配置
const trendOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: trendData.value.length > 0 
      ? trendData.value.map(item => item.date)
      : ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: { type: 'value' },
  series: [
    {
      name: '学习时长',
      type: 'line',
      smooth: true,
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(79, 70, 229, 0.3)' },
            { offset: 1, color: 'rgba(79, 70, 229, 0.05)' }
          ]
        }
      },
      lineStyle: { color: '#4F46E5', width: 3 },
      itemStyle: { color: '#4F46E5' },
      data: trendData.value.length > 0 
        ? trendData.value.map(item => item.value)
        : [0, 0, 0, 0, 0, 0, 0]
    }
  ]
}))

// 职业小贴士
const careerTips = ref([
  { icon: '💡', title: '优化简历', desc: '用STAR法则描述工作经历，突出量化成果' },
  { icon: '🎯', title: '技能提升', desc: '根据目标岗位要求，优先学习核心技能' },
  { icon: '📝', title: '面试准备', desc: '整理项目经验，准备常见技术面试题' },
  { icon: '🌐', title: '人脉拓展', desc: '参加行业活动，建立职业社交网络' }
])

// 快捷操作
const quickActions = ref([
  { icon: '📄', label: '分析简历', path: '/resume', color: '#4F46E5' },
  { icon: '🤖', label: 'AI 顾问', path: '/ai-chat', color: '#059669' },
  { icon: '👤', label: '个人资料', path: '/profile', color: '#D97706' }
])

// 颜色数组
const colors = ['#4F46E5', '#059669', '#D97706', '#DC2626', '#64748b', '#8B5CF6', '#06B6D4']

function getColorByIndex(index: number): string {
  return colors[index % colors.length]
}

// 加载数据
async function loadDashboardData() {
  loading.value = true
  try {
    // 并行请求所有数据
    const [overviewRes, skillRes, trendRes] = await Promise.all([
      getDashboardOverview(),
      getSkillDistribution(),
      getLearningTrend('7d')
    ])

    if (overviewRes.success) {
      overview.value = overviewRes.data
    }

    if (skillRes.success) {
      skillData.value = skillRes.data || []
    }

    if (trendRes.success) {
      trendData.value = trendRes.data || []
    }
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<template>
  <div class="dashboard-page" v-loading="loading">
    <div class="page-header">
      <h1>数据仪表盘</h1>
      <p>您的学业与职业发展数据一览</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div v-for="stat in stats" :key="stat.label" class="stat-card">
        <div class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</div>
        <div class="stat-label">{{ stat.label }}</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <el-card class="chart-card" shadow="never">
        <template #header>
          <span class="card-title">技能分布</span>
        </template>
        <v-chart class="chart" :option="skillOption" autoresize />
      </el-card>

      <el-card class="chart-card" shadow="never">
        <template #header>
          <span class="card-title">本周学习趋势</span>
        </template>
        <v-chart class="chart" :option="trendOption" autoresize />
      </el-card>

      <!-- 快捷操作 + 职业小贴士 -->
      <el-card class="chart-card chart-card-full action-card" shadow="never">
        <div class="action-grid">
          <!-- 快捷操作 -->
          <div class="quick-actions">
            <h3 class="section-title">🚀 快捷操作</h3>
            <div class="action-buttons">
              <div 
                v-for="action in quickActions" 
                :key="action.label" 
                class="action-btn"
                :style="{ '--btn-color': action.color }"
                @click="router.push(action.path)"
              >
                <span class="action-icon">{{ action.icon }}</span>
                <span class="action-label">{{ action.label }}</span>
              </div>
            </div>
          </div>
          
          <!-- 职业小贴士 -->
          <div class="career-tips">
            <h3 class="section-title">💼 职业小贴士</h3>
            <div class="tips-list">
              <div v-for="(tip, index) in careerTips" :key="index" class="tip-item">
                <span class="tip-icon">{{ tip.icon }}</span>
                <div class="tip-content">
                  <div class="tip-title">{{ tip.title }}</div>
                  <div class="tip-desc">{{ tip.desc }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.dashboard-page {
  padding: 0;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
}

.page-header p {
  font-size: 15px;
  color: #64748b;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
}

.stat-value {
  font-size: 36px;
  font-weight: 800;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.chart-card {
  border-radius: 16px;
  border: none;
}

.chart-card-full {
  grid-column: span 2;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.chart {
  height: 300px;
  width: 100%;
}

/* 快捷操作 + 职业小贴士样式 */
.action-card {
  :deep(.el-card__body) {
    padding: 24px;
  }
}

.action-grid {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 32px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 16px 0;
}

/* 快捷操作按钮 */
.quick-actions {
  padding-right: 32px;
  border-right: 1px solid #ebeef5;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: linear-gradient(135deg, var(--btn-color) 0%, color-mix(in srgb, var(--btn-color) 80%, #000) 100%);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px color-mix(in srgb, var(--btn-color) 30%, transparent);
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px color-mix(in srgb, var(--btn-color) 40%, transparent);
}

.action-icon {
  font-size: 24px;
}

.action-label {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
}

/* 职业小贴士 */
.tips-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.tip-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.tip-item:hover {
  background: #f0f5ff;
  transform: translateX(4px);
}

.tip-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.tip-content {
  flex: 1;
}

.tip-title {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.tip-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
}

@media (max-width: 1024px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }

  .chart-card-full {
    grid-column: span 1;
  }

  .action-grid {
    grid-template-columns: 1fr;
  }

  .quick-actions {
    padding-right: 0;
    border-right: none;
    padding-bottom: 24px;
    border-bottom: 1px solid #ebeef5;
  }

  .action-buttons {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .action-btn {
    flex: 1;
    min-width: 120px;
  }

  .tips-list {
    grid-template-columns: 1fr;
  }
}
</style>
