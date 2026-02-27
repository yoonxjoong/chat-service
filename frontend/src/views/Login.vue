<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50 px-4">
    <div class="max-w-md w-full bg-white rounded-2xl shadow-xl shadow-slate-200/50 p-8 space-y-8 border border-slate-100">
      <div class="text-center">
        <h2 class="text-3xl font-black text-slate-800 tracking-tighter italic uppercase">Swim<span class="text-primary-600">Record</span></h2>
        <p class="mt-2 text-sm text-slate-400 font-bold">당신의 수영을 기록하고 공유하세요</p>
      </div>

      <div v-if="error" class="bg-red-50 text-red-600 p-3 rounded-2xl text-xs font-bold border border-red-100">
        아이디 또는 비밀번호가 올바르지 않습니다.
      </div>

      <form class="mt-8 space-y-6" @submit.prevent="handleLogin">
        <div class="space-y-4">
          <div>
            <label for="username" class="block text-sm font-semibold text-slate-700 mb-1">아이디</label>
            <input 
              id="username" v-model="form.username" type="text" required 
              class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-200 outline-none transition-all placeholder:text-slate-400"
              placeholder="아이디를 입력하세요"
            />
          </div>
          <div>
            <label for="password" class="block text-sm font-semibold text-slate-700 mb-1">비밀번호</label>
            <input 
              id="password" v-model="form.password" type="password" required 
              class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-200 outline-none transition-all placeholder:text-slate-400"
              placeholder="비밀번호를 입력하세요"
            />
          </div>
        </div>

        <button 
          type="submit" :disabled="loading"
          class="w-full py-4 bg-primary-600 hover:bg-primary-700 text-white rounded-xl font-bold text-lg shadow-lg shadow-primary-200 transition-all active:scale-95 disabled:opacity-50"
        >
          {{ loading ? '로그인 중...' : '로그인' }}
        </button>

        <div class="text-center text-sm">
          <span class="text-slate-500">계정이 없으신가요?</span>
          <router-link to="/register" class="ml-2 font-bold text-primary-600 hover:text-primary-700 underline underline-offset-4 decoration-primary-200">
            회원가입
          </router-link>
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
