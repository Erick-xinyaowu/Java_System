<template>
  <div class="profile-page">
    <div class="profile-container">
      <!-- 头部信息卡片 -->
      <div class="profile-header">
        <div class="avatar-section">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :http-request="uploadAvatar"
          >
            <el-avatar :size="120" :src="userInfo.avatar || defaultAvatar">
              <el-icon :size="60"><User /></el-icon>
            </el-avatar>
            <div class="avatar-overlay">
              <el-icon><Camera /></el-icon>
              <span>更换头像</span>
            </div>
          </el-upload>
        </div>
        <div class="user-basic">
          <h2 class="username">{{ userInfo.nickname || userInfo.username }}</h2>
          <p class="user-id">用户ID: {{ userInfo.id }}</p>
          <p class="join-time">
            <el-icon><Clock /></el-icon>
            加入时间: {{ formatDate(userInfo.createdAt) }}
          </p>
        </div>
      </div>

      <!-- 个人资料表单 -->
      <el-card class="profile-form-card">
        <template #header>
          <div class="card-header">
            <span>
              <el-icon><EditPen /></el-icon>
              个人资料
            </span>
            <el-button 
              v-if="!isEditing" 
              type="primary" 
              text 
              @click="startEditing"
            >
              <el-icon><Edit /></el-icon>
              编辑资料
            </el-button>
          </div>
        </template>

        <el-form 
          ref="formRef"
          :model="formData" 
          :rules="rules"
          label-width="100px"
          :disabled="!isEditing"
        >
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="用户名">
                <el-input v-model="userInfo.username" disabled>
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
                <div class="form-tip">用户名不可修改</div>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="昵称" prop="nickname">
                <el-input 
                  v-model="formData.nickname" 
                  placeholder="请输入昵称"
                  maxlength="50"
                  show-word-limit
                >
                  <template #prefix>
                    <el-icon><UserFilled /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input 
                  v-model="formData.email" 
                  placeholder="请输入邮箱"
                  type="email"
                >
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input 
                  v-model="formData.phone" 
                  placeholder="请输入手机号"
                  maxlength="20"
                >
                  <template #prefix>
                    <el-icon><Iphone /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="性别">
                <el-radio-group v-model="formData.gender">
                  <el-radio :value="0">保密</el-radio>
                  <el-radio :value="1">男</el-radio>
                  <el-radio :value="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="生日">
                <el-date-picker
                  v-model="formData.birthday"
                  type="date"
                  placeholder="请选择生日"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  :disabled-date="disabledDate"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item v-if="isEditing" class="form-actions">
            <el-button type="primary" @click="saveProfile" :loading="saving">
              <el-icon><Check /></el-icon>
              保存修改
            </el-button>
            <el-button @click="cancelEditing">
              <el-icon><Close /></el-icon>
              取消
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 账号安全 -->
      <el-card class="security-card">
        <template #header>
          <div class="card-header">
            <span>
              <el-icon><Lock /></el-icon>
              账号安全
            </span>
          </div>
        </template>

        <div class="security-item">
          <div class="security-info">
            <el-icon class="security-icon"><Key /></el-icon>
            <div class="security-text">
              <span class="security-title">登录密码</span>
              <span class="security-desc">定期更换密码可以保护账号安全</span>
            </div>
          </div>
          <el-button type="primary" text @click="showPasswordDialog">
            修改密码
          </el-button>
        </div>

        <el-divider />

        <div class="security-item">
          <div class="security-info">
            <el-icon class="security-icon" :class="{ bound: userInfo.email }"><Message /></el-icon>
            <div class="security-text">
              <span class="security-title">邮箱绑定</span>
              <span class="security-desc">
                {{ userInfo.email ? `已绑定: ${userInfo.email}` : '未绑定邮箱' }}
              </span>
            </div>
          </div>
          <el-tag :type="userInfo.email ? 'success' : 'info'">
            {{ userInfo.email ? '已绑定' : '未绑定' }}
          </el-tag>
        </div>

        <el-divider />

        <div class="security-item">
          <div class="security-info">
            <el-icon class="security-icon" :class="{ bound: userInfo.phone }"><Iphone /></el-icon>
            <div class="security-text">
              <span class="security-title">手机绑定</span>
              <span class="security-desc">
                {{ userInfo.phone ? `已绑定: ${maskPhone(userInfo.phone)}` : '未绑定手机' }}
              </span>
            </div>
          </div>
          <el-tag :type="userInfo.phone ? 'success' : 'info'">
            {{ userInfo.phone ? '已绑定' : '未绑定' }}
          </el-tag>
        </div>
      </el-card>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码（至少6位）"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="changePassword" :loading="changingPassword">
          确认修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules, type UploadRawFile } from 'element-plus'
import {
  User,
  UserFilled,
  Camera,
  Clock,
  EditPen,
  Edit,
  Message,
  Iphone,
  Check,
  Close,
  Lock,
  Key
} from '@element-plus/icons-vue'
import { getUserInfo, updateUserInfo, type UpdateUserParams } from '@/api/user'
import request from '@/api/request'

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 用户信息
const userInfo = ref({
  id: 0,
  username: '',
  email: '',
  phone: '',
  nickname: '',
  avatar: '',
  gender: 0,
  birthday: '',
  status: 1,
  createdAt: ''
})

