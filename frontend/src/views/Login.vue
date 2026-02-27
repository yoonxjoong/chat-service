<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-white to-indigo-50 p-6">
    <div class="max-w-md w-full bg-white/80 backdrop-blur-lg rounded-3xl shadow-2xl shadow-indigo-100/50 p-10 border border-white/50 transition-all duration-300 hover:shadow-indigo-200/50">
      
      <!-- Brand Section -->
      <div class="text-center mb-10">
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-gradient-to-tr from-blue-600 to-indigo-600 text-white mb-6 shadow-lg shadow-blue-500/30">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-8 h-8">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
          </svg>
        </div>
        <h2 class="text-3xl font-bold text-slate-800 tracking-tight mb-2">내일 수영</h2>
        <p class="text-slate-500 text-sm">더 나은 내일을 위한 오늘의 기록</p>
      </div>

      <!-- Error Message -->
      <transition
        enter-active-class="transition ease-out duration-200"
        enter-from-class="opacity-0 translate-y-2"
        enter-to-class="opacity-100 translate-y-0"
        leave-active-class="transition ease-in duration-150"
        leave-from-class="opacity-100 translate-y-0"
        leave-to-class="opacity-0 translate-y-2"
      >
        <div v-if="error" class="mb-6 bg-red-50 text-red-600 p-4 rounded-xl text-sm font-medium border border-red-100 flex items-center gap-3 shadow-sm">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-5 h-5 flex-shrink-0">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
          </svg>
          아이디 또는 비밀번호를 확인해주세요.
        </div>
      </transition>

      <form class="space-y-6" @submit.prevent="handleLogin">
        <!-- Username Input -->
        <div class="space-y-2">
          <label for="username" class="block text-sm font-semibold text-slate-700 ml-1">아이디</label>
          <div class="relative group">
            <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-slate-400 group-focus-within:text-blue-500 transition-colors">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-5 h-5">
                <path d="M10 8a3 3 0 100-6 3 3 0 000 6zM3.465 14.493a1.23 1.23 0 00.41 1.412A9.957 9.957 0 0010 18c2.31 0 4.438-.784 6.131-2.1.43-.333.604-.903.408-1.41a7.002 7.002 0 00-13.074.003z" />
              </svg>
            </div>
            <input 
              id="username" 
              v-model="form.username" 
              type="text" 
              required 
              class="w-full pl-11 pr-4 py-3.5 bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10 outline-none transition-all duration-200 placeholder:text-slate-400 text-slate-800"
              placeholder="아이디 입력"
            />
          </div>
        </div>

        <!-- Password Input -->
        <div class="space-y-2">
          <label for="password" class="block text-sm font-semibold text-slate-700 ml-1">비밀번호</label>
          <div class="relative group">
            <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-slate-400 group-focus-within:text-blue-500 transition-colors">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-5 h-5">
                <path fill-rule="evenodd" d="M10 1a4.5 4.5 0 00-4.5 4.5V9H5a2 2 0 00-2 2v6a2 2 0 002 2h10a2 2 0 002-2v-6a2 2 0 00-2-2h-.5V5.5A4.5 4.5 0 0010 1zm3 8V5.5a3 3 0 10-6 0V9h6z" clip-rule="evenodd" />
              </svg>
            </div>
            <input 
              id="password" 
              v-model="form.password" 
              type="password" 
              required 
              class="w-full pl-11 pr-4 py-3.5 bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10 outline-none transition-all duration-200 placeholder:text-slate-400 text-slate-800"
              placeholder="비밀번호 입력"
            />
          </div>
        </div>

        <!-- Submit Button -->
        <button 
          type="submit" 
          :disabled="loading"
          class="w-full py-4 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white rounded-xl font-bold text-lg shadow-lg shadow-blue-500/30 transition-all duration-200 transform hover:-translate-y-0.5 active:translate-y-0 active:scale-[0.98] disabled:opacity-70 disabled:cursor-not-allowed flex items-center justify-center gap-2 mt-8"
        >
          <svg v-if="loading" class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          {{ loading ? '로그인 중...' : '로그인하기' }}
        </button>

        <!-- Footer -->
        <div class="pt-4 text-center">
          <p class="text-sm text-slate-500">
            아직 계정이 없으신가요?
            <router-link to="/register" class="ml-1 font-semibold text-blue-600 hover:text-blue-700 hover:underline transition-colors">
              회원가입하기
            </router-link>
          </p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = ref({ username: '', password: '' })
const error = ref(false)
const loading = ref(false)

const handleLogin = async () => {
  loading.value = true
  error.value = false
  
  // URLSearchParams 사용 (기존 백엔드 요구사항 준수)
  const params = new URLSearchParams()
  params.append('username', form.value.username)
  params.append('password', form.value.password)

  try {
    await axios.post('/api/member/login', params, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
    // 로그인 성공 시 약간의 딜레이를 주어 UX 개선 (선택사항)
    router.push('/')
  } catch (err) {
    error.value = true
    // 에러 발생 시 입력 필드 흔들림 효과 등을 줄 수 있음
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 추가적인 커스텀 애니메이션이 필요하다면 여기에 작성 */
</style>
