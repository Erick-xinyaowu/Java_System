<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { User, Message, Location, School, EditPen, Camera } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { updateUserInfo } from '@/api/user'
import PageHeader from '@/components/ui/PageHeader.vue'
import BaseCard from '@/components/ui/BaseCard.vue'

const userStore = useUserStore()
const isEditing = ref(false)
const loading = ref(false)
const avatarUploading = ref(false)

// Init data from store
const formData = reactive({
  nickname: '',
  email: '',
  phone: '',
  intro: '',
  school: '',
  major: '',
  grade: ''
})

onMounted(() => {
  resetForm()
})

function resetForm() {
  const info = userStore.userInfo
  formData.nickname = info?.nickname || info?.username || ''
  formData.email = info?.email || ''
  formData.phone = info?.phone || ''
  formData.intro = info?.intro || ''
  formData.school = info?.school || ''
  formData.major = info?.major || ''
}

function startEditing() {
  isEditing.value = true
}

function cancelEditing() {
  isEditing.value = false
  resetForm()
}

async function saveProfile() {
  loading.value = true
  try {
     // 调用 API 保存用户信息
     await updateUserInfo({
       nickname: formData.nickname,
       email: formData.email,
       phone: formData.phone,
       school: formData.school,
       major: formData.major,
       intro: formData.intro
     })
     
     // 更新本地 store
     if (userStore.userInfo) {
       userStore.userInfo = { 
         ...userStore.userInfo, 
         nickname: formData.nickname,
         email: formData.email,
         phone: formData.phone,
         school: formData.school,
         major: formData.major,
         intro: formData.intro
       }
       // 同时更新 localStorage
       localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
     }
     ElMessage.success('个人资料已更新')
     isEditing.value = false
  } catch(e) {
     ElMessage.error('更新失败')
  } finally {
     loading.value = false
  }
}

// 头像上传前的检查
function beforeAvatarUpload(file: File) {
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

// 处理头像文件选择
async function handleAvatarChange(uploadFile: any) {
  const file = uploadFile.raw
  if (!beforeAvatarUpload(file)) return
  
  avatarUploading.value = true
  
  try {
    // 将图片转为 base64 上传
    const reader = new FileReader()
    reader.onload = async (e) => {
      const base64 = e.target?.result as string
      
      try {
        // 调用 API 更新头像
        await updateUserInfo({ avatar: base64 })
        
        // 更新本地 store 中的头像
        if (userStore.userInfo) {
          userStore.userInfo = { ...userStore.userInfo, avatar: base64 } as any
        }
        
        ElMessage.success('头像更新成功')
      } catch (error) {
        ElMessage.error('头像上传失败')
      } finally {
        avatarUploading.value = false
      }
    }
    reader.onerror = () => {
      ElMessage.error('图片读取失败')
      avatarUploading.value = false
    }
    reader.readAsDataURL(file)
  } catch (error) {
    ElMessage.error('头像上传失败')
    avatarUploading.value = false
  }
}
</script>

<template>
  <div class="profile-page">
    <PageHeader 
      title="个人资料" 
      description="管理您的账户设置和个人信息"
    />

    <div class="profile-grid">
      <!-- Left: Identity Card -->
      <div class="left-col">
        <BaseCard class="identity-card" no-padding>
           <div class="cover-photo"></div>
           <div class="avatar-section">
              <div class="avatar-wrapper">
                 <el-avatar :size="100" class="user-avatar" :src="userStore.userInfo?.avatar">
                    <el-icon :size="40"><User /></el-icon>
                 </el-avatar>
                 <el-upload
                    class="avatar-upload-trigger"
                    action="#"
                    :show-file-list="false"
                    :auto-upload="false"
                    :on-change="handleAvatarChange"
                    :disabled="avatarUploading"
                    accept="image/*"
                 >
                    <button class="camera-btn" :disabled="avatarUploading">
                       <el-icon v-if="avatarUploading" class="is-loading"><Camera /></el-icon>
                       <el-icon v-else><Camera /></el-icon>
                    </button>
                 </el-upload>
              </div>
              <h2 class="display-name">{{ formData.nickname || 'User' }}</h2>
              <p class="role-badge">学生</p>
           </div>
           
           <div class="stats-row">
              <div class="stat-item">
                 <div class="stat-val">12</div>
                 <div class="stat-label">简历数</div>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                 <div class="stat-val">85%</div>
                 <div class="stat-label">平均分</div>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                 <div class="stat-val">4</div>
                 <div class="stat-label">面试数</div>
              </div>
           </div>
        </BaseCard>

        <!-- Security / Quick Links can go here -->
      </div>

      <!-- Right: Settings Form -->
      <div class="right-col">
         <BaseCard title="Personal Information" header-border>
            <template #header-actions>
               <button v-if="!isEditing" class="action-btn" @click="startEditing">
                  <el-icon><EditPen /></el-icon> Edit Profile
               </button>
               <div v-else class="edit-actions">
                  <button class="action-btn cancel" @click="cancelEditing">Cancel</button>
                  <button class="action-btn save" @click="saveProfile" :disabled="loading">
                     {{ loading ? 'Saving...' : 'Save Changes' }}
                  </button>
               </div>
            </template>

            <form class="settings-form" @submit.prevent="saveProfile">
               <div class="form-grid">
                  <div class="form-group">
                     <label>Nickname</label>
                     <div class="input-wrapper">
                        <el-icon class="input-icon"><User /></el-icon>
                        <input 
                           v-model="formData.nickname" 
                           :disabled="!isEditing"
                           type="text" 
                           class="custom-input"
                        />
                     </div>
                  </div>

                  <div class="form-group">
                     <label>Email Address</label>
                     <div class="input-wrapper">
                        <el-icon class="input-icon"><Message /></el-icon>
                        <input 
                           v-model="formData.email" 
                           :disabled="!isEditing"
                           type="email" 
                           class="custom-input"
                        />
                     </div>
                  </div>

                  <div class="form-group">
                     <label>University / School</label>
                     <div class="input-wrapper">
                        <el-icon class="input-icon"><School /></el-icon>
                        <input 
                           v-model="formData.school" 
                           :disabled="!isEditing"
                           type="text" 
                           placeholder="e.g. Stanford University"
                           class="custom-input"
                        />
                     </div>
                  </div>
                  
                  <div class="form-group">
                     <label>Major</label>
                     <div class="input-wrapper">
                        <el-icon class="input-icon"><Location /></el-icon>
                        <input 
                           v-model="formData.major" 
                           :disabled="!isEditing"
                           type="text" 
                           placeholder="e.g. Computer Science"
                           class="custom-input"
                        />
                     </div>
                  </div>
                  
                  <div class="form-group full-width">
                     <label>Bio / Introduction</label>
                     <textarea 
                        v-model="formData.intro" 
                        :disabled="!isEditing"
                        rows="4"
                        class="custom-textarea"
                        placeholder="Tell us a bit about yourself..."
                     ></textarea>
                  </div>
               </div>
            </form>
         </BaseCard>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.profile-page {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  
  @media (max-width: 900px) {
    grid-template-columns: 1fr;
  }
}

/* Identity Card */
.identity-card {
  text-align: center;
  position: relative;
  overflow: hidden;
}

.cover-photo {
  height: 140px;
  background: var(--gradient-primary);
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -30%;
    width: 80%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.15) 0%, transparent 60%);
  }
  
  &::after {
    content: '';
    position: absolute;
    bottom: -50%;
    left: -20%;
    width: 60%;
    height: 150%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 50%);
  }
}

