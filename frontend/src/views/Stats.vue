<template>
  <div class="min-h-screen bg-slate-50 flex flex-col">
    <AppHeader />

    <!-- Main Content -->
    <main class="flex-1 p-6 max-w-5xl mx-auto w-full space-y-8">
      <div class="flex flex-col">
        <h2 class="text-3xl font-black text-slate-800 tracking-tighter">데이터 시각화</h2>
        <p class="text-sm text-slate-400 font-bold">나의 수영 성장 그래프</p>
      </div>

      <!-- Charts Grid -->
      <div class="grid grid-cols-1 gap-8">
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
import AppHeader from '../components/AppHeader.vue'

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

onMounted(async () => {
  await fetchUser()
  await renderCharts()
})
</script>
