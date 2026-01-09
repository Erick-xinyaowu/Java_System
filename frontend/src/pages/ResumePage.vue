<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload, Delete, Edit } from '@element-plus/icons-vue'
import { 
  getMyResume, 
  updateResume, 
  addSkill, 
  updateSkill, 
  deleteSkill as deleteSkillApi,
  addWorkExperience,
  updateWorkExperience,
  deleteWorkExperience as deleteWorkApi,
  addEducation,
  updateEducation,
  deleteEducation as deleteEduApi,
  uploadResume,
  createResume
} from '@/api/resume'
import type { ResumeDetail, Skill, Education, WorkExperience } from '@/api/resume'

// 页面加载状态
const loading = ref(true)
const uploading = ref(false)

// 简历数据
const resumeId = ref<number | null>(null)
const hasResume = ref(false)

// 基本信息
const basicInfo = reactive({
  name: '',
  email: '',
  phone: '',
  location: '',
  summary: '',
  targetPosition: ''
})

// 技能列表
const skills = ref<Skill[]>([])

// 工作经历
const experiences = ref<WorkExperience[]>([])

// 教育经历
const educations = ref<Education[]>([])

// 编辑状态
const editingBasic = ref(false)

// 技能对话框
const skillDialogVisible = ref(false)
const skillForm = reactive({
  id: 0,
  name: '',
  level: 50,
  category: ''
})

// 经历对话框
const expDialogVisible = ref(false)
const expForm = reactive({
  id: 0,
  company: '',
  position: '',
  startDate: '',
  endDate: '',
  description: ''
})

// 教育对话框
const eduDialogVisible = ref(false)
const eduForm = reactive({
  id: 0,
  school: '',
  major: '',
  degree: '',
  startDate: '',
  endDate: '',
  description: ''
})

// 加载简历数据
async function loadResume() {
  loading.value = true
  try {
    const res = await getMyResume()
    if ((res.success || res.code === 200) && res.data) {
      const resume = res.data
      resumeId.value = resume.id
      hasResume.value = true
      
      // 基本信息 - 映射后端字段
      basicInfo.name = resume.realName || ''
      basicInfo.email = ''  // 后端简历表没有email字段
      basicInfo.phone = ''  // 后端简历表没有phone字段
      basicInfo.location = resume.workCity || ''
      basicInfo.summary = resume.selfIntroduction || ''
      basicInfo.targetPosition = resume.targetPosition || ''
      
      // 技能
      skills.value = resume.skills || []
      
      // 工作经历
      experiences.value = resume.workExperiences || []
      
      // 教育经历
      educations.value = resume.educations || []
    } else if (!res.data) {
      // 用户暂无简历
      hasResume.value = false
    }
  } catch (error: any) {
    if (error.response?.status === 404) {
      hasResume.value = false
      ElMessage.info('您还没有简历，可以上传或手动创建')
    } else {
      console.error('加载简历失败:', error)
    }
  } finally {
    loading.value = false
  }
}

// 创建新简历
async function createNewResume() {
  try {
    const res = await createResume({
      title: '我的简历',
      realName: basicInfo.name || '未命名',
      targetPosition: basicInfo.targetPosition,
      workCity: basicInfo.location,
      selfIntroduction: basicInfo.summary
    })
    if ((res.success || res.code === 200) && res.data) {
      resumeId.value = res.data.id
      hasResume.value = true
      ElMessage.success('简历创建成功')
    }
  } catch (error) {
    console.error('创建简历失败:', error)
  }
}

// 保存基本信息
async function saveBasicInfo() {
  if (!resumeId.value) {
    await createNewResume()
    editingBasic.value = false
    return
  }
  
  try {
    const res = await updateResume(resumeId.value, {
      title: '我的简历',
      realName: basicInfo.name,
      targetPosition: basicInfo.targetPosition,
      workCity: basicInfo.location,
      selfIntroduction: basicInfo.summary
    })
    if (res.success || res.code === 200) {
      editingBasic.value = false
      ElMessage.success('基本信息已保存')
    }
  } catch (error) {
    console.error('保存失败:', error)
  }
}

// ========== 技能操作 ==========
function openSkillDialog(skill?: Skill) {
  if (skill) {
    skillForm.id = skill.id
    skillForm.name = skill.name
    skillForm.level = skill.level
    skillForm.category = skill.category || ''
  } else {
    skillForm.id = 0
    skillForm.name = ''
    skillForm.level = 50
    skillForm.category = ''
  }
  skillDialogVisible.value = true
}

async function saveSkill() {
  if (!skillForm.name) {
    ElMessage.warning('请输入技能名称')
    return
  }
  
  if (!resumeId.value) {
    ElMessage.warning('请先创建简历')
    return
  }

  try {
    if (skillForm.id) {
      // 更新技能
      const res = await updateSkill(resumeId.value, skillForm.id, {
        name: skillForm.name,
        level: skillForm.level,
        category: skillForm.category
      })
      if (res.success) {
        const idx = skills.value.findIndex((s) => s.id === skillForm.id)
        if (idx !== -1 && res.data) {
          skills.value[idx] = res.data
        }
        ElMessage.success('技能已更新')
      }
    } else {
      // 添加技能
      const res = await addSkill(resumeId.value, {
        name: skillForm.name,
        level: skillForm.level,
        category: skillForm.category
      })
      if (res.success && res.data) {
        skills.value.push(res.data)
        ElMessage.success('技能已添加')
      }
    }
    skillDialogVisible.value = false
  } catch (error) {
    console.error('保存技能失败:', error)
  }
}

