<template>
  <div class="ai-recommend-page">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <button class="new-chat-btn" @click="createNewSession">
          <el-icon><Plus /></el-icon>
          <span>新对话</span>
        </button>
      </div>
      
      <div class="session-list">
        <div class="session-search">
          <el-input
            v-model="searchQuery"
            placeholder="搜索对话..."
            prefix-icon="Search"
            clearable
            size="small"
          />
        </div>
        
        <div class="sessions-container">
          <div v-if="loading" class="loading-sessions">
            <el-skeleton :rows="5" animated />
          </div>
          <div v-else-if="filteredSessions.length === 0" class="no-sessions">
            <el-icon :size="48"><ChatDotSquare /></el-icon>
            <p>暂无对话记录</p>
            <p class="hint">点击上方按钮开始新对话</p>
          </div>
          <div
            v-else
            v-for="session in filteredSessions"
            :key="session.id"
            :class="['session-item', { active: currentSession?.id === session.id }]"
            @click="selectSession(session)"
          >
            <el-icon class="session-icon"><ChatDotRound /></el-icon>
            <div class="session-info">
              <span v-if="editingSessionId !== session.id" class="session-title">
                {{ session.title }}
              </span>
              <el-input
                v-else
                v-model="editingTitle"
                size="small"
                @blur="saveTitle(session.id)"
                @keyup.enter="saveTitle(session.id)"
                ref="titleInput"
              />
            </div>
            <div class="session-actions">
              <el-tooltip content="重命名" placement="top">
                <el-icon @click.stop="startEditTitle(session)"><Edit /></el-icon>
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-icon @click.stop="confirmDeleteSession(session)"><Delete /></el-icon>
              </el-tooltip>
            </div>
          </div>
        </div>
      </div>
      
      <div class="sidebar-footer">
        <el-popconfirm
          title="确定要清空所有对话吗？"
          confirm-button-text="确定"
          cancel-button-text="取消"
          @confirm="clearAll"
        >
          <template #reference>
            <button class="clear-btn" :disabled="sessions.length === 0">
              <el-icon><DeleteFilled /></el-icon>
              <span>清空对话</span>
            </button>
          </template>
        </el-popconfirm>
      </div>
    </aside>
    
    <!-- 主聊天区域 -->
    <main class="chat-main">
      <!-- 欢迎界面 -->
      <div v-if="!currentSession" class="welcome-container">
        <div class="welcome-content">
          <div class="welcome-icon">
            <el-icon :size="80"><ChatDotSquare /></el-icon>
          </div>
          <h1>职业规划 AI 助手</h1>
          <p class="welcome-desc">
            我是您的专业职业顾问"小智"，可以帮助您进行职业规划、简历优化、面试准备等。
          </p>
          <div class="suggestion-cards">
            <div class="suggestion-card" @click="sendSuggestion('请帮我分析一下当前IT行业的就业趋势')">
              <el-icon><TrendCharts /></el-icon>
              <span>行业趋势分析</span>
            </div>
            <div class="suggestion-card" @click="sendSuggestion('我想转行做产品经理，需要准备哪些技能？')">
              <el-icon><Switch /></el-icon>
              <span>职业转型建议</span>
            </div>
            <div class="suggestion-card" @click="sendSuggestion('请帮我准备几个常见的面试问题及答案')">
              <el-icon><ChatLineSquare /></el-icon>
              <span>面试问题准备</span>
            </div>
            <div class="suggestion-card" @click="sendSuggestion('怎样才能让我的简历更有吸引力？')">
              <el-icon><Document /></el-icon>
              <span>简历优化技巧</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 聊天界面 -->
      <div v-else class="chat-container">
        <!-- 消息列表 -->
        <div class="messages-container" ref="messagesContainer">
          <div v-if="messages.length === 0" class="empty-chat">
            <p>开始和小智聊天吧！</p>
          </div>
          <div
            v-else
            v-for="message in messages"
            :key="message.id"
            :class="['message', message.role]"
          >
            <div class="message-avatar">
              <el-avatar v-if="message.role === 'user'" :size="36" :icon="User" />
              <el-avatar v-else :size="36" class="ai-avatar">
                <el-icon><Service /></el-icon>
              </el-avatar>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="renderMarkdown(message.content)"></div>
              <div class="message-time">
                {{ formatTime(message.createdAt) }}
              </div>
            </div>
          </div>
          
          <!-- AI 正在输入 -->
          <div v-if="isThinking" class="message assistant thinking">
            <div class="message-avatar">
              <el-avatar :size="36" class="ai-avatar">
                <el-icon><Service /></el-icon>
              </el-avatar>
            </div>
            <div class="message-content">
              <div class="thinking-indicator">
                <span class="dot"></span>
                <span class="dot"></span>
                <span class="dot"></span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 输入区域 -->
        <div class="input-container">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="1"
              :autosize="{ minRows: 1, maxRows: 6 }"
              placeholder="输入消息..."
              @keydown.enter.prevent="handleEnter"
              :disabled="isThinking"
            />
            <el-button
              type="primary"
              :icon="Promotion"
              circle
              :disabled="!inputMessage.trim() || isThinking"
              @click="sendMessageToAI"
              class="send-btn"
            />
          </div>
          <p class="input-hint">按 Enter 发送，Shift + Enter 换行</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  ChatDotSquare,
  ChatDotRound,
  Edit,
  Delete,
  DeleteFilled,
  User,
  Service,
  Promotion,
  TrendCharts,
  Switch,
  ChatLineSquare,
  Document
} from '@element-plus/icons-vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import {
  getSessions,
  getSessionDetail,
  createSession,
  sendMessage,
  updateSessionTitle,
  deleteSession,
  clearAllSessions,
  type ChatSession,
  type ChatMessage
} from '@/api/chat'

