<template>
  <div class="min-h-screen bg-white flex flex-col font-sans antialiased text-slate-900">
    <AppHeader />

    <!-- Main Content -->
    <main class="flex-1 p-4 md:p-6 max-w-5xl mx-auto w-full space-y-8">
      <!-- Header Section -->
      <div class="flex justify-between items-end px-1">
        <div>
          <h2 class="text-xl md:text-2xl font-bold tracking-tight">물옷 갤러리</h2>
          <p class="text-xs md:text-sm text-slate-400 font-medium">다양한 수영 라이프를 만나보세요.</p>
        </div>
        <button @click="showUploadModal = true" class="bg-slate-900 text-white px-5 py-2.5 rounded-full text-xs font-bold hover:bg-blue-600 transition-colors shadow-lg shadow-slate-200">
          기록 공유
        </button>
      </div>

      <!-- Compact Tabs -->
      <div class="flex gap-1.5 overflow-x-auto no-scrollbar px-1">
        <button v-for="tab in ['Feed', 'Popular', 'Styles', 'Gear']" :key="tab" 
          :class="['px-5 py-2 rounded-full text-[11px] font-bold transition-all whitespace-nowrap', 
                   tab === 'Feed' ? 'bg-slate-100 text-slate-900' : 'text-slate-400 hover:text-slate-600']">
          {{ tab }}
        </button>
      </div>

      <!-- Refined 3x3 Grid -->
      <div class="grid grid-cols-3 gap-1 md:gap-4 lg:gap-6">
        <div 
          v-for="post in placeholderPosts" :key="post.id" 
          class="group relative aspect-square bg-slate-50 rounded-lg overflow-hidden border border-slate-100 cursor-pointer active:scale-[0.98] transition-transform"
        >
          <!-- Placeholder Content -->
          <div class="absolute inset-0 flex flex-col items-center justify-center text-slate-200 group-hover:text-slate-300 transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.2" stroke="currentColor" class="w-8 h-8 md:w-10 md:h-10">
              <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 15.75l5.159-5.159a2.25 2.25 0 013.182 0l5.159 5.159m-1.5-1.5l1.409-1.409a2.25 2.25 0 013.182 0l2.909 2.909m-18 3.75h16.5a1.5 1.5 0 001.5-1.5V6a1.5 1.5 0 00-1.5-1.5H3.75A1.5 1.5 0 002.25 6v12a1.5 1.5 0 001.5 1.5zm10.5-11.25h.008v.008h-.008V8.25zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z" />
            </svg>
          </div>

          <!-- Minimal Hover Info -->
          <div class="absolute inset-0 bg-slate-900/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-4 text-white">
            <div class="flex items-center gap-1.5">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-4 h-4 md:w-5 md:h-5">
                <path d="M11.645 20.91l-.007-.003-.022-.012a15.247 15.247 0 01-.383-.218 25.18 25.18 0 01-4.244-3.17C4.688 15.36 2.25 12.174 2.25 8.25 2.25 5.322 4.714 3 7.688 3A5.5 5.5 0 0112 5.052 5.5 5.5 0 0116.313 3c2.973 0 5.437 2.322 5.437 5.25 0 3.925-2.438 7.111-4.739 9.256a25.175 25.175 0 01-4.244 3.17 15.247 15.247 0 01-.383.219l-.022.012-.007.004-.003.001a.752.752 0 01-.704 0l-.003-.001z" />
              </svg>
              <span class="text-xs md:text-sm font-bold">{{ post.likes }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Minimal Load More -->
      <div class="flex justify-center pt-4">
        <button class="text-[11px] font-bold text-slate-400 hover:text-slate-900 transition-colors uppercase tracking-widest">Load More</button>
      </div>
    </main>

    <!-- Upload Modal (Refined) -->
    <transition name="modal">
      <div v-if="showUploadModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="showUploadModal = false"></div>
        <div class="relative bg-white w-full max-w-sm rounded-2xl shadow-2xl overflow-hidden p-6 space-y-6 animate-in zoom-in duration-200">
          <div class="flex justify-between items-center">
            <h3 class="text-base font-bold">새 포스트</h3>
            <button @click="showUploadModal = false" class="text-slate-400">✕</button>
          </div>
          
          <div class="aspect-square bg-slate-50 rounded-xl border border-dashed border-slate-200 flex flex-col items-center justify-center gap-3 text-slate-400 hover:bg-slate-100 transition-colors cursor-pointer">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-10 h-10">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
            </svg>
            <p class="text-[11px] font-bold uppercase tracking-wider">Photo Upload</p>
          </div>

          <textarea placeholder="정보 공유 및 내용 입력" class="w-full px-4 py-3 rounded-xl bg-slate-50 border-none outline-none focus:ring-1 focus:ring-slate-900 h-24 resize-none text-xs"></textarea>

          <button class="w-full py-3.5 bg-slate-900 text-white rounded-xl font-bold text-xs hover:bg-slate-800 transition-all active:scale-95">공유하기</button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AppHeader from '../components/AppHeader.vue'

const showUploadModal = ref(false)

const placeholderPosts = ref(Array.from({ length: 9 }, (_, i) => ({
  id: i + 1,
  user: `Swimmer_${i + 1}`,
  likes: Math.floor(Math.random() * 200) + 50
})))
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }

.modal-enter-active, .modal-leave-active { transition: all 0.2s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; transform: scale(0.98); }
</style>
