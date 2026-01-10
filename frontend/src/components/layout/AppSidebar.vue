<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useLayoutStore } from '@/stores/layout'
import {
  House,
  DataAnalysis,
  Document,
  ChatDotSquare,
  Monitor,
  UserFilled,
  User
} from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import { checkAdmin } from '@/api/admin'

const route = useRoute()
const router = useRouter()
const layoutStore = useLayoutStore()

const isCollapsed = computed(() => layoutStore.isCollapsed)
const isAdmin = ref(false)

const menuItems = computed(() => {
  const items = [
    { path: '/dashboard', title: '数据仪表盘', icon: DataAnalysis },
    { path: '/resume', title: '智能简历分析', icon: Document },
    { path: '/ai-chat', title: 'AI 职业顾问', icon: ChatDotSquare },
    { path: '/profile', title: '个人中心', icon: User }
  ]
  if (isAdmin.value) {
    items.push({ path: '/admin', title: '管理员控制台', icon: Monitor })
    items.push({ path: '/admin/users', title: '用户管理', icon: UserFilled })
  }
  return items
})

onMounted(async () => {
  try {
    const res = await checkAdmin()
    if (res.code === 200) {
      isAdmin.value = res.data
    }
  } catch (error) {
    console.error('Failed to check admin status:', error)
  }
})

const activePath = computed(() => route.path)

function handleSelect(path: string) {
  router.push(path)
}

function goHome() {
  router.push('/')
}
</script>

<template>
  <aside 
    class="app-sidebar" 
    :class="{ 'is-collapsed': isCollapsed }"
  >
    <!-- Logo -->
    <div class="sidebar-header" @click="goHome">
      <div class="logo-icon-wrapper">
        <el-icon :size="20" class="logo-icon">
          <House />
        </el-icon>
      </div>
      <transition name="fade">
        <span v-if="!isCollapsed" class="logo-text">Career Planner</span>
      </transition>
    </div>

    <!-- Navigation -->
    <nav class="sidebar-nav">
      <div 
        v-for="item in menuItems" 
        :key="item.path"
        class="nav-item-wrapper"
      >
        <div 
          class="nav-item" 
          :class="{ 'is-active': activePath === item.path }"
          @click="handleSelect(item.path)"
        >
          <el-icon :size="20" class="nav-icon">
            <component :is="item.icon" />
          </el-icon>
          <transition name="fade">
            <span v-if="!isCollapsed" class="nav-text">{{ item.title }}</span>
          </transition>
          
          <div v-if="activePath === item.path" class="active-indicator"></div>
        </div>
      </div>
    </nav>
  </aside>
</template>

<style scoped lang="scss">
.app-sidebar {
  width: 260px;
  height: 100vh;
  background-color: var(--color-white);
  border-right: 1px solid var(--color-neutral-200);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 100;
  flex-shrink: 0;

  &.is-collapsed {
    width: 80px;
    
    .nav-item {
      justify-content: center;
      padding: 0;
      width: 48px;
    }
    
    .logo-text, .nav-text {
      display: none;
    }
    
    .sidebar-header {
       justify-content: center;
       padding: 0;
    }
  }
}

.sidebar-header {
  height: 72px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  cursor: pointer;
  border-bottom: 1px solid transparent; /* Placeholder for potential border */
}

.logo-icon-wrapper {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--color-primary-500), var(--color-primary-700));
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 6px -1px rgba(99, 102, 241, 0.3);
  
  .logo-icon {
    color: white;
  }
}

.logo-text {
  margin-left: 12px;
  font-weight: 700;
  font-size: 18px;
  color: var(--color-neutral-900);
  white-space: nowrap;
  letter-spacing: -0.02em;
}

.sidebar-nav {
  flex: 1;
  padding: 24px 16px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 6px;
  
  .is-collapsed & {
    align-items: center;
    padding: 24px 0;
  }
}

.nav-item {
  height: 48px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-radius: var(--radius-md);
  cursor: pointer;
  color: var(--color-neutral-500);
  transition: all 0.2s ease;
  position: relative;
  font-weight: 500;
  
  &:hover {
    background-color: var(--color-neutral-50);
    color: var(--color-neutral-900);
  }
  
  &.is-active {
    background-color: var(--color-primary-50);
    color: var(--color-primary-600);
    
    .nav-icon {
      color: var(--color-primary-600); /* Ensure icon takes color */
    }
  }

  .nav-icon {
    flex-shrink: 0;
    transition: color 0.2s;
  }

  .nav-text {
    margin-left: 12px;
    font-size: 14px;
    white-space: nowrap;
  }
}

.active-indicator {
  position: absolute;
  right: -16px; /* Outside the item, on the edge of sidebar */
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 24px;
  background-color: var(--color-primary-500);
  border-radius: 4px 0 0 4px;
  display: none; /* Hidden for now, maybe enable for a different style */
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
