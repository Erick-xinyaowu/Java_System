<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, Upload } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

// 简历数据
const resumeData = ref({
  id: null as number | null,
  rawText: '',
  parsedSkills: '',
  education: '',
  experience: '',
  updatedAt: ''
})

const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()

const resumeForm = reactive({
  rawText: '',
  parsedSkills: '',
  education: '',
  experience: ''
})

const rules: FormRules = {
  education: [
    { required: true, message: '请输入教育背景', trigger: 'blur' }
  ],
  experience: [
    { required: true, message: '请输入工作/项目经历', trigger: 'blur' }
  ]
}

// 技能标签
const skillTags = ref<string[]>([])
const inputVisible = ref(false)
const inputValue = ref('')

const handleClose = (tag: string) => {
  skillTags.value.splice(skillTags.value.indexOf(tag), 1)
}

const showInput = () => {
  inputVisible.value = true
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    skillTags.value.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}

// 打开编辑对话框
const openEditDialog = () => {
  resumeForm.rawText = resumeData.value.rawText
  resumeForm.parsedSkills = resumeData.value.parsedSkills
  resumeForm.education = resumeData.value.education
  resumeForm.experience = resumeData.value.experience
  
  // 解析技能标签
  if (resumeForm.parsedSkills) {
    try {
      skillTags.value = JSON.parse(resumeForm.parsedSkills)
    } catch {
      skillTags.value = resumeForm.parsedSkills.split(',').map(s => s.trim()).filter(Boolean)
    }
  } else {
    skillTags.value = []
  }
  
  dialogVisible.value = true
}

// 保存简历
const handleSave = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 将技能标签转换为JSON
        resumeForm.parsedSkills = JSON.stringify(skillTags.value)
        
        // TODO: 调用API保存
        // await saveResume(resumeForm)
        
        // 模拟保存
        resumeData.value = {
          ...resumeData.value,
          ...resumeForm,
          updatedAt: new Date().toISOString()
        }
        
        ElMessage.success('简历保存成功')
        dialogVisible.value = false
      } catch {
        ElMessage.error('保存失败')
      } finally {
        loading.value = false
      }
    }
  })
}

// 上传简历文件
const handleUpload = (file: File) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    resumeForm.rawText = e.target?.result as string
    ElMessage.success('文件读取成功')
  }
  reader.readAsText(file)
  return false
}

// 删除简历
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除简历吗？此操作不可恢复', '警告', {
      type: 'warning'
    })
    
    // TODO: 调用API删除
    resumeData.value = {
      id: null,
      rawText: '',
      parsedSkills: '',
      education: '',
      experience: '',
      updatedAt: ''
    }
    skillTags.value = []
    
    ElMessage.success('删除成功')
  } catch {
    // 用户取消
  }
}
</script>

<template>
  <div class="resume-page">
    <div class="page-header">
      <div class="header-left">
        <h1>简历管理</h1>
        <p>管理你的个人简历信息，支持智能解析技能</p>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Edit" @click="openEditDialog">
          {{ resumeData.id ? '编辑简历' : '创建简历' }}
        </el-button>
        <el-button v-if="resumeData.id" type="danger" :icon="Delete" @click="handleDelete">
          删除
        </el-button>
      </div>
    </div>

    <!-- 简历展示卡片 -->
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="resume-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>简历详情</span>
              <el-tag v-if="resumeData.updatedAt" type="info" size="small">
                更新于: {{ new Date(resumeData.updatedAt).toLocaleDateString() }}
              </el-tag>
            </div>
          </template>
          
          <div v-if="resumeData.education || resumeData.experience" class="resume-content">
            <div class="resume-section">
              <h3>教育背景</h3>
              <p>{{ resumeData.education || '暂无信息' }}</p>
            </div>
            
            <el-divider />
            
            <div class="resume-section">
              <h3>工作/项目经历</h3>
              <p style="white-space: pre-wrap;">{{ resumeData.experience || '暂无信息' }}</p>
            </div>
            
            <el-divider />
            
            <div class="resume-section">
              <h3>原始内容</h3>
              <p style="white-space: pre-wrap;">{{ resumeData.rawText || '暂无信息' }}</p>
            </div>
          </div>
          
          <el-empty v-else description="暂无简历信息，点击右上角创建">
            <el-button type="primary" @click="openEditDialog">创建简历</el-button>
          </el-empty>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="skills-card" shadow="hover">
          <template #header>
            <span>技能标签</span>
          </template>
          
          <div class="skills-container">
            <template v-if="skillTags.length > 0">
              <el-tag
                v-for="tag in skillTags"
                :key="tag"
                class="skill-tag"
                type="primary"
                effect="light"
              >
                {{ tag }}
              </el-tag>
            </template>
            <el-empty v-else description="暂无技能标签" :image-size="80" />
          </div>
        </el-card>
        
        <el-card class="tips-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <span>简历小贴士</span>
          </template>
          <ul class="tips-list">
            <li>保持简历简洁，突出重点</li>
            <li>量化你的成就和贡献</li>
            <li>根据目标岗位调整内容</li>
            <li>使用专业的简历模板</li>
            <li>定期更新你的简历</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="resumeData.id ? '编辑简历' : '创建简历'"
      width="700px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="resumeForm"
        :rules="rules"
        label-width="100px"
        label-position="top"
      >
        <el-form-item label="上传简历文件">
          <el-upload
            :auto-upload="false"
            :before-upload="handleUpload"
            accept=".txt,.md"
            drag
          >
            <el-icon class="el-icon--upload"><Upload /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">支持 txt、md 格式的简历文件</div>
            </template>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="教育背景" prop="education">
          <el-input
            v-model="resumeForm.education"
            type="textarea"
            :rows="3"
            placeholder="例如：北京大学 计算机科学与技术 本科 2020-2024"
          />
        </el-form-item>
        
        <el-form-item label="工作/项目经历" prop="experience">
          <el-input
            v-model="resumeForm.experience"
            type="textarea"
            :rows="5"
            placeholder="请描述你的工作或项目经历..."
          />
        </el-form-item>
        
        <el-form-item label="技能标签">
          <div class="skill-tags-input">
            <el-tag
              v-for="tag in skillTags"
              :key="tag"
              closable
              @close="handleClose(tag)"
              class="skill-tag"
            >
              {{ tag }}
            </el-tag>
            <el-input
              v-if="inputVisible"
              v-model="inputValue"
              class="tag-input"
              size="small"
              @keyup.enter="handleInputConfirm"
              @blur="handleInputConfirm"
            />
            <el-button v-else size="small" @click="showInput">
              + 添加技能
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item label="原始内容">
          <el-input
            v-model="resumeForm.rawText"
            type="textarea"
            :rows="5"
            placeholder="可以粘贴完整的简历内容..."
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSave">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.resume-page {
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

.header-right {
  display: flex;
  gap: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.resume-card {
  min-height: 500px;
}

.resume-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.resume-section p {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
}

.skills-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.skill-tag {
  margin: 4px;
}

.tips-list {
  padding-left: 20px;
  margin: 0;
}

.tips-list li {
  font-size: 14px;
  color: #666;
  line-height: 2;
}

.skill-tags-input {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.tag-input {
  width: 100px;
}
</style>
