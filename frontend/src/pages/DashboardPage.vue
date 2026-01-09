<script setup lang="ts">
import { ref, onMounted } from 'vue'
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

// 统计卡片数据
const stats = ref([
  { label: '已完成测评', value: 3, color: '#4F46E5' },
  { label: '简历完成度', value: '85%', color: '#059669' },
  { label: '技能数量', value: 12, color: '#D97706' },
  { label: '学习时长(h)', value: 156, color: '#DC2626' }
])

// 技能雷达图配置
const skillOption = ref({
  tooltip: { trigger: 'item' },
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
      data: [
        { value: 30, name: 'Java', itemStyle: { color: '#4F46E5' } },
        { value: 25, name: 'Python', itemStyle: { color: '#059669' } },
        { value: 20, name: 'Vue', itemStyle: { color: '#D97706' } },
        { value: 15, name: 'SQL', itemStyle: { color: '#DC2626' } },
        { value: 10, name: '其他', itemStyle: { color: '#64748b' } }
      ]
    }
  ]
})

// 学习趋势图
const trendOption = ref({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
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
      data: [3, 4, 2, 5, 4, 6, 4]
    }
  ]
})

// 成绩分布图
const gradeOption = ref({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: ['高数', '英语', '数据结构', '操作系统', '计网', '数据库']
  },
  yAxis: { type: 'value', max: 100 },
  series: [
    {
      type: 'bar',
      barWidth: '50%',
      itemStyle: {
        borderRadius: [8, 8, 0, 0],
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: '#4F46E5' },
            { offset: 1, color: '#7C3AED' }
          ]
        }
      },
      data: [92, 85, 88, 90, 78, 95]
    }
  ]
})

onMounted(() => {
  setTimeout(() => {
    loading.value = false
  }, 500)
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

      <el-card class="chart-card chart-card-full" shadow="never">
        <template #header>
          <span class="card-title">课程成绩分布</span>
        </template>
        <v-chart class="chart" :option="gradeOption" autoresize />
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

@media (max-width: 1024px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }

  .chart-card-full {
    grid-column: span 1;
  }
}
</style>
