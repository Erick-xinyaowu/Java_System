<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowRight, ArrowLeft, Check } from '@element-plus/icons-vue'

interface Question {
  id: number
  text: string
  options: { value: string; label: string }[]
}

// 测评类型
const assessmentTypes = [
  {
    id: 'mbti',
    name: 'MBTI 性格测试',
    desc: '了解您的性格类型，发现适合的职业方向',
    duration: '约15分钟',
    questions: 20
  },
  {
    id: 'holland',
    name: '霍兰德职业兴趣测试',
    desc: '探索您的职业兴趣，匹配理想职业',
    duration: '约20分钟',
    questions: 30
  },
  {
    id: 'ability',
    name: '职业能力评估',
    desc: '评估您的核心能力，发现优势与提升空间',
    duration: '约25分钟',
    questions: 40
  }
]

// MBTI 示例题目
const mbtiQuestions: Question[] = [
  {
    id: 1,
    text: '在社交场合中，您通常会：',
    options: [
      { value: 'E', label: '主动与陌生人交流，感到精力充沛' },
      { value: 'I', label: '倾向于与熟悉的朋友待在一起' }
    ]
  },
  {
    id: 2,
    text: '在做决定时，您更倾向于：',
    options: [
      { value: 'T', label: '依靠逻辑分析和客观事实' },
      { value: 'F', label: '考虑他人感受和价值观' }
    ]
  },
  {
    id: 3,
    text: '面对新信息时，您更关注：',
    options: [
      { value: 'S', label: '具体的事实和细节' },
      { value: 'N', label: '整体的模式和可能性' }
    ]
  },
  {
    id: 4,
    text: '在生活方式上，您更喜欢：',
    options: [
      { value: 'J', label: '有计划和组织的生活方式' },
      { value: 'P', label: '灵活和随性的生活方式' }
    ]
  },
  {
    id: 5,
    text: '与朋友相处时，您通常：',
    options: [
      { value: 'E', label: '喜欢参加各种聚会和活动' },
      { value: 'I', label: '更享受一对一的深度交流' }
    ]
  }
]

// 状态
const currentStep = ref(0) // 0: 选择测评, 1: 答题, 2: 结果
const selectedType = ref('')
const currentQuestion = ref(0)
const answers = ref<Record<number, string>>({})
const loading = ref(false)

// 计算属性
const questions = computed(() => {
  if (selectedType.value === 'mbti') return mbtiQuestions
  return mbtiQuestions // 简化演示
})

const progress = computed(() => {
  return Math.round((currentQuestion.value / questions.value.length) * 100)
})

const canNext = computed(() => {
  return answers.value[questions.value[currentQuestion.value]?.id] !== undefined
})

// 方法
function selectType(typeId: string) {
  selectedType.value = typeId
  currentStep.value = 1
  currentQuestion.value = 0
  answers.value = {}
}

function selectOption(value: string) {
  answers.value[questions.value[currentQuestion.value].id] = value
}

function prevQuestion() {
  if (currentQuestion.value > 0) {
    currentQuestion.value--
  }
}

function nextQuestion() {
  if (!canNext.value) {
    ElMessage.warning('请选择一个选项')
    return
  }

  if (currentQuestion.value < questions.value.length - 1) {
    currentQuestion.value++
  } else {
    submitAssessment()
  }
}

async function submitAssessment() {
  loading.value = true
  // 模拟提交
  await new Promise((resolve) => setTimeout(resolve, 2000))
  loading.value = false
  currentStep.value = 2
  ElMessage.success('测评完成！')
}

function restartAssessment() {
  currentStep.value = 0
  selectedType.value = ''
  currentQuestion.value = 0
  answers.value = {}
}
</script>

