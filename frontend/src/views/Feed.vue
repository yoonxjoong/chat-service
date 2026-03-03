<template>
  <div class="min-h-screen bg-white flex flex-col font-sans antialiased text-slate-900">
    <AppHeader />

    <!-- Main Content -->
    <main class="flex-1 p-4 md:p-6 max-w-5xl mx-auto w-full space-y-8">
      <!-- Header Section -->
      <div class="px-1">
        <h2 class="text-xl md:text-2xl font-bold tracking-tight">물옷 갤러리</h2>
        <p class="text-xs md:text-sm text-slate-400 font-medium">나만의 수영복과 장비 기록을 남겨보세요.</p>
      </div>

      <!-- Private/Public Tabs -->
      <div class="flex flex-col gap-4">
        <div class="flex gap-1.5 overflow-x-auto no-scrollbar px-1">
          <button v-for="tab in tabs" :key="tab.name" @click="activeTab = tab.name"
            :disabled="tab.disabled"
            :class="['px-5 py-2 rounded-full text-[11px] font-bold transition-all whitespace-nowrap border', 
                     activeTab === tab.name ? 'bg-slate-100 border-slate-100 text-slate-900' : 'bg-white border-transparent text-slate-300',
                     tab.disabled ? 'opacity-50 cursor-not-allowed' : 'hover:text-slate-600']">
            {{ tab.label }}
          </button>
        </div>

        <!-- Coming Soon Notice for Public Feed -->
        <div v-if="activeTab !== 'mine'" class="bg-blue-50/50 p-4 rounded-2xl border border-blue-100/50 flex items-center gap-3">
          <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center text-blue-600 shadow-sm">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-4 h-4">
              <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
            </svg>
          </div>
          <p class="text-[11px] md:text-xs font-bold text-blue-600">
            다른 사람의 물옷을 구경하는 기능은 곧 업데이트될 예정입니다.<br class="md:hidden">
            우선 나만의 물옷 기록을 차곡차곡 쌓아보세요!
          </p>
        </div>
      </div>

      <!-- My Private Grid (3x3) -->
      <div v-if="activeTab === 'mine'" class="space-y-8">
        <div v-if="visiblePosts.length > 0">
          <div class="grid grid-cols-3 gap-1 md:gap-4 lg:gap-6">
            <div 
              v-for="post in visiblePosts" :key="post.id" 
              class="group relative aspect-square bg-slate-50 rounded-lg overflow-hidden border border-slate-100 cursor-pointer active:scale-[0.98] transition-transform"
            >
              <!-- Actual image -->
              <img v-if="post.imageUrl" :src="post.imageUrl" class="w-full h-full object-cover" />
              <!-- Placeholder if no image -->
              <div v-else class="absolute inset-0 flex flex-col items-center justify-center text-slate-200 group-hover:text-slate-300 transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.2" stroke="currentColor" class="w-8 h-8 md:w-10 md:h-10">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 15.75l5.159-5.159a2.25 2.25 0 013.182 0l5.159 5.159m-1.5-1.5l1.409-1.409a2.25 2.25 0 013.182 0l2.909 2.909m-18 3.75h16.5a1.5 1.5 0 001.5-1.5V6a1.5 1.5 0 00-1.5-1.5H3.75A1.5 1.5 0 002.25 6v12a1.5 1.5 0 001.5 1.5zm10.5-11.25h.008v.008h-.008V8.25zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z" />
                </svg>
              </div>
              <!-- Minimal Overlay for Private View -->
              <div class="absolute bottom-0 left-0 right-0 p-2 md:p-3 bg-gradient-to-t from-black/60 to-transparent opacity-0 group-hover:opacity-100 transition-opacity">
                <p class="text-[9px] md:text-[10px] text-white font-bold">{{ post.date }}</p>
                <p v-if="post.memo" class="text-[8px] md:text-[9px] text-white/80 truncate">{{ post.memo }}</p>
              </div>
            </div>
          </div>

          <!-- Load More Button -->
          <div v-if="hasMore" class="flex justify-center pt-8">
            <button @click="loadMore" :disabled="isLoadingMore" class="text-[11px] font-bold text-slate-400 hover:text-slate-900 transition-colors uppercase tracking-widest flex items-center gap-2">
              <div v-if="isLoadingMore" class="animate-spin h-3 w-3 border-2 border-slate-300 border-t-slate-900 rounded-full"></div>
              {{ isLoadingMore ? 'Loading...' : 'Load More' }}
            </button>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="py-20 flex flex-col items-center justify-center text-center space-y-4">
          <div class="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center text-slate-200">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-8 h-8">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6.827 6.175A2.31 2.31 0 015.186 7.23c-.38.054-.757.112-1.134.175C2.999 7.58 2.25 8.507 2.25 9.574V18a2.25 2.25 0 002.25 2.25h15A2.25 2.25 0 0021.75 18V9.574c0-1.067-.75-1.994-1.802-2.169a47.865 47.865 0 00-1.134-.175 2.31 2.31 0 01-1.64-1.055l-.822-1.316a2.192 2.192 0 00-1.736-1.039 48.774 48.774 0 00-5.232 0 2.192 2.192 0 00-1.736 1.039l-.821 1.316z" />
              <path stroke-linecap="round" stroke-linejoin="round" d="M16.5 12.75a4.5 4.5 0 11-9 0 4.5 4.5 0 019 0zM18.75 10.5h.008v.008h-.008V10.5z" />
            </svg>
          </div>
          <div>
            <p class="text-sm font-bold text-slate-800">아직 등록된 사진이 없어요</p>
            <p class="text-xs text-slate-400 font-medium">첫 번째 물옷 사진을 공유해보세요!</p>
          </div>
          <button @click="showUploadModal = true" class="text-xs font-bold text-blue-600 hover:text-blue-700">+ 사진 추가하기</button>
        </div>
      </div>

      <!-- Other Tabs Placeholder -->
      <div v-else class="py-20 flex flex-col items-center justify-center text-center space-y-4">
        <div class="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center text-slate-200">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-8 h-8">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
          </svg>
        </div>
        <div class="px-6">
          <p class="text-sm font-bold text-slate-800">커뮤니티 기능 준비 중</p>
          <p class="text-xs text-slate-400 font-medium mt-1">곧 다른 수영인들의 멋진 물옷을 만날 수 있어요!</p>
        </div>
      </div>
    </main>

    <!-- Floating Action Button (Upload Photo) -->
    <button
      @click="showUploadModal = true"
      class="fixed bottom-20 md:bottom-6 right-6 w-12 h-12 md:w-14 md:h-14 bg-slate-900 text-white rounded-full shadow-lg flex items-center justify-center hover:bg-blue-600 active:scale-90 transition-all z-40"
    >      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="3" stroke="currentColor" class="w-5 h-5 md:w-6 md:h-6">
        <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
      </svg>
    </button>

    <!-- Upload Modal -->
    <transition name="modal">
      <div v-if="showUploadModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="showUploadModal = false"></div>
        <div class="relative bg-white w-full max-w-sm rounded-2xl shadow-2xl overflow-hidden p-6 space-y-6 animate-in zoom-in duration-200">
          <div class="p-5 border-b border-slate-100 flex justify-between items-center">
            <h3 class="text-base font-bold">사진 기록</h3>
            <button @click="showUploadModal = false" class="text-slate-400 p-1">✕</button>
          </div>
          
          <div class="aspect-square bg-slate-50 rounded-xl border border-dashed border-slate-200 flex flex-col items-center justify-center gap-3 text-slate-400 hover:bg-slate-100 transition-colors cursor-pointer relative overflow-hidden">
            <input type="file" class="absolute inset-0 opacity-0 cursor-pointer" accept="image/*" @change="handleImageUpload" :disabled="isUploading" />
            
            <template v-if="uploadForm.imageUrl">
              <img :src="uploadForm.imageUrl" class="w-full h-full object-cover" />
              <div class="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 hover:opacity-100 transition-opacity">
                <p class="text-[10px] text-white font-bold">변경하려면 클릭</p>
              </div>
            </template>
            <template v-else>
              <div v-if="isUploading" class="animate-spin h-6 w-6 border-2 border-slate-300 border-t-slate-900 rounded-full"></div>
              <svg v-else xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-10 h-10">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
              </svg>
              <p class="text-[11px] font-bold uppercase tracking-wider">{{ isUploading ? 'Uploading...' : 'Photo Upload' }}</p>
            </template>
          </div>

          <textarea v-model="uploadForm.memo" placeholder="제품 정보나 간단한 한마디" class="w-full px-4 py-3 rounded-xl bg-slate-50 border-none outline-none focus:ring-1 focus:ring-slate-900 h-24 resize-none text-xs"></textarea>

          <button @click="saveRecord" :disabled="isSaving || !uploadForm.imageUrl" class="w-full py-3.5 bg-slate-900 text-white rounded-xl font-bold text-xs hover:bg-slate-800 transition-all active:scale-95 disabled:opacity-30">
            {{ isSaving ? '저장 중...' : '저장하기' }}
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import AppHeader from '../components/AppHeader.vue'

