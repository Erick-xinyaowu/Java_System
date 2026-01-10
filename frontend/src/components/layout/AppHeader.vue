<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useLayoutStore } from '@/stores/layout'
import { Fold, Expand, User, SwitchButton, Bell } from '@element-plus/icons-vue'

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
      <!-- Notification Icon (Mockup) -->
      <button v-if="isLoggedIn" class="icon-btn notification-btn">
        <el-icon :size="18"><Bell /></el-icon>
        <span class="notification-dot"></span>
      </button>

      <div class="divider-vertical" v-if="isLoggedIn"></div>

      <template v-if="isLoggedIn">
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-profile-trigger">
            <el-avatar :size="32" :src="userStore.userInfo?.avatar || ''" :icon="User" class="custom-avatar" />
            <span class="username">{{ username }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="custom-dropdown">
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>Profile
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided class="text-danger">
                <el-icon><SwitchButton /></el-icon>Logout
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
      <template v-else>
        <button class="login-btn" @click="goLogin">Sign In</button>
      </template>
    </div>
  </header>
</template>

<style scoped lang="scss">
.app-header {
  height: 64px;
  padding: 0 24px;
  background-color: var(--color-white); /* Opaque background */
  /* backdrop-filter: blur(12px); // Removed for performance/compatibility, can enable if desired */
  border-bottom: 1px solid var(--color-neutral-200);
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 90;
  transition: all 0.3s ease;
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
  padding: 8px;
  border-radius: 8px;
  color: var(--color-neutral-500);
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background-color: var(--color-neutral-100);
    color: var(--color-primary-500);
  }
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-neutral-900);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.divider-vertical {
  width: 1px;
  height: 24px;
  background-color: var(--color-neutral-200);
  margin: 0 4px;
}

.notification-btn {
  position: relative;
  
  .notification-dot {
    position: absolute;
    top: 6px;
    right: 6px;
    width: 6px;
    height: 6px;
    background-color: var(--color-error);
    border-radius: 50%;
    border: 1px solid var(--color-white);
  }
}

.user-profile-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px 4px 4px;
  border-radius: 32px;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: var(--color-neutral-50);
  }
}

.custom-avatar {
  background-color: var(--color-primary-100);
  color: var(--color-primary-600);
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-neutral-700);
}

.login-btn {
  padding: 8px 20px;
  background-color: var(--color-primary-500);
  color: white;
  border: none;
  border-radius: var(--radius-full);
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: var(--color-primary-600);
  }
}

.text-danger {
  color: var(--color-error);
}
</style>
