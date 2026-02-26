<template>
  <div class="min-h-screen bg-slate-50 flex flex-col">
    <!-- Navbar -->
    <nav class="bg-white border-b border-slate-200 px-6 py-4 flex justify-between items-center shadow-sm sticky top-0 z-20">
      <div class="flex items-center gap-8">
        <div class="flex items-center gap-2">
          <div class="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center text-white shadow-md">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9" stroke-width="2" /></svg>
          </div>
          <h1 class="text-xl font-black text-slate-800 tracking-tighter italic uppercase">Swim<span class="text-primary-600">Record</span></h1>
        </div>
        <div class="flex gap-1 bg-slate-100 p-1 rounded-xl">
          <router-link to="/" class="px-4 py-1.5 text-xs font-bold rounded-lg text-slate-500 hover:text-slate-700">대시보드</router-link>
          <router-link to="/stats" class="px-4 py-1.5 text-xs font-bold rounded-lg bg-white text-primary-600 shadow-sm">통계</router-link>
          <router-link to="/chat" class="px-4 py-1.5 text-xs font-bold rounded-lg text-slate-500 hover:text-slate-700">채팅</router-link>
        </div>
      </div>
      <div class="flex items-center gap-4">
        <span class="text-xs font-black text-slate-700">{{ user.nickname }}님</span>
        <button @click="logout" class="p-2 hover:bg-red-50 text-slate-300 hover:text-red-500 rounded-lg transition-colors">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" stroke-width="2" /></svg>
        </button>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="flex-1 p-6 max-w-5xl mx-auto w-full space-y-8">
      <div class="flex flex-col">
        <h2 class="text-3xl font-black text-slate-800 tracking-tighter">데이터 시각화</h2>
        <p class="text-sm text-slate-400 font-bold">나의 수영 성장 그래프</p>
      </div>

      <!-- Charts Grid -->
      <div class="grid grid-cols-1 gap-8">
        <!-- Weekly Chart -->
        <div class="bg-white p-8 rounded-[2.5rem] shadow-xl shadow-slate-200/40 border border-slate-100">
          <div class="flex justify-between items-center mb-8">
            <div>
              <h3 class="text-lg font-black text-slate-800 tracking-tight">주간 수영 거리</h3>
              <p class="text-xs text-slate-400 font-bold">최근 7일간의 기록 (m)</p>
            </div>
            <div class="text-primary-600 font-black text-sm">WEEKLY</div>
          </div>
          <div class="h-64 relative">
            <canvas ref="weeklyChartCanvas"></canvas>
          </div>
        </div>

        <!-- Monthly Chart -->
        <div class="bg-white p-8 rounded-[2.5rem] shadow-xl shadow-slate-200/40 border border-slate-100">
          <div class="flex justify-between items-center mb-8">
            <div>
              <h3 class="text-lg font-black text-slate-800 tracking-tight">월간 성장 추이</h3>
              <p class="text-xs text-slate-400 font-bold">최근 6개월간의 합계 (m)</p>
            </div>
            <div class="text-orange-500 font-black text-sm">MONTHLY</div>
          </div>
          <div class="h-64 relative">
            <canvas ref="monthlyChartCanvas"></canvas>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import Chart from 'chart.js/auto'

const router = useRouter()
const user = ref({ nickname: '' })
const weeklyChartCanvas = ref(null)
const monthlyChartCanvas = ref(null)

const fetchUser = async () => {
  try {
    const res = await axios.get('/api/user/me')
    user.value = res.data
  } catch (err) {
    router.push('/login')
  }
}

const renderCharts = async () => {
  try {
    const res = await axios.get('/api/swimming/stats/summary')
    const { weekly, monthly } = res.data

    // Weekly Chart
    new Chart(weeklyChartCanvas.value, {
      type: 'bar',
      data: {
        labels: weekly.map(d => d.date.split('-').slice(1).join('/')),
        datasets: [{
          label: '거리 (m)',
          data: weekly.map(d => d.distance),
          backgroundColor: '#0ea5e9',
          borderRadius: 12,
          borderSkipped: false,
          barPercentage: 0.6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, grid: { display: false }, border: { display: false } },
          x: { grid: { display: false }, border: { display: false } }
        }
      }
    })

    // Monthly Chart
    new Chart(monthlyChartCanvas.value, {
      type: 'line',
      data: {
        labels: monthly.map(d => d.month),
        datasets: [{
          label: '총 거리 (m)',
          data: monthly.map(d => d.distance),
          borderColor: '#f97316',
          backgroundColor: 'rgba(249, 115, 22, 0.1)',
          fill: true,
          tension: 0.4,
          borderWidth: 4,
          pointRadius: 6,
          pointBackgroundColor: '#fff',
          pointBorderWidth: 3
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, grid: { color: '#f8fafc' }, border: { display: false } },
          x: { grid: { display: false }, border: { display: false } }
        }
      }
    })
  } catch (err) {
    console.error('Failed to load stats', err)
  }
}

const logout = async () => {
  await axios.post('/api/member/logout')
  router.push('/login')
}

onMounted(async () => {
  await fetchUser()
  await renderCharts()
})
</script>
