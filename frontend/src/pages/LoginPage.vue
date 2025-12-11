<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const isRegister = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

const validateConfirmPassword = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value !== loginForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (isRegister.value) {
          // 注册逻辑
          await userStore.register({
            username: loginForm.username,
            password: loginForm.password,
            email: loginForm.email
          })
          ElMessage.success('注册成功，请登录')
          isRegister.value = false
          loginForm.password = ''
          loginForm.confirmPassword = ''
        } else {
          // 登录逻辑
          await userStore.login({
            username: loginForm.username,
            password: loginForm.password
          })
          ElMessage.success('登录成功')
          router.push('/dashboard')
        }
      } catch (error: unknown) {
        ElMessage.error((error as Error).message || '操作失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const toggleMode = () => {
  isRegister.value = !isRegister.value
  formRef.value?.resetFields()
}
</script>

<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="brand">
          <h1>Career Planner</h1>
          <p>智能职业规划与学业分析系统</p>
        </div>
        <div class="features-list">
          <div class="feature-item">
            <el-icon :size="24"><User /></el-icon>
            <span>个性化职业测评</span>
          </div>
          <div class="feature-item">
            <el-icon :size="24"><Lock /></el-icon>
            <span>AI智能分析</span>
          </div>
        </div>
      </div>
      
      <div class="login-right">
        <el-card class="login-card" shadow="never">
          <h2 class="login-title">{{ isRegister ? '注册账号' : '欢迎回来' }}</h2>
          <p class="login-subtitle">{{ isRegister ? '创建新账号' : '请登录你的账号' }}</p>
          
          <el-form
            ref="formRef"
            :model="loginForm"
            :rules="loginRules"
            label-position="top"
            size="large"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
              />
            </el-form-item>
            
            <el-form-item v-if="isRegister" label="邮箱" prop="email">
              <el-input
                v-model="loginForm.email"
                placeholder="请输入邮箱"
                type="email"
              />
            </el-form-item>
            
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            
            <el-form-item v-if="isRegister" label="确认密码" prop="confirmPassword">
              <el-input
                v-model="loginForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                class="submit-btn"
                @click="handleSubmit"
              >
                {{ isRegister ? '注册' : '登录' }}
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="login-footer">
            <span>{{ isRegister ? '已有账号？' : '没有账号？' }}</span>
            <el-button type="primary" link @click="toggleMode">
              {{ isRegister ? '立即登录' : '立即注册' }}
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-container {
  display: flex;
  width: 900px;
  min-height: 500px;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-left {
  flex: 1;
  padding: 60px 40px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.brand h1 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
}

.brand p {
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 40px;
}

.features-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  opacity: 0.9;
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-card {
  width: 100%;
  border: none;
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #666;
  margin-bottom: 32px;
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

.login-footer {
  text-align: center;
  margin-top: 16px;
  color: #666;
  font-size: 14px;
}
</style>
