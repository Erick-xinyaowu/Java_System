<template>
  <div class="admin-console">
    <!-- 头部 -->
    <header class="console-header">
      <div class="header-left">
        <el-icon :size="32" class="header-icon"><Monitor /></el-icon>
        <div class="header-title">
          <h1>管理员控制台</h1>
          <p>实时监控系统运行状态和用户数据分析</p>
        </div>
      </div>
      <div class="header-right">
        <el-tag type="success" effect="dark" class="status-tag">
          <el-icon><CircleCheck /></el-icon>
          <span>系统运行正常</span>
        </el-tag>
        <div class="current-time">
          <el-icon><Clock /></el-icon>
          <span>{{ currentTime }}</span>
        </div>
      </div>
    </header>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-info">
          <span class="stat-label">总用户数</span>
          <span class="stat-value">{{ stats.totalUsers }}</span>
          <span class="stat-change up">
            <el-icon><Top /></el-icon>
            {{ stats.todayNewUsers }} 较上周
          </span>
        </div>
        <div class="stat-icon users">
          <el-icon :size="32"><User /></el-icon>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-info">
          <span class="stat-label">简历总数</span>
          <span class="stat-value">{{ stats.totalResumes }}</span>
          <span class="stat-change up">
            <el-icon><Top /></el-icon>
            {{ stats.todayNewResumes }} 较上周
          </span>
        </div>
        <div class="stat-icon resumes">
          <el-icon :size="32"><Document /></el-icon>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-info">
          <span class="stat-label">活跃用户</span>
          <span class="stat-value">{{ stats.activeUsers }}</span>
          <span class="stat-change neutral">24小时内</span>
        </div>
        <div class="stat-icon active">
          <el-icon :size="32"><TrendCharts /></el-icon>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-info">
          <span class="stat-label">系统健康度</span>
          <span class="stat-value">{{ stats.systemHealth }}%</span>
          <span class="stat-change neutral">运行稳定</span>
        </div>
        <div class="stat-icon health">
          <el-icon :size="32"><Odometer /></el-icon>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-row">
      <div class="chart-card">
        <div class="chart-header">
          <el-icon><TrendCharts /></el-icon>
          <span>用户增长趋势</span>
        </div>
        <div class="chart-body">
          <v-chart :option="userGrowthOption" autoresize />
        </div>
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <el-icon><PieChart /></el-icon>
          <span>简历处理状态</span>
        </div>
        <div class="chart-body">
          <v-chart :option="resumeStatusOption" autoresize />
        </div>
      </div>
    </div>

    <div class="charts-row">
      <div class="chart-card">
        <div class="chart-header">
          <el-icon><Histogram /></el-icon>
          <span>热门技能 Top 6</span>
        </div>
        <div class="chart-body">
          <v-chart :option="topSkillsOption" autoresize />
        </div>
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <el-icon><DataLine /></el-icon>
          <span>24小时活跃度</span>
        </div>
        <div class="chart-body">
          <v-chart :option="hourlyActivityOption" autoresize />
        </div>
      </div>
    </div>

    <div class="charts-row">
      <div class="chart-card wide">
        <div class="chart-header">
          <el-icon><School /></el-icon>
          <span>专业分布统计</span>
        </div>
        <div class="chart-body">
          <v-chart :option="majorDistributionOption" autoresize />
        </div>
      </div>

      <div class="chart-card resource-card">
        <div class="chart-header">
          <el-icon><Cpu /></el-icon>
          <span>系统资源</span>
        </div>
        <div class="resource-list">
          <div class="resource-item">
            <span class="resource-label">CPU 使用率</span>
            <el-progress 
              :percentage="stats.cpuUsage" 
              :color="getProgressColor(stats.cpuUsage)"
              :stroke-width="10"
            />
            <span class="resource-value">{{ stats.cpuUsage }}%</span>
          </div>
          <div class="resource-item">
            <span class="resource-label">内存使用</span>
            <el-progress 
              :percentage="stats.memoryUsage" 
              :color="getProgressColor(stats.memoryUsage)"
              :stroke-width="10"
            />
            <span class="resource-value">{{ stats.memoryUsage }}%</span>
          </div>
          <div class="resource-item">
            <span class="resource-label">磁盘使用</span>
            <el-progress 
              :percentage="stats.diskUsage" 
              :color="getProgressColor(stats.diskUsage)"
              :stroke-width="10"
            />
            <span class="resource-value">{{ stats.diskUsage?.toFixed(0) }}%</span>
          </div>
          <div class="resource-item">
            <span class="resource-label">网络流量</span>
            <el-progress 
              :percentage="stats.networkUsage" 
              :color="getProgressColor(stats.networkUsage)"
              :stroke-width="10"
            />
            <span class="resource-value">{{ stats.networkUsage?.toFixed(0) }}%</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Monitor,
  CircleCheck,
  Clock,
  User,
  Document,
  TrendCharts,
  Odometer,
  Top,
  PieChart,
  Histogram,
  DataLine,
  School,
  Cpu
} from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart as EchartsPie, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import { getAdminStats, checkAdmin, type AdminStats } from '@/api/admin'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  LineChart,
  EchartsPie,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const router = useRouter()

