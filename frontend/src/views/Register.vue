<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50 p-6 font-sans antialiased text-slate-900">
    <div class="max-w-md w-full bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
      <!-- Top Section -->
      <div class="p-8 md:p-10 text-center space-y-4 pb-6">
        <h2 class="text-xl md:text-2xl font-black tracking-tight">회원가입</h2>
        <p class="text-[11px] md:text-xs text-slate-400 font-bold uppercase tracking-[0.2em]">Create your account</p>
      </div>

      <form class="p-8 md:p-10 pt-0 space-y-5" @submit.prevent="handleRegister">
        <!-- ID Input -->
        <div class="space-y-1.5">
          <label class="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Username</label>
          <div class="flex gap-2">
            <input 
              v-model="form.username" type="text" required 
              class="flex-1 px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:bg-white focus:ring-1 focus:ring-slate-900 outline-none transition-all text-sm font-bold text-slate-700 placeholder:text-slate-300"
              placeholder="아이디 입력"
              @input="isIdChecked = false"
            />
            <button 
              type="button" @click="checkDuplicate"
              class="px-4 py-3 bg-slate-100 hover:bg-slate-200 text-slate-600 rounded-xl font-bold text-[11px] transition-all active:scale-95 whitespace-nowrap"
            >
              중복 확인
            </button>
          </div>
          <p v-if="idMsg" :class="['text-[10px] ml-1 font-bold', idMsgType === 'success' ? 'text-green-500' : 'text-red-400']">
            {{ idMsg }}
          </p>
        </div>

        <!-- Nickname Input -->
        <div class="space-y-1.5">
          <label class="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Nickname</label>
          <input 
            v-model="form.nickname" type="text" required 
            class="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:bg-white focus:ring-1 focus:ring-slate-900 outline-none transition-all text-sm font-bold text-slate-700 placeholder:text-slate-300"
            placeholder="닉네임 입력"
          />
        </div>

        <!-- Password Input -->
        <div class="space-y-1.5">
          <label class="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Password</label>
          <input 
            v-model="form.password" type="password" required 
            class="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:bg-white focus:ring-1 focus:ring-slate-900 outline-none transition-all text-sm font-bold text-slate-700 placeholder:text-slate-300"
            placeholder="비밀번호 입력"
          />
        </div>

        <!-- Password Confirm -->
        <div class="space-y-1.5">
          <label class="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Confirm Password</label>
          <input 
            v-model="form.passwordConfirm" type="password" required 
            class="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:bg-white focus:ring-1 focus:ring-slate-900 outline-none transition-all text-sm font-bold text-slate-700 placeholder:text-slate-300"
            placeholder="비밀번호 재입력"
          />
          <p v-if="pwMsg" :class="['text-[10px] ml-1 font-bold', pwMsgType === 'success' ? 'text-green-500' : 'text-red-400']">
            {{ pwMsg }}
          </p>
        </div>

        <button 
          type="submit" :disabled="!isFormValid || loading"
          class="w-full py-4 bg-slate-900 text-white rounded-xl font-bold text-sm shadow-lg shadow-slate-200 hover:bg-slate-800 transition-all active:scale-[0.98] disabled:opacity-30 mt-4"
        >
          <div v-if="loading" class="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full"></div>
          {{ loading ? '가입 중' : '가입하기' }}
        </button>

        <div class="pt-4 text-center">
          <p class="text-[11px] text-slate-400 font-medium">
            이미 계정이 있으신가요?
            <router-link to="/login" class="ml-1 font-bold text-blue-600 hover:text-blue-700 underline underline-offset-4">
              로그인
            </router-link>
          </p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const isIdChecked = ref(false)
const idMsg = ref('')
const idMsgType = ref('error')

const form = ref({
  username: '',
  nickname: '',
  password: '',
  passwordConfirm: ''
})

const checkDuplicate = async () => {
  if (!form.value.username) return
  try {
    const res = await axios.get(`/api/member/check-id?username=${form.value.username}`)
    if (res.data.exists) {
      idMsg.value = '이미 사용 중인 아이디입니다.'
      idMsgType.value = 'error'
      isIdChecked.value = false
    } else {
      idMsg.value = '사용 가능한 아이디입니다.'
      idMsgType.value = 'success'
      isIdChecked.value = true
    }
  } catch (err) {
    idMsg.value = '확인 중 오류가 발생했습니다.'
  }
}

const pwMsg = computed(() => {
  if (!form.value.passwordConfirm) return ''
  return form.value.password === form.value.passwordConfirm ? '비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.'
})

const pwMsgType = computed(() => form.value.password === form.value.passwordConfirm ? 'success' : 'error')

const isFormValid = computed(() => {
  return isIdChecked.value && 
         form.value.nickname && 
         form.value.password && 
         form.value.password === form.value.passwordConfirm
})

const handleRegister = async () => {
  loading.value = true
  try {
    await axios.post('/api/member/register', {
      username: form.value.username,
      nickname: form.value.nickname,
      password: form.value.password
    })
    router.push('/login')
  } catch (err) {
    alert(err.response?.data?.message || '가입 중 오류가 발생했습니다.')
  } finally {
    loading.value = false
  }
}
</script>