<template>
  <div class="assessment-page">
    <div class="page-header">
      <h1>职业测评</h1>
      <p>通过专业测评，发现您的职业潜能</p>
    </div>

    <!-- 步骤 1: 选择测评类型 -->
    <div v-if="currentStep === 0" class="type-selection">
      <div class="types-grid">
        <div
          v-for="type in assessmentTypes"
          :key="type.id"
          class="type-card"
          @click="selectType(type.id)"
        >
          <h3>{{ type.name }}</h3>
          <p class="type-desc">{{ type.desc }}</p>
          <div class="type-meta">
            <span>{{ type.duration }}</span>
            <span>{{ type.questions }} 题</span>
          </div>
          <el-button type="primary" class="start-btn">
            开始测评 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 步骤 2: 答题 -->
    <div v-else-if="currentStep === 1" class="question-section">
      <div class="progress-bar">
        <div class="progress-info">
          <span>进度：{{ currentQuestion + 1 }} / {{ questions.length }}</span>
          <span>{{ progress }}%</span>
        </div>
        <el-progress :percentage="progress" :show-text="false" :stroke-width="8" />
      </div>

      <el-card class="question-card" shadow="never">
        <h2 class="question-text">{{ questions[currentQuestion].text }}</h2>
        <div class="options">
          <div
            v-for="option in questions[currentQuestion].options"
            :key="option.value"
            class="option-item"
            :class="{ active: answers[questions[currentQuestion].id] === option.value }"
            @click="selectOption(option.value)"
          >
            <div class="option-radio">
              <el-icon v-if="answers[questions[currentQuestion].id] === option.value">
                <Check />
              </el-icon>
            </div>
            <span>{{ option.label }}</span>
          </div>
        </div>
      </el-card>

      <div class="question-actions">
        <el-button :disabled="currentQuestion === 0" @click="prevQuestion">
          <el-icon><ArrowLeft /></el-icon> 上一题
        </el-button>
        <el-button type="primary" :loading="loading" @click="nextQuestion">
          {{ currentQuestion === questions.length - 1 ? '提交' : '下一题' }}
          <el-icon v-if="currentQuestion < questions.length - 1"><ArrowRight /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- 步骤 3: 结果 -->
    <div v-else class="result-section">
      <el-card class="result-card" shadow="never">
        <div class="result-header">
          <div class="result-badge">INTJ</div>
          <h2>建筑师型人格</h2>
          <p class="result-subtitle">战略思想家，富有创造力和决心</p>
        </div>

        <div class="result-content">
          <div class="result-block">
            <h3>性格特点</h3>
            <ul>
              <li>独立思考，富有远见</li>
              <li>逻辑性强，善于分析</li>
              <li>追求完美，标准严格</li>
              <li>喜欢挑战，不断学习</li>
            </ul>
          </div>

          <div class="result-block">
            <h3>适合的职业方向</h3>
            <div class="career-tags">
              <el-tag>软件工程师</el-tag>
              <el-tag>数据分析师</el-tag>
              <el-tag>产品经理</el-tag>
              <el-tag>战略顾问</el-tag>
              <el-tag>科研人员</el-tag>
            </div>
          </div>

          <div class="result-block">
            <h3>发展建议</h3>
            <p>您具备出色的战略思维和分析能力，适合从事需要深度思考和创新的工作。建议在保持独立思考的同时，多关注团队协作和人际沟通技巧的提升。</p>
          </div>
        </div>

        <div class="result-actions">
          <el-button @click="restartAssessment">重新测评</el-button>
          <el-button type="primary">查看完整报告</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.assessment-page {
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

/* 类型选择 */
.types-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.type-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.type-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px -8px rgba(0, 0, 0, 0.1);
  border-color: #4F46E5;
}

.type-card h3 {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 12px;
}

.type-desc {
  font-size: 15px;
  color: #64748b;
  margin-bottom: 16px;
  line-height: 1.6;
}

.type-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 24px;
}

.start-btn {
  width: 100%;
}

/* 答题区 */
.question-section {
  max-width: 700px;
  margin: 0 auto;
}

.progress-bar {
  margin-bottom: 32px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
}

.question-card {
  border-radius: 16px;
  border: none;
  margin-bottom: 32px;
}

.question-text {
  font-size: 22px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 32px;
  line-height: 1.5;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.option-item:hover {
  border-color: #4F46E5;
  background: #f8fafc;
}

.option-item.active {
  border-color: #4F46E5;
  background: #eef2ff;
}

.option-radio {
  width: 24px;
  height: 24px;
  border: 2px solid #d1d5db;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.option-item.active .option-radio {
  background: #4F46E5;
  border-color: #4F46E5;
  color: #fff;
}

.question-actions {
  display: flex;
  justify-content: space-between;
}

/* 结果区 */
.result-section {
  max-width: 700px;
  margin: 0 auto;
}

.result-card {
  border-radius: 16px;
  border: none;
}

.result-header {
  text-align: center;
  padding-bottom: 32px;
  border-bottom: 1px solid #f1f5f9;
  margin-bottom: 32px;
}

.result-badge {
  display: inline-block;
  padding: 8px 24px;
  background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
  color: #fff;
  border-radius: 20px;
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 16px;
}

.result-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
}

.result-subtitle {
  font-size: 16px;
  color: #64748b;
}

.result-block {
  margin-bottom: 32px;
}

.result-block h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
}

.result-block ul {
  padding-left: 20px;
  color: #475569;
  line-height: 2;
}

.result-block p {
  color: #475569;
  line-height: 1.8;
}

.career-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.result-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding-top: 32px;
  border-top: 1px solid #f1f5f9;
}
</style>
