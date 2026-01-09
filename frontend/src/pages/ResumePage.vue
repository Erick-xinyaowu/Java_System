<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Upload, Delete, Edit } from '@element-plus/icons-vue'

interface Skill {
  id: number
  name: string
  level: number
}

interface Experience {
  id: number
  company: string
  position: string
  startDate: string
  endDate: string
  description: string
}

interface Education {
  id: number
  school: string
  major: string
  degree: string
  startDate: string
  endDate: string
}

// 基本信息
const basicInfo = reactive({
  name: '张三',
  email: 'zhangsan@example.com',
  phone: '138****8888',
  location: '北京市',
  summary: '计算机科学与技术专业学生，熟悉 Java、Python 等编程语言，有较强的学习能力和团队协作精神。'
})

// 技能列表
const skills = ref<Skill[]>([
  { id: 1, name: 'Java', level: 80 },
  { id: 2, name: 'Python', level: 70 },
  { id: 3, name: 'Vue.js', level: 75 },
  { id: 4, name: 'MySQL', level: 65 },
  { id: 5, name: 'Spring Boot', level: 60 }
])

// 工作经历
const experiences = ref<Experience[]>([
  {
    id: 1,
    company: 'XX科技有限公司',
    position: 'Java 开发实习生',
    startDate: '2023-06',
    endDate: '2023-09',
    description: '参与公司后台管理系统开发，负责用户模块的设计与实现。'
  }
])

// 教育经历
const educations = ref<Education[]>([
  {
    id: 1,
    school: 'XX大学',
    major: '计算机科学与技术',
    degree: '本科',
    startDate: '2021-09',
    endDate: '2025-06'
  }
])

// 编辑状态
const editingBasic = ref(false)

// 技能对话框
const skillDialogVisible = ref(false)
const skillForm = reactive({
  id: 0,
  name: '',
  level: 50
})

// 经历对话框
const expDialogVisible = ref(false)
const expForm = reactive<Experience>({
  id: 0,
  company: '',
  position: '',
  startDate: '',
  endDate: '',
  description: ''
})

function saveBasicInfo() {
  editingBasic.value = false
  ElMessage.success('基本信息已保存')
}

function openSkillDialog(skill?: Skill) {
  if (skill) {
    skillForm.id = skill.id
    skillForm.name = skill.name
    skillForm.level = skill.level
  } else {
    skillForm.id = 0
    skillForm.name = ''
    skillForm.level = 50
  }
  skillDialogVisible.value = true
}

function saveSkill() {
  if (!skillForm.name) {
    ElMessage.warning('请输入技能名称')
    return
  }
  if (skillForm.id) {
    const idx = skills.value.findIndex((s) => s.id === skillForm.id)
    if (idx !== -1) {
      skills.value[idx] = { ...skillForm }
    }
  } else {
    skills.value.push({
      id: Date.now(),
      name: skillForm.name,
      level: skillForm.level
    })
  }
  skillDialogVisible.value = false
  ElMessage.success('技能已保存')
}

function deleteSkill(id: number) {
  skills.value = skills.value.filter((s) => s.id !== id)
  ElMessage.success('已删除')
}

function openExpDialog(exp?: Experience) {
  if (exp) {
    Object.assign(expForm, exp)
  } else {
    expForm.id = 0
    expForm.company = ''
    expForm.position = ''
    expForm.startDate = ''
    expForm.endDate = ''
    expForm.description = ''
  }
  expDialogVisible.value = true
}

function saveExperience() {
  if (!expForm.company || !expForm.position) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (expForm.id) {
    const idx = experiences.value.findIndex((e) => e.id === expForm.id)
    if (idx !== -1) {
      experiences.value[idx] = { ...expForm }
    }
  } else {
    experiences.value.push({
      ...expForm,
      id: Date.now()
    })
  }
  expDialogVisible.value = false
  ElMessage.success('经历已保存')
}

function deleteExperience(id: number) {
  experiences.value = experiences.value.filter((e) => e.id !== id)
  ElMessage.success('已删除')
}

function handleUpload() {
  ElMessage.info('简历上传功能开发中')
}
</script>

