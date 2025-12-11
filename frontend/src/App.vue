<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppSidebar from '@/components/layout/AppSidebar.vue'

const route = useRoute()

// 检查是否需要隐藏布局（如登录页）
const hideLayout = computed(() => route.meta.hideLayout)
</script>

<template>
  <!-- 登录页等不需要布局的页面 -->
  <template v-if="hideLayout">
    <router-view />
  </template>
  
  <!-- 带布局的页面 -->
  <template v-else>
    <el-container class="app-container">
      <AppSidebar />
      <el-container class="main-container">
        <AppHeader />
        <el-main class="main-content">
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

html, body, #app {
  height: 100%;
  width: 100%;
}

.app-container {
  height: 100%;
}

.main-container {
  flex-direction: column;
}

.main-content {
  background-color: #f5f7fa;
  padding: 0;
  overflow-y: auto;
}

/* Element Plus 全局样式调整 */
.el-message-box {
  max-width: 420px;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