// 状态
const loading = ref(false)
const isThinking = ref(false)
const sessions = ref<ChatSession[]>([])
const currentSession = ref<ChatSession | null>(null)
const messages = ref<ChatMessage[]>([])
const inputMessage = ref('')
const searchQuery = ref('')
const editingSessionId = ref<number | null>(null)
const editingTitle = ref('')
const messagesContainer = ref<HTMLElement | null>(null)

// 计算属性
const filteredSessions = computed(() => {
  if (!searchQuery.value) return sessions.value
  const query = searchQuery.value.toLowerCase()
  return sessions.value.filter(s => s.title.toLowerCase().includes(query))
})

// 加载会话列表
const loadSessions = async () => {
  loading.value = true
  try {
    const res = await getSessions()
    if (res.code === 200) {
      sessions.value = res.data || []
    }
  } catch (error) {
    console.error('加载会话列表失败', error)
  } finally {
    loading.value = false
  }
}

// 选择会话
const selectSession = async (session: ChatSession) => {
  if (currentSession.value?.id === session.id) return
  
  try {
    const res = await getSessionDetail(session.id)
    if (res.code === 200) {
      currentSession.value = res.data
      messages.value = res.data.messages || []
      await nextTick()
      scrollToBottom()
    }
  } catch (error) {
    ElMessage.error('加载会话失败')
  }
}

// 创建新会话
const createNewSession = async () => {
  try {
    const res = await createSession()
    if (res.code === 200) {
      sessions.value.unshift(res.data)
      currentSession.value = res.data
      messages.value = []
    }
  } catch (error) {
    ElMessage.error('创建会话失败')
  }
}