<template>
  <div class="resume-page">
    <div class="page-header">
      <div>
        <h1>简历管理</h1>
        <p>管理您的个人简历信息</p>
      </div>
      <el-button type="primary" :icon="Upload" @click="handleUpload">
        上传简历
      </el-button>
    </div>

    <!-- 基本信息 -->
    <el-card class="section-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">基本信息</span>
          <el-button
            type="primary"
            link
            :icon="Edit"
            @click="editingBasic = !editingBasic"
          >
            {{ editingBasic ? '取消' : '编辑' }}
          </el-button>
        </div>
      </template>

      <el-form v-if="editingBasic" label-width="80px">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="basicInfo.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="basicInfo.email" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话">
              <el-input v-model="basicInfo.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所在地">
              <el-input v-model="basicInfo.location" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="个人简介">
              <el-input v-model="basicInfo.summary" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-button type="primary" @click="saveBasicInfo">保存</el-button>
      </el-form>

      <div v-else class="info-display">
        <div class="info-row">
          <span class="info-label">姓名：</span>
          <span>{{ basicInfo.name }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">邮箱：</span>
          <span>{{ basicInfo.email }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">电话：</span>
          <span>{{ basicInfo.phone }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">所在地：</span>
          <span>{{ basicInfo.location }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">简介：</span>
          <span>{{ basicInfo.summary }}</span>
        </div>
      </div>
    </el-card>

    <!-- 技能 -->
    <el-card class="section-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">专业技能</span>
          <el-button type="primary" link :icon="Plus" @click="openSkillDialog()">
            添加技能
          </el-button>
        </div>
      </template>

      <div class="skills-list">
        <div v-for="skill in skills" :key="skill.id" class="skill-item">
          <div class="skill-info">
            <span class="skill-name">{{ skill.name }}</span>
            <div class="skill-actions">
              <el-button type="primary" link size="small" @click="openSkillDialog(skill)">
                编辑
              </el-button>
              <el-button type="danger" link size="small" @click="deleteSkill(skill.id)">
                删除
              </el-button>
            </div>
          </div>
          <el-progress :percentage="skill.level" :stroke-width="8" />
        </div>
      </div>
    </el-card>

    <!-- 工作经历 -->
    <el-card class="section-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">工作/实习经历</span>
          <el-button type="primary" link :icon="Plus" @click="openExpDialog()">
            添加经历
          </el-button>
        </div>
      </template>

      <el-timeline>
        <el-timeline-item
          v-for="exp in experiences"
          :key="exp.id"
          :timestamp="`${exp.startDate} - ${exp.endDate}`"
          placement="top"
        >
          <div class="exp-card">
            <div class="exp-header">
              <div>
                <h4>{{ exp.position }}</h4>
                <p class="exp-company">{{ exp.company }}</p>
              </div>
              <div class="exp-actions">
                <el-button type="primary" link size="small" @click="openExpDialog(exp)">
                  编辑
                </el-button>
                <el-button type="danger" link size="small" @click="deleteExperience(exp.id)">
                  删除
                </el-button>
              </div>
            </div>
            <p class="exp-desc">{{ exp.description }}</p>
          </div>
        </el-timeline-item>
      </el-timeline>

      <el-empty v-if="experiences.length === 0" description="暂无经历" />
    </el-card>

    <!-- 教育经历 -->
    <el-card class="section-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">教育经历</span>
        </div>
      </template>

      <div v-for="edu in educations" :key="edu.id" class="edu-item">
        <div class="edu-info">
          <h4>{{ edu.school }}</h4>
          <p>{{ edu.major }} · {{ edu.degree }}</p>
        </div>
        <div class="edu-date">{{ edu.startDate }} - {{ edu.endDate }}</div>
      </div>
    </el-card>

    <!-- 技能对话框 -->
    <el-dialog v-model="skillDialogVisible" title="编辑技能" width="400px">
      <el-form label-width="80px">
        <el-form-item label="技能名称">
          <el-input v-model="skillForm.name" placeholder="如：Java" />
        </el-form-item>
        <el-form-item label="熟练度">
          <el-slider v-model="skillForm.level" :min="0" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="skillDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSkill">保存</el-button>
      </template>
    </el-dialog>

    <!-- 经历对话框 -->
    <el-dialog v-model="expDialogVisible" title="编辑经历" width="500px">
      <el-form label-width="80px">
        <el-form-item label="公司名称">
          <el-input v-model="expForm.company" />
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="expForm.position" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-input v-model="expForm.startDate" placeholder="2023-01" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-input v-model="expForm.endDate" placeholder="2023-06" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="工作描述">
          <el-input v-model="expForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="expDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveExperience">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.resume-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
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

.section-card {
  margin-bottom: 24px;
  border-radius: 16px;
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.info-display {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-row {
  font-size: 14px;
  color: #475569;
}

.info-label {
  color: #94a3b8;
  margin-right: 8px;
}

.skills-list {
  display: grid;
  gap: 20px;
}

.skill-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.skill-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.skill-name {
  font-weight: 500;
  color: #334155;
}

.exp-card {
  padding-bottom: 16px;
}

.exp-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.exp-header h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.exp-company {
  font-size: 14px;
  color: #64748b;
}

.exp-desc {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
}

.edu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f1f5f9;
}

.edu-item:last-child {
  border-bottom: none;
}

.edu-info h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.edu-info p {
  font-size: 14px;
  color: #64748b;
}

.edu-date {
  font-size: 14px;
  color: #94a3b8;
}
</style>
