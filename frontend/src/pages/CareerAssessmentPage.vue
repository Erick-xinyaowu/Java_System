<script setup lang="ts">
import { ref, computed } from 'vue'
import { QuestionFilled, Check } from '@element-plus/icons-vue'

// 测评结果类型
interface TestResult {
  type: string | null
  result: string
  description: string
  strengths: string[]
  careers: string[]
  suggestions: string[]
}

// 测评历史类型
interface AssessmentHistoryItem {
  id: number
  type: string
  result: string
  createdAt: string
  summary: string
}

// 测评类型
const assessmentTypes = [
  {
    type: 'MBTI',
    name: 'MBTI 职业性格测试',
    description: '探索你的性格类型，发现最适合你的职业方向',
    duration: '15-20分钟',
    questions: 93,
    color: '#409EFF'
  },
  {
    type: 'HOLLAND',
    name: '霍兰德职业兴趣测试',
    description: '基于职业兴趣类型，找到与你兴趣匹配的职业',
    duration: '10-15分钟',
    questions: 60,
    color: '#67C23A'
  },
  {
    type: 'DISC',
    name: 'DISC 行为风格测试',
    description: '了解你的行为风格，提升职场沟通效率',
    duration: '10分钟',
    questions: 40,
    color: '#E6A23C'
  }
]

// 测评历史
const assessmentHistory = ref<AssessmentHistoryItem[]>([
  {
    id: 1,
    type: 'MBTI',
    result: 'INTJ - 建筑师',
    createdAt: '2024-01-10',
    summary: '独立思考者，善于规划和分析'
  },
  {
    id: 2,
    type: 'HOLLAND',
    result: 'IAR - 研究型/艺术型/现实型',
    createdAt: '2024-01-08',
    summary: '适合研究、创意和技术相关工作'
  }
])

// 当前测评状态
const currentAssessment = ref<string | null>(null)
const currentQuestion = ref(0)
const answers = ref<Record<number, string>>({})
const showResult = ref(false)
const testResult = ref<TestResult | null>(null)

// 问题数据类型
interface QuestionOption {
  value: string
  label: string
}

interface Question {
  id: number
  question: string
  options: QuestionOption[]
}

// 模拟问题数据
const mockQuestions: Question[] = [
  {
    id: 1,
    question: '在社交场合中，你更倾向于：',
    options: [
      { value: 'E', label: '主动与陌生人交谈，享受认识新朋友' },
      { value: 'I', label: '等待他人来接近你，或只与熟悉的人交流' }
    ]
  },
  {
    id: 2,
    question: '当你需要做决定时，你更看重：',
    options: [
      { value: 'T', label: '逻辑分析和客观事实' },
      { value: 'F', label: '个人价值观和他人的感受' }
    ]
  },
  {
    id: 3,
    question: '你更喜欢的工作方式是：',
    options: [
      { value: 'J', label: '有计划、有组织、按部就班' },
      { value: 'P', label: '灵活应变、随机应对' }
    ]
  },
  {
    id: 4,
    question: '在获取信息时，你更倾向于：',
    options: [
      { value: 'S', label: '关注具体事实和细节' },
      { value: 'N', label: '关注整体概念和可能性' }
    ]
  },
  {
    id: 5,
    question: '在团队合作中，你通常：',
    options: [
      { value: 'E', label: '积极发言，主导讨论' },
      { value: 'I', label: '倾听他人，深思熟虑后再发言' }
    ]
  }
]

// 开始测评
const startAssessment = (type: string) => {
  currentAssessment.value = type
  currentQuestion.value = 0
  answers.value = {}
  showResult.value = false
}

// 选择答案
const selectAnswer = (value: string) => {
  answers.value[currentQuestion.value] = value
}

// 下一题
const nextQuestion = () => {
  if (currentQuestion.value < mockQuestions.length - 1) {
    currentQuestion.value++
  } else {
    // 完成测评，生成结果
    generateResult()
  }
}

// 上一题
const prevQuestion = () => {
  if (currentQuestion.value > 0) {
    currentQuestion.value--
  }
}

