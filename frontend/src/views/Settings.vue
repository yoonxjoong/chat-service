<template>
  <div class="min-h-screen bg-slate-50 p-6 font-sans antialiased text-slate-900 pb-24">
    <div class="max-w-md mx-auto space-y-8">
      <!-- Header -->
      <div class="flex items-center justify-between">
        <button @click="$router.back()" class="p-2 -ml-2 text-slate-400 hover:text-slate-900 transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" />
          </svg>
        </button>
        <h2 class="text-sm font-black uppercase tracking-widest text-slate-400">Settings</h2>
        <div class="w-9"></div>
      </div>

      <!-- Profile Section -->
      <div class="space-y-4">
        <h3 class="text-[10px] font-bold text-slate-400 uppercase tracking-[0.2em] ml-1">Profile</h3>
        <div class="bg-white rounded-3xl p-6 shadow-sm border border-slate-100 space-y-6">
          <div class="flex items-center gap-5">
            <div class="relative group">
              <div class="w-20 h-20 rounded-3xl border-2 border-slate-50 overflow-hidden flex items-center justify-center bg-slate-50 shadow-inner transition-all group-hover:border-slate-200">
                <img v-if="profileEdit.profileImageUrl" :src="profileEdit.profileImageUrl" class="w-full h-full object-cover" />
                <span v-else class="text-slate-300 font-black text-2xl">{{ profileEdit.nickname?.charAt(0) }}</span>
              </div>
              <!-- Upload Button Overlay -->
              <label class="absolute -bottom-1 -right-1 w-8 h-8 bg-slate-900 text-white rounded-xl flex items-center justify-center cursor-pointer shadow-lg hover:bg-slate-800 transition-all active:scale-90">
                <input type="file" class="hidden" accept="image/*" @change="handleImageUpload" :disabled="isUploading" />
                <svg v-if="!isUploading" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M6.827 6.175A2.31 2.31 0 015.186 7.23c-.38.054-.757.112-1.134.175C2.999 7.58 2.25 8.507 2.25 9.574V18a2.25 2.25 0 002.25 2.25h15A2.25 2.25 0 0021.75 18V9.574c0-1.067-.75-1.994-1.802-2.169a47.865 47.865 0 00-1.134-.175 2.31 2.31 0 01-1.64-1.055l-.822-1.316a2.192 2.192 0 00-1.736-1.039 48.774 48.774 0 00-5.232 0 2.192 2.192 0 00-1.736 1.039l-.821 1.316z" />
                  <path stroke-linecap="round" stroke-linejoin="round" d="M16.5 12.75a4.5 4.5 0 11-9 0 4.5 4.5 0 019 0zM18.75 10.5h.008v.008h-.008V10.5z" />
                </svg>
                <div v-else class="w-3 h-3 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
              </label>
            </div>
            <div class="flex-1 space-y-4">
              <div class="space-y-1">
                <label class="text-[9px] font-bold text-slate-400 uppercase tracking-tighter ml-1">Nickname</label>
                <input v-model="profileEdit.nickname" class="w-full px-4 py-2.5 bg-slate-50 border border-transparent rounded-xl focus:bg-white focus:ring-1 focus:ring-slate-900 outline-none transition-all text-sm font-bold text-slate-700" />
              </div>
            </div>
          </div>
          <button @click="updateProfile" :disabled="isUpdating || isUploading" class="w-full py-3.5 bg-slate-900 text-white rounded-2xl font-bold text-sm hover:bg-slate-800 transition-all active:scale-[0.98] disabled:opacity-30 shadow-lg shadow-slate-200">
            {{ isUpdating ? '저장 중...' : '프로필 저장' }}
          </button>
        </div>
      </div>

      <!-- Account Settings -->
      <div class="space-y-4">
        <h3 class="text-[10px] font-bold text-slate-400 uppercase tracking-[0.2em] ml-1">Account</h3>
        <div class="bg-white rounded-3xl overflow-hidden shadow-sm border border-slate-100 divide-y divide-slate-50">
          <div class="p-5 flex items-center justify-between group cursor-pointer hover:bg-slate-50 transition-colors" @click="toggleUnit">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M12 21a9.004 9.004 0 008.716-6.747M12 21a9.004 9.004 0 01-8.716-6.747M12 21c2.485 0 4.5-4.03 4.5-9S14.485 3 12 3m0 18c-2.485 0-4.5-4.03-4.5-9s2.015-9 4.5-9m0 18c-2.485 0-4.5-4.03-4.5-9s2.015-9 4.5-9" />
                </svg>
              </div>
              <div class="space-y-0.5 text-left">
                <p class="text-xs font-bold text-slate-900">거리 단위 설정</p>
                <p class="text-[10px] text-slate-400 font-medium">기록 표시 단위를 선택하세요.</p>
              </div>
            </div>
            <span class="text-[11px] font-black text-blue-600 bg-blue-50 px-3 py-1.5 rounded-lg uppercase tracking-wider">
              {{ profileEdit.distanceUnit === 'YARD' ? 'Yards (yd)' : 'Meters (m)' }}
            </span>
          </div>

          <div class="p-5 flex items-center justify-between group cursor-pointer hover:bg-slate-50 transition-colors" @click="handleLogout">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-xl bg-slate-50 text-slate-500 flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15m3 0l3-3m0 0l-3-3m3 3H9" />
                </svg>
              </div>
              <div class="space-y-0.5 text-left">
                <p class="text-xs font-bold text-slate-900">로그아웃</p>
                <p class="text-[10px] text-slate-400 font-medium">현재 계정에서 로그아웃합니다.</p>
              </div>
            </div>
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-4 h-4 text-slate-300">
              <path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
            </svg>
          </div>
          
          <div class="p-5 flex items-center justify-between group cursor-pointer hover:bg-slate-50 transition-colors" @click="handleWithdraw">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-xl bg-red-50 text-red-500 flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M22 10.5h-6m-2.25-4.125a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zM4 19.235v-.11a6.375 6.375 0 0112.75 0v.109A12.318 12.318 0 0110.374 21c-2.331 0-4.512-.645-6.374-1.766z" />
                </svg>
              </div>
              <div class="space-y-0.5 text-left">
                <p class="text-xs font-bold text-red-600">회원 탈퇴</p>
                <p class="text-[10px] text-slate-400 font-medium">모든 기록이 즉시 삭제됩니다.</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Support & Legal -->
      <div class="space-y-4">
        <h3 class="text-[10px] font-bold text-slate-400 uppercase tracking-[0.2em] ml-1">Support & Legal</h3>
        <div class="bg-white rounded-3xl overflow-hidden shadow-sm border border-slate-100 divide-y divide-slate-50">
          <router-link to="/terms" class="p-5 flex items-center justify-between group hover:bg-slate-50 transition-colors">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-xl bg-slate-50 text-slate-600 flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" />
                </svg>
              </div>
              <p class="text-xs font-bold text-slate-900">이용약관</p>
            </div>
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-4 h-4 text-slate-300">
              <path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
            </svg>
          </router-link>

          <router-link to="/privacy" class="p-5 flex items-center justify-between group hover:bg-slate-50 transition-colors">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-xl bg-slate-50 text-slate-600 flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75m-3-7.036A11.959 11.959 0 013.598 6 11.99 11.99 0 003 9.749c0 5.592 3.824 10.29 9 11.623 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751A11.959 11.959 0 0112 2.714z" />
                </svg>
              </div>
              <p class="text-xs font-bold text-slate-900">개인정보처리방침</p>
            </div>
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-4 h-4 text-slate-300">
              <path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
            </svg>
          </router-link>

          <div class="p-5 flex items-center justify-between group cursor-pointer hover:bg-slate-50 transition-colors" @click="handleContact">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-xl bg-slate-50 text-slate-600 flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M20.25 8.511c.884.284 1.5 1.128 1.5 2.097v4.286c0 1.136-.847 2.1-1.98 2.193-.303.025-.607.047-.912.066a48.623 48.623 0 01-5.592 0 48.204 48.204 0 01-5.592 0 48.252 48.252 0 01-.912-.066 2.188 2.188 0 01-1.98-2.193V10.608c0-.969.616-1.813 1.5-2.097a48.11 48.11 0 0115.408 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" d="M8.445 14.927A4.514 4.514 0 0010.5 15.75c.675 0 1.312-.15 1.886-.418a4.51 4.51 0 002.169-2.581m-5.11 2.176a4.51 4.51 0 01-2.169-2.581m5.11 2.176a4.514 4.514 0 012.055-.823m-2.055.823l.011.011" />
                </svg>
              </div>
              <p class="text-xs font-bold text-slate-900">문의하기</p>
            </div>
            <p class="text-[10px] font-bold text-slate-300">yhj8372@naver.com</p>
          </div>
        </div>
      </div>

      <!-- App Info -->
      <div class="text-center pt-8 pb-4">
        <p class="text-[10px] font-black text-slate-200 uppercase tracking-[0.4em]">Swim Too v1.0.0</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = ref({ username: '', nickname: '', profileImageUrl: '', distanceUnit: 'METER' })