.avatar-section {
  position: relative;
  margin-top: -55px;
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-wrapper {
  position: relative;
  border: 5px solid var(--color-white);
  border-radius: 50%;
  box-shadow: var(--shadow-lg);
  background: white;
}

.user-avatar {
  background: var(--gradient-primary);
  color: white;
}

.camera-btn {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gradient-primary);
  border: 3px solid white;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
  
  &:hover {
    transform: scale(1.1);
    box-shadow: var(--shadow-lg);
  }
  
  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }
}

.display-name {
  margin-top: 16px;
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--color-neutral-900);
  letter-spacing: -0.02em;
}

.role-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  padding: 6px 14px;
  background: linear-gradient(135deg, var(--color-primary-50) 0%, rgba(139, 92, 246, 0.1) 100%);
  color: var(--color-primary-700);
  border-radius: var(--radius-full);
  font-size: 0.8rem;
  font-weight: 600;
  
  &::before {
    content: '🎓';
  }
}

.stats-row {
  display: flex;
  justify-content: space-around;
  padding: 28px 20px;
  border-top: 1px solid var(--color-neutral-100);
  background: var(--color-neutral-50);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 16px;
}

.stat-val {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--color-neutral-900);
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 0.8rem;
  color: var(--color-neutral-500);
  margin-top: 4px;
  font-weight: 500;
}

.stat-divider {
  width: 1px;
  background: linear-gradient(180deg, transparent, var(--color-neutral-200), transparent);
  height: 40px;
}

/* Settings Form */
.settings-form {
  margin-top: 8px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  
  @media (max-width: 640px) {
     grid-template-columns: 1fr;
  }
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  
  &.full-width {
    grid-column: 1 / -1;
  }
  
  label {
    font-size: 0.875rem;
    font-weight: 600;
    color: var(--color-neutral-700);
  }
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  
  .input-icon {
    position: absolute;
    left: 14px;
    color: var(--color-neutral-400);
    z-index: 1;
    transition: color var(--transition-fast);
  }
  
  &:focus-within .input-icon {
    color: var(--color-primary-500);
  }
}

.custom-input, .custom-textarea {
  width: 100%;
  padding: 12px 14px 12px 42px;
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-lg);
  font-size: 0.95rem;
  color: var(--color-neutral-800);
  transition: all var(--transition-normal);
  background-color: var(--color-white);
  
  &:disabled {
    background-color: var(--color-neutral-50);
    color: var(--color-neutral-600);
    cursor: not-allowed;
  }
  
  &:not(:disabled):hover {
    border-color: var(--color-neutral-300);
  }
  
  &:not(:disabled):focus {
    border-color: var(--color-primary-500);
    box-shadow: var(--shadow-focus);
    outline: none;
  }
}

.custom-textarea {
  padding: 14px;
  resize: vertical;
  min-height: 100px;
}

/* Actions */
.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border-radius: var(--radius-lg);
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid var(--color-neutral-200);
  background-color: var(--color-white);
  color: var(--color-neutral-700);
  transition: all var(--transition-normal);
  
  &:hover {
    background-color: var(--color-neutral-100);
    color: var(--color-neutral-900);
    transform: translateY(-1px);
  }
  
  &.save {
    background: var(--gradient-primary);
    color: white;
    border: none;
    box-shadow: var(--shadow-colored);
    
    &:hover { 
      box-shadow: 0 8px 25px -8px rgba(99, 102, 241, 0.5);
      transform: translateY(-2px);
    }
    
    &:disabled {
      opacity: 0.7;
      cursor: not-allowed;
      transform: none;
    }
  }
  
  &.cancel {
     border-color: transparent;
     color: var(--color-neutral-500);
     
     &:hover { 
       background-color: var(--color-error-light); 
       color: var(--color-error); 
     }
  }
}

.edit-actions {
  display: flex;
  gap: 10px;
}
</style>