// 表单数据
const formData = reactive<UpdateUserParams>({
  nickname: '',
  email: '',
  phone: '',
  gender: 0,
  birthday: ''
})

// 状态
const isEditing = ref(false)
const saving = ref(false)
const formRef = ref<FormInstance>()

// 表单验证规则
const rules: FormRules = {
  nickname: [
    { max: 50, message: '昵称长度不能超过50个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 密码修改
const passwordDialogVisible = ref(false)
const changingPassword = ref(false)
const passwordFormRef = ref<FormInstance>()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: Function) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.code === 200) {
      Object.assign(userInfo.value, res.data)
      // 同步到表单
      formData.nickname = res.data.nickname || ''
      formData.email = res.data.email || ''
      formData.phone = res.data.phone || ''
      formData.gender = res.data.gender || 0
      formData.birthday = res.data.birthday || ''
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

// 开始编辑
const startEditing = () => {
  isEditing.value = true
}

// 取消编辑
const cancelEditing = () => {
  isEditing.value = false
  // 恢复原数据
  formData.nickname = userInfo.value.nickname || ''
  formData.email = userInfo.value.email || ''
  formData.phone = userInfo.value.phone || ''
  formData.gender = userInfo.value.gender || 0
  formData.birthday = userInfo.value.birthday || ''
}

// 保存资料
const saveProfile = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    saving.value = true
    try {
      await updateUserInfo(formData)
      ElMessage.success('保存成功')
      isEditing.value = false
      // 更新本地数据
      Object.assign(userInfo.value, formData)
      // 更新 localStorage 中的用户信息
      const storedUser = localStorage.getItem('userInfo')
      if (storedUser) {
        const user = JSON.parse(storedUser)
        user.nickname = formData.nickname
        user.email = formData.email
        localStorage.setItem('userInfo', JSON.stringify(user))
      }
    } catch (error) {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  })
}

// 头像上传
const beforeAvatarUpload = (file: UploadRawFile) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const uploadAvatar = async (options: any) => {
  const formData = new FormData()
  formData.append('file', options.file)
  
  try {
    // 这里假设有文件上传接口，暂时使用 base64
    const reader = new FileReader()
    reader.onload = async (e) => {
      const base64 = e.target?.result as string
      // 更新头像
      await updateUserInfo({ avatar: base64 })
      userInfo.value.avatar = base64
      ElMessage.success('头像更新成功')
    }
    reader.readAsDataURL(options.file)
  } catch (error) {
    ElMessage.error('头像上传失败')
  }
}

// 显示修改密码对话框
const showPasswordDialog = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    changingPassword.value = true
    try {
      await request.put('/user/password', {
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      })
      ElMessage.success('密码修改成功')
      passwordDialogVisible.value = false
    } catch (error) {
      // 错误已由拦截器处理
    } finally {
      changingPassword.value = false
    }
  })
}

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '未知'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 手机号脱敏
const maskPhone = (phone: string) => {
  if (!phone || phone.length < 7) return phone
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 禁用未来日期
const disabledDate = (date: Date) => {
  return date > new Date()
}

// 初始化
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped lang="scss">
.profile-page {
  min-height: 100%;
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.profile-container {
  max-width: 900px;
  margin: 0 auto;
}

// 头部信息卡片
.profile-header {
  display: flex;
  align-items: center;
  gap: 32px;
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
}

.avatar-section {
  position: relative;
  
  .avatar-uploader {
    cursor: pointer;
    position: relative;
    
    &:hover .avatar-overlay {
      opacity: 1;
    }
  }
  
  :deep(.el-avatar) {
    border: 4px solid rgba(255, 255, 255, 0.3);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  }
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
  font-size: 12px;
  gap: 4px;
  
  .el-icon {
    font-size: 24px;
  }
}

.user-basic {
  color: #fff;
  
  .username {
    margin: 0 0 8px;
    font-size: 28px;
    font-weight: 600;
  }
  
  .user-id {
    margin: 0 0 8px;
    font-size: 14px;
    opacity: 0.8;
  }
  
  .join-time {
    margin: 0;
    font-size: 14px;
    opacity: 0.8;
    display: flex;
    align-items: center;
    gap: 6px;
  }
}

// 表单卡片
.profile-form-card {
  margin-bottom: 24px;
  border-radius: 12px;
  
  :deep(.el-card__header) {
    padding: 16px 20px;
    border-bottom: 1px solid #ebeef5;
  }
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  
  > span {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 500;
    color: #303133;
  }
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.form-actions {
  margin-top: 24px;
  margin-bottom: 0;
  
  :deep(.el-form-item__content) {
    justify-content: flex-end;
  }
}

// 安全卡片
.security-card {
  border-radius: 12px;
  
  :deep(.el-card__header) {
    padding: 16px 20px;
    border-bottom: 1px solid #ebeef5;
  }
}

.security-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
}

.security-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.security-icon {
  font-size: 24px;
  color: #909399;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  
  &.bound {
    color: #67C23A;
    background: #f0f9eb;
  }
}

.security-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.security-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.security-desc {
  font-size: 12px;
  color: #909399;
}

:deep(.el-divider) {
  margin: 8px 0;
}

// 响应式
@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .user-basic .join-time {
    justify-content: center;
  }
  
  .el-row {
    .el-col {
      width: 100%;
      max-width: 100%;
      flex: 0 0 100%;
    }
  }
}
</style>
