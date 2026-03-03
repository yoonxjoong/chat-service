<template>
  <div class="min-h-screen flex items-center justify-center bg-white p-6 font-sans antialiased text-slate-900">
    <div class="max-w-sm w-full space-y-12">
      <!-- Logo & Welcome -->
      <div class="text-center space-y-4">
        <div class="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-slate-900 text-white shadow-xl mb-2">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-7 h-7">
            <path d="M12 21.75c4.48 0 8.274-3.673 8.274-8.274 0-4.48-3.794-8.274-8.274-8.274-4.48 0-8.274 3.794-8.274 8.274 0 4.601 3.794 8.274 8.274 8.274zm0-15c3.673 0 6.774 3.1 6.774 6.726 0 3.626-3.1 6.726-6.774 6.726-3.674 0-6.774-3.1-6.774-6.726 0-3.626 3.1-6.726 6.774-6.726zM12 9a.75.75 0 01.75.75v3a.75.75 0 01-.75.75H9a.75.75 0 010-1.5h2.25V9.75A.75.75 0 0112 9z" />
          </svg>
        </div>
        <div class="space-y-1">
          <h2 class="text-2xl font-black tracking-tighter">매일수영 시작하기</h2>
          <p class="text-sm text-slate-400 font-medium">더 나은 수영 라이프의 시작</p>
        </div>
      </div>

      <!-- Main Action Area -->
      <div class="space-y-3">
        <!-- 1. Social Logins -->
        <button @click="handleSocialLogin('kakao')" class="w-full py-4 bg-[#FEE500] text-[#191919] rounded-xl font-bold text-sm flex items-center justify-center gap-3 hover:opacity-90 transition-all active:scale-[0.98]">
          <img src="https://developers.kakao.com/assets/img/lib/logos/kakaotalksharing/kakaotalk_sharing_btn_medium.png" class="w-5 h-5" alt="Kakao">
          카카오로 계속하기
        </button>
        
        <button @click="handleSocialLogin('naver')" class="w-full py-4 bg-[#03C75A] text-white rounded-xl font-bold text-sm flex items-center justify-center gap-3 hover:opacity-90 transition-all active:scale-[0.98]">
          <span class="font-black text-lg leading-none">N</span>
          네이버로 계속하기
        </button>

        <button @click="handleSocialLogin('apple')" class="w-full py-4 bg-black text-white rounded-xl font-bold text-sm flex items-center justify-center gap-3 hover:opacity-90 transition-all active:scale-[0.98]">
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M17.05 20.28c-.98.95-2.05 1.61-3.22 1.61-1.14 0-1.55-.67-2.85-.67-1.31 0-1.89.65-2.87.67-1.14.02-2.15-.63-3.15-1.61-2.05-2.02-3.15-5.75-3.15-8.47 0-3.1 1.95-4.75 3.82-4.75 1.05 0 1.85.65 2.65.65.75 0 1.75-.75 2.95-.75 1.15 0 2.15.5 2.85 1.25-2.15 1.25-1.85 4.35.65 5.25-.65 1.55-1.45 3.1-2.73 4.82zM12.05 4.18c-.05-1.15.45-2.25 1.15-3.1.95-1.1 2.25-1.85 3.4-1.85.1 1.2-.4 2.35-1.1 3.15-.9 1.05-2.3 1.85-3.45 1.8z"/></svg>
          Apple로 계속하기
        </button>

        <!-- Divider -->
        <div class="relative py-6">
          <div class="absolute inset-0 flex items-center"><div class="w-full border-t border-slate-100"></div></div>
          <div class="relative flex justify-center text-[10px] font-bold uppercase tracking-widest text-slate-300 bg-white px-4">OR</div>
        </div>

        <!-- 2. Phone Number Login -->
        <div v-if="step === 'phone'" class="space-y-3 animate-in fade-in slide-in-from-bottom-2 duration-300">
          <div class="relative group">
            <input 
              v-model="phoneNumber" 
              type="tel" 
              inputmode="numeric"
              placeholder="휴대전화 번호 (- 제외)"
              class="w-full px-5 py-4 bg-slate-50 border border-slate-100 rounded-xl focus:bg-white focus:ring-1 focus:ring-slate-900 outline-none transition-all text-sm font-bold"
              @input="onlyNumbers"
              @keyup.enter="isValidPhone && sendCode()"
            />
          </div>
          <button @click="sendCode" :disabled="!isValidPhone" class="w-full py-4 bg-slate-900 text-white rounded-xl font-bold text-sm hover:bg-slate-800 transition-all active:scale-[0.98] disabled:opacity-30">
            인증번호 받기
          </button>
        </div>

        <!-- 3. Verification Code Input -->
        <div v-if="step === 'code'" class="space-y-6 animate-in fade-in slide-in-from-bottom-2 duration-300">
          <div class="text-center space-y-1">
            <p class="text-xs font-bold text-slate-500">{{ formatPhone(phoneNumber) }} 번호로 보냈어요.</p>
            <p class="text-[10px] font-bold text-blue-500 bg-blue-50 inline-block px-2 py-0.5 rounded-full uppercase tracking-tighter">테스트용 번호: 1234</p>
          </div>
          
          <div class="flex justify-center gap-3">
            <input 
              v-for="i in 4" :key="i"
              :id="'code-' + (i-1)"
              v-model="codes[i-1]"
              type="tel"
              inputmode="numeric"
              maxlength="1"
              autocomplete="one-time-code"
              class="w-12 h-14 bg-slate-50 border border-slate-100 rounded-xl text-center text-2xl font-black focus:bg-white focus:ring-1 focus:ring-slate-900 outline-none transition-all"
              @input="onCodeInput($event, i-1)"
              @keydown.delete="onCodeDelete($event, i-1)"
            />
          </div>

          <div class="flex flex-col gap-3 pt-2">
            <button @click="verifyCode" :disabled="!isCodeFull" class="w-full py-4 bg-slate-900 text-white rounded-xl font-bold text-sm active:scale-[0.98] disabled:opacity-30">
              인증 완료
            </button>
            <button @click="step = 'phone'" class="text-[11px] font-bold text-slate-400 hover:text-slate-600 transition-colors uppercase tracking-widest">
              번호 다시 입력하기
            </button>
          </div>
        </div>
      </div>

      <!-- Footer Info -->
      <p class="text-center text-[10px] text-slate-300 font-medium leading-relaxed px-4">
        시작하기를 누름으로써 매일수영의 <br>
        <router-link to="/terms" class="underline">이용약관</router-link> 및 <router-link to="/privacy" class="underline">개인정보처리방침</router-link>에 동의하게 됩니다.
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const step = ref('phone')
const phoneNumber = ref('')
const codes = ref(['', '', '', ''])
const loading = ref(false)

