<template>
  <div class="min-h-screen bg-white flex flex-col font-sans antialiased text-slate-900 transition-colors duration-300">
    <AppHeader />

    <!-- Main Content -->
    <main class="flex-1 p-4 md:p-6 max-w-5xl mx-auto w-full space-y-6">
      <!-- Top Section: Welcome & Mini Stats -->
      <div class="flex justify-between items-center px-1">
        <div>
          <h2 class="text-xl md:text-2xl font-bold tracking-tight">대시보드</h2>
          <p class="text-xs md:text-sm text-slate-400 font-medium">오늘도 시원하게 어푸어푸! 🏊‍♂️</p>
        </div>
        <div class="flex gap-2">
           <div class="bg-slate-50 px-2 py-2 rounded-xl border border-slate-100 flex flex-col items-center w-[64px] md:w-20 shrink-0">
              <span class="text-[9px] md:text-[10px] font-bold text-slate-400 uppercase tracking-tighter">연속</span>
              <span class="text-xs md:text-sm font-black text-blue-600 truncate">{{ currentStreak }}일</span>
           </div>
           <div class="bg-slate-50 px-2 py-2 rounded-xl border border-slate-100 flex flex-col items-center w-[64px] md:w-20 shrink-0">
              <span class="text-[9px] md:text-[10px] font-bold text-slate-400 uppercase tracking-tighter">이달</span>
              <span class="text-xs md:text-sm font-black text-indigo-600 truncate">{{ monthlyTotalDistanceStr }}</span>
           </div>
        </div>
      </div>

      <!-- Calendar Card -->
      <div class="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
        <div class="p-5 flex justify-between items-center border-b border-slate-50">
          <h3 class="text-base md:text-lg font-bold text-slate-800">{{ currentYear }}년 {{ currentMonth }}월</h3>
          <div class="flex gap-1">
            <button @click="prevMonth" class="p-2 hover:bg-slate-50 rounded-lg transition-colors">
              <svg class="w-4 h-4 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path d="M15 19l-7-7 7-7" /></svg>
            </button>
            <button @click="nextMonth" class="p-2 hover:bg-slate-50 rounded-lg transition-colors">
              <svg class="w-4 h-4 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2.5"><path d="M9 5l7 7-7 7" /></svg>
            </button>
          </div>
        </div>

        <div class="grid grid-cols-7 border-b border-slate-50 bg-slate-50/30">
          <div v-for="day in ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']" :key="day" class="py-2 text-[10px] font-bold text-slate-400 text-center uppercase tracking-wider">{{ day }}</div>
        </div>
        <div class="grid grid-cols-7">
          <div 
            v-for="(date, idx) in calendarDays" :key="idx"
            :class="['group relative min-h-[80px] md:min-h-[110px] p-2 border-r border-b border-slate-50 transition-all cursor-pointer active:bg-slate-50', 
                     date.isCurrentMonth ? 'bg-white' : 'bg-slate-50/20 opacity-40']"
            @click="openRecordModal(date)"
          >
            <span :class="['relative z-10 text-[11px] font-semibold', 
                          hasRecordOn(date.fullDate) ? 'text-blue-600 font-bold' : 'text-slate-400']">
              {{ date.day }}
            </span>
            
            <div v-if="hasRecordOn(date.fullDate)" class="mt-1 space-y-1">
              <div v-for="rec in getRecordsForDate(date.fullDate).slice(0, 2)" :key="rec.id" class="flex items-center gap-1">
                 <div :class="['w-1 h-1 rounded-full', getStrokeColor(rec.strokeType)]"></div>
                 <span class="text-[9px] font-bold text-slate-600 truncate">{{ toDisplayDistance(rec.distance) }}{{ unitLabel }}</span>
              </div>
              <div v-if="getRecordsForDate(date.fullDate).length > 2" class="text-[8px] font-bold text-slate-300">+{{ getRecordsForDate(date.fullDate).length - 2 }}</div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Refined Floating Button (Quick Record for TODAY) -->
    <button 
      @click="openQuickModal"
      class="fixed bottom-6 right-6 w-12 h-12 md:w-14 md:h-14 bg-slate-900 text-white rounded-full shadow-lg flex items-center justify-center hover:bg-blue-600 active:scale-90 transition-all z-40"
    >
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="3" stroke="currentColor" class="w-5 h-5 md:w-6 md:h-6">
        <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
      </svg>
    </button>

    <!-- Unified Easy Record Modal (For both FAB and Calendar) -->
    <transition name="modal">
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="showModal = false"></div>
        <div class="relative bg-white w-full max-w-md rounded-2xl shadow-2xl overflow-hidden animate-in zoom-in duration-200 flex flex-col max-h-[90vh]">
          <!-- Modal Header -->
          <div class="p-5 border-b border-slate-100 flex justify-between items-center bg-white sticky top-0 z-10">
            <div>
              <h3 class="text-base font-bold">{{ formattedSelectedDate }}</h3>
              <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">{{ isToday(selectedDate) ? '오늘의 수영 기록' : '기록 편집' }}</p>
            </div>
            <button @click="showModal = false" class="text-slate-400 hover:text-slate-600 p-1">✕</button>
          </div>

          <div class="p-6 overflow-y-auto space-y-8">
            <!-- Existing Records List -->
            <div v-if="getRecordsForDate(selectedDate).length > 0" class="space-y-3">
               <p class="text-[10px] font-black text-slate-400 uppercase tracking-wider ml-1">저장된 기록</p>
               <div v-for="rec in getRecordsForDate(selectedDate)" :key="rec.id" class="bg-slate-50 p-4 rounded-xl flex justify-between items-center border border-slate-100">
                  <div class="flex items-center gap-3">
                    <div :class="['w-8 h-8 rounded-lg flex items-center justify-center text-white font-bold text-[10px]', getStrokeColor(rec.strokeType)]">
                      {{ getStrokeShortName(rec.strokeType) }}
                    </div>
                    <div>
                      <p class="text-xs font-bold text-slate-800">{{ getStrokeName(rec.strokeType) }}</p>
                      <p class="text-[10px] text-slate-400">{{ rec.duration }}분</p>
                    </div>
                  </div>
                  <div class="text-right">
                    <p class="text-sm font-black text-slate-900">{{ toDisplayDistance(rec.distance) }}{{ unitLabel }}</p>
                    <button @click="removeRecord(rec.id)" class="text-[9px] font-bold text-red-400 hover:text-red-600 transition-colors uppercase tracking-widest mt-0.5">삭제</button>
                  </div>
               </div>
            </div>

            <!-- Easy Add Form (Quick Menu Style) -->
            <div class="space-y-6 pt-2">
              <p class="text-[10px] font-black text-blue-600 uppercase tracking-wider ml-1">{{ getRecordsForDate(selectedDate).length > 0 ? '기록 추가' : '새로운 수영 등록' }}</p>
              
              <!-- Stroke Chips -->
              <div class="space-y-2">
                <label class="text-[10px] font-bold text-slate-400 uppercase tracking-wider ml-1">영법</label>
                <div class="flex flex-wrap gap-1.5">
                  <button 
                    v-for="s in strokeTypes" :key="s.value"
                    @click="recordForm.strokeType = s.value"
                    :class="['px-3.5 py-2 rounded-full text-[10px] font-bold transition-all border', 
                             recordForm.strokeType === s.value ? 'bg-slate-900 text-white border-slate-900 shadow-md' : 'bg-white text-slate-500 border-slate-200 hover:border-slate-300']"
                  >
                    {{ s.label }}
                  </button>
                </div>
              </div>

              <!-- Distance Slider -->
              <div class="space-y-3">
                <div class="flex justify-between items-center">
                  <label class="text-[10px] font-bold text-slate-400 uppercase tracking-wider ml-1">거리 ({{ unitLabel }})</label>
                  <span class="text-xl font-black text-slate-900">{{ recordForm.distance }}</span>
                </div>
                <input v-model.number="recordForm.distance" type="range" min="0" max="2000" step="25" class="w-full h-1 bg-slate-100 rounded-lg appearance-none accent-slate-900 cursor-pointer" />
                <div class="flex gap-1">
                  <button v-for="d in [25, 100, 500]" :key="d" @click="adjustDistance(d)" class="flex-1 py-1.5 bg-slate-50 text-[10px] font-bold text-slate-600 rounded-lg border border-slate-100 hover:bg-slate-100 transition-colors">+{{ d }}</button>
                </div>
              </div>

              <!-- Duration Slider -->
              <div class="space-y-3">
                <div class="flex justify-between items-center">
                  <label class="text-[10px] font-bold text-slate-400 uppercase tracking-wider ml-1">시간 (min)</label>
                  <span class="text-xl font-black text-slate-900">{{ recordForm.duration }}</span>
                </div>
                <input v-model.number="recordForm.duration" type="range" min="0" max="120" step="5" class="w-full h-1 bg-slate-100 rounded-lg appearance-none accent-slate-900 cursor-pointer" />
                <div class="flex gap-1">
                  <button v-for="t in [10, 30]" :key="t" @click="adjustDuration(t)" class="flex-1 py-1.5 bg-slate-50 text-[10px] font-bold text-slate-600 rounded-lg border border-slate-100 hover:bg-slate-100 transition-colors">+{{ t }}</button>
                </div>
              </div>

              <!-- Image Upload for Record -->
              <div class="space-y-2">
                <label class="text-[10px] font-bold text-slate-400 uppercase tracking-wider ml-1">오늘의 한 컷 (선택)</label>
                <div class="relative w-full aspect-video bg-slate-50 rounded-xl border border-dashed border-slate-200 flex flex-col items-center justify-center gap-2 overflow-hidden group cursor-pointer">
                  <input type="file" class="absolute inset-0 opacity-0 cursor-pointer z-10" accept="image/*" @change="handleImageUpload" :disabled="isUploadingImage" />
                  
                  <template v-if="recordForm.imageUrl">
                    <img :src="recordForm.imageUrl" class="w-full h-full object-cover" />
                    <div class="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                      <p class="text-[10px] text-white font-bold">이미지 변경</p>
                    </div>
                  </template>
                  <template v-else>
                    <div v-if="isUploadingImage" class="animate-spin h-5 w-5 border-2 border-slate-300 border-t-slate-900 rounded-full"></div>
                    <svg v-else xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6 text-slate-300">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M6.827 6.175A2.31 2.31 0 015.186 7.23c-.38.054-.757.112-1.134.175C2.999 7.58 2.25 8.507 2.25 9.574V18a2.25 2.25 0 002.25 2.25h15A2.25 2.25 0 0021.75 18V9.574c0-1.067-.75-1.994-1.802-2.169a47.865 47.865 0 00-1.134-.175 2.31 2.31 0 01-1.64-1.055l-.822-1.316a2.192 2.192 0 00-1.736-1.039 48.774 48.774 0 00-5.232 0 2.192 2.192 0 00-1.736 1.039l-.821 1.316z" />
                    </svg>
                    <p class="text-[9px] font-bold text-slate-400 uppercase tracking-widest">{{ isUploadingImage ? 'Uploading...' : 'Upload Photo' }}</p>
                  </template>
                </div>
              </div>

              <button @click="saveRecord" :disabled="isUploading || recordForm.distance <= 0" class="w-full py-4 bg-slate-900 text-white rounded-xl font-bold text-sm hover:bg-slate-800 transition-all active:scale-95 disabled:opacity-30 shadow-lg shadow-slate-200 mt-2">
                {{ isUploading ? '저장 중...' : '저장하기' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter, useRoute } from 'vue-router'
import AppHeader from '../components/AppHeader.vue'

const router = useRouter()
const route = useRoute()
const user = ref({ nickname: '', distanceUnit: 'METER' })
const records = ref([])
const showModal = ref(false)
const selectedDate = ref('')
const recordForm = ref({ strokeType: 'FREE', distance: 0, duration: 0, memo: '', imageUrl: '' })
const isUploading = ref(false)
const isUploadingImage = ref(false)

const adjustDistance = (amount) => {
  recordForm.value.distance = Math.max(0, (recordForm.value.distance || 0) + amount)
}

const adjustDuration = (amount) => {
  recordForm.value.duration = Math.max(0, (recordForm.value.duration || 0) + amount)
}

const M_TO_YD = 1.09361

const strokeTypes = [
  { value: 'FREE', label: '자유형', short: '자' },
  { value: 'BACK', label: '배영', short: '배' },
  { value: 'BREAST', label: '평영', short: '평' },
  { value: 'FLY', label: '접영', short: '접' },
  { value: 'IM', label: '개인혼영', short: '혼' },
  { value: 'KICK', label: '발차기', short: '킥' },
  { value: 'MIXED', label: '기타', short: '기' }
]

const getStrokeName = (val) => strokeTypes.find(s => s.value === val)?.label || '수영'
const getStrokeShortName = (val) => strokeTypes.find(s => s.value === val)?.short || '수'
const getStrokeColor = (val) => {
  const colors = {
    FREE: 'bg-blue-500',
    BACK: 'bg-sky-400',
    BREAST: 'bg-amber-500',
    FLY: 'bg-indigo-600',
    IM: 'bg-purple-600',
    KICK: 'bg-emerald-500',
    MIXED: 'bg-slate-500'
  }
  return colors[val] || 'bg-slate-400'
}

const now = new Date()
const currentYear = ref(now.getFullYear())
const currentMonth = ref(now.getMonth() + 1)

const unitLabel = computed(() => user.value.distanceUnit === 'YARD' ? 'yd' : 'm')

const toDisplayDistance = (m) => {
  if (user.value.distanceUnit === 'YARD') return Math.round(m * M_TO_YD)
  return m
}

const toDbDistance = (val) => {
  if (user.value.distanceUnit === 'YARD') return Math.round(val / M_TO_YD)
  return val
}

const fetchUser = async () => {
  try {
    const res = await axios.get('/api/user/me')
    user.value = res.data
  } catch (err) {
    router.push('/login')
  }
}

const fetchRecords = async () => {
  try {
    const res = await axios.get(`/api/swimming/records?year=${currentYear.value}&month=${currentMonth.value}`)
    records.value = res.data || []
  } catch (err) {}
}

const handleImageUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  const formData = new FormData()
  formData.append('file', file)

  isUploadingImage.value = true
  try {
    const res = await axios.post('/api/image/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    recordForm.value.imageUrl = res.data.url
  } catch (err) {
    alert('이미지 업로드에 실패했습니다.')
  } finally {
    isUploadingImage.value = false
  }
}

const monthlyTotalDistanceStr = computed(() => {
  const totalMeters = records.value.reduce((acc, r) => acc + (Number(r.distance) || 0), 0)
  const displayVal = toDisplayDistance(totalMeters)
  if (displayVal >= 1000) return `${(displayVal / 1000).toFixed(1)}k${unitLabel.value}`
  return `${displayVal}${unitLabel.value}`
})

const currentStreak = computed(() => {
  if (records.value.length === 0) return 0
  const recordDates = new Set(records.value.map(r => r.date))
  let streak = 0
  let checkDate = new Date()
  const todayStr = `${checkDate.getFullYear()}-${String(checkDate.getMonth() + 1).padStart(2, '0')}-${String(checkDate.getDate()).padStart(2, '0')}`
  if (!recordDates.has(todayStr)) checkDate.setDate(checkDate.getDate() - 1)
  while (true) {
    const dateStr = `${checkDate.getFullYear()}-${String(checkDate.getMonth() + 1).padStart(2, '0')}-${String(checkDate.getDate()).padStart(2, '0')}`
    if (recordDates.has(dateStr)) { streak++; checkDate.setDate(checkDate.getDate() - 1) }
    else break
  }
  return streak
})

const formattedSelectedDate = computed(() => {
  if (!selectedDate.value) return ''
  const [y, m, d] = selectedDate.value.split('-')
  return `${y}년 ${parseInt(m)}월 ${parseInt(d)}일`
})

const isToday = (dateStr) => {
  const d = new Date()
  const today = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  return dateStr === today
}

const hasRecordOn = (dateStr) => records.value.some(r => r.date === dateStr)
const getRecordsForDate = (dateStr) => records.value.filter(r => r.date === dateStr)

const calendarDays = computed(() => {
  const year = currentYear.value
  const month = currentMonth.value
  const days = []
  const firstDay = new Date(year, month - 1, 1).getDay()
  const lastDate = new Date(year, month, 0).getDate()
  const prevLastDate = new Date(year, month - 1, 0).getDate()
  for (let i = firstDay - 1; i >= 0; i--) {
    const d = prevLastDate - i
    const m = month === 1 ? 12 : month - 1
    const y = month === 1 ? year - 1 : year
    days.push({ day: d, isCurrentMonth: false, fullDate: `${y}-${String(m).padStart(2,'0')}-${String(d).padStart(2,'0')}` })
  }
  for (let i = 1; i <= lastDate; i++) {
    days.push({ day: i, isCurrentMonth: true, fullDate: `${year}-${String(month).padStart(2,'0')}-${String(i).padStart(2,'0')}` })
  }
  const remain = 42 - days.length
  for (let i = 1; i <= remain; i++) {
    const m = month === 12 ? 1 : month + 1
    const y = month === 12 ? year + 1 : year
    days.push({ day: i, isCurrentMonth: false, fullDate: `${y}-${String(m).padStart(2,'0')}-${String(i).padStart(2,'0')}` })
  }
  return days
})

const prevMonth = () => {
  if (currentMonth.value === 1) { currentMonth.value = 12; currentYear.value-- }
  else { currentMonth.value-- }
  fetchRecords()
}

const nextMonth = () => {
  if (currentMonth.value === 12) { currentMonth.value = 1; currentYear.value++ }
  else { currentMonth.value++ }
  fetchRecords()
}

const openQuickModal = () => {
  const d = new Date()
  const todayStr = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  openRecordModal({ fullDate: todayStr })
}

const openRecordModal = (date) => {
  selectedDate.value = date.fullDate
  recordForm.value = { strokeType: 'FREE', distance: 0, duration: 0, memo: '', imageUrl: '' }
  showModal.value = true
}

const saveRecord = async () => {
  if (recordForm.value.distance <= 0) return
  isUploading.value = true
  try {
    const payload = {
      date: selectedDate.value,
      strokeType: recordForm.value.strokeType,
      distance: Number(toDbDistance(recordForm.value.distance)) || 0,
      duration: Number(recordForm.value.duration) || 0,
      memo: recordForm.value.memo || '',
      imageUrl: recordForm.value.imageUrl || null
    }
    await axios.post('/api/swimming/record', payload)
    // 등록 후 폼 리셋 (날짜 모달은 열린 채 유지하여 추가 등록 가능하게 함)
    recordForm.value.distance = 0
    recordForm.value.duration = 0
    recordForm.value.imageUrl = ''
    await fetchRecords()
  } catch (err) {
    alert('저장 중 오류가 발생했습니다.')
  } finally {
    isUploading.value = false
  }
}

const removeRecord = async (id) => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    await axios.delete(`/api/swimming/record/${id}`)
    await fetchRecords()
  } catch (err) {
    alert('삭제 실패')
  }
}

onMounted(async () => {
  await fetchUser()
  await fetchRecords()
})
</script>

<style scoped>
.modal-enter-active, .modal-leave-active {
  transition: all 0.25s ease-out;
}
.modal-enter-from, .modal-leave-to {
  opacity: 0;
  transform: scale(0.98) translateY(10px);
}

/* Custom range input styling */
input[type=range] {
  -webkit-appearance: none;
  width: 100%;
  background: transparent;
}
input[type=range]:focus {
  outline: none;
}
input[type=range]::-webkit-slider-runnable-track {
  width: 100%;
  height: 4px;
  cursor: pointer;
  background: #f1f5f9;
  border-radius: 2px;
}
input[type=range]::-webkit-slider-thumb {
  height: 18px;
  width: 18px;
  border-radius: 50%;
  background: #0f172a;
  cursor: pointer;
  -webkit-appearance: none;
  margin-top: -7px;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
}
</style>
