<template>
  <div class="min-h-screen bg-slate-50 flex flex-col font-sans antialiased text-slate-900 pb-12">
    <div class="p-6 max-w-2xl mx-auto w-full space-y-10">
      <!-- Header -->
      <div class="flex items-center justify-between pt-4">
        <button @click="$router.back()" class="p-2 -ml-2 text-slate-400 hover:text-slate-900 transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" />
          </svg>
        </button>
        <h2 class="text-[10px] font-black uppercase tracking-[0.3em] text-slate-400">Privacy Policy</h2>
        <div class="w-9"></div>
      </div>

      <div class="space-y-4 text-center">
        <h1 class="text-3xl font-black tracking-tighter text-slate-900">개인정보처리방침</h1>
        <p class="text-xs font-bold text-slate-400 leading-relaxed uppercase tracking-widest">Swim Too Data Protection</p>
      </div>

      <div class="bg-white rounded-[2.5rem] p-8 md:p-12 shadow-sm border border-slate-100 space-y-12">
        <section class="space-y-6">
          <div class="flex items-center gap-3">
            <div class="w-1.5 h-6 bg-blue-600 rounded-full"></div>
            <h3 class="text-lg font-black tracking-tight">1. 수집하는 개인정보 항목</h3>
          </div>
          <div class="space-y-4">
            <p class="text-sm leading-7 text-slate-600 font-medium">서비스는 원활한 서비스 제공을 위해 아래와 같은 개인정보를 수집하고 있습니다.</p>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="p-5 bg-slate-50 rounded-2xl">
                <p class="text-[10px] font-black text-blue-600 uppercase tracking-widest mb-2">필수 정보</p>
                <p class="text-sm text-slate-700 font-bold leading-6">휴대전화 번호, 닉네임, 프로필 사진(선택 시)</p>
              </div>
              <div class="p-5 bg-slate-50 rounded-2xl">
                <p class="text-[10px] font-black text-blue-600 uppercase tracking-widest mb-2">자동 생성 정보</p>
                <p class="text-sm text-slate-700 font-bold leading-6">서비스 이용 기록, 접속 로그, IP 정보, 쿠키</p>
              </div>
            </div>
          </div>
        </section>

        <section class="space-y-6">
          <div class="flex items-center gap-3">
            <div class="w-1.5 h-6 bg-blue-600 rounded-full"></div>
            <h3 class="text-lg font-black tracking-tight">2. 개인정보의 이용 목적</h3>
          </div>
          <ul class="space-y-4">
            <li class="p-5 border border-slate-50 rounded-2xl space-y-2">
              <p class="text-sm font-bold text-slate-900 italic">서비스 제공 및 관리</p>
              <p class="text-sm text-slate-500 leading-6">회원 식별, 가입 의사 확인, 수영 기록 저장 및 통계 시각화 서비스 제공</p>
            </li>
            <li class="p-5 border border-slate-50 rounded-2xl space-y-2">
              <p class="text-sm font-bold text-slate-900 italic">사용자 지원</p>
              <p class="text-sm text-slate-500 leading-6">공지사항 전달, 민원 처리, 서비스 개선을 위한 통계 분석</p>
            </li>
          </ul>
        </section>

        <section class="space-y-6">
          <div class="flex items-center gap-3">
            <div class="w-1.5 h-6 bg-blue-600 rounded-full"></div>
            <h3 class="text-lg font-black tracking-tight">3. 개인정보의 제3자 제공</h3>
          </div>
          <p class="text-sm leading-7 text-slate-600 font-medium">
            서비스는 이용자의 개인정보를 원칙적으로 외부에 제공하지 않습니다. 다만, 아래의 경우에는 예외로 합니다.
          </p>
          <div class="p-5 bg-blue-50/50 rounded-2xl">
            <p class="text-xs text-blue-800 leading-6 font-medium">
              - 이용자가 사전에 동의한 경우<br>
              - 법령의 규정에 의거하거나, 수사 목적으로 법령에 정해진 절차와 방법에 따라 수사기관의 요구가 있는 경우<br>
              - <span class="font-bold underline">이미지 서버 위탁:</span> Cloudinary (사진 업로드 및 저장 서비스 제공)
            </p>
          </div>
        </section>

        <section class="space-y-6">
          <div class="flex items-center gap-3">
            <div class="w-1.5 h-6 bg-blue-600 rounded-full"></div>
            <h3 class="text-lg font-black tracking-tight">4. 개인정보의 보유 및 파기</h3>
          </div>
          <p class="text-sm leading-7 text-slate-600 font-medium">
            1. 이용자의 개인정보는 회원 탈퇴 시 즉시 파기하는 것을 원칙으로 합니다.<br>
            2. 단, 부정 이용 방지를 위해 <span class="text-red-500 font-bold">탈퇴 후 30일간</span> 정보를 보관 후 완전 파기합니다.
          </p>
        </section>

        <section class="space-y-6">
          <div class="flex items-center gap-3">
            <div class="w-1.5 h-6 bg-blue-600 rounded-full"></div>
            <h3 class="text-lg font-black tracking-tight">5. 이용자의 권리</h3>
          </div>
          <p class="text-sm leading-7 text-slate-600 font-medium">
            이용자는 언제든지 등록되어 있는 자신의 개인정보를 조회하거나 수정할 수 있으며, 회원 탈퇴를 통해 개인정보 이용에 대한 동의를 철회할 수 있습니다.
          </p>
        </section>

        <div class="pt-10 border-t border-slate-50">
          <p class="text-[10px] font-black text-slate-300 uppercase tracking-widest">Last Updated: 2026. 03. 03</p>
        </div>
      </div>

      <div class="text-center pb-8">
        <p class="text-[10px] font-bold text-slate-300 uppercase tracking-widest leading-relaxed">
          &copy; 2026 Swim Too. All rights reserved.
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>