// 发送消息
const sendMessageToAI = async () => {
  const content = inputMessage.value.trim()
  if (!content || isThinking.value) return
  
  inputMessage.value = ''
  isThinking.value = true
  
  // 如果没有当前会话，先创建
  let sessionId = currentSession.value?.id ?? null
  
  // 添加用户消息到界面
  const userMessage: ChatMessage = {
    id: Date.now(),
    role: 'user',
    content,
    createdAt: new Date().toISOString()
  }
  messages.value.push(userMessage)
  await nextTick()
  scrollToBottom()
  
  try {
    const res = await sendMessage({ sessionId, content })
    if (res.code === 200) {
      messages.value.push(res.data)
      
      // 如果是新会话，刷新会话列表获取新会话
      if (!sessionId) {
        await loadSessions()
        if (sessions.value.length > 0) {
          currentSession.value = sessions.value[0]
        }
      } else {
        // 更新会话列表中的标题（如果是第一条消息）
        await loadSessions()
      }
    }
  } catch (error: any) {
    ElMessage.error(error.message || '发送消息失败')
    // 移除用户消息
    messages.value.pop()
  } finally {
    isThinking.value = false
    await nextTick()
    scrollToBottom()
  }
}

// 处理 Enter 键
const handleEnter = (e: KeyboardEvent) => {
  if (e.shiftKey) {
    return // Shift + Enter 换行
  }
  sendMessageToAI()
}

// 发送建议问题
const sendSuggestion = async (content: string) => {
  inputMessage.value = content
  await createNewSession()
  await sendMessageToAI()
}

// 开始编辑标题
const startEditTitle = (session: ChatSession) => {
  editingSessionId.value = session.id
  editingTitle.value = session.title
}

// 保存标题
const saveTitle = async (sessionId: number) => {
  if (editingTitle.value.trim()) {
    try {
      await updateSessionTitle(sessionId, editingTitle.value.trim())
      const session = sessions.value.find(s => s.id === sessionId)
      if (session) {
        session.title = editingTitle.value.trim()
      }
    } catch (error) {
      ElMessage.error('更新标题失败')
    }
  }
  editingSessionId.value = null
}

// 确认删除会话
const confirmDeleteSession = async (session: ChatSession) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除对话"${session.title}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await deleteSession(session.id)
    sessions.value = sessions.value.filter(s => s.id !== session.id)
    if (currentSession.value?.id === session.id) {
      currentSession.value = null
      messages.value = []
    }
    ElMessage.success('删除成功')
  } catch (error) {
    // 取消删除
  }
}

// 清空所有会话
const clearAll = async () => {
  try {
    await clearAllSessions()
    sessions.value = []
    currentSession.value = null
    messages.value = []
    ElMessage.success('已清空所有对话')
  } catch (error) {
    ElMessage.error('清空失败')
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 渲染 Markdown
const renderMarkdown = (content: string) => {
  const html = marked.parse(content, { breaks: true }) as string
  return DOMPurify.sanitize(html)
}

// 格式化时间
const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  
  return date.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 监听消息变化，自动滚动
watch(messages, () => {
  nextTick(scrollToBottom)
}, { deep: true })

// 初始化
onMounted(() => {
  loadSessions()
})
</script>

<style scoped lang="scss">
.ai-recommend-page {
  display: flex;
  height: 100%;
  background: #f5f5f5;
}

// 侧边栏
.sidebar {
  width: 280px;
  background: #202123;
  display: flex;
  flex-direction: column;
  color: #fff;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.new-chat-btn {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  background: transparent;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  transition: all 0.2s;
  
  &:hover {
    background: rgba(255, 255, 255, 0.1);
  }
}

.session-list {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.session-search {
  padding: 12px 16px;
  
  :deep(.el-input__wrapper) {
    background: rgba(255, 255, 255, 0.1);
    border: none;
    box-shadow: none;
    
    .el-input__inner {
      color: #fff;
      
      &::placeholder {
        color: rgba(255, 255, 255, 0.5);
      }
    }
  }
}

.sessions-container {
  flex: 1;
  overflow-y: auto;
  padding: 0 8px;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 3px;
  }
}

.loading-sessions {
  padding: 16px;
}

.no-sessions {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 16px;
  color: rgba(255, 255, 255, 0.5);
  text-align: center;
  
  p {
    margin: 8px 0 0;
  }
  
  .hint {
    font-size: 12px;
    margin-top: 4px;
  }
}

.session-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin: 4px 0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    background: rgba(255, 255, 255, 0.1);
    
    .session-actions {
      opacity: 1;
    }
  }
  
  &.active {
    background: rgba(255, 255, 255, 0.15);
  }
}

