<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import AppHeader from '@/components/layout/AppHeader.vue'

const route = useRoute()

// 需要隐藏布局的页面（登录页、Landing Page 等）
const hideLayout = computed(() => route.meta.hideLayout === true)
</script>

<template>
  <!-- Full Screen Layout (Login, Landing) -->
  <router-view v-if="hideLayout" />

  <!-- App Layout (Dashboard style) -->
  <div v-else class="app-layout">
    <AppSidebar />
    
    <div class="main-content-wrapper">
      <AppHeader />
      
      <main class="main-content">
        <!-- Adding a transition wrapper for page transitions -->
        <router-view v-slot="{ Component }">
          <transition name="fade-page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
        
        <!-- Optional: Footer could go here -->
      </main>
    </div>
  </div>
</template>

<style lang="scss">
/* 
NOTE: Global resets and base styles are imported in main.ts -> styles/main.scss 
App.vue styles should strictly handle layout structure.
*/

.app-layout {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden; /* Prevent body scroll, handle scroll in main-content */
  background-color: var(--color-neutral-50);
}

.main-content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0; /* Important for preventing flex items from overflowing */
  position: relative;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: var(--space-6);
  position: relative;
  
  /* Scroll behavior */
  scroll-behavior: smooth;
}

/* Page Transition Effects */
.fade-page-enter-active,
.fade-page-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-page-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
