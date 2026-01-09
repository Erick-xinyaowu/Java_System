<script setup lang="ts">
import { ref } from 'vue'
import { Download, Share, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)

// 模拟报告数据
const report = ref({
  generatedAt: '2024-01-15',
  mbtiType: 'INTJ',
  mbtiName: '建筑师型',
  hollandCode: 'IAR',
  overallScore: 85,
  dimensions: [
    { name: '逻辑思维', score: 92 },
    { name: '创新能力', score: 88 },
    { name: '领导力', score: 75 },
    { name: '沟通能力', score: 70 },
    { name: '执行力', score: 85 }
  ],
  recommendedCareers: [
    {
      title: '软件工程师',
      match: 95,
      salary: '15-35K',
      growth: '高',
      reason: '您的逻辑思维能力强，喜欢解决复杂问题，非常适合软件开发工作。'
    },
    {
      title: '数据分析师',
      match: 90,
      salary: '12-30K',
      growth: '高',
      reason: '您善于从数据中发现规律，分析能力出色，数据分析是理想选择。'
    },
    {
      title: '产品经理',
      match: 85,
      salary: '15-40K',
      growth: '中高',
      reason: '您具备战略思维和创新能力，能够规划产品发展方向。'
    }
  ],
  strengths: ['逻辑分析能力强', '独立思考', '追求卓越', '学习能力强'],
  weaknesses: ['可能忽视他人感受', '有时过于追求完美', '团队协作需加强'],
  developmentPlan: [
    {
      phase: '短期（3个月）',
      goals: ['完成相关课程学习', '参与1-2个实际项目', '提升编程技能']
    },
    {
      phase: '中期（6-12个月）',
      goals: ['获得实习机会', '建立专业人脉', '考取相关证书']
    },
    {
      phase: '长期（1-3年）',
      goals: ['积累工作经验', '明确职业方向', '持续技能提升']
    }
  ]
})

function handleDownload() {
  ElMessage.info('报告下载功能开发中')
}

function handleShare() {
  ElMessage.info('分享功能开发中')
}

async function handleRefresh() {
  loading.value = true
  await new Promise((resolve) => setTimeout(resolve, 2000))
  loading.value = false
  ElMessage.success('报告已更新')
}
</script>

