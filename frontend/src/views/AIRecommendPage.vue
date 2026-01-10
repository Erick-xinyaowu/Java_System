<script setup lang="ts">
import { ref, computed, nextTick, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, ChatDotRound, Delete, DeleteFilled,
  Promotion, UserFilled, Cpu, Search
} from '@element-plus/icons-vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import {
  getSessions,
  getSessionDetail,
  createSession,
  sendMessage as apiSendMessage,
  deleteSession as apiDeleteSession,
  clearAllSessions,
  type ChatSession,
  type ChatMessage
} from '@/api/chat'
import type { ApiResponse } from '@/api/request'

// --- State ---
const sessionsLoading = ref(false)
const sessions = ref<ChatSession[]>([])
const currentSessionId = ref<number | null>(null)
const messages = ref<ChatMessage[]>([])
const userInput = ref('')
const loading = ref(false)
const chatContainerRef = ref<HTMLElement | null>(null)
const searchQuery = ref('')

// --- Computed ---
const currentSession = computed(() => {
  return sessions.value.find(s => s.id === currentSessionId.value) || null
})

const filteredSessions = computed(() => {
  if (!searchQuery.value.trim()) return sessions.value
  const query = searchQuery.value.toLowerCase()
  return sessions.value.filter(s => s.title.toLowerCase().includes(query))
})

// --- Methods ---

// Load all sessions
async function loadSessions() {
  sessionsLoading.value = true
  try {
    const res = await getSessions() as unknown as ApiResponse<ChatSession[]>
    if (res.code === 200) {
      sessions.value = res.data || []
    }
  } catch (error) {
    console.error('加载会话列表失败', error)
  } finally {
    sessionsLoading.value = false
  }
}

// Select a session and load its messages
async function selectSession(id: number) {
  if (currentSessionId.value === id) return
  
  try {
    const res = await getSessionDetail(id) as unknown as ApiResponse<ChatSession>
    if (res.code === 200) {
      currentSessionId.value = id
      messages.value = res.data.messages || []
      scrollToBottom()
    }
  } catch (error) {
    ElMessage.error('加载对话失败')
  }
}

// Create new session
async function createNewSession() {
  try {
    const res = await createSession() as unknown as ApiResponse<ChatSession>
    if (res.code === 200) {
      sessions.value.unshift(res.data)
      currentSessionId.value = res.data.id
      messages.value = []
    }
  } catch (error) {
    ElMessage.error('创建对话失败')
  }
}

// Scroll to bottom
function scrollToBottom() {
  nextTick(() => {
    if (chatContainerRef.value) {
      chatContainerRef.value.scrollTop = chatContainerRef.value.scrollHeight
    }
  })
}

