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
      <div class="nav-section-label" v-if="!isCollapsed">主菜单</div>
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
          <div class="nav-icon-wrapper" :class="{ 'active': activePath === item.path }">
            <el-icon :size="18" class="nav-icon">
              <component :is="item.icon" />
            </el-icon>
          </div>
          <transition name="fade">
            <span v-if="!isCollapsed" class="nav-text">{{ item.title }}</span>
          </transition>
        </div>
      </div>
    </nav>


  </aside>
</template>

<style scoped lang="scss">
.app-sidebar {
  width: 280px;
  height: 100vh;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  border-right: 1px solid var(--color-neutral-100);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 100;
  flex-shrink: 0;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 200px;
    background: var(--gradient-glow);
    pointer-events: none;
  }

  &.is-collapsed {
    width: 80px;
    
    .nav-item {
      justify-content: center;
      padding: 0;
    }
    
    .nav-icon-wrapper {
      margin-right: 0;
    }
    
    .logo-text, .nav-text, .nav-section-label {
      display: none;
    }
    
    .sidebar-header {
       justify-content: center;
       padding: 0;
    }
  }
}

.sidebar-header {
  height: 80px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  cursor: pointer;
  position: relative;
  z-index: 1;
}

.logo-icon-wrapper {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: var(--shadow-colored);
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
  
  &:hover {
    transform: scale(1.05);
    box-shadow: 0 12px 40px -8px rgba(99, 102, 241, 0.5);
  }
  
  .logo-icon {
    color: white;
  }
}

.logo-text {
  margin-left: 14px;
  font-weight: 800;
  font-size: 20px;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  white-space: nowrap;
  letter-spacing: -0.03em;
}

.nav-section-label {
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--color-neutral-400);
  padding: 0 16px;
  margin-bottom: 8px;
}

.sidebar-nav {
  flex: 1;
  padding: 16px 16px 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 4px;
  position: relative;
  z-index: 1;
  
  .is-collapsed & {
    align-items: center;
    padding: 24px 16px;
  }
}

.nav-item {
  height: 48px;
  display: flex;
  align-items: center;
  padding: 0 12px;
  border-radius: var(--radius-lg);
  cursor: pointer;
  color: var(--color-neutral-600);
  transition: all 0.2s ease;
  position: relative;
  font-weight: 500;
  
  &:hover {
    background-color: var(--color-neutral-100);
    color: var(--color-neutral-900);
    
    .nav-icon-wrapper {
      background-color: var(--color-neutral-200);
    }
  }
  
  &.is-active {
    background: linear-gradient(135deg, var(--color-primary-50) 0%, rgba(139, 92, 246, 0.08) 100%);
    color: var(--color-primary-700);
    box-shadow: var(--shadow-sm);
    
    .nav-icon-wrapper.active {
      background: var(--gradient-primary);
      box-shadow: var(--shadow-colored);
      
      .nav-icon {
        color: white;
      }
    }
  }
}

.nav-icon-wrapper {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  background-color: var(--color-neutral-100);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
  transition: all 0.2s ease;

  .nav-icon {
    transition: color 0.2s;
  }
}

.nav-text {
  font-size: 14px;
  white-space: nowrap;
  font-weight: 500;
}

.sidebar-footer {
  padding: 16px;
  position: relative;
  z-index: 1;
}

.footer-card {
  background: var(--gradient-primary);
  border-radius: var(--radius-xl);
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
  box-shadow: var(--shadow-colored);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 15px 40px -10px rgba(99, 102, 241, 0.5);
  }
}

.footer-icon {
  font-size: 24px;
  background: rgba(255,255,255,0.2);
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.footer-title {
  color: white;
  font-weight: 600;
  font-size: 14px;
}

.footer-desc {
  color: rgba(255,255,255,0.8);
  font-size: 12px;
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
