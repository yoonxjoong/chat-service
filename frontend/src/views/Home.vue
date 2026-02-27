<template>
  <div class="min-h-screen bg-slate-50 flex flex-col">
    <AppHeader />

    <!-- Main Content -->
    <main class="flex-1 p-6 max-w-6xl mx-auto w-full space-y-6">
      <!-- Monthly Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 flex items-center gap-4">
          <div class="w-12 h-12 bg-blue-50 rounded-2xl flex items-center justify-center text-blue-600 font-bold text-xl">{{ unitLabel }}</div>
          <div>
            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">이번 달 총 거리</p>
            <p class="text-2xl font-black text-slate-800">{{ displayTotalDistance.toLocaleString() }}{{ unitLabel }}</p>
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
          <div v-for="day in ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT']" :key="day" class="py-3 text-[10px] font-black tracking-widest text-slate-400 text-center uppercase">{{ day }}</div>
        </div>
        <div class="grid grid-cols-7">
          <div 
            v-for="(date, idx) in calendarDays" :key="idx"
            :class="['group relative h-32 p-3 border-r border-b border-slate-50 transition-all cursor-pointer overflow-hidden', 
                     date.isCurrentMonth ? 'bg-white hover:bg-slate-50/50' : 'bg-slate-50/20 opacity-40']"
            @click="openRecordModal(date)"
            :style="getCalendarCellStyle(date.fullDate)"
          >
            <!-- 배경 이미지가 있을 때 정보 가독성을 위한 어두운 오버레이 -->
            <div v-if="getRecordForDate(date.fullDate)?.imageUrl" class="absolute inset-0 bg-black/30 group-hover:bg-black/20 transition-colors"></div>

            <span :class="['relative z-10 text-sm font-bold', 
                          getRecordForDate(date.fullDate)?.imageUrl ? 'text-white drop-shadow-md' : (date.isCurrentMonth ? 'text-slate-700' : 'text-slate-300')]">
              {{ date.day }}
            </span>
            
            <div v-if="getRecordForDate(date.fullDate)" class="relative z-10 mt-2 space-y-0.5">
              <div :class="['h-1 w-6 rounded-full mb-1', getRecordForDate(date.fullDate)?.imageUrl ? 'bg-white shadow-sm' : 'bg-primary-500']"></div>
              <p :class="['text-[11px] font-black tracking-tighter leading-none', getRecordForDate(date.fullDate)?.imageUrl ? 'text-white drop-shadow-md' : 'text-slate-800']">
                {{ toDisplayDistance(getRecordForDate(date.fullDate).distance) }}{{ unitLabel }}
              </p>
              <p :class="['text-[9px] font-bold leading-none', getRecordForDate(date.fullDate)?.imageUrl ? 'text-white/90 drop-shadow-md' : 'text-slate-400']">
                {{ getRecordForDate(date.fullDate).duration }}분
              </p>
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
          <div v-if="recordForm.imageUrl" class="w-full h-40 rounded-2xl overflow-hidden border border-slate-100">
            <img :src="recordForm.imageUrl" class="w-full h-full object-cover" />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div class="bg-slate-50 p-4 rounded-2xl border border-slate-100">
              <p class="text-[9px] font-bold text-slate-400 uppercase tracking-widest">DISTANCE</p>
              <p class="text-2xl font-black text-primary-600">{{ toDisplayDistance(recordForm.distance) }}{{ unitLabel }}</p>
            </div>
            <div class="bg-slate-50 p-4 rounded-2xl border border-slate-100">
              <p class="text-[9px] font-bold text-slate-400 uppercase tracking-widest">DURATION</p>
              <p class="text-2xl font-black text-orange-500">{{ recordForm.duration }}분</p>
            </div>
          </div>
          <div v-if="recordForm.memo" class="bg-slate-50 p-4 rounded-2xl border border-slate-100 italic text-sm text-slate-600">"{{ recordForm.memo }}"</div>
          <button @click="isEditing = true" class="w-full py-4 bg-slate-800 text-white rounded-2xl font-bold text-sm">수정하기</button>
        </div>

        <form v-else @submit.prevent="saveRecord" class="space-y-4">
          <div class="space-y-2">
            <label class="block text-xs font-bold text-slate-400 mb-1 uppercase tracking-widest">인증 사진</label>
            <div 
              @click="triggerFile" 
              class="w-full h-32 bg-slate-50 border-2 border-dashed border-slate-200 rounded-2xl flex flex-col items-center justify-center cursor-pointer hover:bg-slate-100 transition-all overflow-hidden relative"
            >
              <img v-if="previewUrl" :src="previewUrl" class="w-full h-full object-cover" />
              <div v-else class="text-center">
                <svg class="w-8 h-8 text-slate-300 mx-auto mb-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" stroke-width="2" /></svg>
                <p class="text-[10px] font-bold text-slate-400">사진 업로드</p>
              </div>
              <input type="file" ref="fileInput" class="hidden" accept="image/*" @change="onFileChange" />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-xs font-bold text-slate-400 mb-1 uppercase tracking-widest">DISTANCE ({{ unitLabel }})</label>
              <input v-model.number="displayInputDistance" type="number" required class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 font-bold" />
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-400 mb-1 uppercase tracking-widest">DURATION (min)</label>
              <input v-model.number="recordForm.duration" type="number" required class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 font-bold" />
            </div>
          </div>
          <div>
            <label class="block text-xs font-bold text-slate-400 mb-1 uppercase tracking-widest">MEMO</label>
            <textarea v-model="recordForm.memo" class="w-full px-4 py-3 rounded-xl border border-slate-200 outline-none focus:border-primary-500 h-20 resize-none"></textarea>
          </div>
          <button type="submit" :disabled="isUploading" class="w-full py-4 bg-primary-600 text-white rounded-2xl font-bold transition-all active:scale-95 shadow-lg disabled:opacity-50">
            {{ isUploading ? '저장 중...' : '저장하기' }}
          </button>
        </form>
      </div>
    </div>
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
const showModal = ref(false)
const isEditing = ref(false)
const hasRecord = ref(false)
const selectedDate = ref('')
const recordForm = ref({ distance: 0, duration: 0, memo: '', imageUrl: '' })
const displayInputDistance = ref(0)