// Send message
async function sendMessageToAI() {
  const content = userInput.value.trim()
  if (!content || loading.value) return
  
  userInput.value = ''
  loading.value = true
  
  // Get current session ID (could be null for new conversation)
  let sessionId = currentSessionId.value
  
  // Optimistically add user message to UI
  const tempUserMsg: ChatMessage = {
    id: Date.now(),
    role: 'user',
    content,
    createdAt: new Date().toISOString()
  }
  messages.value.push(tempUserMsg)
  scrollToBottom()
  
  try {
    const res = await apiSendMessage({ sessionId, content }) as unknown as ApiResponse<ChatMessage>
    if (res.code === 200) {
      // Add AI response
      messages.value.push(res.data)
      
      // Refresh sessions list (for title update or new session)
      await loadSessions()
      
      // If it was a new conversation, select the first session
      if (!sessionId && sessions.value.length > 0) {
        currentSessionId.value = sessions.value[0].id
      }
    }
  } catch (error: unknown) {
    const errMsg = error instanceof Error ? error.message : '发送失败'
    ElMessage.error(errMsg)
    // Remove the temp user message on error
    messages.value.pop()
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

// Handle Enter key (with IME composition check)
const isComposing = ref(false)

function handleEnterKey(e: KeyboardEvent) {
  // 如果正在使用输入法组合输入（如中文），不发送
  if (e.isComposing || isComposing.value) return
  
  sendMessageToAI()
}

function onCompositionStart() {
  isComposing.value = true
}

function onCompositionEnd() {
  // 延迟重置，确保在 keydown 之后
  setTimeout(() => {
    isComposing.value = false
  }, 0)
}

// Delete session
async function deleteSession(id: number) {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个对话吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await apiDeleteSession(id)
    sessions.value = sessions.value.filter(s => s.id !== id)
    
    if (currentSessionId.value === id) {
      currentSessionId.value = null
      messages.value = []
    }
    
    ElMessage.success('删除成功')
  } catch {
    // User cancelled or error
  }
}

// Clear all sessions
async function clearAll() {
  try {
    await clearAllSessions()
    sessions.value = []
    currentSessionId.value = null
    messages.value = []
    ElMessage.success('已清空所有对话')
  } catch (error) {
    ElMessage.error('清空失败')
  }
}

// Quick start with a suggestion question
async function quickStart(content: string) {
  await createNewSession()
  userInput.value = content
  await sendMessageToAI()
}

// Render markdown content
function renderMarkdown(content: string) {
  const html = marked.parse(content, { breaks: true }) as string
  return DOMPurify.sanitize(html)
}

// Format time
function formatTime(time: string) {
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

// Watch messages and scroll
watch(messages, () => {
  nextTick(scrollToBottom)
}, { deep: true })

// Initialize
onMounted(() => {
  loadSessions()
})
</script>

<template>
  <div class="chat-layout">
    <!-- Sidebar -->
    <aside class="chat-sidebar">
      <div class="sidebar-header">
        <button class="new-chat-btn" @click="createNewSession">
          <el-icon><Plus /></el-icon>
          <span>新对话</span>
        </button>
      </div>

      <!-- Search -->
      <div class="session-search">
        <el-input
          v-model="searchQuery"
          placeholder="搜索对话..."
          :prefix-icon="Search"
          clearable
          size="small"
        />
      </div>

      <div class="session-list">
        <!-- Loading -->
        <div v-if="sessionsLoading" class="sessions-loading">
          <el-skeleton :rows="4" animated />
        </div>
        
        <!-- Empty -->
        <div v-else-if="filteredSessions.length === 0" class="no-sessions">
          <el-icon :size="40"><ChatDotRound /></el-icon>
          <p>暂无对话记录</p>
          <p class="hint">点击上方按钮开始新对话</p>
        </div>
        
        <!-- Sessions -->
        <div 
          v-else
          v-for="session in filteredSessions" 
          :key="session.id"
          class="session-item"
          :class="{ 'is-active': currentSessionId === session.id }"
          @click="selectSession(session.id)"
        >
          <div class="session-icon">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="session-text">
            <div class="session-title">{{ session.title }}</div>
            <div class="session-time">{{ formatTime(session.updatedAt) }}</div>
          </div>
          <button class="delete-btn" @click.stop="deleteSession(session.id)">
             <el-icon><Delete /></el-icon>
          </button>
        </div>
      </div>
      
      <!-- Clear All Button -->
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
              <span>清空所有对话</span>
            </button>
          </template>
        </el-popconfirm>
      </div>
    </aside>

    <!-- Main Chat Area -->
    <main class="chat-main">
      <div v-if="currentSession" class="chat-container">
        <!-- Messages -->
        <div class="messages-area" ref="chatContainerRef">
          <div v-if="messages.length === 0" class="empty-chat">
            <p>开始和AI职业顾问聊天吧！</p>
          </div>
          
          <div class="message-wrapper" v-for="msg in messages" :key="msg.id">
            <div class="message-row" :class="msg.role">
              <!-- Avatar -->
              <div class="avatar-wrapper">
                <div v-if="msg.role === 'assistant'" class="avatar ai-avatar">
                  <el-icon><Cpu /></el-icon>
                </div>
                <div v-else class="avatar user-avatar">
                  <el-icon><UserFilled /></el-icon>
                </div>
              </div>
              
              <!-- Bubble -->
              <div class="bubble">
                <div 
                  class="bubble-content"
                  :class="{ 'markdown-content': msg.role === 'assistant' }"
                  v-html="msg.role === 'assistant' ? renderMarkdown(msg.content) : msg.content"
                ></div>
                <div class="message-time">{{ formatTime(msg.createdAt) }}</div>
              </div>
            </div>
          </div>
           
          <!-- Loading Indicator -->
          <div v-if="loading" class="message-wrapper">
            <div class="message-row assistant">
              <div class="avatar-wrapper">
                <div class="avatar ai-avatar"><el-icon><Cpu /></el-icon></div>
              </div>
              <div class="bubble loading-bubble">
                <div class="dot-flashing"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Input Area -->
        <div class="input-area-wrapper">
          <div class="input-box">
            <div class="input-inner">
              <textarea 
                v-model="userInput" 
                placeholder="输入消息，按 Enter 发送..."
                @keydown.enter.exact.prevent="handleEnterKey"
                @compositionstart="onCompositionStart"
                @compositionend="onCompositionEnd"
                :disabled="loading"
              ></textarea>
              <button 
                class="send-btn" 
                :disabled="!userInput.trim() || loading"
                @click="sendMessageToAI"
              >
                <el-icon><Promotion /></el-icon>
              </button>
            </div>
            <div class="input-footer">
              按 Enter 发送，Shift + Enter 换行 | AI 回复仅供参考
            </div>
          </div>
        </div>
      </div>
      
      <!-- Empty State / Welcome -->
      <div v-else class="empty-state">
        <div class="welcome-box">
          <div class="welcome-icon">
            <el-icon :size="64"><Cpu /></el-icon>
          </div>
          <h1>职业规划 AI 助手</h1>
          <p>我是您的专业职业顾问"小智"，可以帮助您进行职业规划、简历优化、面试准备等。</p>
          <div class="suggestion-cards">
            <div class="suggestion-card" @click="quickStart('请帮我分析一下当前IT行业的就业趋势')">
              <span class="card-title">行业趋势分析</span>
            </div>
            <div class="suggestion-card" @click="quickStart('我想转行做产品经理，需要准备哪些技能？')">
              <span class="card-title">职业转型建议</span>
            </div>
            <div class="suggestion-card" @click="quickStart('请帮我准备几个常见的面试问题及答案')">
              <span class="card-title">面试问题准备</span>
            </div>
            <div class="suggestion-card" @click="quickStart('怎样才能让我的简历更有吸引力？')">
              <span class="card-title">简历优化技巧</span>
            </div>
          </div>
          <button class="start-btn" @click="createNewSession">开始新对话</button>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped lang="scss">
.chat-layout {
  display: flex;
  height: calc(100vh - 80px);
  background-color: var(--color-white);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  overflow: hidden;
  border: 1px solid var(--color-neutral-200);
  margin: 0 auto;
  max-width: 1400px;
}

/* --- Sidebar --- */
.chat-sidebar {
  width: 280px;
  background-color: var(--color-neutral-50);
  border-right: 1px solid var(--color-neutral-200);
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  padding-bottom: 12px;
}

.new-chat-btn {
  width: 100%;
  padding: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background-color: var(--color-white);
  border: 1px solid var(--color-neutral-300);
  border-radius: var(--radius-md);
  color: var(--color-neutral-700);
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--shadow-sm);
  
  &:hover {
    border-color: var(--color-primary-500);
    color: var(--color-primary-600);
    box-shadow: var(--shadow-md);
  }
}

.session-search {
  padding: 0 20px 12px;
  
  :deep(.el-input__wrapper) {
    background-color: var(--color-white);
    border-radius: var(--radius-md);
    box-shadow: none;
    border: 1px solid var(--color-neutral-200);
    
    &:hover, &.is-focus {
      border-color: var(--color-primary-400);
    }
  }
}

.session-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 10px;
}

