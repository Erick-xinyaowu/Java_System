<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Upload, Document, RefreshRight, Delete, Download } from '@element-plus/icons-vue'
import MarkdownIt from 'markdown-it'
import html2canvas from 'html2canvas'
import { jsPDF } from 'jspdf'
import { getResumeVersions, getResumeVersionDetail, deleteResumeVersion, uploadAndParseResume } from '@/api/version'
import type { ResumeVersionVO, ResumeVersionDetailVO } from '@/api/version'
import PageHeader from '@/components/ui/PageHeader.vue'
import BaseCard from '@/components/ui/BaseCard.vue'

// --- Setup ---
const md = new MarkdownIt({ html: true, breaks: true, linkify: true })

const loading = ref(false)
const uploading = ref(false)
const versions = ref<ResumeVersionVO[]>([])
const selectedVersionId = ref<number | null>(null)
const selectedVersion = ref<ResumeVersionDetailVO | null>(null)

// --- Computeds ---
const renderedReport = computed(() => {
  // Use hasReport as a dependency tracker
  void hasReport.value
  if (!selectedVersion.value?.analysisReport) {
    return `<div class="empty-report">
      <div class="empty-icon">📝</div>
      <p>选择一个简历版本查看 AI 分析报告</p>
    </div>`
  }
  return md.render(selectedVersion.value.analysisReport)
})

const hasReport = computed(() => !!selectedVersion.value?.analysisReport)

// 格式化版本名称（优先显示文件名，否则显示日期时间）
function formatVersionName(v: ResumeVersionVO): string {
  // 如果有版本备注，使用备注
  if (v.versionNote && v.versionNote.trim()) {
    return v.versionNote
  }
  // 否则根据上传时间生成名称
  if (v.uploadTime) {
    return `简历 - ${v.uploadTime}`
  }
  // 最后兜底
  return `简历 #${v.id}`
}

// --- Methods ---
async function loadVersions() {
  loading.value = true
  try {
    const res = await getResumeVersions() as any
    if (res.code === 200 || res.success) {
      versions.value = res.data || []
      if (versions.value.length > 0 && !selectedVersionId.value) {
        selectVersion(versions.value[0].id)
      }
    }
  } catch (error) {
    ElMessage.error('加载历史版本失败')
  } finally {
    loading.value = false
  }
}

async function selectVersion(id: number) {
  selectedVersionId.value = id
  loading.value = true
  try {
    const res = await getResumeVersionDetail(id) as any
    if (res.code === 200 || res.success) {
      selectedVersion.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function handleFileUpload(file: any) {
  const fileName = file.name.toLowerCase()
  const fileType = file.raw.type
  
  // 支持 PDF 和 TXT 文件
  const isPDF = fileType === 'application/pdf' || fileName.endsWith('.pdf')
  const isTXT = fileType === 'text/plain' || fileName.endsWith('.txt')
  
  if (!isPDF && !isTXT) {
     ElMessage.error('仅支持 PDF 和 TXT 格式的文件')
     return false
  }
  
  uploading.value = true
  
  // 显示全屏加载，因为AI分析可能需要较长时间
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在解析简历并生成智能分析报告，请稍候...',
    background: 'rgba(0, 0, 0, 0.7)',
  })
  
  try {
    const res = await uploadAndParseResume(file.raw, file.name) as any
    if (res.code === 200 || res.success) {
      ElMessage.success('简历上传并分析成功！')
      await loadVersions()
      
      // 如果返回了版本ID，选中该版本
      if (res.data?.versionId) {
        await selectVersion(res.data.versionId)
      } else if (versions.value.length > 0) {
        await selectVersion(versions.value[0].id)
      }
    }
  } catch (error) {
    ElMessage.error('上传失败，请重试')
  } finally {
    loadingInstance.close()
    uploading.value = false
  }
}

function confirmDelete(id: number) {
  ElMessageBox.confirm('确定要删除这个版本吗？此操作不可恢复。', '确认删除', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteResumeVersion(id)
    ElMessage.success('删除成功')
    if (selectedVersionId.value === id) {
       selectedVersion.value = null
       selectedVersionId.value = null
    }
    loadVersions()
  })
}

