<template>
  <div class="min-h-screen bg-slate-50 flex flex-col font-sans">
    <AppHeader />

    <!-- Main Content -->
    <main class="flex-1 p-6 max-w-6xl mx-auto w-full space-y-6">
      <!-- Monthly Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="bg-white p-6 rounded-[2rem] shadow-sm border border-slate-100 flex items-center gap-4 hover:shadow-md transition-shadow">
          <div class="w-12 h-12 bg-blue-50 rounded-2xl flex items-center justify-center text-blue-600 font-bold text-xl">
             <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-6 h-6">
                <path d="M12 21.75c4.48 0 8.274-3.673 8.274-8.274 0-4.48-3.794-8.274-8.274-8.274-4.48 0-8.274 3.794-8.274 8.274 0 4.601 3.794 8.274 8.274 8.274zm0-15c3.673 0 6.774 3.1 6.774 6.726 0 3.626-3.1 6.726-6.774 6.726-3.674 0-6.774-3.1-6.774-6.726 0-3.626 3.1-6.726 6.774-6.726zM12 9a.75.75 0 01.75.75v3a.75.75 0 01-.75.75H9a.75.75 0 010-1.5h2.25V9.75A.75.75 0 0112 9z" />
             </svg>
          </div>
          <div>
            <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">이번 달 총 거리</p>
            <p class="text-2xl font-black text-slate-800">{{ displayTotalDistance.toLocaleString() }}<span class="text-sm ml-1 font-bold text-slate-400">{{ unitLabel }}</span></p>
          </div>
        </div>
        <div class="bg-white p-6 rounded-[2rem] shadow-sm border border-slate-100 flex items-center gap-4 hover:shadow-md transition-shadow">
          <div class="w-12 h-12 bg-orange-50 rounded-2xl flex items-center justify-center text-orange-600 font-bold text-xl">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-6 h-6">
              <path fill-rule="evenodd" d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25zM12.75 6a.75.75 0 00-1.5 0v6c0 .414.336.75.75.75h4.5a.75.75 0 000-1.5h-3.75V6z" clip-rule="evenodd" />
            </svg>
          </div>
          <div>
            <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">총 수영 시간</p>
            <p class="text-2xl font-black text-slate-800">{{ monthlyTotalDuration }}<span class="text-sm ml-1 font-bold text-slate-400">분</span></p>
          </div>
        </div>
        <div class="bg-white p-6 rounded-[2rem] shadow-sm border border-slate-100 flex items-center gap-4 hover:shadow-md transition-shadow">
          <div class="w-12 h-12 bg-emerald-50 rounded-2xl flex items-center justify-center text-emerald-600 font-bold text-xl">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-6 h-6">
              <path fill-rule="evenodd" d="M19.916 4.626a.75.75 0 01.208 1.04l-9 13.5a.75.75 0 01-1.154.114l-6-6a.75.75 0 011.06-1.06l5.353 5.353 8.493-12.739a.75.75 0 011.04-.208z" clip-rule="evenodd" />
            </svg>
          </div>
          <div>
            <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">수영 간 날</p>
            <p class="text-2xl font-black text-slate-800">{{ attendanceDaysCount }}<span class="text-sm ml-1 font-bold text-slate-400">일</span></p>
          </div>
        </div>
      </div>

      <!-- Calendar Card -->
      <div class="bg-white rounded-[2.5rem] shadow-xl shadow-slate-200/30 overflow-hidden border border-slate-100">
        <div class="p-8 flex justify-between items-center bg-white">
          <div class="flex flex-col">
            <h2 class="text-3xl font-black text-slate-800 tracking-tight">{{ currentYear }}년 {{ currentMonth }}월</h2>
            <p class="text-sm text-slate-400 font-semibold italic">Swim Calendar</p>
          </div>
          <div class="flex gap-2">
            <button @click="prevMonth" class="w-12 h-12 flex items-center justify-center bg-slate-50 hover:bg-slate-100 rounded-2xl transition-all">
              <svg class="w-6 h-6 text-slate-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M15 19l-7-7 7-7" stroke-width="2.5" /></svg>
            </button>
            <button @click="nextMonth" class="w-12 h-12 flex items-center justify-center bg-slate-50 hover:bg-slate-100 rounded-2xl transition-all">
              <svg class="w-6 h-6 text-slate-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" stroke-width="2.5" /></svg>
            </button>
          </div>
        </div>

        <!-- Calendar Grid -->
        <div class="grid grid-cols-7 bg-slate-50/50 border-t border-b border-slate-50">
          <div v-for="day in ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']" :key="day" class="py-4 text-[11px] font-black tracking-widest text-slate-400 text-center uppercase">{{ day }}</div>
        </div>
        <div class="grid grid-cols-7">
          <div 
            v-for="(date, idx) in calendarDays" :key="idx"
            :class="['group relative min-h-[140px] p-4 border-r border-b border-slate-50 transition-all cursor-pointer overflow-hidden', 
                     date.isCurrentMonth ? 'bg-white hover:bg-slate-50/30' : 'bg-slate-50/20 opacity-30']"
            @click="openRecordModal(date)"
            :style="getCalendarCellStyle(date.fullDate)"
          >
            <!-- Overlay for better readability on image backgrounds -->
            <div v-if="hasRecordOn(date.fullDate) && getFirstImageUrl(date.fullDate)" class="absolute inset-0 bg-slate-900/20 group-hover:bg-slate-900/10 transition-colors"></div>

            <span :class="['relative z-10 text-sm font-bold', 
                          (hasRecordOn(date.fullDate) && getFirstImageUrl(date.fullDate)) ? 'text-white drop-shadow-md' : (date.isCurrentMonth ? 'text-slate-800' : 'text-slate-300')]">
              {{ date.day }}
            </span>
            
            <div v-if="hasRecordOn(date.fullDate)" class="relative z-10 mt-2 space-y-1.5">
              <div v-for="rec in getRecordsForDate(date.fullDate)" :key="rec.id" class="flex flex-col">
                 <div class="flex items-center gap-1">
                    <div :class="['w-1 h-3 rounded-full', getStrokeColor(rec.strokeType)]"></div>
                    <span :class="['text-[10px] font-bold tracking-tighter', getFirstImageUrl(date.fullDate) ? 'text-white' : 'text-slate-500']">{{ getStrokeName(rec.strokeType) }}</span>
                 </div>
                 <p :class="['text-[12px] font-black ml-2', getFirstImageUrl(date.fullDate) ? 'text-white' : 'text-slate-800']">
                    {{ toDisplayDistance(rec.distance) }}{{ unitLabel }}
                 </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Detailed Record Modal -->
    <transition name="fade">
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 backdrop-blur-sm p-4">
        <div class="bg-white w-full max-w-lg rounded-[2.5rem] shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
          
          <!-- Modal Header -->
          <div class="p-6 md:p-8 flex justify-between items-center border-b border-slate-100 flex-shrink-0">
            <div class="flex flex-col">
               <h3 class="text-2xl font-black text-slate-800 tracking-tight">{{ formattedSelectedDate }}</h3>
               <p class="text-sm text-slate-400 font-bold">Swim Record Entry</p>
            </div>
            <button @click="showModal = false" class="w-10 h-10 flex items-center justify-center rounded-full hover:bg-slate-100 text-slate-400 transition-colors">✕</button>
          </div>

          <!-- Modal Body (Scrollable) -->
          <div class="p-6 md:p-8 overflow-y-auto space-y-8 flex-1">
            
            <!-- Existing Records List -->
            <div v-if="getRecordsForDate(selectedDate).length > 0" class="space-y-4">
               <h4 class="text-xs font-black text-slate-400 uppercase tracking-widest">오늘의 기록 리스트</h4>
               <div v-for="rec in getRecordsForDate(selectedDate)" :key="rec.id" class="bg-slate-50 rounded-2xl p-4 flex justify-between items-center border border-slate-100">
                  <div class="flex items-center gap-4">
                    <div :class="['w-10 h-10 rounded-xl flex items-center justify-center text-white font-bold text-xs', getStrokeColor(rec.strokeType)]">
                      {{ getStrokeShortName(rec.strokeType) }}
                    </div>
                    <div>
                      <p class="text-sm font-bold text-slate-800">{{ getStrokeName(rec.strokeType) }}</p>
                      <p class="text-[11px] text-slate-500 font-medium">{{ rec.duration }}분 동안 수영</p>
                    </div>
                  </div>
                  <div class="text-right">
                    <p class="text-lg font-black text-blue-600">{{ toDisplayDistance(rec.distance) }}{{ unitLabel }}</p>
                    <button @click="removeRecord(rec.id)" class="text-[10px] font-bold text-red-400 hover:text-red-600 transition-colors uppercase">삭제</button>
                  </div>
               </div>
            </div>

            <!-- New Record Form -->
            <div class="space-y-6 bg-blue-50/30 p-6 rounded-[2rem] border border-blue-100/50">
              <h4 class="text-xs font-black text-blue-600 uppercase tracking-widest text-center">기록 추가하기</h4>
              
              <!-- Stroke Selector -->
              <div class="space-y-3">
                <p class="text-xs font-bold text-slate-500 ml-1">영법 선택</p>
                <div class="grid grid-cols-4 gap-2">
                  <button 
                    v-for="stroke in strokeTypes" :key="stroke.value"
                    @click="recordForm.strokeType = stroke.value"
                    :class="['py-3 rounded-xl text-xs font-bold transition-all border shadow-sm', 
                             recordForm.strokeType === stroke.value ? 'bg-blue-600 text-white border-blue-600 scale-105 shadow-blue-200' : 'bg-white text-slate-500 border-slate-200 hover:border-blue-300']"
                  >
                    {{ stroke.label }}
                  </button>
                </div>
              </div>

              <!-- Distance Quick Input -->
              <div class="space-y-3">
                <p class="text-xs font-bold text-slate-500 ml-1">거리 입력 ({{ unitLabel }})</p>
                <div class="flex items-center gap-3">
                  <input v-model.number="displayInputDistance" type="number" class="flex-1 px-5 py-4 rounded-2xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10 font-black text-xl bg-white" />
                </div>
                <div class="grid grid-cols-4 gap-2">
                  <button v-for="d in [25, 50, 100, 500]" :key="d" @click="addDistance(d)" class="py-2 bg-white rounded-xl text-xs font-black text-slate-600 border border-slate-200 hover:bg-blue-50 hover:border-blue-300 transition-all">+{{ d }}</button>
                  <button @click="displayInputDistance = 0" class="py-2 bg-slate-100 rounded-xl text-xs font-black text-slate-400 border border-slate-200 hover:bg-white transition-all">Reset</button>
                </div>
              </div>

              <!-- Duration & Memo -->
              <div class="space-y-2">
                <label class="text-xs font-bold text-slate-500 ml-1">시간 (분)</label>
                <input v-model.number="recordForm.duration" type="number" class="w-full px-5 py-3.5 rounded-2xl border border-slate-200 outline-none focus:border-blue-500 bg-white font-bold" />
              </div>
              
              <div class="space-y-2">
                <label class="text-xs font-bold text-slate-500 ml-1">메모</label>
                <textarea v-model="recordForm.memo" placeholder="수영에 대한 소감을 남겨주세요." class="w-full px-5 py-3.5 rounded-2xl border border-slate-200 outline-none focus:border-blue-500 bg-white h-24 resize-none text-sm"></textarea>
              </div>

              <button @click="saveRecord" :disabled="isUploading" class="w-full py-4 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white rounded-2xl font-black text-lg shadow-xl shadow-blue-500/30 transition-all active:scale-[0.98] disabled:opacity-50 mt-4">
                {{ isUploading ? '기록 저장 중...' : '기록 추가하기' }}
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
const displayInputDistance = ref(0)

