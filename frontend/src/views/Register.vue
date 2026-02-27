<template>
  <!-- 가입 화면도 로그인(시작하기) 화면으로 통합하여 리다이렉트 느낌을 줌 -->
  <div class="min-h-screen flex items-center justify-center bg-slate-50 p-6 font-sans antialiased text-slate-900">
    <div class="text-center space-y-6">
      <div class="animate-spin h-8 w-8 border-4 border-slate-900 border-t-transparent rounded-full mx-auto"></div>
      <p class="text-sm font-bold text-slate-500 italic">통합 시작하기 화면으로 이동 중...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  // 제안해주신 4단계 설계에 따라 가입/로그인을 '시작하기' 하나로 통합
  router.replace('/login')
})
</script>
