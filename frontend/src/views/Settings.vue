<template>
  <div class="min-h-screen bg-white flex flex-col font-sans antialiased text-slate-900">
    <AppHeader />

    <main class="flex-1 p-4 md:p-6 max-w-2xl mx-auto w-full space-y-8">
      <!-- Header -->
      <div class="px-1">
        <h2 class="text-xl md:text-2xl font-bold tracking-tight">설정</h2>
        <p class="text-xs md:text-sm text-slate-400 font-medium">계정 및 서비스 개인 설정을 관리하세요.</p>
      </div>

      <!-- Profile Section -->
      <section class="space-y-4">
        <h3 class="text-[10px] font-bold text-slate-400 uppercase tracking-[0.2em] ml-1">Profile</h3>
        <div class="bg-slate-50 p-6 md:p-8 rounded-2xl border border-slate-100 flex flex-col items-center gap-6">
          <div class="relative group">
            <div class="w-20 h-20 md:w-24 md:h-24 rounded-full border-2 border-white shadow-md overflow-hidden bg-white flex items-center justify-center">
              <img v-if="user.profileImageUrl" :src="user.profileImageUrl" class="w-full h-full object-cover" />
              <span v-else class="text-slate-300 font-bold text-2xl">{{ user.nickname?.charAt(0) }}</span>
            </div>
          </div>
          
          <div class="w-full space-y-4">
            <div class="grid grid-cols-1 gap-4">
              <div class="space-y-1.5">
                <label class="text-[10px] font-bold text-slate-400 uppercase ml-1">닉네임</label>
                <input v-model="profileEdit.nickname" class="w-full px-4 py-2.5 bg-white border border-slate-200 rounded-xl focus:ring-1 focus:ring-slate-900 outline-none transition-all text-sm font-bold text-slate-700" />
              </div>
              <div class="space-y-1.5">
                <label class="text-[10px] font-bold text-slate-400 uppercase ml-1">이미지 URL</label>
                <input v-model="profileEdit.profileImageUrl" placeholder="https://..." class="w-full px-4 py-2.5 bg-white border border-slate-200 rounded-xl focus:ring-1 focus:ring-slate-900 outline-none transition-all text-xs font-medium" />
              </div>
            </div>
            <button @click="updateProfile" :disabled="isUpdating" class="w-full py-3 bg-slate-900 text-white rounded-xl font-bold text-sm hover:bg-slate-800 transition-all active:scale-[0.98] disabled:opacity-50">
              변경 사항 저장
            </button>
          </div>
        </div>
      </section>

      <!-- App Preferences -->
      <section class="space-y-4">
        <h3 class="text-[10px] font-bold text-slate-400 uppercase tracking-[0.2em] ml-1">Preferences</h3>
        <div class="bg-white rounded-2xl border border-slate-100 overflow-hidden divide-y divide-slate-50">
          <div class="flex items-center justify-between p-5 hover:bg-slate-50 transition-colors">
            <div>
              <p class="font-bold text-slate-700 text-sm">거리 단위 설정</p>
              <p class="text-[11px] text-slate-400 font-medium">미터(m) 또는 야드(yd)를 선택합니다.</p>
            </div>
            <button @click="toggleUnit" class="px-4 py-1.5 bg-slate-100 hover:bg-slate-200 rounded-lg text-[11px] font-black text-slate-600 transition-all active:scale-95 uppercase">
              {{ profileEdit.distanceUnit === 'YARD' ? 'Yards (yd)' : 'Meters (m)' }}
            </button>
          </div>
        </div>
      </section>

      <!-- Information -->
      <section class="space-y-4">
        <h3 class="text-[10px] font-bold text-slate-400 uppercase tracking-[0.2em] ml-1">Legal & Info</h3>
        <div class="bg-white rounded-2xl border border-slate-100 overflow-hidden divide-y divide-slate-50">
          <button @click="$router.push('/privacy')" class="w-full flex items-center justify-between p-5 hover:bg-slate-50 transition-all group">
            <span class="text-sm font-bold text-slate-600 group-hover:text-slate-900">개인정보 처리방침</span>
            <svg class="w-4 h-4 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" stroke-width="2.5" /></svg>
          </button>
          <button @click="$router.push('/terms')" class="w-full flex items-center justify-between p-5 hover:bg-slate-50 transition-all group">
            <span class="text-sm font-bold text-slate-600 group-hover:text-slate-900">서비스 이용약관</span>
            <svg class="w-4 h-4 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" stroke-width="2.5" /></svg>
          </button>
          <div class="p-5 flex justify-between items-center text-[10px] font-bold text-slate-300 tracking-wider">
            <span>VERSION</span>
            <span>1.0.4-STABLE</span>
          </div>
        </div>
      </section>

      <!-- Withdraw -->
      <div class="pt-4 px-1 flex justify-center">
        <button @click="handleWithdraw" class="text-[11px] font-bold text-red-300 hover:text-red-500 transition-colors uppercase tracking-widest">
          Withdraw Account
        </button>
      </div>
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
      alert('회원 탈퇴가 완료되었습니다.')
      router.push('/login')
    } catch (err) {
      alert('탈퇴 처리 중 오류가 발생했습니다.')
    }
  }
}

onMounted(fetchUser)
</script>
