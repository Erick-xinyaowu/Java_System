<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Upload, Delete, Document, Plus, ArrowLeft } from '@element-plus/icons-vue'
import MarkdownIt from 'markdown-it'
import {
  getResumeVersions,
  getResumeVersionDetail,
  deleteResumeVersion,
  uploadAndParseResume,
  saveParseResult
} from '@/api/version'
import type { ResumeVersionVO, ResumeVersionDetailVO, ParseResultVO } from '@/api/version'

// Markdown 渲染器
const md = new MarkdownIt({
  html: true,
  breaks: true,
  linkify: true,
})

// 页面状态
const loading = ref(false)
const uploading = ref(false)

// 版本列表
const versions = ref<ResumeVersionVO[]>([])

// 当前选中的版本
const selectedVersionId = ref<number | null>(null)
const selectedVersion = ref<ResumeVersionDetailVO | null>(null)

// 上传对话框
const uploadDialogVisible = ref(false)
const versionNote = ref('')
const fileList = ref<any[]>([])

// 解析结果（上传后保存）
const parseResult = ref<ParseResultVO | null>(null)
const showSaveConfirm = ref(false)

// 计算属性：渲染后的分析报告
const renderedReport = computed(() => {
  if (!selectedVersion.value?.analysisReport) {
    return '<div class="no-report">暂无分析报告</div>'
  }
  return md.render(selectedVersion.value.analysisReport)
})

// 计算属性：格式化文件大小
function formatFileSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

// 计算属性：格式化日期
function formatDate(dateStr: string): string {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载版本列表
async function loadVersions() {
  loading.value = true
  try {
    const res = await getResumeVersions()
    if (res.success || res.code === 200) {
      versions.value = res.data || []
      // 默认选中最新版本
      if (versions.value.length > 0 && !selectedVersionId.value) {
        selectVersion(versions.value[0].id)
      }
    }
  } catch (error) {
    console.error('加载版本列表失败:', error)
    ElMessage.error('加载版本列表失败')
  } finally {
    loading.value = false
  }
}

// 选择版本
async function selectVersion(id: number) {
  selectedVersionId.value = id
  loading.value = true
  try {
    const res = await getResumeVersionDetail(id)
    if (res.success || res.code === 200) {
      selectedVersion.value = res.data
    }
  } catch (error) {
    console.error('加载版本详情失败:', error)
    ElMessage.error('加载版本详情失败')
  } finally {
    loading.value = false
  }
}

// 打开上传对话框
function openUploadDialog() {
  uploadDialogVisible.value = true
  versionNote.value = ''
  fileList.value = []
}

// 文件变化
function handleFileChange(file: any) {
  // 只保留最后一个文件
  fileList.value = [file]
}

// 移除文件
function handleFileRemove() {
  fileList.value = []
}

// 上传简历
async function handleUpload() {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择要上传的简历文件')
    return
  }
  
  uploading.value = true
  uploadDialogVisible.value = false
  
  // 显示全屏加载，因为AI分析可能需要30-60秒
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在解析简历并生成智能分析报告，请稍候...',
    background: 'rgba(0, 0, 0, 0.7)',
  })
  
  try {
    const file = fileList.value[0].raw
    const res = await uploadAndParseResume(file, versionNote.value || undefined)
    
    if (res.success || res.code === 200) {
      ElMessage.success('简历解析成功！分析报告已生成')
      parseResult.value = res.data
      
      // 刷新版本列表
      await loadVersions()
      
      // 如果返回了版本ID，选中该版本
      if (res.data?.versionId) {
        await selectVersion(res.data.versionId)
      }
      
      // 询问是否保存到简历
      showSaveConfirm.value = true
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error: any) {
    console.error('上传失败:', error)
    ElMessage.error(error.message || '上传失败，请重试')
  } finally {
    loadingInstance.close()
    uploading.value = false
    fileList.value = []
    versionNote.value = ''
  }
}