.session-icon {
  margin-right: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.session-info {
  flex: 1;
  min-width: 0;
}

.session-title {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
}

.session-actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.2s;
  
  .el-icon {
    padding: 4px;
    border-radius: 4px;
    cursor: pointer;
    
    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }
  }
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.clear-btn {
  width: 100%;
  padding: 10px 16px;
  border: none;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  transition: all 0.2s;
  
  &:hover:not(:disabled) {
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
  }
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

// 主聊天区域
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

// 欢迎界面
.welcome-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}

.welcome-content {
  text-align: center;
  max-width: 600px;
}

.welcome-icon {
  color: #409eff;
  margin-bottom: 24px;
}

.welcome-content h1 {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px;
}

.welcome-desc {
  font-size: 16px;
  color: #606266;
  margin: 0 0 32px;
  line-height: 1.6;
}

.suggestion-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.suggestion-card {
  padding: 16px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 12px;
  
  &:hover {
    border-color: #409eff;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  }
  
  .el-icon {
    font-size: 24px;
    color: #409eff;
  }
  
  span {
    font-size: 14px;
    color: #303133;
  }
}

// 聊天界面
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  
  &::-webkit-scrollbar {
    width: 8px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #c0c4cc;
    border-radius: 4px;
  }
}

.empty-chat {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.message {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
  
  &.user {
    flex-direction: row-reverse;
    
    .message-content {
      align-items: flex-end;
    }
    
    .message-text {
      background: #409eff;
      color: #fff;
    }
  }
  
  &.assistant {
    .message-text {
      background: #fff;
      border: 1px solid #e4e7ed;
    }
  }
}

.message-avatar {
  flex-shrink: 0;
}

.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-width: calc(100% - 60px);
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  
  :deep(p) {
    margin: 0 0 8px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  :deep(ul), :deep(ol) {
    margin: 8px 0;
    padding-left: 20px;
  }
  
  :deep(code) {
    background: rgba(0, 0, 0, 0.1);
    padding: 2px 6px;
    border-radius: 4px;
    font-family: monospace;
  }
  
  :deep(pre) {
    background: #282c34;
    color: #abb2bf;
    padding: 12px;
    border-radius: 8px;
    overflow-x: auto;
    
    code {
      background: transparent;
      padding: 0;
    }
  }
  
  :deep(h1), :deep(h2), :deep(h3) {
    margin: 16px 0 8px;
    
    &:first-child {
      margin-top: 0;
    }
  }
  
  :deep(table) {
    border-collapse: collapse;
    margin: 8px 0;
    
    th, td {
      border: 1px solid #e4e7ed;
      padding: 8px 12px;
    }
    
    th {
      background: #f5f7fa;
    }
  }
}

.message-time {
  font-size: 12px;
  color: #909399;
}

// 思考动画
.thinking-indicator {
  display: flex;
  gap: 4px;
  padding: 16px;
  
  .dot {
    width: 8px;
    height: 8px;
    background: #909399;
    border-radius: 50%;
    animation: bounce 1.4s infinite ease-in-out both;
    
    &:nth-child(1) {
      animation-delay: -0.32s;
    }
    
    &:nth-child(2) {
      animation-delay: -0.16s;
    }
  }
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

// 输入区域
.input-container {
  padding: 16px 24px 24px;
  background: #f5f5f5;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  padding: 8px 8px 8px 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  
  :deep(.el-textarea__inner) {
    border: none;
    box-shadow: none;
    resize: none;
    padding: 8px 0;
    
    &:focus {
      box-shadow: none;
    }
  }
}

.send-btn {
  align-self: flex-end;
  margin-bottom: 4px;
}

.input-hint {
  text-align: center;
  font-size: 12px;
  color: #909399;
  margin: 8px 0 0;
}
</style>
