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
        <h2 class="text-[10px] font-black uppercase tracking-[0.3em] text-slate-400">Terms of Service</h2>
        <div class="w-9"></div>
      </div>

      <div class="space-y-4 text-center">
        <h1 class="text-3xl font-black tracking-tighter text-slate-900">서비스 이용약관</h1>
        <p class="text-xs font-bold text-slate-400 leading-relaxed uppercase tracking-widest">Swim Too Service Agreement</p>
      </div>

      <div class="bg-white rounded-[2.5rem] p-8 md:p-12 shadow-sm border border-slate-100 space-y-12">
        <section class="space-y-6">
          <div class="flex items-center gap-3">
            <div class="w-1.5 h-6 bg-slate-900 rounded-full"></div>
            <h3 class="text-lg font-black tracking-tight">제 1 조 (목적)</h3>
          </div>
          <p class="text-sm leading-7 text-slate-600 font-medium">
            본 약관은 "매일수영"(이하 "서비스")이 제공하는 수영 기록 관리, 데이터 분석 및 커뮤니티 서비스의 이용과 관련하여, 서비스와 이용자 간의 권리, 의무 및 책임사항, 기타 필요한 사항을 규정함을 목적으로 합니다.
          </p>
        </section>

        <section class="space-y-6">
          <div class="flex items-center gap-3">
            <div class="w-1.5 h-6 bg-slate-900 rounded-full"></div>
            <h3 class="text-lg font-black tracking-tight">제 2 조 (용어의 정의)</h3>
          </div>
          <div class="space-y-4">
            <div class="p-5 bg-slate-50 rounded-2xl space-y-3">
              <p class="text-sm text-slate-600 leading-6"><span class="font-bold text-slate-900">1. 서비스:</span> 구현되는 단말기(PC, 휴대형 단말기 등)와 상관없이 이용자가 이용할 수 있는 매일수영 관련 제반 서비스를 의미합니다.</p>
              <p class="text-sm text-slate-600 leading-6"><span class="font-bold text-slate-900">2. 이용자:</span> 서비스에 접속하여 본 약관에 따라 서비스를 이용하는 회원 및 비회원을 말합니다.</p>
              <p class="text-sm text-slate-600 leading-6"><span class="font-bold text-slate-900">3. 게시물:</span> 이용자가 서비스를 이용함에 있어 서비스상에 게시한 부호·문자·음성·음향·화상·동영상 등의 정보 형태의 글, 사진, 동영상 및 각종 파일과 링크 등을 의미합니다.</p>
            </div>
          </div>
        </section>

        <section class="space-y-6">
          <div class="flex items-center gap-3">
            <div class="w-1.5 h-6 bg-slate-900 rounded-full"></div>
            <h3 class="text-lg font-black tracking-tight">제 3 조 (이용자의 의무)</h3>
          </div>
          <p class="text-sm leading-7 text-slate-600 font-medium">이용자는 다음 행위를 하여서는 안 됩니다.</p>
          <ul class="space-y-3">
            <li class="flex gap-3 items-start text-sm text-slate-500 font-medium">
              <span class="mt-1.5 w-1 h-1 rounded-full bg-slate-300 flex-shrink-0"></span>
              타인의 정보도용 및 허위사실 기재
            </li>
            <li class="flex gap-3 items-start text-sm text-slate-500 font-medium">
              <span class="mt-1.5 w-1 h-1 rounded-full bg-slate-300 flex-shrink-0"></span>
              서비스의 정상적인 운영을 방해하는 행위
            </li>
            <li class="flex gap-3 items-start text-sm text-slate-500 font-medium">
              <span class="mt-1.5 w-1 h-1 rounded-full bg-slate-300 flex-shrink-0"></span>
              타인의 명예를 손상시키거나 불이익을 주는 행위
            </li>
            <li class="flex gap-3 items-start text-sm text-slate-500 font-medium">
              <span class="mt-1.5 w-1 h-1 rounded-full bg-slate-300 flex-shrink-0"></span>
              외설적 또는 폭력적인 메시지, 화상, 음성, 기타 공서양속에 반하는 정보를 서비스에 공개 또는 게시하는 행위
            </li>
          </ul>
        </section>

        <section class="space-y-6">
          <div class="flex items-center gap-3">
            <div class="w-1.5 h-6 bg-slate-900 rounded-full"></div>
            <h3 class="text-lg font-black tracking-tight">제 4 조 (게시물의 저작권)</h3>
          </div>
          <div class="space-y-4">
            <p class="text-sm leading-7 text-slate-600 font-medium">
              1. 이용자가 서비스 내에 게시한 게시물의 저작권은 해당 게시물의 저작자에게 귀속됩니다.<br>
              2. 서비스는 게시물을 서비스 내에 게시하거나 서비스 홍보 등의 목적으로 활용할 수 있습니다. 단, 이 경우 게시자의 개인정보는 철저히 보호됩니다.
            </p>
          </div>
        </section>

        <section class="space-y-6">
          <div class="flex items-center gap-3">
            <div class="w-1.5 h-6 bg-slate-900 rounded-full"></div>
            <h3 class="text-lg font-black tracking-tight">제 5 조 (책임제한)</h3>
          </div>
          <p class="text-sm leading-7 text-slate-600 font-medium">
            1. 서비스는 천재지변 또는 이에 준하는 불가항력으로 인하여 서비스를 제공할 수 없는 경우에는 서비스 제공에 관한 책임이 면제됩니다.<br>
            2. 서비스는 이용자의 귀책사유로 인한 서비스 이용의 장애에 대하여는 책임을 지지 않습니다.
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