// 状态
const loading = ref(false)
const currentTime = ref('')
const stats = ref<AdminStats>({
  totalUsers: 0,
  totalResumes: 0,
  activeUsers: 0,
  systemHealth: 100,
  userGrowthDates: [],
  userGrowthCounts: [],
  processedResumes: 0,
  pendingResumes: 0,
  topSkillNames: [],
  topSkillCounts: [],
  hourlyLabels: [],
  hourlyCounts: [],
  majorNames: [],
  majorCounts: [],
  cpuUsage: 0,
  memoryUsage: 0,
  diskUsage: 0,
  networkUsage: 0,
  todayNewUsers: 0,
  todayNewResumes: 0,
  todayAnalysis: 0
})

let timeInterval: number | null = null
let statsInterval: number | null = null

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取进度条颜色
const getProgressColor = (percentage: number) => {
  if (percentage < 50) return '#67C23A'
  if (percentage < 80) return '#E6A23C'
  return '#F56C6C'
}

// 用户增长趋势图配置
const userGrowthOption = computed(() => ({
  backgroundColor: 'transparent',
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    textStyle: { color: '#fff' }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: stats.value.userGrowthDates,
    axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } },
    axisLabel: { color: 'rgba(255,255,255,0.7)' }
  },
  yAxis: {
    type: 'value',
    axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } },
    axisLabel: { color: 'rgba(255,255,255,0.7)' },
    splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
  },
  series: [{
    data: stats.value.userGrowthCounts,
    type: 'line',
    smooth: true,
    areaStyle: {
      color: {
        type: 'linear',
        x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
        ]
      }
    },
    lineStyle: { color: '#409EFF', width: 2 },
    itemStyle: { color: '#409EFF' }
  }]
}))

// 简历处理状态饼图配置
const resumeStatusOption = computed(() => ({
  backgroundColor: 'transparent',
  tooltip: {
    trigger: 'item',
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    textStyle: { color: '#fff' }
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    textStyle: { color: 'rgba(255,255,255,0.7)' }
  },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    center: ['60%', '50%'],
    avoidLabelOverlap: false,
    itemStyle: {
      borderRadius: 10,
      borderColor: '#0d1b2a',
      borderWidth: 2
    },
    label: {
      show: true,
      position: 'outside',
      color: 'rgba(255,255,255,0.7)',
      formatter: '{b} {d}%'
    },
    data: [
      { value: stats.value.processedResumes, name: '已完成', itemStyle: { color: '#67C23A' } },
      { value: stats.value.pendingResumes, name: '待处理', itemStyle: { color: '#409EFF' } }
    ]
  }]
}))

// 热门技能柱状图配置
const topSkillsOption = computed(() => ({
  backgroundColor: 'transparent',
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' },
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    textStyle: { color: '#fff' }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'value',
    axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } },
    axisLabel: { color: 'rgba(255,255,255,0.7)' },
    splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
  },
  yAxis: {
    type: 'category',
    data: stats.value.topSkillNames,
    axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } },
    axisLabel: { color: 'rgba(255,255,255,0.7)' }
  },
  series: [{
    type: 'bar',
    data: stats.value.topSkillCounts,
    itemStyle: {
      color: {
        type: 'linear',
        x: 0, y: 0, x2: 1, y2: 0,
        colorStops: [
          { offset: 0, color: '#409EFF' },
          { offset: 1, color: '#67C23A' }
        ]
      },
      borderRadius: [0, 4, 4, 0]
    }
  }]
}))

