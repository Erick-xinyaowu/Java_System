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
}

.cover-photo {
  height: 120px;
  background: linear-gradient(135deg, var(--color-primary-400), var(--color-primary-600));
}

.avatar-section {
  position: relative;
  margin-top: -50px;
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-wrapper {
  position: relative;
  border: 4px solid var(--color-white);
  border-radius: 50%;
  box-shadow: var(--shadow-sm);
}

.user-avatar {
  background-color: var(--color-neutral-100);
  color: var(--color-neutral-400);
}

.camera-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: var(--color-white);
  border: 1px solid var(--color-neutral-200);
  color: var(--color-neutral-600);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  transition: all 0.2s;
  
  &:hover {
    color: var(--color-primary-600);
    border-color: var(--color-primary-200);
  }
}

.display-name {
  margin-top: 12px;
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-neutral-900);
}

.role-badge {
  display: inline-block;
  margin-top: 4px;
  padding: 2px 10px;
  background-color: var(--color-primary-50);
  color: var(--color-primary-600);
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.stats-row {
  display: flex;
  justify-content: space-around;
  padding: 24px 16px;
  border-top: 1px solid var(--color-neutral-100);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-val {
  font-size: 1.125rem;
  font-weight: 700;
  color: var(--color-neutral-900);
}

.stat-label {
  font-size: 0.75rem;
  color: var(--color-neutral-500);
  margin-top: 2px;
}

.stat-divider {
  width: 1px;
  background-color: var(--color-neutral-200);
  height: 32px;
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
    font-weight: 500;
    color: var(--color-neutral-700);
  }
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  
  .input-icon {
    position: absolute;
    left: 12px;
    color: var(--color-neutral-400);
    z-index: 1;
  }
}

.custom-input, .custom-textarea {
  width: 100%;
  padding: 10px 12px 10px 36px;
  border: 1px solid var(--color-neutral-300);
  border-radius: var(--radius-md);
  font-size: 0.95rem;
  color: var(--color-neutral-800);
  transition: all 0.2s;
  background-color: var(--color-white);
  
  &:disabled {
    background-color: var(--color-neutral-50);
    color: var(--color-neutral-500);
    cursor: not-allowed;
  }
  
  &:not(:disabled):focus {
    border-color: var(--color-primary-500);
    box-shadow: 0 0 0 3px var(--color-primary-100);
    outline: none;
  }
}

.custom-textarea {
  padding: 12px;
  resize: vertical;
}

/* Actions */
.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: var(--radius-md);
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  border: 1px solid var(--color-neutral-300);
  background-color: var(--color-white);
  color: var(--color-neutral-700);
  transition: all 0.2s;
  
  &:hover {
    background-color: var(--color-neutral-50);
    color: var(--color-neutral-900);
  }
  
  &.save {
    background-color: var(--color-primary-600);
    color: white;
    border: none;
    
    &:hover { background-color: var(--color-primary-700); }
  }
  
  &.cancel {
     border-color: transparent;
     &:hover { background-color: var(--color-error); color: white; }
  }
}

.edit-actions {
  display: flex;
  gap: 8px;
}
</style>