// 生成结果
const generateResult = () => {
  // 模拟结果生成
  testResult.value = {
    type: currentAssessment.value,
    result: 'INTJ - 建筑师',
    description: '你是一个独立思考者，善于规划和分析。你有远见，喜欢挑战复杂问题。',
    strengths: ['战略思维', '独立性强', '高效执行', '追求卓越'],
    careers: ['软件工程师', '数据分析师', '产品经理', '研究员', '咨询顾问'],
    suggestions: [
      '发挥你的分析能力，在技术或研究领域深耕',
      '提升人际交往能力，更好地与团队协作',
      '学会适当放松，避免过度追求完美'
    ]
  }
  showResult.value = true
  
  // 添加到历史记录
  if (testResult.value) {
    assessmentHistory.value.unshift({
      id: Date.now(),
      type: currentAssessment.value || '',
      result: testResult.value.result,
      createdAt: new Date().toISOString().split('T')[0] || '',
      summary: testResult.value.description.slice(0, 30) + '...'
    })
  }
}

// 返回测评列表
const backToList = () => {
  currentAssessment.value = null
  showResult.value = false
}

// 计算进度
const progress = () => {
  return Math.round(((currentQuestion.value + 1) / mockQuestions.length) * 100)
}

// 安全获取当前问题（mockQuestions 数组在初始化时就有数据，不会为空）
const currentQuestionData = computed((): Question => {
  return mockQuestions[currentQuestion.value]!
})
</script>

<template>
  <div class="assessment-page">
    <!-- 测评列表 -->
    <template v-if="!currentAssessment">
      <div class="page-header">
        <h1>职业测评</h1>
        <p>通过专业测评工具，发现你的职业潜力和发展方向</p>
      </div>

      <el-row :gutter="20">
        <el-col :span="16">
          <div class="assessment-types">
            <el-card
              v-for="item in assessmentTypes"
              :key="item.type"
              class="assessment-card"
              shadow="hover"
            >
              <div class="assessment-info">
                <div class="assessment-icon" :style="{ backgroundColor: item.color + '20' }">
                  <el-icon :size="32" :style="{ color: item.color }">
                    <QuestionFilled />
                  </el-icon>
                </div>
                <div class="assessment-content">
                  <h3>{{ item.name }}</h3>
                  <p>{{ item.description }}</p>
                  <div class="assessment-meta">
                    <span>⏱️ {{ item.duration }}</span>
                    <span>📝 {{ item.questions }} 题</span>
                  </div>
                </div>
              </div>
              <el-button type="primary" @click="startAssessment(item.type)">
                开始测评
              </el-button>
            </el-card>
          </div>
        </el-col>
        
        <el-col :span="8">
          <el-card class="history-card" shadow="hover">
            <template #header>
              <span>测评历史</span>
            </template>
            <div v-if="assessmentHistory.length > 0" class="history-list">
              <div
                v-for="item in assessmentHistory"
                :key="item.id"
                class="history-item"
              >
                <div class="history-header">
                  <el-tag size="small">{{ item.type }}</el-tag>
                  <span class="history-date">{{ item.createdAt }}</span>
                </div>
                <div class="history-result">{{ item.result }}</div>
                <div class="history-summary">{{ item.summary }}</div>
              </div>
            </div>
            <el-empty v-else description="暂无测评记录" :image-size="80" />
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 测评进行中 -->
    <template v-else-if="!showResult">
      <div class="assessment-test">
        <el-card class="test-card">
          <div class="test-header">
            <el-button link @click="backToList">← 返回</el-button>
            <span class="test-title">{{ currentAssessment }} 测评</span>
            <span class="test-progress">{{ currentQuestion + 1 }} / {{ mockQuestions.length }}</span>
          </div>
          
          <el-progress :percentage="progress()" :stroke-width="8" />
          
          <div class="question-container">
            <h2 class="question-text">
              {{ currentQuestionData.question }}
            </h2>
            
            <div class="options-list">
              <div
                v-for="option in currentQuestionData.options"
                :key="option.value"
                class="option-item"
                :class="{ selected: answers[currentQuestion] === option.value }"
                @click="selectAnswer(option.value)"
              >
                <div class="option-indicator">
                  <el-icon v-if="answers[currentQuestion] === option.value">
                    <Check />
                  </el-icon>
                </div>
                <span>{{ option.label }}</span>
              </div>
            </div>
          </div>
          
          <div class="test-actions">
            <el-button :disabled="currentQuestion === 0" @click="prevQuestion">
              上一题
            </el-button>
            <el-button
              type="primary"
              :disabled="!answers[currentQuestion]"
              @click="nextQuestion"
            >
              {{ currentQuestion === mockQuestions.length - 1 ? '完成测评' : '下一题' }}
              <el-icon><Right /></el-icon>
            </el-button>
          </div>
        </el-card>
      </div>
    </template>

    <!-- 测评结果 -->
    <template v-else>
      <div class="assessment-result">
        <el-card class="result-card">
          <div class="result-header">
            <el-button link @click="backToList">← 返回测评列表</el-button>
          </div>
          
          <div class="result-content">
            <div class="result-type">
              <el-tag type="success" size="large">{{ testResult?.type }}</el-tag>
            </div>
            <h1 class="result-title">{{ testResult?.result }}</h1>
            <p class="result-description">{{ testResult?.description }}</p>
            
            <el-divider />
            
            <div class="result-section">
              <h3>你的优势</h3>
              <div class="strengths-list">
                <el-tag
                  v-for="strength in testResult?.strengths || []"
                  :key="strength"
                  type="primary"
                  effect="light"
                >
                  {{ strength }}
                </el-tag>
              </div>
            </div>
            
            <div class="result-section">
              <h3>推荐职业方向</h3>
              <div class="careers-list">
                <el-tag
                  v-for="career in testResult?.careers || []"
                  :key="career"
                  type="success"
                  effect="light"
                >
                  {{ career }}
                </el-tag>
              </div>
            </div>
            
            <div class="result-section">
              <h3>发展建议</h3>
              <ul class="suggestions-list">
                <li v-for="(suggestion, index) in testResult?.suggestions || []" :key="index">
                  {{ suggestion }}
                </li>
              </ul>
            </div>
          </div>
          
          <div class="result-actions">
            <el-button type="primary" @click="backToList">
              继续其他测评
            </el-button>
            <el-button>
              生成职业报告
            </el-button>
          </div>
        </el-card>
      </div>
    </template>
  </div>