const fileInput = ref(null)
const previewUrl = ref('')
const selectedFile = ref(null)
const isUploading = ref(false)

const M_TO_YD = 1.09361

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

const monthlyTotalDuration = computed(() => records.value.reduce((acc, r) => acc + (Number(r.duration) || 0), 0))
const displayTotalDistance = computed(() => {
  const totalMeters = records.value.reduce((acc, r) => acc + (Number(r.distance) || 0), 0)
  return toDisplayDistance(totalMeters)
})

const hasTodayRecord = computed(() => {
  const todayStr = new Date().toISOString().split('T')[0]
  return records.value.some(r => r.date === todayStr)
})

const goToTodayChat = () => {
  if (hasTodayRecord.value) {
    router.push('/chat?roomId=today-swim-room')
  }
}

const formattedSelectedDate = computed(() => {
  if (!selectedDate.value) return ''
  const [y, m, d] = selectedDate.value.split('-')
  return `${y}년 ${parseInt(m)}월 ${parseInt(d)}일`
})

const getRecordForDate = (dateStr) => records.value.find(r => r.date === dateStr)

const getCalendarCellStyle = (dateStr) => {
  const record = getRecordForDate(dateStr)
  if (record?.imageUrl) {
    return {
      backgroundImage: `url(${record.imageUrl})`,
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
  const existing = getRecordForDate(date.fullDate)
  selectedFile.value = null
  if (existing) {
    recordForm.value = { ...existing }
    displayInputDistance.value = toDisplayDistance(existing.distance)
    previewUrl.value = existing.imageUrl || ''
    hasRecord.value = true
    isEditing.value = false
  } else {
    recordForm.value = { distance: 0, duration: 0, memo: '', imageUrl: '' }
    displayInputDistance.value = 0
    previewUrl.value = ''
    hasRecord.value = false
    isEditing.value = true
  }
  showModal.value = true
}

const triggerFile = () => fileInput.value.click()

const onFileChange = (e) => {
  const file = e.target.files[0]
  if (!file) return
  selectedFile.value = file
  previewUrl.value = URL.createObjectURL(file)
}

const saveRecord = async () => {
  isUploading.value = true
  try {
    let finalImageUrl = recordForm.value.imageUrl

    if (selectedFile.value) {
      const formData = new FormData()
      formData.append('file', selectedFile.value)
      const uploadRes = await axios.post('/api/upload/image', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      finalImageUrl = uploadRes.data.url
    }

    const payload = {
      date: selectedDate.value,
      distance: Number(toDbDistance(displayInputDistance.value)) || 0,
      duration: Number(recordForm.value.duration) || 0,
      memo: recordForm.value.memo || '',
      imageUrl: finalImageUrl || null
    }
    await axios.post('/api/swimming/record', payload)
    showModal.value = false
    await fetchRecords()
  } catch (err) {
    console.error('Save error:', err.response?.data)
    alert('저장 중 오류가 발생했습니다.')
  } finally {
    isUploading.value = false
  }
}

onMounted(async () => {
  await fetchUser()
  await fetchRecords()
})
</script>
