<template>
  <div class="min-h-screen bg-white flex flex-col font-sans antialiased text-slate-900">
    <AppHeader />

    <!-- Main Content -->
    <main class="flex-1 p-4 md:p-6 max-w-5xl mx-auto w-full space-y-8">
      <!-- Header Section -->
      <div class="px-1">
        <h2 class="text-xl md:text-2xl font-bold tracking-tight">성장 리포트</h2>
        <p class="text-xs md:text-sm text-slate-400 font-medium">나의 수영 기록을 데이터로 확인하세요.</p>
      </div>

      <!-- Compact Monthly Summary -->
      <div class="grid grid-cols-3 gap-3 md:gap-4">
        <div class="bg-slate-50 p-4 rounded-2xl border border-slate-100 flex flex-col items-center text-center">
          <span class="text-[9px] md:text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1">총 거리</span>
          <p class="text-lg md:text-2xl font-black text-blue-600 leading-none">
            {{ displayTotalDistance.toLocaleString() }}<span class="text-[10px] md:text-xs ml-0.5 text-slate-400">{{ unitLabel }}</span>
          </p>
        </div>
        <div class="bg-slate-50 p-4 rounded-2xl border border-slate-100 flex flex-col items-center text-center">
          <span class="text-[9px] md:text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1">총 시간</span>
          <p class="text-lg md:text-2xl font-black text-indigo-600 leading-none">
            {{ monthlyTotalDuration }}<span class="text-[10px] md:text-xs ml-0.5 text-slate-400">min</span>
          </p>
        </div>
        <div class="bg-slate-50 p-4 rounded-2xl border border-slate-100 flex flex-col items-center text-center">
          <span class="text-[9px] md:text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1">수영일</span>
          <p class="text-lg md:text-2xl font-black text-emerald-600 leading-none">
            {{ attendanceDaysCount }}<span class="text-[10px] md:text-xs ml-0.5 text-slate-400">day</span>
          </p>
        </div>
      </div>

      <!-- Charts Grid -->
      <div class="space-y-6">
        <!-- Weekly Chart -->
        <div class="bg-white p-5 md:p-6 rounded-2xl border border-slate-100 shadow-sm transition-colors duration-300">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-sm md:text-base font-bold text-slate-800">주간 거리 패턴</h3>
            <span class="text-[10px] font-black text-blue-500 bg-blue-50 px-2 py-1 rounded-md">WEEKLY</span>
          </div>
          <div class="h-56 md:h-64 relative">
            <canvas ref="weeklyChartCanvas"></canvas>
          </div>
        </div>

        <!-- Monthly Chart -->
        <div class="bg-white p-5 md:p-6 rounded-2xl border border-slate-100 shadow-sm transition-colors duration-300">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-sm md:text-base font-bold text-slate-800">월간 성장 추이</h3>
            <span class="text-[10px] font-black text-orange-500 bg-orange-50 px-2 py-1 rounded-md">MONTHLY</span>
          </div>
          <div class="h-56 md:h-64 relative">
            <canvas ref="monthlyChartCanvas"></canvas>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import Chart from 'chart.js/auto'
import AppHeader from '../components/AppHeader.vue'

const router = useRouter()
const user = ref({ nickname: '', distanceUnit: 'METER' })
const records = ref([])
const weeklyChartCanvas = ref(null)
const monthlyChartCanvas = ref(null)
let weeklyChart = null
let monthlyChart = null

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

const fetchCurrentMonthRecords = async () => {
  try {
    const now = new Date()
    const year = now.getFullYear()
    const month = now.getMonth() + 1
    const res = await axios.get(`/api/swimming/records?year=${year}&month=${month}`)
    records.value = res.data || []
  } catch (err) {
    console.error('Failed to load records', err)
  }
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

const renderCharts = async () => {
  try {
    const res = await axios.get('/api/swimming/stats/summary')
    const { weekly, monthly } = res.data

    const gridColor = '#f8fafc'
    const textColor = '#cbd5e1'
    const chartFont = { family: 'Pretendard', weight: '600', size: 11 }

    if (weeklyChart) weeklyChart.destroy()
    if (monthlyChart) monthlyChart.destroy()

    // Weekly Chart
    weeklyChart = new Chart(weeklyChartCanvas.value, {
      type: 'bar',
      data: {
        labels: weekly.map(d => d.date.split('-').slice(1).join('/')),
        datasets: [{
          label: `거리 (${unitLabel.value})`,
          data: weekly.map(d => toDisplayDistance(d.distance)),
          backgroundColor: '#3b82f6',
          borderRadius: 8,
          barPercentage: 0.4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, grid: { color: gridColor }, border: { display: false }, ticks: { font: chartFont, color: textColor } },
          x: { grid: { display: false }, border: { display: false }, ticks: { font: chartFont, color: textColor } }
        }
      }
    })

    // Monthly Chart
    monthlyChart = new Chart(monthlyChartCanvas.value, {
      type: 'line',
      data: {
        labels: monthly.map(d => d.month),
        datasets: [{
          label: `거리 (${unitLabel.value})`,
          data: monthly.map(d => toDisplayDistance(d.distance)),
          borderColor: '#f97316',
          borderWidth: 3,
          pointRadius: 4,
          pointBackgroundColor: '#fff',
          pointBorderWidth: 2,
          tension: 0.4,
          fill: true,
          backgroundColor: 'rgba(249, 115, 22, 0.05)'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, grid: { color: gridColor }, border: { display: false }, ticks: { font: chartFont, color: textColor } },
          x: { grid: { display: false }, border: { display: false }, ticks: { font: chartFont, color: textColor } }
        }
      }
    })
  } catch (err) {}
}

onMounted(async () => {
  await fetchUser()
  await fetchCurrentMonthRecords()
  await renderCharts()
})
</script>
