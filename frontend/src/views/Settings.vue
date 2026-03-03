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
          
          <div class="p-5 flex items-center justify-between group cursor-pointer hover:bg-slate-50 transition-colors" @click="handleWithdraw">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-xl bg-red-50 text-red-500 flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M22 10.5h-6m-2.25-4.125a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zM4 19.235v-.11a6.375 6.375 0 0112.75 0v.109A12.318 12.318 0 0110.374 21c-2.331 0-4.512-.645-6.374-1.766z" />
                </svg>
              </div>
              <div class="space-y-0.5 text-left">
                <p class="text-xs font-bold text-slate-900">회원 탈퇴</p>
                <p class="text-[10px] text-slate-400 font-medium">모든 기록이 삭제됩니다.</p>
              </div>
            </div>
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-4 h-4 text-slate-300">
              <path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
            </svg>
          </div>
        </div>
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