// 24小时活跃度图配置
const hourlyActivityOption = computed(() => ({
  backgroundColor: 'transparent',
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    textStyle: { color: '#fff' }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: stats.value.hourlyLabels,
    axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } },
    axisLabel: { 
      color: 'rgba(255,255,255,0.7)',
      interval: 3
    }
  },
  yAxis: {
    type: 'value',
    axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } },
    axisLabel: { color: 'rgba(255,255,255,0.7)' },
    splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
  },
  series: [{
    data: stats.value.hourlyCounts,
    type: 'line',
    smooth: true,
    lineStyle: { color: '#E6A23C', width: 2 },
    itemStyle: { color: '#E6A23C' },
    areaStyle: {
      color: {
        type: 'linear',
        x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
          { offset: 1, color: 'rgba(230, 162, 60, 0.05)' }
        ]
      }
    }
  }]
}))

// 专业分布柱状图配置
const majorDistributionOption = computed(() => ({
  backgroundColor: 'transparent',
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' },
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    textStyle: { color: '#fff' }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: stats.value.majorNames,
    axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } },
    axisLabel: { color: 'rgba(255,255,255,0.7)' }
  },
  yAxis: {
    type: 'value',
    axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } },
    axisLabel: { color: 'rgba(255,255,255,0.7)' },
    splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
  },
  series: [{
    type: 'bar',
    data: stats.value.majorCounts,
    itemStyle: {
      color: (params: any) => {
        const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
        return colors[params.dataIndex % colors.length]
      },
      borderRadius: [4, 4, 0, 0]
    },
    barWidth: '50%'
  }]
}))

// 加载数据
const loadStats = async () => {
  try {
    loading.value = true
    const res = await getAdminStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error: any) {
    if (error.code === 403) {
      ElMessage.error('无权访问管理员控制台')
      router.push('/dashboard')
    }
  } finally {
    loading.value = false
  }
}

// 检查管理员权限
const checkAdminAccess = async () => {
  try {
    const res = await checkAdmin()
    if (res.code === 200 && !res.data) {
      ElMessage.error('无权访问管理员控制台')
      router.push('/dashboard')
      return false
    }
    return true
  } catch (error) {
    router.push('/dashboard')
    return false
  }
}

// 初始化
onMounted(async () => {
  const hasAccess = await checkAdminAccess()
  if (hasAccess) {
    updateTime()
    timeInterval = window.setInterval(updateTime, 1000)
    await loadStats()
    // 每30秒刷新一次数据
    statsInterval = window.setInterval(loadStats, 30000)
  }
})

// 清理
onUnmounted(() => {
  if (timeInterval) clearInterval(timeInterval)
  if (statsInterval) clearInterval(statsInterval)
})
</script>

<style scoped lang="scss">
.admin-console {
  min-height: 100%;
  padding: 24px;
  background: linear-gradient(135deg, #0d1b2a 0%, #1b2838 100%);
}

// 头部
.console-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  color: #409EFF;
}

.header-title {
  h1 {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
    color: #fff;
  }
  
  p {
    margin: 4px 0 0;
    font-size: 14px;
    color: rgba(255, 255, 255, 0.6);
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.status-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  font-size: 14px;
}

.current-time {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  font-family: 'Monaco', 'Menlo', monospace;
}

// 统计卡片
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s;
  
  &:hover {
    background: rgba(255, 255, 255, 0.08);
    transform: translateY(-2px);
  }
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #fff;
}

.stat-change {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  
  &.up {
    color: #67C23A;
  }
  
  &.down {
    color: #F56C6C;
  }
  
  &.neutral {
    color: rgba(255, 255, 255, 0.5);
  }
}

.stat-icon {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  
  &.users {
    background: rgba(64, 158, 255, 0.2);
    color: #409EFF;
  }
  
  &.resumes {
    background: rgba(103, 194, 58, 0.2);
    color: #67C23A;
  }
  
  &.active {
    background: rgba(230, 162, 60, 0.2);
    color: #E6A23C;
  }
  
  &.health {
    background: rgba(103, 194, 58, 0.2);
    color: #67C23A;
  }
}

// 图表区域
.charts-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
  
  &.wide {
    grid-column: span 1;
  }
}

.chart-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
  font-size: 15px;
  font-weight: 500;
}

.chart-body {
  height: 280px;
  padding: 16px;
}

// 资源卡片
.resource-card {
  .resource-list {
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
}

.resource-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.resource-label {
  width: 80px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

.resource-item :deep(.el-progress) {
  flex: 1;
  
  .el-progress-bar__outer {
    background: rgba(255, 255, 255, 0.1);
  }
}

.resource-value {
  width: 50px;
  text-align: right;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

// 响应式
@media (max-width: 1400px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 1024px) {
  .charts-row {
    grid-template-columns: 1fr;
  }
  
  .chart-card.wide {
    grid-column: span 1;
  }
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .console-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .header-left {
    flex-direction: column;
  }
  
  .header-right {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
