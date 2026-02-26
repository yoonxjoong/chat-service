<template>
  <div class="min-h-screen bg-slate-50 flex flex-col">
    <AppHeader />

    <!-- Main Content -->
    <main class="flex-1 p-6 max-w-6xl mx-auto w-full space-y-8">
      <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h2 class="text-3xl font-black text-slate-800 tracking-tighter">수영 피드</h2>
          <p class="text-sm text-slate-400 font-bold tracking-widest uppercase">나의 수영 기록 갤러리</p>
        </div>
        
        <!-- Filter/Sort (Optional) -->
        <div class="flex gap-2">
          <select v-model="sortOrder" class="bg-white px-4 py-2 rounded-xl border border-slate-200 text-xs font-bold text-slate-600 outline-none">
            <option value="desc">최신순</option>
            <option value="asc">오래된순</option>
          </select>
        </div>
      </div>

      <!-- Feed Grid -->
      <div v-if="sortedRecords.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
        <div 
          v-for="record in sortedRecords" :key="record.id"
          class="group bg-white rounded-[2rem] overflow-hidden shadow-sm hover:shadow-xl transition-all duration-500 border border-slate-100 flex flex-col"
        >
          <!-- Image Section -->
          <div class="relative aspect-square overflow-hidden bg-slate-100">
            <img 
              v-if="record.imageUrl" 
              :src="record.imageUrl" 
              class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
            />
            <div v-else class="w-full h-full flex flex-col items-center justify-center text-slate-300 gap-3">
              <div class="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center text-3xl">🏊‍♂️</div>
              <span class="text-[10px] font-black tracking-widest uppercase">No Image</span>
            </div>
            
            <!-- Date Badge -->
            <div class="absolute top-4 left-4 px-3 py-1.5 bg-white/90 backdrop-blur-md rounded-full shadow-sm text-[10px] font-black text-slate-800">
              {{ formatDate(record.date) }}
            </div>
          </div>

          <!-- Content Section -->
          <div class="p-6 space-y-4">
            <div class="flex justify-between items-center">
              <div class="flex flex-col">
                <span class="text-[10px] font-black text-primary-500 uppercase tracking-widest leading-none mb-1">Distance</span>
                <span class="text-2xl font-black text-slate-800 tracking-tighter">{{ toDisplayDistance(record.distance) }}{{ unitLabel }}</span>
              </div>
              <div class="flex flex-col items-end">
                <span class="text-[10px] font-black text-orange-400 uppercase tracking-widest leading-none mb-1">Duration</span>
                <span class="text-2xl font-black text-slate-800 tracking-tighter">{{ record.duration }}분</span>
              </div>
            </div>

            <div v-if="record.memo" class="bg-slate-50 p-4 rounded-2xl border border-slate-100">
              <p class="text-sm text-slate-600 leading-relaxed italic">
                "{{ record.memo }}"
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="py-20 flex flex-col items-center justify-center text-slate-300">
        <div class="text-6xl mb-4">📸</div>
        <p class="font-bold text-lg">아직 등록된 수영 기록이 없습니다.</p>
        <p class="text-sm">대시보드에서 오늘의 수영을 인증해보세요!</p>
        <button @click="$router.push('/')" class="mt-6 px-6 py-3 bg-primary-600 text-white rounded-2xl font-bold shadow-lg shadow-primary-100 hover:bg-primary-700 transition-all">
          첫 기록 남기기
        </button>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import AppHeader from '../components/AppHeader.vue'

const router = useRouter()
const user = ref({ nickname: '', distanceUnit: 'METER' })
const records = ref([])
const sortOrder = ref('desc')

const M_TO_YD = 1.09361
const unitLabel = computed(() => user.value.distanceUnit === 'YARD' ? 'yd' : 'm')

const toDisplayDistance = (m) => {
  if (user.value.distanceUnit === 'YARD') return Math.round(m * M_TO_YD)
  return m
}

const fetchUser = async () => {
  try {
    const res = await axios.get('/api/user/me')
    user.value = res.data
  } catch (err) {
    router.push('/login')
  }
}

const fetchAllRecords = async () => {
  try {
    // 모든 기록을 가져오기 위해 현재 날짜 기준으로 넉넉하게 필터링하거나 별도 엔드포인트 필요
    // 현재는 기존 API를 활용하되 최근 12개월 데이터를 가져오는 방식으로 처리 (임시)
    const now = new Date()
    const currentYear = now.getFullYear()
    const currentMonth = now.getMonth() + 1
    
    // 단순화를 위해 현재 달 기록만 먼저 가져오되, 향후 전체 기록 API 추가 권장
    const res = await axios.get(`/api/swimming/records?year=${currentYear}&month=${currentMonth}`)
    records.value = res.data || []
  } catch (err) {}
}

const sortedRecords = computed(() => {
  return [...records.value].sort((a, b) => {
    return sortOrder.value === 'desc' 
      ? new Date(b.date) - new Date(a.date)
      : new Date(a.date) - new Date(b.date)
  })
})

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('ko-KR', { year: 'numeric', month: 'short', day: 'numeric', weekday: 'short' })
}

onMounted(async () => {
  await fetchUser()
  await fetchAllRecords()
})
</script>