.sessions-loading {
  padding: 16px;
}

.no-sessions {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 16px;
  color: var(--color-neutral-400);
  text-align: center;
  
  p {
    margin: 8px 0 0;
    font-size: 0.9rem;
  }
  
  .hint {
    font-size: 0.8rem;
    margin-top: 4px;
  }
}

.session-item {
  display: flex;
  align-items: flex-start;
  padding: 12px;
  border-radius: var(--radius-md);
  cursor: pointer;
  margin-bottom: 4px;
  transition: background-color 0.2s;
  position: relative;
  
  &:hover {
    background-color: var(--color-neutral-200);
    
    .delete-btn {
       opacity: 1;
    }
  }
  
  &.is-active {
    background-color: var(--color-white);
    box-shadow: var(--shadow-sm);
    
    .session-icon { color: var(--color-primary-500); }
  }
}

.session-icon {
  margin-top: 2px;
  color: var(--color-neutral-400);
  margin-right: 10px;
  flex-shrink: 0;
}

.session-text {
  flex: 1;
  overflow: hidden;
}

.session-title {
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--color-neutral-800);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-time {
  font-size: 0.75rem;
  color: var(--color-neutral-400);
  margin-top: 2px;
}

.delete-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--color-neutral-400);
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
  padding: 4px;
  border-radius: var(--radius-sm);
  
  &:hover { 
    color: var(--color-error);
    background-color: rgba(239, 68, 68, 0.1);
  }
}

