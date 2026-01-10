<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  // 标题
  title?: string
  // 副标题
  subtitle?: string
  // 是否带内边距
  noPadding?: boolean
  // 自定义类名
  customClass?: string
  // 头部是否需要分割线
  headerBorder?: boolean
}>()

const cardClass = computed(() => {
  return [
    'base-card',
    props.customClass
  ]
})
</script>

<template>
  <div :class="cardClass">
    <!-- Header Area -->
    <div 
      v-if="$slots.header || title" 
      class="card-header"
      :class="{ 'has-border': headerBorder }"
    >
      <div class="header-left">
        <h3 v-if="title" class="card-title">{{ title }}</h3>
        <p v-if="subtitle" class="card-subtitle">{{ subtitle }}</p>
        <slot name="header-left"></slot>
      </div>
      <div class="header-right">
        <slot name="header-actions"></slot>
      </div>
    </div>

    <!-- Body Area -->
    <div class="card-body" :class="{ 'no-padding': noPadding }">
      <slot></slot>
    </div>

    <!-- Footer Area -->
    <div v-if="$slots.footer" class="card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<style scoped lang="scss">
.base-card {
  background-color: var(--color-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-neutral-200);
  transition: box-shadow 0.3s ease, border-color 0.3s ease;
  overflow: hidden; /* Ensure content respects border radius */
  display: flex;
  flex-direction: column;

  &:hover {
    box-shadow: var(--shadow-md);
    border-color: var(--color-neutral-300);
  }
}

.card-header {
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  
  &.has-border {
    border-bottom: 1px solid var(--color-neutral-100);
  }
}

.header-left {
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 1.125rem; /* 18px */
  font-weight: 600;
  color: var(--color-neutral-900);
  line-height: 1.4;
}

.card-subtitle {
  font-size: 0.875rem;
  color: var(--color-neutral-500);
  margin-top: 4px;
}

.card-body {
  padding: 24px;
  flex: 1;
  position: relative; // For loading overlays if needed
  min-height: 0; // Critical for nested flex scroll
  overflow: hidden; // Let children handle their own scroll

  &.no-padding {
    padding: 0;
  }
}

.card-footer {
  padding: 16px 24px;
  background-color: var(--color-neutral-50);
  border-top: 1px solid var(--color-neutral-100);
  display: flex;
  align-items: center;
}
</style>
