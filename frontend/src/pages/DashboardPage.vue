<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { TrendCharts, DataAnalysis, Timer, Reading } from '@element-plus/icons-vue'

// 统计卡片数据
const statsCards = ref([
  { title: '学习总时长', value: '156', unit: '小时', icon: Timer, color: '#409EFF', trend: '+12%' },
  { title: '完成课程', value: '24', unit: '门', icon: Reading, color: '#67C23A', trend: '+3' },
  { title: '职业测评', value: '5', unit: '次', icon: DataAnalysis, color: '#E6A23C', trend: '+1' },
  { title: '生成报告', value: '8', unit: '份', icon: TrendCharts, color: '#F56C6C', trend: '+2' }
])

// 图表引用
const barChartRef = ref<HTMLElement | null>(null)
const lineChartRef = ref<HTMLElement | null>(null)
const pieChartRef = ref<HTMLElement | null>(null)

let barChart: echarts.ECharts | null = null
let lineChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null

// 初始化柱状图 - 按类别学习时长
const initBarChart = () => {
  if (!barChartRef.value) return
  
  barChart = echarts.init(barChartRef.value)
  const option: echarts.EChartsOption = {
    title: {
      text: '学习时长分布（按类别）',
      left: 'center',
      textStyle: { fontSize: 14, fontWeight: 500 }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: ['课程学习', '项目实践', '竞赛活动', '自主学习', '其他']
    },
    yAxis: {
      type: 'value',
      name: '小时'
    },
    series: [{
      data: [45, 32, 28, 35, 16],
      type: 'bar',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#667eea' },
          { offset: 1, color: '#764ba2' }
        ])
      },
      barWidth: '50%'
    }]
  }
  barChart.setOption(option)
}

// 初始化折线图 - 学习趋势
const initLineChart = () => {
  if (!lineChartRef.value) return
  
  lineChart = echarts.init(lineChartRef.value)
  const option: echarts.EChartsOption = {
    title: {
      text: '近7天学习趋势',
      left: 'center',
      textStyle: { fontSize: 14, fontWeight: 500 }
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value',
      name: '小时'
    },
    series: [{
      data: [3, 4, 2, 5, 6, 8, 4],
      type: 'line',
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
          { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
        ])
      },
      lineStyle: { color: '#667eea', width: 2 },
      itemStyle: { color: '#667eea' }
    }]
  }
  lineChart.setOption(option)
}

// 初始化饼图 - 活动分布
const initPieChart = () => {
  if (!pieChartRef.value) return
  
  pieChart = echarts.init(pieChartRef.value)
  const option: echarts.EChartsOption = {
    title: {
      text: '学习活动分布',
      left: 'center',
      textStyle: { fontSize: 14, fontWeight: 500 }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 0
    },
    series: [{
      name: '活动类型',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: 45, name: '课程学习', itemStyle: { color: '#409EFF' } },
        { value: 32, name: '项目实践', itemStyle: { color: '#67C23A' } },
        { value: 28, name: '竞赛活动', itemStyle: { color: '#E6A23C' } },
        { value: 35, name: '自主学习', itemStyle: { color: '#F56C6C' } },
        { value: 16, name: '其他', itemStyle: { color: '#909399' } }
      ]
    }]
  }
  pieChart.setOption(option)
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  barChart?.resize()
  lineChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  initBarChart()
  initLineChart()
  initPieChart()
  window.addEventListener('resize', handleResize)
})
</script>

<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h1>数据仪表盘</h1>
      <p>查看你的学习数据和职业规划进度</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card v-for="stat in statsCards" :key="stat.title" class="stat-card" shadow="hover">
        <div class="stat-icon" :style="{ backgroundColor: stat.color + '20' }">
          <el-icon :size="28" :style="{ color: stat.color }">
            <component :is="stat.icon" />
          </el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">
            {{ stat.value }}<span class="stat-unit">{{ stat.unit }}</span>
          </div>
          <div class="stat-title">{{ stat.title }}</div>
        </div>
        <div class="stat-trend" :class="{ positive: stat.trend.includes('+') }">
          {{ stat.trend }}
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <div ref="barChartRef" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <div ref="lineChartRef" class="chart"></div>
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <div ref="pieChartRef" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card recent-activity" shadow="hover">
            <template #header>
              <span>最近活动</span>
            </template>
            <el-timeline>
              <el-timeline-item timestamp="2024-01-15" placement="top" color="#409EFF">
                完成《Java高级编程》课程学习
              </el-timeline-item>
              <el-timeline-item timestamp="2024-01-14" placement="top" color="#67C23A">
                提交项目实践报告
              </el-timeline-item>
              <el-timeline-item timestamp="2024-01-13" placement="top" color="#E6A23C">
                完成MBTI职业测评
              </el-timeline-item>
              <el-timeline-item timestamp="2024-01-12" placement="top" color="#F56C6C">
                获得AI生成的职业规划报告
              </el-timeline-item>
            </el-timeline>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style scoped>
.dashboard-page {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.page-header p {
  font-size: 14px;
  color: #666;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
}

.stat-unit {
  font-size: 14px;
  color: #666;
  margin-left: 4px;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.stat-trend {
  font-size: 14px;
  color: #F56C6C;
  font-weight: 500;
}

.stat-trend.positive {
  color: #67C23A;
}

.chart-card {
  height: 350px;
}

.chart {
  width: 100%;
  height: 300px;
}

.recent-activity :deep(.el-card__header) {
  font-weight: 600;
  color: #333;
}

@media (max-width: 1200px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
