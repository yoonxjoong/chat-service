<template>
  <div class="min-h-screen bg-slate-50 flex flex-col pb-12">
    <AppHeader />

    <main class="flex-1 p-4 md:p-8 max-w-3xl mx-auto w-full space-y-6">
      <div class="flex flex-col mb-4">
        <h2 class="text-3xl font-black text-slate-800 tracking-tighter">설정</h2>
        <p class="text-sm text-slate-400 font-bold uppercase tracking-widest">Settings & Privacy</p>
      </div>

      <!-- Profile Section -->
      <section class="bg-white rounded-[2rem] shadow-sm border border-slate-100 overflow-hidden">
        <div class="p-6 border-b border-slate-50 bg-slate-50/30 flex items-center justify-between">
          <h3 class="font-black text-slate-800 flex items-center gap-2">
            <svg class="w-5 h-5 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" stroke-width="2" /></svg>
            프로필 설정
          </h3>
        </div>
        <div class="p-8 flex flex-col md:flex-row items-center gap-8">
          <div class="relative group">
            <div class="w-24 h-24 rounded-full border-4 border-slate-50 shadow-inner overflow-hidden bg-primary-100 flex items-center justify-center">
              <img v-if="user.profileImageUrl" :src="user.profileImageUrl" class="w-full h-full object-cover" />
              <span v-else class="text-primary-700 font-bold text-3xl">{{ user.nickname?.charAt(0) }}</span>
            </div>
          </div>
          <div class="flex-1 space-y-4 w-full text-center md:text-left">
            <div>
              <p class="text-xs font-bold text-slate-400 uppercase tracking-widest mb-1">ID</p>
              <p class="font-bold text-slate-700">{{ user.username }}</p>
            </div>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-xs font-bold text-slate-400 uppercase mb-1">닉네임</label>
                <input v-model="profileEdit.nickname" class="w-full px-4 py-2 bg-slate-50 border border-slate-100 rounded-xl focus:ring-2 focus:ring-primary-500 outline-none transition-all font-bold text-slate-700" />
              </div>
              <div>
                <label class="block text-xs font-bold text-slate-400 uppercase mb-1">이미지 URL</label>
                <input v-model="profileEdit.profileImageUrl" placeholder="https://..." class="w-full px-4 py-2 bg-slate-50 border border-slate-100 rounded-xl focus:ring-2 focus:ring-primary-500 outline-none transition-all text-xs font-medium" />
              </div>
            </div>
            <button @click="updateProfile" :disabled="isUpdating" class="px-6 py-2.5 bg-primary-600 text-white rounded-xl font-bold text-sm hover:bg-primary-700 transition-all shadow-lg shadow-primary-100 active:scale-95 disabled:opacity-50">
              저장하기
            </button>
          </div>
        </div>
      </section>

      <!-- App Preferences -->
      <section class="bg-white rounded-[2rem] shadow-sm border border-slate-100 overflow-hidden">
        <div class="p-6 border-b border-slate-50 bg-slate-50/30">
          <h3 class="font-black text-slate-800 flex items-center gap-2">
            <svg class="w-5 h-5 text-orange-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" stroke-width="2" /><path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" stroke-width="2" /></svg>
            서비스 설정
          </h3>
        </div>
        <div class="p-2">
          <div class="flex items-center justify-between p-6 hover:bg-slate-50 transition-colors rounded-2xl">
            <div>
              <p class="font-bold text-slate-700 text-sm">메시지 알림</p>
              <p class="text-xs text-slate-400 font-medium">새로운 메시지가 오면 바탕화면에 알립니다.</p>
            </div>
            <div class="w-12 h-6 bg-primary-600 rounded-full relative cursor-not-allowed">
              <div class="w-4 h-4 bg-white rounded-full absolute right-1 top-1"></div>
            </div>
          </div>
          <div class="flex items-center justify-between p-6 hover:bg-slate-50 transition-colors rounded-2xl cursor-pointer" @click="toggleUnit">
            <div>
              <p class="font-bold text-slate-700 text-sm">단위 설정 (m / yards)</p>
              <p class="text-xs text-slate-400 font-medium">기록 시 사용할 기본 단위를 선택합니다.</p>
            </div>
            <span class="text-xs font-black text-primary-600 px-3 py-1 bg-primary-50 rounded-lg uppercase transition-all active:scale-95">
              {{ profileEdit.distanceUnit === 'YARD' ? 'YARD (yd)' : 'METER (m)' }}
            </span>
          </div>
        </div>
      </section>

      <!-- Policy & Info -->
      <section class="bg-white rounded-[2rem] shadow-sm border border-slate-100 overflow-hidden">
        <div class="p-6 border-b border-slate-50 bg-slate-50/30">
          <h3 class="font-black text-slate-800 flex items-center gap-2">
            <svg class="w-5 h-5 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" stroke-width="2" /></svg>
            약관 및 정보
          </h3>
        </div>
        <div class="divide-y divide-slate-50">
          <button @click="$router.push('/privacy')" class="w-full flex items-center justify-between p-6 hover:bg-slate-50 transition-all group text-left">
            <span class="text-sm font-bold text-slate-600 group-hover:text-slate-900">개인정보 처리방침</span>
            <svg class="w-4 h-4 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" stroke-width="3" /></svg>
          </button>
          <button @click="$router.push('/terms')" class="w-full flex items-center justify-between p-6 hover:bg-slate-50 transition-all group text-left">
            <span class="text-sm font-bold text-slate-600 group-hover:text-slate-900">서비스 이용약관</span>
            <svg class="w-4 h-4 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" stroke-width="3" /></svg>
          </button>
          <div class="p-6 flex justify-between items-center bg-slate-50/20 text-xs font-bold text-slate-400">
            <span>앱 버전</span>
            <span>v1.0.4-stable</span>
          </div>
        </div>
      </section>

      <!-- Danger Zone -->
      <section class="bg-red-50/30 rounded-[2rem] border border-red-100 overflow-hidden">
        <div class="p-8 flex flex-col md:flex-row items-center justify-between gap-4">
          <div>
            <h3 class="font-black text-red-700 text-lg">회원 탈퇴</h3>
            <p class="text-xs text-red-500 font-bold">계정을 삭제하면 모든 수영 기록과 대화 내역이 영구적으로 사라집니다.</p>
          </div>
          <button @click="handleWithdraw" class="px-8 py-3 bg-red-600 text-white rounded-2xl font-black text-xs tracking-widest hover:bg-red-700 transition-all shadow-lg shadow-red-100 active:scale-95 whitespace-nowrap">
            WITHDRAW ACCOUNT
          </button>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import AppHeader from '../components/AppHeader.vue'

