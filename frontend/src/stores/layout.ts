import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLayoutStore = defineStore('layout', () => {
  const isCollapsed = ref(false)

  function toggleCollapse() {
    isCollapsed.value = !isCollapsed.value
  }

  function setCollapse(value: boolean) {
    isCollapsed.value = value
  }

  return {
    isCollapsed,
    toggleCollapse,
    setCollapse
  }
})