</template>

<style scoped>
.assessment-page {
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

.assessment-types {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.assessment-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.assessment-card :deep(.el-card__body) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.assessment-info {
  display: flex;
  gap: 16px;
  align-items: center;
}

.assessment-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.assessment-content h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.assessment-content p {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.assessment-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.history-card {
  height: 100%;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-item {
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.history-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.history-date {
  font-size: 12px;
  color: #999;
}

.history-result {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.history-summary {
  font-size: 13px;
  color: #666;
}

/* 测评进行中 */
.assessment-test {
  max-width: 800px;
  margin: 0 auto;
}

.test-card {
  padding: 20px;
}

.test-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.test-title {
  font-size: 18px;
  font-weight: 600;
}

.test-progress {
  color: #666;
}

.question-container {
  padding: 40px 0;
}

.question-text {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin-bottom: 32px;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 600px;
  margin: 0 auto;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.option-item:hover {
  border-color: #409EFF;
  background-color: #f0f7ff;
}

.option-item.selected {
  border-color: #409EFF;
  background-color: #ecf5ff;
}

.option-indicator {
  width: 24px;
  height: 24px;
  border: 2px solid #dcdfe6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.option-item.selected .option-indicator {
  border-color: #409EFF;
  background-color: #409EFF;
  color: white;
}

.test-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

/* 测评结果 */
.assessment-result {
  max-width: 800px;
  margin: 0 auto;
}

.result-card {
  padding: 20px;
}

.result-header {
  margin-bottom: 20px;
}

.result-content {
  text-align: center;
  padding: 20px 0;
}

.result-type {
  margin-bottom: 16px;
}

.result-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 16px;
}

.result-description {
  font-size: 16px;
  color: #666;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.6;
}

.result-section {
  margin: 24px 0;
  text-align: left;
}

.result-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.strengths-list,
.careers-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.suggestions-list {
  padding-left: 20px;
}

.suggestions-list li {
  font-size: 14px;
  color: #666;
  line-height: 2;
}

.result-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
}
</style>
