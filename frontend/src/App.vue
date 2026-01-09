<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import AppHeader from '@/components/layout/AppHeader.vue'

const route = useRoute()

// 需要隐藏布局的页面（登录页、首页等）
const hideLayout = computed(() => route.meta.hideLayout === true)
</script>

<template>
  <!-- 无布局页面（登录、首页） -->
  <template v-if="hideLayout">
    <router-view />
  </template>

  <!-- 带侧边栏的布局页面 -->
  <template v-else>
    <el-container class="layout-container">
      <AppSidebar />
      <el-container class="layout-main-wrapper" direction="vertical">
        <AppHeader />
        <el-main class="layout-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </template>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body,
#app {
  width: 100%;
  height: 100%;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto,
    'Helvetica Neue', Arial, sans-serif;
}

/* 主布局容器 */
.layout-container {
  width: 100%;
  height: 100vh;
}

/* 右侧主区域 */
.layout-main-wrapper {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  height: 100vh;
}

/* 内容区 */
.layout-main {
  flex: 1;
  padding: 24px !important;
  overflow-y: auto;
  background-color: #f0f2f5;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