<template>
  <div class="report-page" v-loading="loading">
    <div class="page-header">
      <div>
        <h1>职业规划报告</h1>
        <p>基于您的测评结果生成的个性化职业规划建议</p>
      </div>
      <div class="header-actions">
        <el-button :icon="Refresh" @click="handleRefresh">刷新报告</el-button>
        <el-button :icon="Share" @click="handleShare">分享</el-button>
        <el-button type="primary" :icon="Download" @click="handleDownload">
          下载PDF
        </el-button>
      </div>
    </div>

    <!-- 概览卡片 -->
    <div class="overview-grid">
      <el-card class="overview-card primary" shadow="never">
        <div class="overview-content">
          <div class="overview-label">MBTI 类型</div>
          <div class="overview-value">{{ report.mbtiType }}</div>
          <div class="overview-sub">{{ report.mbtiName }}</div>
        </div>
      </el-card>
      <el-card class="overview-card" shadow="never">
        <div class="overview-content">
          <div class="overview-label">霍兰德代码</div>
          <div class="overview-value">{{ report.hollandCode }}</div>
          <div class="overview-sub">研究型-艺术型-现实型</div>
        </div>
      </el-card>
      <el-card class="overview-card" shadow="never">
        <div class="overview-content">
          <div class="overview-label">综合匹配度</div>
          <div class="overview-value">{{ report.overallScore }}%</div>
          <div class="overview-sub">职业匹配评分</div>
        </div>
      </el-card>
    </div>

    <!-- 能力维度 -->
    <el-card class="section-card" shadow="never">
      <template #header>
        <span class="card-title">能力维度分析</span>
      </template>
      <div class="dimensions-list">
        <div v-for="dim in report.dimensions" :key="dim.name" class="dimension-item">
          <div class="dimension-info">
            <span class="dimension-name">{{ dim.name }}</span>
            <span class="dimension-score">{{ dim.score }}分</span>
          </div>
          <el-progress
            :percentage="dim.score"
            :stroke-width="12"
            :show-text="false"
            :color="dim.score >= 80 ? '#4F46E5' : dim.score >= 60 ? '#059669' : '#D97706'"
          />
        </div>
      </div>
    </el-card>

    <!-- 推荐职业 -->
    <el-card class="section-card" shadow="never">
      <template #header>
        <span class="card-title">推荐职业方向</span>
      </template>
      <div class="careers-grid">
        <div v-for="career in report.recommendedCareers" :key="career.title" class="career-card">
          <div class="career-header">
            <h3>{{ career.title }}</h3>
            <el-tag type="success">匹配度 {{ career.match }}%</el-tag>
          </div>
          <div class="career-meta">
            <span>💰 薪资：{{ career.salary }}</span>
            <span>📈 发展：{{ career.growth }}</span>
          </div>
          <p class="career-reason">{{ career.reason }}</p>
        </div>
      </div>
    </el-card>

    <!-- 优劣势分析 -->
    <div class="two-col-grid">
      <el-card class="section-card" shadow="never">
        <template #header>
          <span class="card-title">✅ 优势特点</span>
        </template>
        <ul class="trait-list success">
          <li v-for="item in report.strengths" :key="item">{{ item }}</li>
        </ul>
      </el-card>

      <el-card class="section-card" shadow="never">
        <template #header>
          <span class="card-title">⚠️ 提升空间</span>
        </template>
        <ul class="trait-list warning">
          <li v-for="item in report.weaknesses" :key="item">{{ item }}</li>
        </ul>
      </el-card>
    </div>

    <!-- 发展规划 -->
    <el-card class="section-card" shadow="never">
      <template #header>
        <span class="card-title">职业发展规划</span>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="plan in report.developmentPlan"
          :key="plan.phase"
          :timestamp="plan.phase"
          placement="top"
          color="#4F46E5"
        >
          <ul class="plan-goals">
            <li v-for="goal in plan.goals" :key="goal">{{ goal }}</li>
          </ul>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <!-- 报告信息 -->
    <div class="report-footer">
      <p>报告生成时间：{{ report.generatedAt }}</p>
      <p>本报告基于您的测评数据由 AI 智能生成，仅供参考</p>
    </div>
  </div>
</template>

<style scoped>
.report-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  flex-wrap: wrap;
  gap: 16px;
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

.header-actions {
  display: flex;
  gap: 12px;
}

/* 概览卡片 */
.overview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.overview-card {
  border-radius: 16px;
  border: none;
}

.overview-card.primary {
  background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
  color: #fff;
}

.overview-content {
  text-align: center;
}

.overview-label {
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 8px;
}

.overview-value {
  font-size: 36px;
  font-weight: 800;
  margin-bottom: 4px;
}

.overview-card:not(.primary) .overview-value {
  color: #4F46E5;
}

.overview-sub {
  font-size: 13px;
  opacity: 0.7;
}

/* 通用卡片 */
.section-card {
  border-radius: 16px;
  border: none;
  margin-bottom: 24px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

/* 能力维度 */
.dimensions-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dimension-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.dimension-info {
  display: flex;
  justify-content: space-between;
}

.dimension-name {
  font-weight: 500;
  color: #334155;
}

.dimension-score {
  font-weight: 600;
  color: #4F46E5;
}

/* 职业推荐 */
.careers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.career-card {
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
}

.career-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.career-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.career-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #64748b;
  margin-bottom: 12px;
}

.career-reason {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
}

/* 两列布局 */
.two-col-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.trait-list {
  list-style: none;
  padding: 0;
}

.trait-list li {
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 8px;
  font-size: 15px;
}

.trait-list.success li {
  background: #ecfdf5;
  color: #047857;
}

.trait-list.warning li {
  background: #fffbeb;
  color: #b45309;
}

/* 发展规划 */
.plan-goals {
  padding-left: 20px;
  margin: 0;
}

.plan-goals li {
  font-size: 15px;
  color: #475569;
  line-height: 2;
}

/* 页脚 */
.report-footer {
  text-align: center;
  padding: 32px 0;
  color: #94a3b8;
  font-size: 13px;
}

.report-footer p {
  margin-bottom: 4px;
}

@media (max-width: 768px) {
  .two-col-grid {
    grid-template-columns: 1fr;
  }
}
</style>
