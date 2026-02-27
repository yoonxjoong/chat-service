<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50 p-6 font-sans antialiased text-slate-900">
    <div class="max-w-md w-full bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
      <!-- Top Branding -->
      <div class="p-8 md:p-10 text-center space-y-4">
        <div class="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-slate-900 text-white shadow-lg">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-6 h-6">
            <path d="M12 21.75c4.48 0 8.274-3.673 8.274-8.274 0-4.48-3.794-8.274-8.274-8.274-4.48 0-8.274 3.794-8.274 8.274 0 4.601 3.794 8.274 8.274 8.274zm0-15c3.673 0 6.774 3.1 6.774 6.726 0 3.626-3.1 6.726-6.774 6.726-3.674 0-6.774-3.1-6.774-6.726 0-3.626 3.1-6.726 6.774-6.726zM12 9a.75.75 0 01.75.75v3a.75.75 0 01-.75.75H9a.75.75 0 010-1.5h2.25V9.75A.75.75 0 0112 9z" />
          </svg>
        </div>
        <div>
          <h2 class="text-xl md:text-2xl font-black tracking-tight">내일수영</h2>
          <p class="text-xs md:text-sm text-slate-400 font-medium mt-1 uppercase tracking-[0.2em]">Start your record</p>
        </div>
      </div>

      <!-- Error Message -->
      <transition name="fade">
        <div v-if="error" class="mx-8 mb-4 bg-red-50 text-red-500 p-3.5 rounded-xl text-[11px] font-bold border border-red-100 flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-4 h-4">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
          </svg>
          아이디 또는 비밀번호를 확인해주세요.
        </div>
      </transition>

      <!-- Form Section -->
      <form class="p-8 md:p-10 pt-0 space-y-5" @submit.prevent="handleLogin">
        <div class="space-y-1.5">
          <label class="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Username</label>
          <input 
            v-model="form.username" 
            type="text" 
            required 
            class="w-full px-4 py-3.5 bg-slate-50 border border-slate-100 rounded-xl focus:bg-white focus:ring-1 focus:ring-slate-900 outline-none transition-all text-sm font-bold text-slate-700 placeholder:text-slate-300"
            placeholder="아이디 입력"
          />
        </div>

        <div class="space-y-1.5">
          <label class="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Password</label>
          <input 
            v-model="form.password" 
            type="password" 
            required 
            class="w-full px-4 py-3.5 bg-slate-50 border border-slate-100 rounded-xl focus:bg-white focus:ring-1 focus:ring-slate-900 outline-none transition-all text-sm font-bold text-slate-700 placeholder:text-slate-300"
            placeholder="비밀번호 입력"
          />
        </div>

        <button 
          type="submit" 
          :disabled="loading"
          class="w-full py-4 bg-slate-900 text-white rounded-xl font-bold text-sm shadow-lg shadow-slate-200 hover:bg-slate-800 transition-all active:scale-[0.98] disabled:opacity-50 flex items-center justify-center gap-2 mt-4"
        >
          <div v-if="loading" class="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full"></div>
          {{ loading ? '로그인 중' : '로그인' }}
        </button>

        <div class="pt-4 text-center">
          <p class="text-[11px] text-slate-400 font-medium">
            아직 계정이 없으신가요?
            <router-link to="/register" class="ml-1 font-bold text-blue-600 hover:text-blue-700 underline underline-offset-4">
              회원가입
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
  
  const params = new URLSearchParams()
  params.append('username', form.value.username)
  params.append('password', form.value.password)

  try {
    await axios.post('/api/member/login', params, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
    router.push('/')
  } catch (err) {
    error.value = true
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
