<template>
  <header class="bg-white/80 backdrop-blur-md border-b border-slate-50 px-4 md:px-6 py-3 md:py-4 flex justify-between items-center sticky top-0 z-40 transition-all duration-300">
    <div class="flex items-center gap-6 md:gap-12">
      <!-- Minimal Logo -->
      <div class="flex items-center gap-2 cursor-pointer group" @click="$router.push('/')">
        <div class="w-8 h-8 bg-slate-900 rounded-lg flex items-center justify-center text-white transition-transform group-hover:scale-105">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-5 h-5">
            <path d="M12 21.75c4.48 0 8.274-3.673 8.274-8.274 0-4.48-3.794-8.274-8.274-8.274-4.48 0-8.274 3.794-8.274 8.274 0 4.601 3.794 8.274 8.274 8.274zm0-15c3.673 0 6.774 3.1 6.774 6.726 0 3.626-3.1 6.726-6.774 6.726-3.674 0-6.774-3.1-6.774-6.726 0-3.626 3.1-6.726 6.774-6.726zM12 9a.75.75 0 01.75.75v3a.75.75 0 01-.75.75H9a.75.75 0 010-1.5h2.25V9.75A.75.75 0 0112 9z" />
          </svg>
        </div>
        <h1 class="text-base md:text-lg font-black text-slate-900 tracking-tight">
          매일<span class="text-blue-600">수영</span>
        </h1>
      </div>

      <!-- Refined Desktop Navigation -->
      <nav class="hidden md:flex items-center gap-1">
        <router-link 
          v-for="menu in menuItems" 
          :key="menu.path"
          :to="menu.path" 
          class="px-4 py-1.5 text-[13px] font-bold rounded-full transition-all duration-200" 
          :class="activeClass(menu.path)"
        >
          {{ menu.label }}
        </router-link>
      </nav>
    </div>

    <!-- Right Section -->
    <div class="flex items-center gap-3 md:gap-5">
      <!-- Mobile Nav Icons (Simplified) -->
      <nav class="md:hidden flex gap-0.5">
         <router-link 
          v-for="menu in menuItems.slice(0, 3)" 
          :key="menu.path"
          :to="menu.path" 
          class="p-2 rounded-lg text-slate-400"
          :class="route.path === menu.path ? 'text-slate-900' : ''"
        >
          <span class="text-[11px] font-bold">{{ menu.label }}</span>
        </router-link>
      </nav>

      <div class="h-4 w-px bg-slate-100 hidden sm:block"></div>

      <!-- Authenticated View -->
      <template v-if="user.username">
        <!-- User Profile (Compact) -->
        <div class="flex items-center gap-2 cursor-pointer group px-1.5 py-1 rounded-full hover:bg-slate-50 transition-all duration-200" @click="$router.push('/settings')">
          <div class="w-8 h-8 md:w-9 md:h-9 rounded-full border border-slate-100 overflow-hidden flex items-center justify-center bg-slate-50 shadow-sm group-hover:border-slate-300">
            <img v-if="user.profileImageUrl" :src="user.profileImageUrl" class="w-full h-full object-cover" />
            <span v-else class="text-slate-400 font-bold text-xs">{{ user.nickname?.charAt(0) }}</span>
          </div>
          <span class="hidden sm:block text-xs font-bold text-slate-600 group-hover:text-slate-900">{{ user.nickname }}</span>
        </div>
        
        <button @click="logout" class="p-2 text-slate-300 hover:text-red-500 transition-colors" title="로그아웃">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15m3 0l3-3m0 0l-3-3m3 3H9" />
          </svg>
        </button>
      </template>

      <!-- Unauthenticated View -->
      <template v-else>
        <router-link to="/login" class="text-xs font-bold text-slate-400 hover:text-slate-900 px-2 py-1 transition-colors">
          로그인
        </router-link>
      </template>
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

const menuItems = [
  { path: '/', label: '대시보드' },
  { path: '/stats', label: '성장 리포트' },
  { path: '/feed', label: '물옷 갤러리' }
]

const activeClass = (path) => {
  return route.path === path 
    ? 'bg-slate-100 text-slate-900 shadow-sm' 
    : 'text-slate-400 hover:text-slate-600 hover:bg-slate-50'
}

const fetchUser = async () => {
  try {
    const res = await axios.get('/api/user/me')
    user.value = res.data
  } catch (err) {
    // 로그인이 안 되어 있어도 약관 페이지 등은 볼 수 있어야 하므로 에러를 무시합니다.
    user.value = { username: '', nickname: '', profileImageUrl: '' }
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
