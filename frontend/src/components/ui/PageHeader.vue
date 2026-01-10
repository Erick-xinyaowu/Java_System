<script setup lang="ts">
defineProps<{
  title: string
  description?: string
}>()
</script>

<template>
  <div class="page-header">
    <div class="header-content">
      <h1 class="page-title">{{ title }}</h1>
      <p v-if="description" class="page-desc">{{ description }}</p>
    </div>
    <div class="header-actions" v-if="$slots.actions">
      <slot name="actions"></slot>
    </div>
  </div>
</template>

<style scoped lang="scss">
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-6);
  
  @media (max-width: 640px) {
    flex-direction: column;
    gap: 16px;
  }
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  font-size: 1.75rem; /* 28px */
  font-weight: 700;
  color: var(--color-neutral-900);
  line-height: 1.2;
}

.page-desc {
  font-size: 1rem;
  color: var(--color-neutral-500);
  max-width: 600px;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}
</style>
