<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Plus, View, Delete, Download } from '@element-plus/icons-vue'

// 报告类型定义
interface Report {
  id: number
  title: string
  content: string
  createdAt: string
  type: string
}

// 职业报告列表
const reports = ref([
  {
    id: 1,
    title: '软件工程师职业发展规划',
    content: `基于你的MBTI测评结果（INTJ）和技能背景分析，我们为你制定了以下职业发展规划：

## 职业定位
你适合从事需要深度思考和系统规划的技术岗位，如软件架构师、技术负责人等。

## 短期目标（1-2年）
1. 深耕Java/Python等主流编程语言
2. 学习云计算和分布式系统
3. 参与开源项目，提升技术影响力

## 中期目标（3-5年）
1. 成为团队技术骨干或小组负责人
2. 在特定领域建立专业优势
3. 开始关注技术管理相关技能

## 长期目标（5年以上）
1. 向架构师或技术管理方向发展
2. 建立行业影响力
3. 持续学习和创新`,
    createdAt: '2024-01-15',
    type: 'AI生成'
  },
  {
    id: 2,
    title: '数据分析师转型指南',
    content: `根据你的学习背景和职业兴趣测评，数据分析方向是一个不错的选择。

## 技能要求
- SQL和数据库操作
- Python/R数据分析
- 数据可视化工具
- 统计学基础

## 学习路径
1. 在线课程学习数据分析基础
2. 实践项目积累经验
3. 考取相关认证证书`,
    createdAt: '2024-01-10',
    type: 'AI生成'
  }
])

const dialogVisible = ref(false)
const currentReport = ref<Report | null>(null)
const loading = ref(false)

// 查看报告详情
const viewReport = (report: Report) => {
  currentReport.value = report
  dialogVisible.value = true
}

// 生成新报告
const generateReport = async () => {
  loading.value = true
  ElMessage.info('正在生成职业规划报告，请稍候...')
  
  // 模拟AI生成报告
  setTimeout(() => {
    const newReport: Report = {
      id: Date.now(),
      title: '个性化职业发展建议',
      content: `## 综合分析

基于你的简历信息、测评结果和学习数据，我们进行了全面分析。

## 核心优势
- 较强的逻辑思维能力
- 良好的学习能力和适应能力
- 扎实的专业基础

## 发展建议
1. 继续深化专业技能
2. 提升软技能（沟通、团队协作）
3. 关注行业动态和新技术

## 推荐学习资源
- Coursera/edX 在线课程
- 技术博客和社区
- 行业会议和研讨会`,
      createdAt: new Date().toISOString().split('T')[0] || '',
      type: 'AI生成'
    }
    
    reports.value.unshift(newReport)
    loading.value = false
    ElMessage.success('报告生成成功！')
    viewReport(newReport)
  }, 2000)
}

// 删除报告
const deleteReport = async (report: Report) => {
  try {
    await ElMessageBox.confirm('确定要删除这份报告吗？', '提示', {
      type: 'warning'
    })
    
    const index = reports.value.findIndex(r => r.id === report.id)
    if (index > -1) {
      reports.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消
  }
}

// 下载报告
const downloadReport = (report: Report) => {
  const blob = new Blob([report.content], { type: 'text/markdown' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${report.title}.md`
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('报告下载成功')
}
</script>

<template>
  <div class="report-page">
    <div class="page-header">
      <div class="header-left">
        <h1>职业报告</h1>
        <p>查看AI生成的个性化职业规划建议</p>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Plus" :loading="loading" @click="generateReport">
          生成新报告
        </el-button>
      </div>
    </div>

    <div class="reports-container">
      <el-row :gutter="20">
        <el-col v-for="report in reports" :key="report.id" :span="8">
          <el-card class="report-card" shadow="hover">
            <div class="report-icon">
              <el-icon :size="40" color="#409EFF">
                <Document />
              </el-icon>
            </div>
            <h3 class="report-title">{{ report.title }}</h3>
            <div class="report-meta">
              <el-tag size="small" type="success">{{ report.type }}</el-tag>
              <span class="report-date">{{ report.createdAt }}</span>
            </div>
            <p class="report-preview">
              {{ report.content.slice(0, 100) }}...
            </p>
            <div class="report-actions">
              <el-button type="primary" text :icon="View" @click="viewReport(report)">
                查看
              </el-button>
              <el-button type="primary" text :icon="Download" @click="downloadReport(report)">
                下载
              </el-button>
              <el-button type="danger" text :icon="Delete" @click="deleteReport(report)">
                删除
              </el-button>
            </div>
          </el-card>
        </el-col>
        
        <!-- 空状态 -->
        <el-col v-if="reports.length === 0" :span="24">
          <el-empty description="暂无职业报告">
            <el-button type="primary" @click="generateReport">
              生成第一份报告
            </el-button>
          </el-empty>
        </el-col>
      </el-row>
    </div>

    <!-- 报告详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentReport?.title"
      width="800px"
      destroy-on-close
    >
      <div v-if="currentReport" class="report-detail">
        <div class="report-detail-meta">
          <el-tag type="success">{{ currentReport.type }}</el-tag>
          <span>创建时间：{{ currentReport.createdAt }}</span>
        </div>
        <el-divider />
        <div class="report-content markdown-body" v-html="formatMarkdown(currentReport.content)">
        </div>
      </div>
      
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="currentReport && downloadReport(currentReport)">
          下载报告
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
// 简单的Markdown格式化（实际项目中可以使用 marked 或 markdown-it）
function formatMarkdown(content: string): string {
  return content
    .replace(/^## (.+)$/gm, '<h2>$1</h2>')
    .replace(/^### (.+)$/gm, '<h3>$1</h3>')
    .replace(/^- (.+)$/gm, '<li>$1</li>')
    .replace(/(<li>.*<\/li>\n?)+/g, '<ul>$&</ul>')
    .replace(/^\d+\. (.+)$/gm, '<li>$1</li>')
    .replace(/\n\n/g, '</p><p>')
    .replace(/^(.+)$/gm, (match) => {
      if (match.startsWith('<')) return match
      return `<p>${match}</p>`
    })
}

export default {
  methods: {
    formatMarkdown
  }
}
</script>

<style scoped>
.report-page {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.header-left p {
  font-size: 14px;
  color: #666;
}

.report-card {
  text-align: center;
  padding: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.report-card:hover {
  transform: translateY(-4px);
}

.report-icon {
  margin-bottom: 16px;
}

.report-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  line-height: 1.4;
}

.report-meta {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.report-date {
  font-size: 12px;
  color: #999;
}

.report-preview {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  text-align: left;
  margin-bottom: 16px;
  min-height: 60px;
}

.report-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
  border-top: 1px solid #eee;
  padding-top: 16px;
}

.report-detail-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #666;
  font-size: 14px;
}

.report-content {
  line-height: 1.8;
  color: #333;
}

.report-content :deep(h2) {
  font-size: 18px;
  font-weight: 600;
  margin: 24px 0 12px;
  color: #333;
}

.report-content :deep(h3) {
  font-size: 16px;
  font-weight: 600;
  margin: 16px 0 8px;
  color: #333;
}

.report-content :deep(p) {
  margin: 8px 0;
}

.report-content :deep(ul) {
  padding-left: 20px;
  margin: 8px 0;
}

.report-content :deep(li) {
  margin: 4px 0;
}
</style>