.sidebar-footer {
  padding: 16px 20px;
  border-top: 1px solid var(--color-neutral-200);
}

.clear-btn {
  width: 100%;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: transparent;
  border: none;
  border-radius: var(--radius-md);
  color: var(--color-neutral-500);
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover:not(:disabled) {
    background-color: var(--color-neutral-200);
    color: var(--color-neutral-700);
  }
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

/* --- Main Chat --- */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  background-color: var(--color-white);
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  max-width: 900px;
  width: 100%;
  margin: 0 auto;
}

.empty-chat {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--color-neutral-400);
  font-size: 0.95rem;
}

.message-wrapper {
  margin-bottom: 24px;
}

.message-row {
  display: flex;
  gap: 16px;
  
  &.user {
    flex-direction: row-reverse;
    
    .bubble {
       background-color: var(--color-primary-500);
       color: white;
       border-radius: 18px 18px 4px 18px;
    }
    
    .user-avatar {
       background-color: var(--color-neutral-200);
       color: var(--color-neutral-600);
    }
    
    .message-time {
      text-align: right;
    }
  }
  
  &.assistant {
    .bubble {
       background-color: var(--color-neutral-50);
       color: var(--color-neutral-800);
       border: 1px solid var(--color-neutral-100);
       border-radius: 18px 18px 18px 4px;
    }
    
    .ai-avatar {
       background: linear-gradient(135deg, #6366f1, #8b5cf6);
       color: white;
    }
  }
}

.avatar-wrapper {
  flex-shrink: 0;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bubble {
  padding: 12px 16px;
  max-width: 70%;
  line-height: 1.6;
  font-size: 0.95rem;
  box-shadow: var(--shadow-sm);
  position: relative;
}

.bubble-content {
  white-space: pre-wrap;
  word-break: break-word;
  
  &.markdown-content {
    white-space: normal;
    
    :deep(p) {
      margin: 0 0 8px;
      &:last-child { margin-bottom: 0; }
    }
    
    :deep(ul), :deep(ol) {
      margin: 8px 0;
      padding-left: 20px;
    }
    
    :deep(li) {
      margin: 4px 0;
    }
    
    :deep(code) {
      background: rgba(0, 0, 0, 0.08);
      padding: 2px 6px;
      border-radius: 4px;
      font-family: 'Fira Code', monospace;
      font-size: 0.9em;
    }
    
    :deep(pre) {
      background: #1e1e1e;
      color: #d4d4d4;
      padding: 12px;
      border-radius: 8px;
      overflow-x: auto;
      margin: 8px 0;
      
      code {
        background: transparent;
        padding: 0;
        color: inherit;
      }
    }
    
    :deep(h1), :deep(h2), :deep(h3), :deep(h4) {
      margin: 16px 0 8px;
      font-weight: 600;
      &:first-child { margin-top: 0; }
    }
    
    :deep(blockquote) {
      border-left: 3px solid var(--color-primary-400);
      padding-left: 12px;
      margin: 8px 0;
      color: var(--color-neutral-600);
    }
    
    :deep(table) {
      border-collapse: collapse;
      margin: 8px 0;
      width: 100%;
      
      th, td {
        border: 1px solid var(--color-neutral-200);
        padding: 8px 12px;
        text-align: left;
      }
      
      th {
        background: var(--color-neutral-100);
        font-weight: 600;
      }
    }
    
    :deep(a) {
      color: var(--color-primary-500);
      text-decoration: underline;
    }
    
    :deep(strong) {
      font-weight: 600;
    }
  }
}

.message-time {
  font-size: 0.7rem;
  color: var(--color-neutral-400);
  margin-top: 6px;
}

.loading-bubble {
  padding: 16px 24px;
}

/* Input Area */
.input-area-wrapper {
  padding: 24px;
  background: white;
  border-top: 1px solid var(--color-neutral-100);
}

.input-box {
  max-width: 800px;
  margin: 0 auto;
}

.input-inner {
  position: relative;
  border: 1px solid var(--color-neutral-300);
  border-radius: var(--radius-lg);
  padding: 12px;
  background-color: var(--color-white);
  transition: border-color 0.2s, box-shadow 0.2s;
  display: flex;
  gap: 10px;
  align-items: flex-end;

  &:focus-within {
    border-color: var(--color-primary-500);
    box-shadow: 0 0 0 3px var(--color-primary-100);
  }
}

textarea {
  flex: 1;
  border: none;
  resize: none;
  min-height: 24px;
  max-height: 150px;
  outline: none;
  font-family: inherit;
  font-size: 1rem;
  padding: 0;
  line-height: 24px;
  
  &:disabled {
    background: transparent;
    color: var(--color-neutral-400);
  }
}

.send-btn {
  background-color: var(--color-primary-500);
  color: white;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.2s;
  flex-shrink: 0;
  
  &:disabled {
    background-color: var(--color-neutral-300);
    cursor: default;
  }
  
  &:not(:disabled):hover {
    background-color: var(--color-primary-600);
  }
}

.input-footer {
  text-align: center;
  margin-top: 8px;
  font-size: 0.75rem;
  color: var(--color-neutral-400);
}

/* Loading Dot Animation */
.dot-flashing {
  margin: 0 8px;
  width: 6px;
  height: 6px;
  border-radius: 5px;
  background-color: var(--color-primary-400);
  animation: dot-flashing 1s infinite linear alternate;
  animation-delay: 0.5s;
  position: relative;
  left: -15px;
}
.dot-flashing::before, .dot-flashing::after {
  content: "";
  display: inline-block;
  position: absolute;
  top: 0;
}
.dot-flashing::before {
  left: 15px;
  width: 6px;
  height: 6px;
  border-radius: 5px;
  background-color: var(--color-primary-400);
  animation: dot-flashing 1s infinite alternate;
  animation-delay: 0s;
}
.dot-flashing::after {
  left: 30px;
  width: 6px;
  height: 6px;
  border-radius: 5px;
  background-color: var(--color-primary-400);
  animation: dot-flashing 1s infinite alternate;
  animation-delay: 1s;
}

@keyframes dot-flashing {
  0% { background-color: var(--color-primary-500); }
  50%, 100% { background-color: var(--color-primary-200); }
}

/* Empty State / Welcome */
.empty-state {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.welcome-box {
  text-align: center;
  background: var(--color-neutral-50);
  padding: 48px 40px;
  border-radius: var(--radius-xl);
  max-width: 600px;
  
  h1 { 
    margin-bottom: 12px;
    font-size: 1.75rem;
    font-weight: 600;
    color: var(--color-neutral-900);
  }
  
  > p { 
    margin-bottom: 32px; 
    color: var(--color-neutral-500);
    line-height: 1.6;
  }
}

.welcome-icon {
  margin-bottom: 16px;
  color: var(--color-primary-500);
  
  :deep(.el-icon) {
    background: linear-gradient(135deg, #6366f1, #8b5cf6);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

.suggestion-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.suggestion-card {
  padding: 14px 16px;
  background: var(--color-white);
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
  
  &:hover {
    border-color: var(--color-primary-400);
    box-shadow: var(--shadow-md);
    transform: translateY(-2px);
  }
  
  .card-title {
    font-size: 0.9rem;
    font-weight: 500;
    color: var(--color-neutral-700);
  }
}

.start-btn {
  padding: 12px 28px;
  background-color: var(--color-primary-500);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover { 
    background-color: var(--color-primary-600);
    transform: translateY(-1px);
    box-shadow: var(--shadow-md);
  }
}
</style>
