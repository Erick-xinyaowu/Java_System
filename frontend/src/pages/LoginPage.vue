<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isLogin = ref(true)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const res = await userStore.login({
      username: loginForm.username,
      password: loginForm.password
    })

    if (res.success) {
      ElMessage.success('登录成功')
      const redirect = route.query.redirect as string
      router.push(redirect || '/dashboard')
    }
  } catch (error: any) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (!registerForm.username || !registerForm.email || !registerForm.password) {
    ElMessage.warning('请填写完整信息')
    return
  }

  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.warning('两次密码输入不一致')
    return
  }

  if (registerForm.password.length < 6) {
    ElMessage.warning('密码长度至少6位')
    return
  }

  loading.value = true
  try {
    const res = await userStore.register({
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password
    })

    if (res.success) {
      ElMessage.success('注册成功')
      router.push('/dashboard')
    }
  } catch (error: any) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

function toggleMode() {
  isLogin.value = !isLogin.value
}

function goHome() {
  router.push('/')
}
</script>

<template>
  <div class="login-container">
    <!-- Left Section: Visual & Brand -->
    <div class="brand-section">
      <div class="brand-content" @click="goHome">
        <h1 class="brand-logo">Career Planner</h1>
        <p class="brand-slogan">
          解锁你的职业潜能<br>
          智能驱动的学业与职业规划助手
        </p>
      </div>
      
      <!-- Abstract Background Shapes -->
      <div class="abstract-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="glass-overlay"></div>
      </div>
    </div>

    <!-- Right Section: Auth Form -->
    <div class="form-section">
      <div class="form-wrapper">
        <div class="form-header">
          <h2>{{ isLogin ? 'Welcome Back' : 'Create Account' }}</h2>
          <p class="form-subtitle">
            {{ isLogin ? '请输入您的凭据以访问面板' : '填写下方信息开始您的旅程' }}
          </p>
        </div>

        <transition name="fade-slide" mode="out-in">
          <!-- Login Form -->
          <el-form 
            v-if="isLogin" 
            class="custom-form" 
            @submit.prevent="handleLogin"
            hide-required-asterisk
          >
            <el-form-item>
              <el-input
                v-model="loginForm.username"
                placeholder="用户名"
                class="custom-input"
                :prefix-icon="User"
              />
            </el-form-item>
            <el-form-item>
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="密码"
                class="custom-input"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <div class="form-actions">
              <el-button type="primary" link class="forgot-pwd">忘记密码?</el-button>
            </div>
            <el-form-item>
              <button 
                class="primary-btn" 
                :disabled="loading" 
                @click.prevent="handleLogin"
              >
                <span v-if="!loading">登录</span>
                <span v-else>处理中...</span>
              </button>
            </el-form-item>
          </el-form>

          <!-- Register Form -->
          <el-form 
            v-else 
            class="custom-form" 
            @submit.prevent="handleRegister"
            hide-required-asterisk
          >
            <el-form-item>
              <el-input
                v-model="registerForm.username"
                placeholder="用户名"
                class="custom-input"
                :prefix-icon="User"
              />
            </el-form-item>
            <el-form-item>
              <el-input
                v-model="registerForm.email"
                placeholder="电子邮箱"
                class="custom-input"
                :prefix-icon="Message"
              />
            </el-form-item>
            <el-form-item>
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="设置密码"
                class="custom-input"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="确认密码"
                class="custom-input"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <button 
                class="primary-btn" 
                :disabled="loading" 
                @click.prevent="handleRegister"
              >
                <span v-if="!loading">注册账户</span>
                <span v-else>创建中...</span>
              </button>
            </el-form-item>
          </el-form>
        </transition>

        <div class="form-footer">
          <span>{{ isLogin ? "还没有账户?" : "已经有账号了?" }}</span>
          <button class="link-btn" @click="toggleMode">
            {{ isLogin ? '立即注册' : '直接登录' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-container {
  display: flex;
  min-height: 100vh;
  width: 100%;
  background-color: var(--color-white);
}

/* --- Brand Section (Left) --- */
.brand-section {
  flex: 1;
  background-color: #0f172a; /* Fallback */
  background: linear-gradient(135deg, var(--color-primary-900), var(--color-primary-800));
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 80px;
  overflow: hidden;
  color: white;

  @media (max-width: 900px) {
    display: none;
  }
}

.brand-content {
  position: relative;
  z-index: 10;
  cursor: pointer;
}

.brand-logo {
  font-size: 3.5rem;
  font-weight: 800;
  line-height: 1.1;
  margin-bottom: 1.5rem;
  background: linear-gradient(to right, #fff, #a5b4fc);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.brand-slogan {
  font-size: 1.25rem;
  line-height: 1.6;
  color: var(--color-primary-200);
  font-weight: 300;
}

/* Abstract Background */
.abstract-shapes {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: float 20s infinite ease-in-out alternate;
}

.shape-1 {
  background: var(--color-primary-600);
  width: 400px;
  height: 400px;
  top: -50px;
  right: -50px;
  animation-delay: -5s;
}

.shape-2 {
  background: #ec4899; /* Pink-ish accent */
  width: 300px;
  height: 300px;
  bottom: 10%;
  left: 10%;
  animation-duration: 25s;
}

.shape-3 {
  background: #3b82f6; /* Blue accent */
  width: 250px;
  height: 250px;
  top: 40%;
  right: 20%;
  animation-duration: 18s;
}

.glass-overlay {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top left, rgba(255,255,255,0.1), transparent 70%);
  z-index: 2;
}

@keyframes float {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(30px, 50px) scale(1.1); }
}

/* --- Form Section (Right) --- */
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background-color: var(--color-neutral-50);
}

.form-wrapper {
  width: 100%;
  max-width: 420px;
  padding: 48px;
  background-color: var(--color-white);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
}

.form-header {
  margin-bottom: 32px;
  text-align: center;
  
  h2 {
    font-size: 2rem;
    font-weight: 700;
    color: var(--color-neutral-900);
    margin-bottom: 8px;
  }
  
  .form-subtitle {
    color: var(--color-neutral-500);
    font-size: 0.95rem;
  }
}

/* Custom Input Styling Override */
.custom-input {
  --el-input-height: 48px;
  --el-input-bg-color: var(--color-neutral-50);
  --el-input-border-color: transparent;
  --el-input-hover-border-color: var(--color-neutral-200);
  --el-input-focus-border-color: var(--color-primary-500);
  font-size: 15px;
  
  :deep(.el-input__wrapper) {
    box-shadow: none !important;
    background-color: var(--color-neutral-50);
    border: 1px solid transparent;
    transition: all 0.3s ease;
    border-radius: var(--radius-md);
    padding-left: 16px;
  }
  
  :deep(.el-input__wrapper.is-focus) {
    background-color: var(--color-white);
    border-color: var(--color-primary-500);
    box-shadow: 0 0 0 4px var(--color-primary-100) !important;
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 24px;
}

/* Custom Button */
.primary-btn {
  width: 100%;
  height: 48px;
  background-color: var(--color-primary-600);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--shadow-md);
  
  &:hover {
    background-color: var(--color-primary-700);
    transform: translateY(-1px);
    box-shadow: var(--shadow-lg);
  }
  
  &:active {
    transform: translateY(0);
  }
  
  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }
}

.link-btn {
  background: none;
  border: none;
  color: var(--color-primary-600);
  font-weight: 600;
  cursor: pointer;
  margin-left: 8px;
  font-size: 14px;
  
  &:hover {
    text-decoration: underline;
  }
}

.form-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: var(--color-neutral-500);
}

/* Vue Transitions */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
