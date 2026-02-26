<template>
  <header class="bg-white border-b border-slate-200 px-3 py-2 md:px-6 md:py-4 flex justify-between items-center shadow-sm sticky top-0 z-30">
    <div class="flex items-center gap-3 md:gap-8">
      <!-- Logo -->
      <div class="flex items-center gap-1.5 md:gap-2 cursor-pointer" @click="$router.push('/')">
        <div class="w-7 h-7 md:w-8 md:h-8 bg-primary-600 rounded-lg flex items-center justify-center text-white shadow-md">
          <svg class="w-4 h-4 md:w-5 md:h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9" stroke-width="2" /></svg>
        </div>
        <h1 class="text-base md:text-xl font-black text-slate-800 tracking-tighter italic uppercase">
          Swim<span class="text-primary-600 hidden xs:inline">Record</span>
        </h1>
      </div>

      <!-- Navigation -->
      <nav class="flex gap-0.5 md:gap-1 bg-slate-100 p-1 rounded-xl">
        <router-link 
          v-for="menu in menuItems" 
          :key="menu.path"
          :to="menu.path" 
          class="px-2 py-1 md:px-4 md:py-1.5 text-[10px] md:text-xs font-bold rounded-lg transition-all whitespace-nowrap" 
          :class="activeClass(menu.path)"
        >
          {{ menu.label }}
        </router-link>
      </nav>
    </div>

    <!-- User Section -->
    <div class="flex items-center gap-2 md:gap-4">
      <div class="flex items-center gap-2 md:gap-3 cursor-pointer group px-1 md:px-3 py-1.5 rounded-xl hover:bg-slate-50 transition-colors" @click="openProfileModal">
        <div class="hidden sm:flex flex-col items-end leading-tight">
          <span class="text-sm font-bold text-slate-700 group-hover:text-primary-600 transition-colors">{{ user.nickname }}</span>
          <span class="text-[10px] text-slate-400">프로필 설정</span>
        </div>
        <span class="sm:hidden text-xs font-bold text-slate-700">{{ user.nickname }}</span>
        
        <div class="w-8 h-8 md:w-10 md:h-10 rounded-full border-2 border-white shadow-sm overflow-hidden flex items-center justify-center bg-primary-100 flex-shrink-0">
          <img v-if="user.profileImageUrl" :src="user.profileImageUrl" class="w-full h-full object-cover" />
          <span v-else class="text-primary-700 font-bold text-xs md:text-sm">{{ user.nickname?.charAt(0) }}</span>
        </div>
      </div>
      
      <div class="h-6 w-px bg-slate-200 mx-0.5 md:mx-1"></div>
      
      <button @click="logout" class="p-2 hover:bg-red-50 text-slate-300 hover:text-red-500 rounded-lg transition-colors" title="로그아웃">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" stroke-width="2" /></svg>
      </button>
    </div>

    <!-- Profile Edit Modal -->
    <div v-if="showProfileModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div class="bg-white w-full max-w-md rounded-3xl shadow-2xl overflow-hidden p-6 md:p-8 space-y-6">
        <div class="flex justify-between items-center">
          <h3 class="text-xl md:text-2xl font-bold text-slate-800">프로필 편집</h3>
          <button @click="showProfileModal = false" class="text-slate-400 hover:text-slate-600 text-xl">✕</button>
        </div>
        <div class="flex flex-col items-center gap-4">
          <div class="w-20 h-20 md:w-24 md:h-24 rounded-full border-4 border-slate-50 shadow-inner overflow-hidden bg-primary-100 flex items-center justify-center">
            <img v-if="profileEdit.profileImageUrl" :src="profileEdit.profileImageUrl" class="w-full h-full object-cover" />
            <span v-else class="text-primary-700 font-bold text-2xl md:text-3xl">{{ profileEdit.nickname?.charAt(0) }}</span>
          </div>
        </div>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-bold text-slate-700 mb-1">닉네임</label>
            <input v-model="profileEdit.nickname" class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:border-primary-500 outline-none transition-all text-sm" />
          </div>
          <div>
            <label class="block text-sm font-bold text-slate-700 mb-1">프로필 이미지 URL</label>
            <input v-model="profileEdit.profileImageUrl" placeholder="https://..." class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:border-primary-500 outline-none transition-all text-sm" />
          </div>
        </div>
        <button @click="updateProfile" :disabled="isUpdating" class="w-full py-3 md:py-4 bg-primary-600 text-white rounded-2xl font-bold transition-all active:scale-95 disabled:opacity-50 shadow-lg shadow-primary-100 text-sm md:text-base">
          {{ isUpdating ? '저장 중...' : '변경 내용 저장' }}
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const user = ref({ username: '', nickname: '', profileImageUrl: '' })
const showProfileModal = ref(false)
const isUpdating = ref(false)
const profileEdit = ref({ nickname: '', profileImageUrl: '' })

const menuItems = [
  { path: '/', label: '대시보드' },
  { path: '/stats', label: '통계' },
  { path: '/feed', label: '피드' },
  { path: '/chat', label: '채팅' },
  { path: '/settings', label: '설정' }
]

const activeClass = (path) => {
  return route.path === path 
    ? 'bg-white text-primary-600 shadow-sm' 
    : 'text-slate-500 hover:text-slate-700'
}

const fetchUser = async () => {
  try {
    const res = await axios.get('/api/user/me')
    user.value = res.data
    profileEdit.value = { nickname: res.data.nickname, profileImageUrl: res.data.profileImageUrl || '' }
  } catch (err) {
    router.push('/login')
  }
}

const openProfileModal = () => {
  profileEdit.value = { nickname: user.value.nickname, profileImageUrl: user.value.profileImageUrl || '' }
  showProfileModal.value = true
}

const updateProfile = async () => {
  if (!profileEdit.value.nickname.trim()) return
  isUpdating.value = true
  try {
    await axios.put('/api/user/profile', profileEdit.value)
    await fetchUser()
    showProfileModal.value = false
    window.location.reload()
  } catch (err) {
    alert('프로필 수정 실패')
  } finally {
    isUpdating.value = false
  }
}

const logout = async () => {
  try {
    await axios.post('/api/member/logout')
    router.push('/login')
  } catch (err) {
    router.push('/login')
  }
}

onMounted(fetchUser)
</script>
