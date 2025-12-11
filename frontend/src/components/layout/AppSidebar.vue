<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  House,
  Document,
  DataAnalysis,
  Reading,
  TrendCharts
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)

// 菜单项配置
const menuItems = [
  { index: '/', title: '首页', icon: House },
  { index: '/dashboard', title: '数据仪表盘', icon: TrendCharts },
  { index: '/resume', title: '简历管理', icon: Document },
  { index: '/career-assessment', title: '职业测评', icon: DataAnalysis },
  { index: '/career-report', title: '职业报告', icon: Reading },
]

const activeMenu = computed(() => route.path)

const handleSelect = (index: string) => {
  router.push(index)
}

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}
</script>

<template>
  <el-aside :width="isCollapse ? '64px' : '220px'" class="app-sidebar">
    <div class="logo-container" @click="toggleCollapse">
      <img src="@/assets/logo.svg" alt="Logo" class="logo" />
      <span v-if="!isCollapse" class="logo-title">Career Planner</span>
    </div>
    
    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      :collapse="isCollapse"
      :collapse-transition="true"
      background-color="#001529"
      text-color="#ffffffa6"
      active-text-color="#1890ff"
      @select="handleSelect"
    >
      <el-menu-item v-for="item in menuItems" :key="item.index" :index="item.index">
        <el-icon><component :is="item.icon" /></el-icon>
        <template #title>{{ item.title }}</template>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<style scoped>
.app-sidebar {
  background-color: #001529;
  height: 100vh;
  transition: width 0.3s;
  overflow: hidden;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64px;
  padding: 16px;
  cursor: pointer;
  border-bottom: 1px solid #ffffff1a;
}

.logo {
  width: 32px;
  height: 32px;
}

.logo-title {
  margin-left: 12px;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}
</style>