const router = useRouter()
const user = ref({ username: '', nickname: '', profileImageUrl: '', distanceUnit: 'METER' })
const isUpdating = ref(false)
const profileEdit = ref({ nickname: '', profileImageUrl: '', distanceUnit: 'METER' })

const fetchUser = async () => {
  try {
    const res = await axios.get('/api/user/me')
    user.value = res.data
    profileEdit.value = { 
      nickname: res.data.nickname, 
      profileImageUrl: res.data.profileImageUrl || '',
      distanceUnit: res.data.distanceUnit || 'METER'
    }
  } catch (err) {
    router.push('/login')
  }
}

const toggleUnit = async () => {
  profileEdit.value.distanceUnit = profileEdit.value.distanceUnit === 'METER' ? 'YARD' : 'METER'
  await updateProfile()
}

const updateProfile = async () => {
  if (!profileEdit.value.nickname.trim()) return
  isUpdating.value = true
  try {
    await axios.put('/api/user/profile', profileEdit.value)
    alert('프로필이 수정되었습니다.')
    await fetchUser()
    window.location.reload()
  } catch (err) {
    alert('프로필 수정 실패')
  } finally {
    isUpdating.value = false
  }
}

const handleWithdraw = async () => {
  if (confirm("정말로 탈퇴하시겠습니까?\n모든 데이터가 삭제되며 복구할 수 없습니다.")) {
    try {
      await axios.delete('/api/user/withdraw')
      alert('회원 탈퇴가 완료되었습니다. 이용해 주셔서 감사합니다.')
      router.push('/login')
    } catch (err) {
      alert('탈퇴 처리 중 오류가 발생했습니다.')
    }
  }
}

onMounted(fetchUser)
</script>
