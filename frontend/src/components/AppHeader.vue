<template>
  <header class="bg-white/80 backdrop-blur-md border-b border-slate-100 px-4 py-3 md:px-8 md:py-4 flex justify-between items-center sticky top-0 z-40 transition-all duration-300 shadow-sm shadow-slate-100/50">
    <div class="flex items-center gap-4 md:gap-10">
      <!-- Logo -->
      <div class="flex items-center gap-2 md:gap-3 cursor-pointer group" @click="$router.push('/')">
        <div class="w-8 h-8 md:w-10 md:h-10 bg-gradient-to-tr from-blue-600 to-indigo-600 rounded-xl flex items-center justify-center text-white shadow-lg shadow-blue-500/20 group-hover:scale-105 transition-transform duration-300">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-5 h-5 md:w-6 md:h-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
          </svg>
        </div>
        <h1 class="text-lg md:text-2xl font-bold text-slate-800 tracking-tight">
          내일<span class="text-blue-600">수영</span>
        </h1>
      </div>

      <!-- Navigation -->
      <nav class="hidden md:flex items-center bg-slate-100/50 p-1 rounded-2xl border border-slate-200/50">
        <router-link 
          v-for="menu in menuItems" 
          :key="menu.path"
          :to="menu.path" 
          class="px-5 py-2 text-xs font-bold rounded-xl transition-all duration-300" 
          :class="activeClass(menu.path)"
        >
          {{ menu.label }}
        </router-link>
      </nav>
    </div>

    <!-- User Section -->
    <div class="flex items-center gap-3 md:gap-6">
      <!-- Mobile Nav Trigger (Simple) -->
      <nav class="md:hidden flex gap-1">
         <router-link 
          v-for="menu in menuItems.slice(0, 3)" 
          :key="menu.path"
          :to="menu.path" 
          class="p-2 rounded-lg text-slate-400"
          :class="route.path === menu.path ? 'text-blue-600 bg-blue-50' : ''"
        >
          <span class="text-[10px] font-bold">{{ menu.label }}</span>
        </router-link>
      </nav>

      <div class="h-6 w-px bg-slate-200 hidden sm:block"></div>

      <div class="flex items-center gap-3 cursor-pointer group px-2 py-1 rounded-2xl hover:bg-slate-50 transition-all duration-300" @click="openProfileModal">
        <div class="hidden sm:flex flex-col items-end leading-none gap-1">
          <span class="text-sm font-bold text-slate-700 group-hover:text-blue-600 transition-colors">{{ user.nickname }}</span>
          <span class="text-[10px] text-slate-400 font-medium italic">Member</span>
        </div>
        
        <div class="w-9 h-9 md:w-11 md:h-11 rounded-full border-2 border-white shadow-md overflow-hidden flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-50 flex-shrink-0 transition-transform group-hover:scale-105">
          <img v-if="user.profileImageUrl" :src="user.profileImageUrl" class="w-full h-full object-cover" />
          <span v-else class="text-blue-600 font-bold text-sm md:text-base">{{ user.nickname?.charAt(0) }}</span>
        </div>
      </div>
      
      <button @click="logout" class="p-2.5 bg-slate-50 hover:bg-red-50 text-slate-400 hover:text-red-500 rounded-xl transition-all duration-300 group shadow-sm" title="로그아웃">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5 group-hover:rotate-12 transition-transform">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15m3 0l3-3m0 0l-3-3m3 3H9" />
        </svg>
      </button>
    </div>

    <!-- Profile Edit Modal -->
    <transition name="fade">
      <div v-if="showProfileModal" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm p-4">
        <div class="bg-white w-full max-w-md rounded-[2.5rem] shadow-2xl overflow-hidden p-8 md:p-10 space-y-8 animate-in fade-in zoom-in duration-300 border border-slate-100">
          <div class="flex justify-between items-center">
            <h3 class="text-2xl font-bold text-slate-800 tracking-tight">프로필 설정</h3>
            <button @click="showProfileModal = false" class="w-10 h-10 flex items-center justify-center rounded-full hover:bg-slate-100 text-slate-400 hover:text-slate-600 transition-colors">✕</button>
          </div>
          
          <div class="flex flex-col items-center gap-6">
            <div class="relative group">
              <div class="w-24 h-24 md:w-32 md:h-32 rounded-full border-4 border-white shadow-xl overflow-hidden bg-gradient-to-br from-blue-50 to-indigo-50 flex items-center justify-center transition-transform group-hover:scale-105">
                <img v-if="profileEdit.profileImageUrl" :src="profileEdit.profileImageUrl" class="w-full h-full object-cover" />
                <span v-else class="text-blue-600 font-bold text-3xl md:text-4xl">{{ profileEdit.nickname?.charAt(0) }}</span>
              </div>
              <div class="absolute bottom-1 right-1 bg-white p-2 rounded-full shadow-lg border border-slate-100 text-blue-600">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-5 h-5">
                  <path d="M2.695 14.763l-1.262 3.154a.5.5 0 00.65.65l3.154-1.262a.5.5 0 00.191-.155l8.5-8.5a.5.5 0 000-.707l-2.03-2.03a.5.5 0 00-.707 0l-8.5 8.5a.5.5 0 00-.155.191z" />
                </svg>
              </div>
            </div>
          </div>

          <div class="space-y-5">
            <div class="space-y-2">
              <label class="block text-sm font-semibold text-slate-700 ml-1">닉네임</label>
              <input v-model="profileEdit.nickname" class="w-full px-5 py-3.5 bg-slate-50 rounded-2xl border border-slate-200 focus:bg-white focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10 outline-none transition-all text-sm font-medium" />
            </div>
            <div class="space-y-2">
              <label class="block text-sm font-semibold text-slate-700 ml-1">프로필 이미지 URL</label>
              <input v-model="profileEdit.profileImageUrl" placeholder="https://..." class="w-full px-5 py-3.5 bg-slate-50 rounded-2xl border border-slate-200 focus:bg-white focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10 outline-none transition-all text-sm font-medium" />
            </div>
          </div>

          <button @click="updateProfile" :disabled="isUpdating" class="w-full py-4 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white rounded-2xl font-bold shadow-lg shadow-blue-500/20 transition-all active:scale-[0.98] disabled:opacity-50 text-base">
            {{ isUpdating ? '변경 사항 적용 중...' : '저장하기' }}
          </button>
        </div>
      </div>
    </transition>
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
  { path: '/stats', label: '통계 분석' },
  { path: '/feed', label: '커뮤니티' },
  { path: '/settings', label: '환경 설정' }
]

const activeClass = (path) => {
  return route.path === path 
    ? 'bg-white text-blue-600 shadow-md shadow-blue-500/10 scale-105' 
    : 'text-slate-500 hover:text-blue-600 hover:bg-white/50'
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

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
