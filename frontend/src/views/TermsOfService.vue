<template>
  <div class="min-h-screen bg-slate-50 flex flex-col font-sans antialiased text-slate-900">
    <AppHeader />

    <main class="flex-1 p-6 md:p-12 max-w-3xl mx-auto w-full space-y-10">
      <div class="space-y-2">
        <h2 class="text-3xl font-black tracking-tight">서비스 이용약관</h2>
        <p class="text-slate-400 font-bold uppercase tracking-widest text-xs">Terms of Service</p>
      </div>

      <div class="bg-white rounded-2xl p-8 md:p-10 shadow-sm border border-slate-100 space-y-8 text-sm md:text-base leading-relaxed text-slate-600">
        <section class="space-y-4">
          <h3 class="text-lg font-bold text-slate-900">제 1 조 (목적)</h3>
          <p>본 약관은 매일수영(이하 "서비스")이 제공하는 수영 기록 관리 및 커뮤니티 서비스의 이용조건 및 절차, 이용자와 서비스 간의 권리, 의무 및 책임사항 등을 규정함을 목적으로 합니다.</p>
        </section>

        <section class="space-y-4">
          <h3 class="text-lg font-bold text-slate-900">제 2 조 (용어의 정의)</h3>
          <p>1. "서비스"란 매일수영이 제공하는 수영 관련 기록 및 소셜 기능을 의미합니다.<br>
             2. "이용자"란 서비스에 접속하여 본 약관에 따라 서비스를 이용하는 회원 및 비회원을 말합니다.</p>
        </section>

        <section class="space-y-4">
          <h3 class="text-lg font-bold text-slate-900">제 3 조 (이용계약의 체결)</h3>
          <p>이용계약은 이용자가 본 약관의 내용에 동의하고 회원가입을 신청한 후, 서비스가 이를 승낙함으로써 체결됩니다. 휴대전화 번호를 통한 본인 식별을 기본으로 합니다.</p>
        </section>

        <section class="space-y-4">
          <h3 class="text-lg font-bold text-slate-900">제 4 조 (서비스의 제공 및 변경)</h3>
          <p>서비스는 다음과 같은 업무를 수행합니다:<br>
             - 수영 기록(거리, 시간, 영법) 저장 및 통계 시각화<br>
             - 수영 관련 사진 및 정보 공유(물옷 갤러리)<br>
             - 기타 서비스가 정하는 업무</p>
        </section>

        <section class="space-y-4 pt-6 border-t border-slate-50">
          <p class="text-xs text-slate-400 italic">최종 수정일: 2026년 2월 27일</p>
        </section>
      </div>

      <div class="flex justify-center pb-10">
        <button @click="$router.back()" class="text-xs font-bold text-slate-400 hover:text-slate-900 transition-colors uppercase tracking-widest">Go Back</button>
      </div>
    </main>
  </div>
</template>

<script setup>
import AppHeader from '../components/AppHeader.vue'
</script>