const isUploading = ref(false)

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

const addDistance = (d) => {
  displayInputDistance.value = (Number(displayInputDistance.value) || 0) + d
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

const monthlyTotalDuration = computed(() => records.value.reduce((acc, r) => acc + (Number(r.duration) || 0), 0))
const displayTotalDistance = computed(() => {
  const totalMeters = records.value.reduce((acc, r) => acc + (Number(r.distance) || 0), 0)
  return toDisplayDistance(totalMeters)
})

const attendanceDaysCount = computed(() => {
  const uniqueDays = new Set(records.value.map(r => r.date))
  return uniqueDays.size
})

const formattedSelectedDate = computed(() => {
  if (!selectedDate.value) return ''
  const [y, m, d] = selectedDate.value.split('-')
  return `${y}년 ${parseInt(m)}월 ${parseInt(d)}일`
})

const hasRecordOn = (dateStr) => records.value.some(r => r.date === dateStr)
const getRecordsForDate = (dateStr) => records.value.filter(r => r.date === dateStr)
const getFirstImageUrl = (dateStr) => getRecordsForDate(dateStr).find(r => r.imageUrl)?.imageUrl

const getCalendarCellStyle = (dateStr) => {
  const imageUrl = getFirstImageUrl(dateStr)
  if (imageUrl) {
    return {
      backgroundImage: `url(${imageUrl})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center'
    }
  }
  return {}
}

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

const openRecordModal = (date) => {
  selectedDate.value = date.fullDate
  displayInputDistance.value = 0
  recordForm.value = { strokeType: 'FREE', distance: 0, duration: 0, memo: '', imageUrl: '' }
  showModal.value = true
}

const saveRecord = async () => {
  if (displayInputDistance.value <= 0) return
  isUploading.value = true
  try {
    const payload = {
      date: selectedDate.value,
      strokeType: recordForm.value.strokeType,
      distance: Number(toDbDistance(displayInputDistance.value)) || 0,
      duration: Number(recordForm.value.duration) || 0,
      memo: recordForm.value.memo || '',
      imageUrl: null
    }
    await axios.post('/api/swimming/record', payload)
    // 리셋 후 기록 다시 불러오기
    displayInputDistance.value = 0
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
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
