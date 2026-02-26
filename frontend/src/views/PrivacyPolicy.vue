<template>
  <div class="min-h-screen bg-slate-50 flex flex-col pb-12">
    <AppHeader />
    <main class="flex-1 p-4 md:p-8 max-w-3xl mx-auto w-full space-y-6">
      <button @click="$router.back()" class="flex items-center gap-2 text-slate-400 hover:text-primary-600 transition-colors font-bold text-sm mb-4">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M15 19l-7-7 7-7" stroke-width="3" /></svg>
        뒤로가기
      </button>

      <section class="bg-white rounded-[2.5rem] shadow-sm border border-slate-100 p-8 md:p-12 space-y-8">
        <div class="border-b border-slate-50 pb-6">
          <h2 class="text-3xl font-black text-slate-800 tracking-tighter">개인정보 처리방침</h2>
          <p class="text-sm text-slate-400 font-bold mt-2">시행일: 2026년 2월 26일</p>
        </div>

        <div class="prose prose-slate max-w-none space-y-6 text-slate-600 leading-relaxed text-sm">
          <div>
            <h3 class="text-lg font-black text-slate-800 mb-3">1. 수집하는 개인정보 항목</h3>
            <p>회사는 서비스 제공을 위해 아래와 같은 개인정보를 수집하고 있습니다.</p>
            <ul class="list-disc ml-5 mt-2 space-y-1 font-medium">
              <li>필수항목: 아이디, 비밀번호, 닉네임</li>
              <li>선택항목: 프로필 이미지 URL, 수영 기록(거리, 시간, 메모)</li>
            </ul>
          </div>

          <div>
            <h3 class="text-lg font-black text-slate-800 mb-3">2. 개인정보의 수집 및 이용 목적</h3>
            <p>수집한 개인정보를 다음의 목적을 위해 활용합니다.</p>
            <ul class="list-disc ml-5 mt-2 space-y-1 font-medium">
              <li>서비스 제공에 관한 계약 이행 및 서비스 제공에 따른 요금정산</li>
              <li>회원 관리: 회원제 서비스 이용에 따른 본인확인, 개인 식별, 불량회원의 부정 이용 방지와 비인가 사용 방지</li>
            </ul>
          </div>

          <div>
            <h3 class="text-lg font-black text-slate-800 mb-3">3. 개인정보의 보유 및 이용기간</h3>
            <p>원칙적으로, 개인정보 수집 및 이용목적이 달성된 후에는 해당 정보를 지체 없이 파기합니다. 단, 관계법령의 규정에 의하여 보존할 필요가 있는 경우 회사는 관계법령에서 정한 일정한 기간 동안 회원정보를 보관합니다.</p>
          </div>

          <div>
            <h3 class="text-lg font-black text-slate-800 mb-3">4. 개인정보의 파기절차 및 방법</h3>
            <p>회원탈퇴 시 수집된 개인정보와 수영 기록 데이터는 즉시 영구 파기됩니다. 종이에 출력된 개인정보는 분쇄기로 분쇄하거나 소각을 통하여 파기하고, 전자적 파일형태로 저장된 개인정보는 기록을 재생할 수 없는 기술적 방법을 사용하여 삭제합니다.</p>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import AppHeader from '../components/AppHeader.vue'
</script>