// 保存解析结果到简历
async function confirmSaveParseResult() {
  if (!parseResult.value) return
  
  try {
    const res = await saveParseResult(parseResult.value)
    if (res.success || res.code === 200) {
      ElMessage.success('简历信息已更新')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    showSaveConfirm.value = false
    parseResult.value = null
  }
}

// 取消保存
function cancelSaveParseResult() {
  showSaveConfirm.value = false
  parseResult.value = null
}

// 删除版本
async function handleDeleteVersion(id: number, event: Event) {
  event.stopPropagation()
  
  try {
    await ElMessageBox.confirm('确定要删除这个版本吗？此操作不可恢复。', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteResumeVersion(id)
    if (res.success || res.code === 200) {
      ElMessage.success('删除成功')
      
      // 如果删除的是当前选中的版本，清空选中
      if (selectedVersionId.value === id) {
        selectedVersionId.value = null
        selectedVersion.value = null
      }
      
      // 刷新列表
      await loadVersions()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 导出PDF（后续实现）
function exportPDF() {
  ElMessage.info('导出PDF功能开发中...')
}

// 页面加载时获取版本列表
onMounted(() => {
  loadVersions()
})
</script>

<template>
  <div class="resume-analysis-page">
    <!-- 左侧版本列表 -->
    <aside class="version-sidebar">
      <div class="sidebar-header">
        <h2>📄 简历版本</h2>
        <el-button type="primary" :icon="Plus" circle size="small" @click="openUploadDialog" />
      </div>
      
      <div class="version-list" v-loading="loading && versions.length === 0">
        <div v-if="versions.length === 0 && !loading" class="empty-tip">
          <el-empty description="暂无简历版本" :image-size="80">
            <el-button type="primary" @click="openUploadDialog">
              <el-icon><Upload /></el-icon>
              上传简历
            </el-button>
          </el-empty>
        </div>
        
        <div
          v-for="version in versions"
          :key="version.id"
          class="version-item"
          :class="{ active: selectedVersionId === version.id }"
          @click="selectVersion(version.id)"
        >
          <div class="version-info">
            <div class="version-number">
              <el-icon><Document /></el-icon>
              版本 {{ version.versionNumber }}
            </div>
            <div class="version-name">{{ version.fileName }}</div>
            <div class="version-meta">
              <span>{{ formatFileSize(version.fileSize) }}</span>
              <span>{{ formatDate(version.uploadTime) }}</span>
            </div>
            <div v-if="version.versionNote" class="version-note">
              {{ version.versionNote }}
            </div>
          </div>
          <div class="version-actions">
            <el-tag v-if="version.hasAnalysis" type="success" size="small">已分析</el-tag>
            <el-tag v-else type="info" size="small">无报告</el-tag>
            <el-button
              type="danger"
              :icon="Delete"
              circle
              size="small"
              @click="handleDeleteVersion(version.id, $event)"
            />
          </div>
        </div>
      </div>
    </aside>

    <!-- 右侧分析报告 -->
    <main class="report-main">
      <div class="report-header">
        <h1>🧾 智能简历分析</h1>
        <div class="header-actions">
          <el-button type="primary" @click="openUploadDialog">
            <el-icon><Upload /></el-icon>
            分析新简历
          </el-button>
          <el-button @click="exportPDF" :disabled="!selectedVersion?.hasAnalysis">
            导出PDF
          </el-button>
        </div>
      </div>

      <div class="report-content" v-loading="loading && selectedVersionId !== null">
        <template v-if="selectedVersion">
          <div class="report-info">
            <el-descriptions :column="3" border size="small">
              <el-descriptions-item label="候选人">
                {{ selectedVersion.candidateName || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="版本">
                版本 {{ selectedVersion.versionNumber }}
              </el-descriptions-item>
              <el-descriptions-item label="上传时间">
                {{ formatDate(selectedVersion.uploadTime) }}
              </el-descriptions-item>
              <el-descriptions-item label="文件名">
                {{ selectedVersion.fileName }}
              </el-descriptions-item>
              <el-descriptions-item label="文件大小">
                {{ formatFileSize(selectedVersion.fileSize) }}
              </el-descriptions-item>
              <el-descriptions-item label="备注">
                {{ selectedVersion.versionNote || '无' }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="report-body markdown-body" v-html="renderedReport"></div>
        </template>
        
        <template v-else>
          <div class="no-selection">
            <el-empty description="请选择一个简历版本查看分析报告">
              <el-button v-if="versions.length === 0" type="primary" @click="openUploadDialog">
                <el-icon><Upload /></el-icon>
                上传第一份简历
              </el-button>
            </el-empty>
          </div>
        </template>
      </div>
    </main>

    <!-- 上传对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传简历"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form label-width="80px">
        <el-form-item label="选择文件">
          <el-upload
            class="upload-area"
            drag
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :file-list="fileList"
            accept=".pdf,.doc,.docx,.txt"
          >
            <el-icon class="el-icon--upload"><Upload /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 PDF、Word、TXT 格式，文件大小不超过 10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="版本备注">
          <el-input
            v-model="versionNote"
            type="textarea"
            :rows="2"
            placeholder="可选：添加版本说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="handleUpload">
          {{ uploading ? '解析中...' : '上传并分析' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 保存确认对话框 -->
    <el-dialog
      v-model="showSaveConfirm"
      title="更新简历信息"
      width="400px"
    >
      <p>简历解析成功！是否将解析的信息更新到您的简历中？</p>
      <p class="confirm-tip">这将更新您的姓名、目标职位、个人简介等基本信息。</p>
      <template #footer>
        <el-button @click="cancelSaveParseResult">暂不更新</el-button>
        <el-button type="primary" @click="confirmSaveParseResult">更新简历</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.resume-analysis-page {
  display: flex;
  height: calc(100vh - 60px);
  background: #f5f7fa;
}

/* 左侧版本列表 */
.version-sidebar {
  width: 320px;
  background: #fff;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.version-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.empty-tip {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.version-item {
  padding: 12px 16px;
  margin-bottom: 8px;
  background: #f9fafc;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid transparent;
}

.version-item:hover {
  background: #f0f5ff;
  border-color: #d4e3fc;
}

.version-item.active {
  background: #ecf5ff;
  border-color: #409eff;
}

.version-info {
  margin-bottom: 8px;
}

.version-number {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.version-name {
  font-size: 13px;
  color: #606266;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.version-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.version-note {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
  padding: 4px 8px;
  background: #fff;
  border-radius: 4px;
}

.version-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 右侧报告区域 */
.report-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}

.report-header h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.report-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.report-info {
  margin-bottom: 20px;
}

.report-body {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.no-report {
  text-align: center;
  padding: 60px;
  color: #909399;
  font-size: 16px;
}

.no-selection {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

/* Markdown 样式 */
.markdown-body {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
}

.markdown-body :deep(h1) {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid #409eff;
  color: #303133;
}

.markdown-body :deep(h2) {
  font-size: 20px;
  font-weight: 600;
  margin-top: 32px;
  margin-bottom: 16px;
  color: #409eff;
}

.markdown-body :deep(h3) {
  font-size: 17px;
  font-weight: 600;
  margin-top: 24px;
  margin-bottom: 12px;
  color: #606266;
}

.markdown-body :deep(p) {
  margin-bottom: 12px;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  margin-bottom: 16px;
  padding-left: 24px;
}

.markdown-body :deep(li) {
  margin-bottom: 8px;
}

.markdown-body :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
}

.markdown-body :deep(th),
.markdown-body :deep(td) {
  padding: 10px 14px;
  border: 1px solid #ebeef5;
  text-align: left;
}

.markdown-body :deep(th) {
  background: #f5f7fa;
  font-weight: 600;
}

.markdown-body :deep(tr:hover td) {
  background: #f9fafc;
}

.markdown-body :deep(strong) {
  font-weight: 600;
  color: #303133;
}

.markdown-body :deep(code) {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Menlo', 'Monaco', monospace;
  font-size: 13px;
}

.markdown-body :deep(blockquote) {
  margin: 16px 0;
  padding: 12px 20px;
  background: #f5f7fa;
  border-left: 4px solid #409eff;
  color: #606266;
}

/* 上传区域样式 */
.upload-area {
  width: 100%;
}

.upload-area :deep(.el-upload-dragger) {
  width: 100%;
}

.confirm-tip {
  color: #909399;
  font-size: 13px;
  margin-top: 8px;
}
</style>