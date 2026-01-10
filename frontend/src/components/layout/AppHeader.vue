<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useLayoutStore } from '@/stores/layout'
import { Fold, Expand, User, SwitchButton, Bell, Search, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const layoutStore = useLayoutStore()

const isCollapsed = computed(() => layoutStore.isCollapsed)
const isLoggedIn = computed(() => userStore.isLoggedIn)
const username = computed(() => userStore.userInfo?.username || 'Guest')

// Simple mapping for demonstration. Ideally this comes from route meta
const pageTitle = computed(() => {
  const titleMap: Record<string, string> = {
    '/dashboard': 'Dashboard',
    '/resume': 'Resume Analysis',
    '/ai-chat': 'AI Consultant',
    '/profile': 'My Profile',
    '/admin': 'Admin Console'
  }
  // Try to match start of string
  const path = route.path
  for (const key in titleMap) {
    if (path.startsWith(key)) return titleMap[key]
  }
  return 'OverView'
})

function toggleSidebar() {
  layoutStore.toggleCollapse()
}

function handleCommand(command: string) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      userStore.logout()
      router.push('/login')
      break
  }
}

function goLogin() {
  router.push('/login') // Make sure this route exists
}
</script>

<template>
  <header class="app-header">
    <div class="header-left">
      <button class="icon-btn toggle-btn" @click="toggleSidebar">
        <el-icon :size="20">
          <Fold v-if="!isCollapsed" />
          <Expand v-else />
        </el-icon>
      </button>
      
      <div class="breadcrumb-area">
        <span class="page-title">{{ pageTitle }}</span>
      </div>
    </div>

    <div class="header-right">
      <!-- Search -->
      <div class="search-wrapper" v-if="isLoggedIn">
        <el-icon class="search-icon"><Search /></el-icon>
        <input type="text" placeholder="Search..." class="search-input" />
        <span class="search-shortcut">⌘K</span>
      </div>

      <!-- Notification Icon -->
      <button v-if="isLoggedIn" class="icon-btn notification-btn">
        <el-icon :size="18"><Bell /></el-icon>
        <span class="notification-dot"></span>
      </button>

      <div class="divider-vertical" v-if="isLoggedIn"></div>

      <template v-if="isLoggedIn">
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-profile-trigger">
            <el-avatar :size="36" :src="userStore.userInfo?.avatar || ''" class="custom-avatar">
              <el-icon :size="18"><User /></el-icon>
            </el-avatar>
            <div class="user-info">
              <span class="username">{{ username }}</span>
              <span class="user-role">Student</span>
            </div>
            <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="custom-dropdown">
              <div class="dropdown-header">
                <el-avatar :size="48" :src="userStore.userInfo?.avatar || ''" class="dropdown-avatar">
                  <el-icon :size="24"><User /></el-icon>
                </el-avatar>
                <div class="dropdown-user-info">
                  <span class="dropdown-name">{{ username }}</span>
                  <span class="dropdown-email">{{ userStore.userInfo?.email || 'user@email.com' }}</span>
                </div>
              </div>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>Profile Settings
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided class="text-danger">
                <el-icon><SwitchButton /></el-icon>Sign Out
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
      <template v-else>
        <button class="login-btn" @click="goLogin">
          <span>Sign In</span>
        </button>
      </template>
    </div>
  </header>
</template>

<style scoped lang="scss">
.app-header {
  height: 72px;
  padding: 0 28px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-neutral-100);
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 90;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 10px;
  border-radius: var(--radius-lg);
  color: var(--color-neutral-500);
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background-color: var(--color-neutral-100);
    color: var(--color-primary-500);
  }
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-neutral-900);
  letter-spacing: -0.02em;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* Search */
.search-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  background: var(--color-neutral-100);
  border: 1px solid transparent;
  border-radius: var(--radius-lg);
  padding: 8px 14px;
  transition: all var(--transition-normal);
  
  &:focus-within {
    background: white;
    border-color: var(--color-primary-300);
    box-shadow: var(--shadow-focus);
  }
}

.search-icon {
  color: var(--color-neutral-400);
  font-size: 16px;
}

.search-input {
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
  width: 180px;
  color: var(--color-neutral-700);
  
  &::placeholder {
    color: var(--color-neutral-400);
  }
}

.search-shortcut {
  font-size: 11px;
  font-weight: 500;
  color: var(--color-neutral-400);
  background: var(--color-neutral-200);
  padding: 2px 6px;
  border-radius: 4px;
}

.divider-vertical {
  width: 1px;
  height: 28px;
  background-color: var(--color-neutral-200);
  margin: 0 6px;
}

.notification-btn {
  position: relative;
  
  .notification-dot {
    position: absolute;
    top: 8px;
    right: 8px;
    width: 8px;
    height: 8px;
    background: linear-gradient(135deg, #ef4444 0%, #f87171 100%);
    border-radius: 50%;
    border: 2px solid white;
    animation: pulse 2s infinite;
  }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

.user-profile-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px 6px 6px;
  border-radius: var(--radius-xl);
  transition: all var(--transition-fast);
  
  &:hover {
    background-color: var(--color-neutral-100);
  }
}

.custom-avatar {
  background: var(--gradient-primary);
  color: white;
  border: 2px solid white;
  box-shadow: var(--shadow-sm);
}

.user-info {
  display: flex;
  flex-direction: column;
  text-align: left;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-neutral-800);
  line-height: 1.2;
}

.user-role {
  font-size: 12px;
  color: var(--color-neutral-500);
}

.dropdown-arrow {
  color: var(--color-neutral-400);
  font-size: 14px;
  transition: transform var(--transition-fast);
}

/* Dropdown Styles */
:deep(.custom-dropdown) {
  padding: 8px !important;
  min-width: 220px !important;
  border-radius: var(--radius-xl) !important;
  box-shadow: var(--shadow-xl) !important;
  border: 1px solid var(--color-neutral-100) !important;
}

.dropdown-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  margin-bottom: 8px;
  background: var(--color-neutral-50);
  border-radius: var(--radius-lg);
}

.dropdown-avatar {
  background: var(--gradient-primary);
  color: white;
}

.dropdown-user-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.dropdown-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-neutral-900);
}

.dropdown-email {
  font-size: 12px;
  color: var(--color-neutral-500);
}

:deep(.el-dropdown-menu__item) {
  padding: 10px 12px !important;
  border-radius: var(--radius-md) !important;
  font-size: 14px !important;
  
  .el-icon {
    margin-right: 8px;
  }
}

.login-btn {
  padding: 10px 24px;
  background: var(--gradient-primary);
  color: white;
  border: none;
  border-radius: var(--radius-full);
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-colored);
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 8px 25px -8px rgba(99, 102, 241, 0.5);
  }
}

.text-danger {
  color: var(--color-error) !important;
}
</style>