// PDF导出功能
const exporting = ref(false)

async function exportPDF() {
  if (!selectedVersion.value?.analysisReport) {
    ElMessage.warning('当前版本暂无分析报告，无法导出')
    return
  }

  exporting.value = true
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在生成PDF文件，请稍候...',
    background: 'rgba(0, 0, 0, 0.7)',
  })

  try {
    await nextTick()

    // 获取要导出的内容区域
    const reportElement = document.querySelector('.markdown-body') as HTMLElement
    
    if (!reportElement) {
      throw new Error('未找到报告内容')
    }

    // 创建一个临时容器来组合内容
    const tempContainer = document.createElement('div')
    tempContainer.style.cssText = `
      position: absolute;
      left: -9999px;
      top: 0;
      width: 800px;
      padding: 40px;
      background: white;
      font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
    `
    
    // 添加标题
    const title = document.createElement('div')
    title.innerHTML = `
      <div style="text-align: center; margin-bottom: 30px;">
        <h1 style="font-size: 28px; color: #303133; margin: 0 0 10px 0;">📊 智能简历分析报告</h1>
        <p style="color: #909399; font-size: 14px;">Career Planner 智能职业规划系统</p>
      </div>
    `
    tempContainer.appendChild(title)

    // 添加基本信息
    const infoClone = document.createElement('div')
    infoClone.innerHTML = `
      <div style="margin-bottom: 24px; padding: 16px; background: #f5f7fa; border-radius: 8px;">
        <table style="width: 100%; border-collapse: collapse; font-size: 14px;">
          <tr>
            <td style="padding: 8px; color: #909399; width: 80px;">文件名</td>
            <td style="padding: 8px; color: #303133;">${selectedVersion.value?.versionNote || '简历'}</td>
            <td style="padding: 8px; color: #909399; width: 80px;">导出时间</td>
            <td style="padding: 8px; color: #303133;">${new Date().toLocaleString('zh-CN')}</td>
          </tr>
        </table>
      </div>
    `
    tempContainer.appendChild(infoClone)

    // 克隆报告内容
    const reportClone = reportElement.cloneNode(true) as HTMLElement
    reportClone.style.cssText = `
      background: white;
      padding: 0;
      box-shadow: none;
      font-size: 14px;
      line-height: 1.8;
    `
    tempContainer.appendChild(reportClone)

    // 添加页脚
    const footer = document.createElement('div')
    footer.innerHTML = `
      <div style="margin-top: 40px; padding-top: 20px; border-top: 1px solid #ebeef5; text-align: center; color: #909399; font-size: 12px;">
        <p>本报告由 Career Planner 智能职业规划系统自动生成</p>
        <p>生成时间：${new Date().toLocaleString('zh-CN')}</p>
      </div>
    `
    tempContainer.appendChild(footer)

    document.body.appendChild(tempContainer)

    // 使用 html2canvas 生成图像
    const canvas = await html2canvas(tempContainer, {
      scale: 2,
      useCORS: true,
      allowTaint: true,
      backgroundColor: '#ffffff',
      logging: false
    })

    document.body.removeChild(tempContainer)

    // 创建 PDF
    const imgData = canvas.toDataURL('image/jpeg', 0.95)
    const imgWidth = 210 // A4 宽度 (mm)
    const pageHeight = 297 // A4 高度 (mm)
    const imgHeight = (canvas.height * imgWidth) / canvas.width
    
    const pdf = new jsPDF({
      orientation: 'portrait',
      unit: 'mm',
      format: 'a4'
    })

    let heightLeft = imgHeight
    let position = 0

    // 添加第一页
    pdf.addImage(imgData, 'JPEG', 0, position, imgWidth, imgHeight)
    heightLeft -= pageHeight

    // 添加更多页
    while (heightLeft > 0) {
      position = heightLeft - imgHeight
      pdf.addPage()
      pdf.addImage(imgData, 'JPEG', 0, position, imgWidth, imgHeight)
      heightLeft -= pageHeight
    }

    // 下载 PDF
    const fileName = `简历分析报告_${selectedVersion.value?.versionNote || '未命名'}_${new Date().toLocaleDateString('zh-CN').replace(/\//g, '-')}.pdf`
    pdf.save(fileName)
    
    ElMessage.success('PDF导出成功')
  } catch (error) {
    console.error('PDF导出失败:', error)
    ElMessage.error('PDF导出失败，请重试')
  } finally {
    loadingInstance.close()
    exporting.value = false
  }
}

