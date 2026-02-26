<template>
  <div class="min-h-screen bg-slate-50 flex flex-col">
    <!-- Navbar -->
    <nav class="bg-white border-b border-slate-200 px-6 py-4 flex justify-between items-center shadow-sm sticky top-0 z-20">
      <div class="flex items-center gap-8">
        <div class="flex items-center gap-2">
          <div class="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center text-white shadow-md">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9" stroke-width="2" /></svg>
          </div>
          <h1 class="text-xl font-black text-slate-800 tracking-tighter italic">SWIM<span class="text-primary-600">RECORD</span></h1>
        </div>
        <div class="flex gap-1 bg-slate-100 p-1 rounded-xl">
          <router-link to="/" class="px-4 py-1.5 text-xs font-bold rounded-lg transition-all" :class="$route.path === '/' ? 'bg-white text-primary-600 shadow-sm' : 'text-slate-500 hover:text-slate-700'">대시보드</router-link>
          <router-link to="/stats" class="px-4 py-1.5 text-xs font-bold rounded-lg transition-all" :class="$route.path === '/stats' ? 'bg-white text-primary-600 shadow-sm' : 'text-slate-500 hover:text-slate-700'">통계</router-link>
          <router-link to="/chat" class="px-4 py-1.5 text-xs font-bold rounded-lg transition-all" :class="$route.path === '/chat' ? 'bg-white text-primary-600 shadow-sm' : 'text-slate-500 hover:text-slate-700'">채팅</router-link>
        </div>
      </div>
      <div class="flex items-center gap-4">
        <div class="flex flex-col items-end">
          <span class="text-xs font-black text-slate-700">{{ user.nickname }}님</span>
          <span class="text-[10px] text-green-500 font-bold">오늘도 열수! 🏊‍♂️</span>
        </div>
        <button @click="logout" class="p-2 hover:bg-red-50 text-slate-300 hover:text-red-500 rounded-lg transition-colors">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" stroke-width="2" /></svg>
        </button>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="flex-1 p-6 max-w-6xl mx-auto w-full space-y-6">
      <!-- Monthly Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 flex items-center gap-4">
          <div class="w-12 h-12 bg-blue-50 rounded-2xl flex items-center justify-center text-blue-600 font-bold text-xl">m</div>
          <div>
            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">이번 달 총 거리</p>
            <p class="text-2xl font-black text-slate-800">{{ monthlyTotalDistance.toLocaleString() }}m</p>
          </div>
        </div>
        <div class="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 flex items-center gap-4">
          <div class="w-12 h-12 bg-orange-50 rounded-2xl flex items-center justify-center text-orange-600 font-bold text-xl">min</div>
          <div>
            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">총 수영 시간</p>
            <p class="text-2xl font-black text-slate-800">{{ monthlyTotalDuration }}분</p>
          </div>
        </div>
        <div class="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 flex items-center gap-4">
          <div class="w-12 h-12 bg-green-50 rounded-2xl flex items-center justify-center text-green-600 font-bold text-xl">✔</div>
          <div>
            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">출석 횟수</p>
            <p class="text-2xl font-black text-slate-800">{{ records.length }}회</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-[2.5rem] shadow-xl shadow-slate-200/40 overflow-hidden border border-slate-100">
        <div class="p-8 flex justify-between items-center border-b border-slate-50">
          <div class="flex flex-col">
            <h2 class="text-3xl font-black text-slate-800 tracking-tighter">{{ currentYear }}년 {{ currentMonth }}월</h2>
            <p class="text-sm text-slate-400 font-medium">수영 달력</p>
          </div>
          <div class="flex gap-3">
            <button @click="prevMonth" class="w-10 h-10 flex items-center justify-center bg-slate-50 hover:bg-slate-100 rounded-xl transition-all">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M15 19l-7-7 7-7" stroke-width="3" /></svg>
            </button>
            <button @click="nextMonth" class="w-10 h-10 flex items-center justify-center bg-slate-50 hover:bg-slate-100 rounded-xl transition-all">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" stroke-width="3" /></svg>
            </button>
          </div>
        </div>

        <!-- Calendar Grid -->
        <div class="grid grid-cols-7 border-b border-slate-50 bg-slate-50/30">
          <div v-for="day in ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT']" :key="day" 
               class="py-3 text-[10px] font-black tracking-widest text-slate-400 text-center">
            {{ day }}
          </div>
        </div>
        <div class="grid grid-cols-7">
          <div 
            v-for="(date, idx) in calendarDays" :key="idx"
            :class="['group relative h-32 p-3 border-r border-b border-slate-50 transition-all cursor-pointer', 
                     date.isCurrentMonth ? 'bg-white hover:bg-slate-50/50' : 'bg-slate-50/20 opacity-40']"
            @click="openRecordModal(date)"
          >
            <span :class="['text-sm font-bold', date.isCurrentMonth ? 'text-slate-700' : 'text-slate-300']">
              {{ date.day }}
            </span>
            
            <div v-if="getRecordForDate(date.fullDate)" class="mt-2">
              <div class="bg-primary-500 h-1 w-6 rounded-full mb-1"></div>
              <p class="text-[11px] font-black text-slate-800 tracking-tighter">{{ getRecordForDate(date.fullDate).distance }}m</p>
              <p class="text-[9px] text-slate-400 font-bold">{{ getRecordForDate(date.fullDate).duration }}분</p>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Modal -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
      <div class="bg-white w-full max-w-sm rounded-3xl shadow-2xl overflow-hidden p-8 space-y-6">
        <div class="flex justify-between items-center">
          <h3 class="text-xl font-black text-slate-800 tracking-tighter">{{ formattedSelectedDate }}</h3>
          <button @click="showModal = false" class="text-slate-400 hover:text-slate-600">✕</button>
        </div>

        <div v-if="!isEditing && hasRecord" class="space-y-6">
          <div class="grid grid-cols-2 gap-4">
            <div class="bg-slate-50 p-4 rounded-2xl border border-slate-100">
              <p class="text-[9px] font-bold text-slate-400 uppercase tracking-widest">DISTANCE</p>
              <p class="text-2xl font-black text-primary-600">{{ recordForm.distance }}m</p>
            </div>
            <div class="bg-slate-50 p-4 rounded-2xl border border-slate-100">
              <p class="text-[9px] font-bold text-slate-400 uppercase tracking-widest">DURATION</p>
              <p class="text-2xl font-black text-orange-500">{{ recordForm.duration }}분</p>
            </div>
          </div>
          <div v-if="recordForm.memo" class="bg-slate-50 p-4 rounded-2xl border border-slate-100 italic text-sm text-slate-600">
            "{{ recordForm.memo }}"
          </div>
          <button @click="isEditing = true" class="w-full py-4 bg-slate-800 text-white rounded-2xl font-bold text-sm">수정하기</button>
        </div>

        <form v-else @submit.prevent="saveRecord" class="space-y-4">
          <div>
            <label class="block text-xs font-bold text-slate-400 mb-1">DISTANCE (m)</label>
            <input v-model.number="recordForm.distance" type="number" required class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 font-bold" />
          </div>
          <div>
            <label class="block text-xs font-bold text-slate-400 mb-1">DURATION (min)</label>
            <input v-model.number="recordForm.duration" type="number" required class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 font-bold" />
          </div>
          <div>
            <label class="block text-xs font-bold text-slate-400 mb-1">MEMO</label>
            <textarea v-model="recordForm.memo" class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 h-24 resize-none"></textarea>
          </div>
          <button type="submit" class="w-full py-4 bg-primary-600 text-white rounded-2xl font-bold transition-all active:scale-95 shadow-lg shadow-primary-100">저장하기</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = ref({ nickname: '' })
