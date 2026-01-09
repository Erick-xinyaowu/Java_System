<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useLayoutStore } from '@/stores/layout'
import {
  House,
  DataAnalysis,
  Document,
  Compass,
  Reading
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const layoutStore = useLayoutStore()

const isCollapsed = computed(() => layoutStore.isCollapsed)

const menuItems = [
  { path: '/dashboard', title: '数据仪表盘', icon: DataAnalysis },
  { path: '/resume', title: '简历管理', icon: Document },
  { path: '/career-assessment', title: '职业测评', icon: Compass },
  { path: '/career-report', title: '职业报告', icon: Reading }
]

const activeMenu = computed(() => route.path)

function handleSelect(path: string) {
  router.push(path)
}

function goHome() {
  router.push('/')
}
</script>

<template>
  <el-aside :width="isCollapsed ? '64px' : '220px'" class="sidebar">
    <div class="logo" @click="goHome">
      <el-icon :size="28" color="#4F46E5">
        <House />
      </el-icon>
      <span v-show="!isCollapsed" class="logo-text">Career Planner</span>
    </div>

    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapsed"
      :collapse-transition="false"
      class="sidebar-menu"
      background-color="#001529"
      text-color="rgba(255,255,255,0.65)"
      active-text-color="#fff"
      @select="handleSelect"
    >
      <el-menu-item
        v-for="item in menuItems"
        :key="item.path"
        :index="item.path"
      >
        <el-icon><component :is="item.icon" /></el-icon>
        <template #title>{{ item.title }}</template>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<style scoped>
.sidebar {
  background-color: #001529;
  height: 100vh;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
}

:deep(.el-menu-item.is-active) {
  background-color: #4F46E5 !important;
}

:deep(.el-menu-item:hover) {
  background-color: rgba(79, 70, 229, 0.5) !important;
}
</style>