async function deleteSkill(id: number) {
  if (!resumeId.value) return
  
  try {
    await ElMessageBox.confirm('确定要删除这个技能吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteSkillApi(resumeId.value, id)
    if (res.success) {
      skills.value = skills.value.filter((s) => s.id !== id)
      ElMessage.success('已删除')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// ========== 工作经历操作 ==========
function openExpDialog(exp?: WorkExperience) {
  if (exp) {
    expForm.id = exp.id
    expForm.company = exp.company
    expForm.position = exp.position
    expForm.startDate = exp.startDate
    expForm.endDate = exp.endDate || ''
    expForm.description = exp.description || ''
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

async function saveExperience() {
  if (!expForm.company || !expForm.position) {
    ElMessage.warning('请填写公司名称和职位')
    return
  }
  
  if (!resumeId.value) {
    ElMessage.warning('请先创建简历')
    return
  }

  try {
    if (expForm.id) {
      const res = await updateWorkExperience(resumeId.value, expForm.id, {
        company: expForm.company,
        position: expForm.position,
        startDate: expForm.startDate,
        endDate: expForm.endDate,
        description: expForm.description
      })
      if (res.success && res.data) {
        const idx = experiences.value.findIndex((e) => e.id === expForm.id)
        if (idx !== -1) {
          experiences.value[idx] = res.data
        }
        ElMessage.success('经历已更新')
      }
    } else {
      const res = await addWorkExperience(resumeId.value, {
        company: expForm.company,
        position: expForm.position,
        startDate: expForm.startDate,
        endDate: expForm.endDate,
        description: expForm.description
      })
      if (res.success && res.data) {
        experiences.value.push(res.data)
        ElMessage.success('经历已添加')
      }
    }
    expDialogVisible.value = false
  } catch (error) {
    console.error('保存经历失败:', error)
  }
}

async function deleteExperience(id: number) {
  if (!resumeId.value) return
  
  try {
    await ElMessageBox.confirm('确定要删除这条工作经历吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteWorkApi(resumeId.value, id)
    if (res.success) {
      experiences.value = experiences.value.filter((e) => e.id !== id)
      ElMessage.success('已删除')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// ========== 教育经历操作 ==========
function openEduDialog(edu?: Education) {
  if (edu) {
    eduForm.id = edu.id
    eduForm.school = edu.school
    eduForm.major = edu.major
    eduForm.degree = edu.degree
    eduForm.startDate = edu.startDate
    eduForm.endDate = edu.endDate || ''
    eduForm.description = edu.description || ''
  } else {
    eduForm.id = 0
    eduForm.school = ''
    eduForm.major = ''
    eduForm.degree = ''
    eduForm.startDate = ''
    eduForm.endDate = ''
    eduForm.description = ''
  }
  eduDialogVisible.value = true
}

async function saveEducation() {
  if (!eduForm.school || !eduForm.major || !eduForm.degree) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  if (!resumeId.value) {
    ElMessage.warning('请先创建简历')
    return
  }

  try {
    if (eduForm.id) {
      const res = await updateEducation(resumeId.value, eduForm.id, {
        school: eduForm.school,
        major: eduForm.major,
        degree: eduForm.degree,
        startDate: eduForm.startDate,
        endDate: eduForm.endDate,
        description: eduForm.description
      })
      if (res.success && res.data) {
        const idx = educations.value.findIndex((e) => e.id === eduForm.id)
        if (idx !== -1) {
          educations.value[idx] = res.data
        }
        ElMessage.success('教育经历已更新')
      }
    } else {
      const res = await addEducation(resumeId.value, {
        school: eduForm.school,
        major: eduForm.major,
        degree: eduForm.degree,
        startDate: eduForm.startDate,
        endDate: eduForm.endDate,
        description: eduForm.description
      })
      if (res.success && res.data) {
        educations.value.push(res.data)
        ElMessage.success('教育经历已添加')
      }
    }
    eduDialogVisible.value = false
  } catch (error) {
    console.error('保存教育经历失败:', error)
  }
}

async function deleteEducation(id: number) {
  if (!resumeId.value) return
  
  try {
    await ElMessageBox.confirm('确定要删除这条教育经历吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteEduApi(resumeId.value, id)
    if (res.success) {
      educations.value = educations.value.filter((e) => e.id !== id)
      ElMessage.success('已删除')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// ========== 简历上传 ==========
async function handleUpload() {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.pdf,.doc,.docx'
  input.onchange = async (e: Event) => {
    const target = e.target as HTMLInputElement
    const file = target.files?.[0]
    if (!file) return
    
    // 检查文件大小 (10MB)
    if (file.size > 10 * 1024 * 1024) {
      ElMessage.error('文件大小不能超过10MB')
      return
    }
    
    uploading.value = true
    ElMessage.info('正在上传并解析简历，请稍候...')
    
    try {
      const res = await uploadResume(file)
      if (res.success && res.data) {
        ElMessage.success('简历解析成功！')
        // 重新加载简历数据
        await loadResume()
      }
    } catch (error) {
      console.error('上传失败:', error)
    } finally {
      uploading.value = false
    }
  }
  input.click()
}

// 页面加载时获取简历
onMounted(() => {
  loadResume()
})
</script>

<template>
  <div class="resume-page" v-loading="loading || uploading" :element-loading-text="uploading ? '正在解析简历...' : '加载中...'">
    <div class="page-header">
      <div>
        <h1>简历管理</h1>
        <p>管理您的个人简历信息</p>
      </div>
      <el-button type="primary" :icon="Upload" @click="handleUpload" :loading="uploading">
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
          <el-col :span="12">
            <el-form-item label="目标职位">
              <el-input v-model="basicInfo.targetPosition" />
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
          <span>{{ basicInfo.name || '未填写' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">邮箱：</span>
          <span>{{ basicInfo.email || '未填写' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">电话：</span>
          <span>{{ basicInfo.phone || '未填写' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">所在地：</span>
          <span>{{ basicInfo.location || '未填写' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">目标职位：</span>
          <span>{{ basicInfo.targetPosition || '未填写' }}</span>
        </div>
        <div class="info-row" style="grid-column: span 2;">
          <span class="info-label">简介：</span>
          <span>{{ basicInfo.summary || '未填写' }}</span>
        </div>
      </div>
    </el-card>

    <!-- 技能 -->
    <el-card class="section-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">专业技能</span>
          <el-button type="primary" link :icon="Plus" @click="openSkillDialog()" :disabled="!hasResume">
            添加技能
          </el-button>
        </div>
      </template>

      <div v-if="skills.length > 0" class="skills-list">
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
      <el-empty v-else description="暂无技能，点击添加" />
    </el-card>

    <!-- 工作经历 -->
    <el-card class="section-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">工作/实习经历</span>
          <el-button type="primary" link :icon="Plus" @click="openExpDialog()" :disabled="!hasResume">
            添加经历
          </el-button>
        </div>
      </template>

      <el-timeline v-if="experiences.length > 0">
        <el-timeline-item
          v-for="exp in experiences"
          :key="exp.id"
          :timestamp="`${exp.startDate} - ${exp.endDate || '至今'}`"
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

      <el-empty v-else description="暂无工作经历" />
    </el-card>

    <!-- 教育经历 -->
    <el-card class="section-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">教育经历</span>
          <el-button type="primary" link :icon="Plus" @click="openEduDialog()" :disabled="!hasResume">
            添加教育经历
          </el-button>
        </div>
      </template>

      <div v-if="educations.length > 0">
        <div v-for="edu in educations" :key="edu.id" class="edu-item">
          <div class="edu-info">
            <h4>{{ edu.school }}</h4>
            <p>{{ edu.major }} · {{ edu.degree }}</p>
          </div>
          <div class="edu-actions">
            <span class="edu-date">{{ edu.startDate }} - {{ edu.endDate || '至今' }}</span>
            <el-button type="primary" link size="small" @click="openEduDialog(edu)">编辑</el-button>
            <el-button type="danger" link size="small" @click="deleteEducation(edu.id)">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无教育经历" />
    </el-card>

    <!-- 技能对话框 -->
    <el-dialog v-model="skillDialogVisible" title="编辑技能" width="400px">
      <el-form label-width="80px">
        <el-form-item label="技能名称">
          <el-input v-model="skillForm.name" placeholder="如：Java" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="skillForm.category" placeholder="如：编程语言" />
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
    <el-dialog v-model="expDialogVisible" title="编辑工作经历" width="500px">
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
              <el-input v-model="expForm.endDate" placeholder="2023-06 或留空表示至今" />
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

    <!-- 教育经历对话框 -->
    <el-dialog v-model="eduDialogVisible" title="编辑教育经历" width="500px">
      <el-form label-width="80px">
        <el-form-item label="学校名称">
          <el-input v-model="eduForm.school" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="eduForm.major" />
        </el-form-item>
        <el-form-item label="学历">
          <el-select v-model="eduForm.degree" placeholder="请选择">
            <el-option label="博士" value="博士" />
            <el-option label="硕士" value="硕士" />
            <el-option label="本科" value="本科" />
            <el-option label="专科" value="专科" />
            <el-option label="高中" value="高中" />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-input v-model="eduForm.startDate" placeholder="2020-09" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-input v-model="eduForm.endDate" placeholder="2024-06 或留空表示至今" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述">
          <el-input v-model="eduForm.description" type="textarea" :rows="2" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="eduDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEducation">保存</el-button>
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

.edu-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.edu-date {
  font-size: 14px;
  color: #94a3b8;
}
</style>