onMounted(() => {
  loadVersions()
})
</script>

<template>
  <div class="resume-workspace">
    <PageHeader 
      title="简历分析" 
      description="智能解析简历，AI驱动的深度分析报告"
    >
      <template #actions>
         <el-upload
            class="header-upload"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleFileUpload"
            accept=".pdf,.txt"
            :disabled="uploading"
         >
            <button class="primary-action-btn" :disabled="uploading">
               <el-icon v-if="uploading" class="is-loading"><RefreshRight /></el-icon>
               <el-icon v-else><Upload /></el-icon>
               <span>{{ uploading ? '分析中...' : '上传简历' }}</span>
            </button>
         </el-upload>
      </template>
    </PageHeader>

    <div class="workspace-layout">
      <!-- Left Panel: Version History -->
      <aside class="history-panel">
        <div class="panel-header">
           <h3>历史版本</h3>
        </div>
        <div class="version-list-wrapper">
           <ul v-if="versions.length" class="version-list">
             <li 
               v-for="v in versions" 
               :key="v.id"
               class="version-item"
               :class="{ 'is-active': selectedVersionId === v.id }"
               @click="selectVersion(v.id)"
             >
               <div class="version-icon">
                 <el-icon><Document /></el-icon>
               </div>
               <div class="version-info">
                 <span class="version-note">{{ formatVersionName(v) }}</span>
                 <span class="version-date">{{ v.uploadTime }}</span>
               </div>
               <button class="delete-btn" @click.stop="confirmDelete(v.id)">
                 <el-icon><Delete /></el-icon>
               </button>
             </li>
           </ul>
           <div v-else class="empty-history">
              暂无历史记录
           </div>
        </div>
      </aside>

      <!-- Main Panel: Analysis Result (Workspace) -->
      <main class="analysis-panel">
         <BaseCard class="result-card" no-padding>
            <div class="result-header">
               <div class="result-title-area">
                  <h3>AI 分析报告</h3>
                  <span v-if="selectedVersion" class="file-tag">
                     {{ selectedVersion.versionNote }}
                  </span>
               </div>
               <!-- Export PDF Button -->
               <button 
                  v-if="hasReport" 
                  class="export-btn" 
                  @click="exportPDF"
                  :disabled="exporting"
               >
                  <el-icon v-if="exporting" class="is-loading"><RefreshRight /></el-icon>
                  <el-icon v-else><Download /></el-icon>
                  <span>{{ exporting ? '导出中...' : '导出PDF' }}</span>
               </button>
            </div>
            
            <div class="result-content-area">
               <div v-if="loading" class="loading-state">
                  <div class="spinner"></div>
                  <p>正在加载分析报告...</p>
               </div>
               
               <div 
                  v-else 
                  class="markdown-body custom-markdown" 
                  v-html="renderedReport"
               ></div>
            </div>
         </BaseCard>
      </main>
    </div>
  </div>
</template>

<style scoped lang="scss">
.resume-workspace {
  height: calc(100vh - 100px); /* Fill remaining height roughly */
  display: flex;
  flex-direction: column;
  overflow: hidden; /* Prevent workspace from growing */
}

.workspace-layout {
  flex: 1;
  display: flex;
  gap: 24px;
  min-height: 0; /* Important for nested scroll */
  overflow: hidden; /* Contain children */
}

/* --- History Panel --- */
.history-panel {
  width: 300px;
  background-color: var(--color-white);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-neutral-200);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  overflow: hidden;

  @media (max-width: 1024px) {
     display: none; /* In real app, user drawer for mobile */
  }
}