const records = ref([])
const showModal = ref(false)
const isEditing = ref(false)
const hasRecord = ref(false)
const selectedDate = ref('')
const recordForm = ref({ distance: 0, duration: 0, memo: '' })

const now = new Date()
const currentYear = ref(now.getFullYear())
const currentMonth = ref(now.getMonth() + 1)

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
    console.log('Records loaded:', records.value)
  } catch (err) {
    console.error('Failed to fetch records', err)
  }
}

const monthlyTotalDistance = computed(() => {
  return records.value.reduce((acc, r) => acc + (Number(r.distance) || 0), 0)
})

const monthlyTotalDuration = computed(() => {
  return records.value.reduce((acc, r) => acc + (Number(r.duration) || 0), 0)
})

const formattedSelectedDate = computed(() => {
  if (!selectedDate.value) return ''
  const [y, m, d] = selectedDate.value.split('-')
  return `${y}년 ${parseInt(m)}월 ${parseInt(d)}일`
})

const getRecordForDate = (dateStr) => records.value.find(r => r.date === dateStr)

const calendarDays = computed(() => {
  const year = currentYear.value
  const month = currentMonth.value
  const days = []
  
  const firstDay = new Date(year, month - 1, 1).getDay()
  const lastDate = new Date(year, month, 0).getDate()
  const prevLastDate = new Date(year, month - 1, 0).getDate()
  
  // Previous month padding
  for (let i = firstDay - 1; i >= 0; i--) {
    const d = prevLastDate - i
    const m = month === 1 ? 12 : month - 1
    const y = month === 1 ? year - 1 : year
    days.push({ day: d, isCurrentMonth: false, fullDate: `${y}-${String(m).padStart(2,'0')}-${String(d).padStart(2,'0')}` })
  }
  
  // Current month
  for (let i = 1; i <= lastDate; i++) {
    days.push({ day: i, isCurrentMonth: true, fullDate: `${year}-${String(month).padStart(2,'0')}-${String(i).padStart(2,'0')}` })
  }
  
  // Next month padding
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
  const existing = getRecordForDate(date.fullDate)
  if (existing) {
    recordForm.value = { ...existing }
    hasRecord.value = true
    isEditing.value = false
  } else {
    recordForm.value = { distance: 0, duration: 0, memo: '' }
    hasRecord.value = false
    isEditing.value = true
  }
  showModal.value = true
}

const saveRecord = async () => {
  try {
    const payload = {
      date: selectedDate.value,
      distance: Number(recordForm.value.distance) || 0,
      duration: Number(recordForm.value.duration) || 0,
      memo: recordForm.value.memo
    }
    const res = await axios.post('/api/swimming/record', payload)
    console.log('Record saved:', res.data)
    showModal.value = false
    await fetchRecords() // DB에서 최신 데이터 다시 가져오기
  } catch (err) {
    console.error('Save failed', err)
    alert('기록 저장 중 오류가 발생했습니다.')
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

onMounted(() => {
  fetchUser()
  fetchRecords()
})
</script>