const profileEdit = ref({ nickname: '', profileImageUrl: '', distanceUnit: 'METER' })
const isUpdating = ref(false)
const isUploading = ref(false)

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

const handleImageUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  const formData = new FormData()
  formData.append('file', file)

  isUploading.value = true
  try {
    const res = await axios.post('/api/image/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    profileEdit.value.profileImageUrl = res.data.url
  } catch (err) {
    alert('이미지 업로드에 실패했습니다.')
  } finally {
    isUploading.value = false
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
    await fetchUser() // 정보 갱신
  } catch (err) {
    alert('프로필 수정 중 오류가 발생했습니다.')
  } finally {
    isUpdating.value = false
  }
}

const handleLogout = async () => {
  if (confirm('로그아웃 하시겠습니까?')) {
    try {
      await axios.post('/api/user/logout') // 백엔드 로그아웃 API 호출 (필요 시)
      router.push('/login')
    } catch (err) {
      // API가 없더라도 클라이언트에서는 로그인 페이지로 이동
      router.push('/login')
    }
  }
}

const handleContact = () => {
  window.location.href = 'mailto:yhj8372@naver.com'
}

const handleWithdraw = async () => {
  if (confirm('정말로 탈퇴하시겠습니까? 모든 기록이 즉시 삭제되며 복구할 수 없습니다.')) {
    try {
      await axios.delete('/api/user/withdraw')
      alert('탈퇴가 완료되었습니다.')
      router.push('/login')
    } catch (err) {
      alert('탈퇴 처리 중 오류가 발생했습니다.')
    }
  }
}

onMounted(fetchUser)
</script>
