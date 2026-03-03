<template>
  <div class="min-h-screen">
    <router-view />
    <BottomNav />
  </div>
</template>

<script setup>
import BottomNav from './components/BottomNav.vue'
</script>

<style>
body { margin: 0; padding: 0; background-color: #f4f7f9; }
</style>