const showUploadModal = ref(false)
const activeTab = ref('mine')
const isLoading = ref(false)
const isUploading = ref(false)
const isSaving = ref(false)
const displayLimit = ref(12)

const uploadForm = ref({
  imageUrl: '',
  memo: ''
})

const tabs = [
  { name: 'mine', label: '나의 물옷', disabled: false },
  { name: 'feed', label: '전체 피드 (예정)', disabled: false },
  { name: 'popular', label: '인기 (예정)', disabled: false }
]

const allPosts = ref([])

const fetchRecords = async () => {
  isLoading.value = true
  try {
    const res = await axios.get('/api/mul-ot/records')
    allPosts.value = res.data || []
  } catch (err) {
    console.error('Failed to fetch records', err)
  } finally {
    isLoading.value = false
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
    uploadForm.value.imageUrl = res.data.url
  } catch (err) {
    alert('이미지 업로드에 실패했습니다.')
  } finally {
    isUploading.value = false
  }
}

const saveRecord = async () => {
  if (!uploadForm.value.imageUrl) return
  
  isSaving.value = true
  try {
    await axios.post('/api/mul-ot/record', {
      imageUrl: uploadForm.value.imageUrl,
      memo: uploadForm.value.memo
    })
    showUploadModal.value = false
    uploadForm.value = { imageUrl: '', memo: '' }
    await fetchRecords()
  } catch (err) {
    alert('기록 저장 중 오류가 발생했습니다.')
  } finally {
    isSaving.value = false
  }
}

// 현재 화면에 보여줄 데이터 (pagination)
const visiblePosts = computed(() => {
  return allPosts.value.slice(0, displayLimit.value)
})

const hasMore = computed(() => {
  return displayLimit.value < allPosts.value.length
})

const loadMore = () => {
  displayLimit.value += 12
}

onMounted(fetchRecords)
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }

.modal-enter-active, .modal-leave-active { transition: all 0.2s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; transform: scale(0.98); }
</style>
