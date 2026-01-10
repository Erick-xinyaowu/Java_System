<template>
  <div class="user-manage-page">
    <!-- 头部 -->
    <header class="page-header">
      <div class="header-left">
        <el-icon :size="28" class="header-icon"><UserFilled /></el-icon>
        <div class="header-title">
          <h1>用户管理</h1>
          <p>管理系统所有用户账户</p>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Plus" @click="openCreateDialog">
          新建用户
        </el-button>
      </div>
    </header>

    <!-- 搜索和筛选 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名、邮箱、昵称、手机号..."
        :prefix-icon="Search"
        clearable
        @input="handleSearch"
        style="width: 320px;"
      />
      <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width: 120px;" @change="handleFilter">
        <el-option label="全部" value="" />
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button :icon="Refresh" @click="loadUsers">刷新</el-button>
    </div>

    <!-- 用户表格 -->
    <el-table
      v-loading="loading"
      :data="filteredUsers"
      stripe
      style="width: 100%"
      class="user-table"
    >
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="用户信息" min-width="200">
        <template #default="{ row }">
          <div class="user-info">
            <el-avatar :size="40" :src="row.avatar" :icon="UserFilled" />
            <div class="user-detail">
              <span class="username">{{ row.username }}</span>
              <span class="nickname">{{ row.nickname || '-' }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" min-width="180">
        <template #default="{ row }">
          {{ row.email || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="130">
        <template #default="{ row }">
          {{ row.phone || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="gender" label="性别" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.gender === 1" type="primary" size="small">男</el-tag>
          <el-tag v-else-if="row.gender === 2" type="danger" size="small">女</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            :disabled="row.username === 'admin'"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="170">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="openEditDialog(row)">
            编辑
          </el-button>
          <el-button type="warning" link :icon="Key" @click="openResetPasswordDialog(row)">
            重置密码
          </el-button>
          <el-popconfirm
            v-if="row.username !== 'admin'"
            title="确定要删除该用户吗？"
            confirm-button-text="确定"
            cancel-button-text="取消"
            @confirm="handleDelete(row)"
          >
            <template #reference>
              <el-button type="danger" link :icon="Delete">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalUsers"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 新建/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑用户' : '新建用户'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="formData.username" 
            placeholder="请输入用户名"
            :disabled="isEditing"
          />
        </el-form-item>
        <el-form-item v-if="!isEditing" label="密码" prop="password">
          <el-input 
            v-model="formData.password" 
            type="password" 
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="formData.gender">
            <el-radio :value="0">未知</el-radio>
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="生日">
          <el-date-picker
            v-model="formData.birthday"
            type="date"
            placeholder="选择生日"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="formData.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEditing ? '保存' : '创建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog
      v-model="resetPasswordVisible"
      title="重置密码"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="80px">
        <el-form-item label="用户">
          <el-input :value="selectedUser?.username" disabled />
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input
            v-model="passwordForm.password"
            type="password"
            placeholder="请输入新密码"
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
        <el-button @click="resetPasswordVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleResetPassword">
          重置密码
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import {
  UserFilled,
  Plus,
  Search,
  Refresh,
  Edit,
  Delete,
  Key
} from '@element-plus/icons-vue'
import {
  getAllUsers,
  searchUsers,
  createUser,
  updateUser,
  deleteUser,
  updateUserStatus,
  resetUserPassword,
  type UserManage,
  type UserManageRequest
} from '@/api/admin'

// 状态
const loading = ref(false)
const submitting = ref(false)
const users = ref<UserManage[]>([])
const searchKeyword = ref('')
const statusFilter = ref<number | ''>('')
const currentPage = ref(1)
const pageSize = ref(10)

// 对话框
const dialogVisible = ref(false)
const isEditing = ref(false)
const formRef = ref<FormInstance>()
const formData = ref<UserManageRequest>({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  gender: 0,
  birthday: '',
  status: 1
})

// 重置密码对话框
const resetPasswordVisible = ref(false)
const passwordFormRef = ref<FormInstance>()
const selectedUser = ref<UserManage | null>(null)
const passwordForm = ref({
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const formRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3-20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6-20 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ]
})

const passwordRules = reactive<FormRules>({
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6-20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== passwordForm.value.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 过滤后的用户列表
const filteredUsers = computed(() => {
  let result = users.value
  if (statusFilter.value !== '') {
    result = result.filter(u => u.status === statusFilter.value)
  }
  return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value)
})

const totalUsers = computed(() => {
  let result = users.value
  if (statusFilter.value !== '') {
    result = result.filter(u => u.status === statusFilter.value)
  }
  return result.length
})

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const res = searchKeyword.value 
      ? await searchUsers(searchKeyword.value)
      : await getAllUsers()
    if (res.code === 200) {
      users.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
let searchTimer: number | null = null
const handleSearch = () => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = window.setTimeout(() => {
    currentPage.value = 1
    loadUsers()
  }, 300)
}

// 筛选
const handleFilter = () => {
  currentPage.value = 1
}

// 分页
const handleSizeChange = () => {
  currentPage.value = 1
}

const handlePageChange = () => {
  // 分页变化时自动更新
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 打开新建对话框
const openCreateDialog = () => {
  isEditing.value = false
  formData.value = {
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
    gender: 0,
    birthday: '',
    status: 1
  }
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (user: UserManage) => {
  isEditing.value = true
  formData.value = {
    id: user.id,
    username: user.username,
    nickname: user.nickname,
    email: user.email,
    phone: user.phone,
    gender: user.gender,
    birthday: user.birthday,
    status: user.status
  }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      if (isEditing.value) {
        const res = await updateUser(formData.value.id!, formData.value)
        if (res.code === 200) {
          ElMessage.success('更新成功')
          dialogVisible.value = false
          loadUsers()
        }
      } else {
        const res = await createUser(formData.value)
        if (res.code === 200) {
          ElMessage.success('创建成功')
          dialogVisible.value = false
          loadUsers()
        }
      }
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

// 删除用户
const handleDelete = async (user: UserManage) => {
  try {
    const res = await deleteUser(user.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadUsers()
    }
  } catch (error: any) {
    ElMessage.error(error.message || '删除失败')
  }
}

// 更新状态
const handleStatusChange = async (user: UserManage) => {
  try {
    await updateUserStatus(user.id, user.status)
    ElMessage.success(user.status === 1 ? '已启用' : '已禁用')
  } catch (error: any) {
    // 恢复原状态
    user.status = user.status === 1 ? 0 : 1
    ElMessage.error(error.message || '操作失败')
  }
}

// 打开重置密码对话框
const openResetPasswordDialog = (user: UserManage) => {
  selectedUser.value = user
  passwordForm.value = { password: '', confirmPassword: '' }
  resetPasswordVisible.value = true
}

// 重置密码
const handleResetPassword = async () => {
  if (!passwordFormRef.value || !selectedUser.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      const res = await resetUserPassword(selectedUser.value!.id, passwordForm.value.password)
      if (res.code === 200) {
        ElMessage.success('密码重置成功')
        resetPasswordVisible.value = false
      }
    } catch (error: any) {
      ElMessage.error(error.message || '重置密码失败')
    } finally {
      submitting.value = false
    }
  })
}

// 初始化
onMounted(() => {
  loadUsers()
})
</script>

<style scoped lang="scss">
.user-manage-page {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  color: #409eff;
}

.header-title {
  h1 {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: #303133;
  }
  
  p {
    margin: 4px 0 0;
    font-size: 14px;
    color: #909399;
  }
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.user-table {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-detail {
  display: flex;
  flex-direction: column;
  
  .username {
    font-weight: 500;
    color: #303133;
  }
  
  .nickname {
    font-size: 12px;
    color: #909399;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
</style>
