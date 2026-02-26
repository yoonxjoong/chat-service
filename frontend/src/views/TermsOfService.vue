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
          <h2 class="text-3xl font-black text-slate-800 tracking-tighter">서비스 이용약관</h2>
          <p class="text-sm text-slate-400 font-bold mt-2">최종 수정일: 2026년 2월 26일</p>
        </div>

        <div class="prose prose-slate max-w-none space-y-6 text-slate-600 leading-relaxed text-sm">
          <div>
            <h3 class="text-lg font-black text-slate-800 mb-3">제 1 조 (목적)</h3>
            <p>본 약관은 SwimRecord(이하 "회사")가 제공하는 수영 기록 및 채팅 서비스(이하 "서비스")의 이용조건 및 절차, 회사와 회원간의 권리, 의무 및 책임사항 등을 규정함을 목적으로 합니다.</p>
          </div>

          <div>
            <h3 class="text-lg font-black text-slate-800 mb-3">제 2 조 (이용계약의 체결)</h3>
            <p>이용계약은 회원이 되고자 하는 자가 약관의 내용에 대하여 동의를 한 다음 회원가입신청을 하고 회사가 이러한 신청에 대하여 승낙함으로써 체결됩니다.</p>
          </div>

          <div>
            <h3 class="text-lg font-black text-slate-800 mb-3">제 3 조 (서비스의 제공 및 변경)</h3>
            <p>회사는 수영 기록 관리, 통계 시각화, 실시간 채팅 서비스를 제공합니다. 회사는 서비스의 기술적 사양의 변경 등의 경우에는 장차 체결되는 계약에 의해 제공할 서비스의 내용을 변경할 수 있습니다.</p>
          </div>

          <div>
            <h3 class="text-lg font-black text-slate-800 mb-3">제 4 조 (회원의 의무)</h3>
            <p>회원은 다음 행위를 하여서는 안 됩니다.</p>
            <ul class="list-disc ml-5 mt-2 space-y-1 font-medium">
              <li>신청 또는 변경 시 허위내용의 등록</li>
              <li>타인의 정보도용</li>
              <li>회사가 정한 정보 이외의 정보(컴퓨터 프로그램 등)의 송신 또는 게시</li>
              <li>채팅방 내 타인에 대한 욕설, 비방 및 비매너 행위</li>
            </ul>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import AppHeader from '../components/AppHeader.vue'
</script>