.panel-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--color-neutral-100);
  
  h3 {
    font-size: 1rem;
    font-weight: 600;
    color: var(--color-neutral-900);
  }
}

.version-list-wrapper {
  flex: 1;
  overflow-y: auto;
}

.version-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.version-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: background-color 0.2s;
  border-bottom: 1px solid transparent;
  
  &:hover {
    background-color: var(--color-neutral-50);
    
    .delete-btn {
      opacity: 1;
    }
  }
  
  &.is-active {
    background-color: var(--color-primary-50);
    border-right: 3px solid var(--color-primary-500);
    
    .version-note {
      color: var(--color-primary-700);
      font-weight: 500;
    }
    
    .version-icon {
      color: var(--color-primary-500);
    }
  }
}

.version-icon {
  margin-right: 12px;
  color: var(--color-neutral-400);
  display: flex;
  align-items: center;
}

.version-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow: hidden;
}

.version-note {
  font-size: 0.9rem;
  color: var(--color-neutral-800);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.version-date {
  font-size: 0.75rem;
  color: var(--color-neutral-400);
}

.delete-btn {
  background: none;
  border: none;
  color: var(--color-neutral-400);
  cursor: pointer;
  padding: 4px;
  opacity: 0; /* Hidden by default */
  transition: all 0.2s;
  
  &:hover {
    color: var(--color-error);
    background-color: #fee2e2;
    border-radius: 4px;
  }
}

.empty-history {
  padding: 24px;
  text-align: center;
  color: var(--color-neutral-400);
  font-size: 0.9rem;
}

/* --- Analysis Panel --- */
.analysis-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  min-height: 0; /* Critical for nested flex scroll */
  overflow: hidden;
}

.result-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0; /* Allow shrinking */
  overflow: hidden;
  
  /* Override BaseCard's card-body to be flex container */
  :deep(.card-body) {
    display: flex;
    flex-direction: column;
    flex: 1;
    min-height: 0;
    overflow: hidden;
  }
}

.result-header {
  padding: 16px 24px;
  border-bottom: 1px solid var(--color-neutral-200);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--color-neutral-50);
}

.result-title-area {
  display: flex;
  align-items: center;
  gap: 12px;
  
  h3 {
    font-size: 1rem;
    font-weight: 600;
  }
}

.file-tag {
  background-color: var(--color-white);
  border: 1px solid var(--color-neutral-200);
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  color: var(--color-neutral-500);
}

.export-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background-color: var(--color-white);
  border: 1px solid var(--color-neutral-300);
  border-radius: var(--radius-md);
  color: var(--color-neutral-700);
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover:not(:disabled) {
    border-color: var(--color-primary-500);
    color: var(--color-primary-600);
    background-color: var(--color-primary-50);
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
  
  .is-loading {
    animation: spin 1s linear infinite;
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.result-content-area {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 32px;
  background-color: var(--color-white);
  min-height: 0; /* Critical: allow flex item to shrink and scroll */
}

/* Loading & Empty States */
.loading-state, 
:deep(.empty-report) {
   height: 100%;
   display: flex;
   flex-direction: column;
   align-items: center;
   justify-content: center;
   color: var(--color-neutral-400);
   gap: 16px;
}

.spinner {
   width: 40px;
   height: 40px;
   border: 3px solid var(--color-neutral-200);
   border-top-color: var(--color-primary-500);
   border-radius: 50%;
   animation: spin 1s linear infinite;
}

@keyframes spin {
   to { transform: rotate(360deg); }
}

:deep(.empty-icon) {
   font-size: 48px;
   opacity: 0.5;
}

/* Markdown Styles (Minimal Reset) */
.markdown-body {
  font-size: 1rem;
  line-height: 1.7;
  color: var(--color-neutral-800);
  max-width: 800px;
  margin: 0 auto;
}

/* Primary Action Button */
.primary-action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: var(--color-primary-600);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: var(--radius-md);
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: var(--color-primary-700);
  }
  
  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }
}
</style>
