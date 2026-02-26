<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50 px-4 py-12">
    <div class="max-w-lg w-full bg-white rounded-2xl shadow-xl shadow-slate-200/50 p-10 border border-slate-100 space-y-8">
      <div class="text-center">
        <h2 class="text-3xl font-extrabold text-primary-700 tracking-tight">회원가입</h2>
        <p class="mt-2 text-sm text-slate-500">새로운 계정을 만들고 시작하세요</p>
      </div>

      <form class="space-y-5" @submit.prevent="handleRegister">
        <!-- 아이디 중복 확인 -->
        <div class="space-y-1">
          <label class="block text-sm font-semibold text-slate-700">아이디</label>
          <div class="flex gap-3">
            <input 
              v-model="form.username" type="text" required 
              class="flex-1 px-4 py-3 rounded-xl border border-slate-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-200 outline-none transition-all"
              placeholder="아이디 입력"
              @input="isIdChecked = false"
            />
            <button 
              type="button" @click="checkDuplicate"
              class="px-5 py-3 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl font-bold text-sm transition-colors whitespace-nowrap"
            >
              중복 확인
            </button>
          </div>
          <p v-if="idMsg" :class="['text-xs mt-1 font-medium', idMsgType === 'success' ? 'text-green-600' : 'text-red-600']">
            {{ idMsg }}
          </p>
        </div>

        <!-- 닉네임 -->
        <div class="space-y-1">
          <label class="block text-sm font-semibold text-slate-700">닉네임</label>
          <input 
            v-model="form.nickname" type="text" required 
            class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-200 outline-none"
            placeholder="채팅에서 사용할 이름"
          />
        </div>

        <!-- 비밀번호 -->
        <div class="space-y-1">
          <label class="block text-sm font-semibold text-slate-700">비밀번호</label>
          <input 
            v-model="form.password" type="password" required 
            class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-200 outline-none"
            placeholder="비밀번호 입력"
          />
        </div>

        <div class="space-y-1">
          <label class="block text-sm font-semibold text-slate-700">비밀번호 확인</label>
          <input 
            v-model="form.passwordConfirm" type="password" required 
            class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-200 outline-none"
            placeholder="비밀번호 재입력"
          />
          <p v-if="pwMsg" :class="['text-xs mt-1 font-medium', pwMsgType === 'success' ? 'text-green-600' : 'text-red-600']">
            {{ pwMsg }}
          </p>
        </div>

        <button 
          type="submit" :disabled="!isFormValid || loading"
          class="w-full py-4 bg-primary-600 hover:bg-primary-700 text-white rounded-xl font-bold text-lg shadow-lg shadow-primary-200 transition-all active:scale-95 disabled:opacity-50 disabled:shadow-none mt-4"
        >
          {{ loading ? '처리 중...' : '가입하기' }}
        </button>

        <div class="text-center text-sm">
          <span class="text-slate-500">이미 계정이 있으신가요?</span>
          <router-link to="/login" class="ml-2 font-bold text-primary-600 hover:text-primary-700 underline underline-offset-4 decoration-primary-200">
            로그인
          </router-link>
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
    alert('회원가입이 완료되었습니다! 로그인해 주세요.')
    router.push('/login')
  } catch (err) {
    alert(err.response?.data?.message || '가입 중 오류가 발생했습니다.')
  } finally {
    loading.value = false
  }
}
</script>
