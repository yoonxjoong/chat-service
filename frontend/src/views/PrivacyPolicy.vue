<template>
  <div class="min-h-screen bg-slate-50 flex flex-col font-sans antialiased text-slate-900">
    <AppHeader />

    <main class="flex-1 p-6 md:p-12 max-w-3xl mx-auto w-full space-y-10">
      <div class="space-y-2">
        <h2 class="text-3xl font-black tracking-tight">개인정보처리방침</h2>
        <p class="text-slate-400 font-bold uppercase tracking-widest text-xs">Privacy Policy</p>
      </div>

      <div class="bg-white rounded-2xl p-8 md:p-10 shadow-sm border border-slate-100 space-y-8 text-sm md:text-base leading-relaxed text-slate-600">
        <section class="space-y-4">
          <h3 class="text-lg font-bold text-slate-900">1. 수집하는 개인정보 항목</h3>
          <p>회사는 회원가입, 원활한 고객상담, 각종 서비스의 제공을 위해 아래와 같은 개인정보를 수집하고 있습니다.</p>
          <ul class="list-disc pl-5 space-y-2">
            <li><strong>필수항목:</strong> 휴대전화 번호(아이디), 닉네임, 비밀번호</li>
            <li><strong>서비스 이용 과정에서 생성되는 정보:</strong> 수영 기록(영법, 거리, 시간, 메모), 프로필 사진, 서비스 이용 기록, 접속 로그, 쿠키, 접속 IP 정보</li>
          </ul>
        </section>

        <section class="space-y-4">
          <h3 class="text-lg font-bold text-slate-900">2. 개인정보의 수집 및 이용 목적</h3>
          <p>수집한 개인정보를 다음의 목적을 위해 활용합니다.</p>
          <ul class="list-disc pl-5 space-y-2">
            <li><strong>서비스 제공에 관한 계약 이행 및 서비스 제공에 따른 요금정산:</strong> 콘텐츠 제공, 특정 맞춤 서비스 제공</li>
            <li><strong>회원 관리:</strong> 회원제 서비스 이용에 따른 본인확인, 개인 식별, 불량회원의 부정 이용 방지와 비인가 사용 방지, 가입 의사 확인, 분쟁 조정을 위한 기록보존, 불만처리 등 민원처리, 고지사항 전달</li>
          </ul>
        </section>

        <section class="space-y-4">
          <h3 class="text-lg font-bold text-slate-900">3. 개인정보의 보유 및 이용기간</h3>
          <p>이용자의 개인정보는 원칙적으로 개인정보의 수집 및 이용목적이 달성되면 지체 없이 파기합니다. 단, 다음의 정보에 대해서는 아래의 이유로 명시한 기간 동안 보존합니다.</p>
          <ul class="list-disc pl-5 space-y-2">
            <li>보존 항목: 휴대전화 번호, 서비스 이용 기록</li>
            <li>보존 근거: 이용자의 탈퇴 의사 철회 시 서비스 재개 대응 및 부정 이용 방지</li>
            <li>보존 기간: <strong>회원 탈퇴 후 30일</strong> (이후 즉시 파기)</li>
          </ul>
        </section>

        <section class="space-y-4">
          <h3 class="text-lg font-bold text-slate-900">4. 개인정보 파기절차 및 방법</h3>
          <p>회사는 원칙적으로 개인정보 수집 및 이용목적이 달성된 후에는 해당 정보를 지체 없이 파기합니다. 전자적 파일형태로 저장된 개인정보는 기록을 재생할 수 없는 기술적 방법을 사용하여 삭제합니다.</p>
        </section>

        <section class="space-y-4 pt-6 border-t border-slate-50">
          <p class="text-xs text-slate-400 italic">본 방침은 2026년 2월 27일부터 시행됩니다.</p>
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