onMounted(async () => {
  // 1. Kakao SDK 초기화
  if (window.Kakao && !window.Kakao.isInitialized()) {
    window.Kakao.init('10370eb1fddb35728f39be1f5a057cb5');
  }

  // 2. 콜백 처리 (카카오 또는 네이버)
  const code = route.query.code;
  const state = route.query.state;

  if (code) {
    loading.value = true;
    try {
      let endpoint = '/api/member/login/kakao';
      let payload = { code, redirectUri: window.location.origin + '/login' };

      // state가 있으면 네이버 로그인으로 판단
      if (state) {
        endpoint = '/api/member/login/naver';
        payload = { code, state };
      }

      const response = await axios.post(endpoint, payload);
      console.log('Social login success', response.data);
      router.push('/');
    } catch (error) {
      console.error('Social login failed', error);
      alert('로그인 처리 중 오류가 발생했습니다.');
      router.replace('/login');
    } finally {
      loading.value = false;
    }
  }
})

const isValidPhone = computed(() => phoneNumber.value.length >= 10)
const isCodeFull = computed(() => codes.value.every(c => c !== ''))

const onlyNumbers = (e) => {
  phoneNumber.value = e.target.value.replace(/[^0-9]/g, '')
}

const formatPhone = (num) => {
  return num.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3')
}

const onCodeInput = (e, index) => {
  const val = e.target.value
  if (!/^[0-9]$/.test(val)) {
    codes.value[index] = ''
    return
  }
  if (val && index < 3) {
    nextTick(() => {
      document.getElementById(`code-${index + 1}`).focus()
    })
  }
  if (isCodeFull.value) {
    verifyCode()
  }
}

const onCodeDelete = (e, index) => {
  if (e.key === 'Backspace' && !codes.value[index] && index > 0) {
    nextTick(() => {
      document.getElementById(`code-${index - 1}`).focus()
    })
  }
}

const sendCode = () => {
  step.value = 'code'
  nextTick(() => {
    document.getElementById('code-0').focus()
  })
}

const verifyCode = async () => {
  if (loading.value) return
  loading.value = true
  const finalCode = codes.value.join('')
  try {
    await axios.post('/api/member/login/phone', {
      phoneNumber: phoneNumber.value,
      code: finalCode
    })
    router.push('/')
  } catch (err) {
    alert(err.response?.data?.message || '인증번호가 일치하지 않습니다. (1234를 입력해주세요)')
    codes.value = ['', '', '', '']
    document.getElementById('code-0').focus()
  } finally {
    loading.value = false
  }
}

const handleSocialLogin = (provider) => {
  const redirectUri = window.location.origin + '/login';

  if (provider === 'kakao') {
    if (!window.Kakao) return alert('카카오 SDK 로드 중...');
    if (!window.Kakao.isInitialized()) window.Kakao.init('10370eb1fddb35728f39be1f5a057cb5');
    
    // prompt 옵션을 제거하여 이미 동의한 사용자는 바로 로그인되도록 함
    window.Kakao.Auth.authorize({ 
      redirectUri
    });
  } 
  else if (provider === 'naver') {
    const clientId = 'yE_8k2KwVSQpt144laNn';
    const state = Math.random().toString(36).substring(2);
    // auth_type=reprompt를 제거하여 반복적인 동의 요청을 방지
    const naverAuthUrl = `https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${clientId}&redirect_uri=${encodeURIComponent(redirectUri)}&state=${state}`;
    window.location.href = naverAuthUrl;
  }
  else {
    alert(`${provider} 로그인은 현재 준비 중입니다.`)
  }
}
</script>

<style scoped>
.animate-in {
  animation-duration: 0.4s;
  animation-fill-mode: both;
}
</style>
