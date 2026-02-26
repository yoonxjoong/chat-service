<template>
  <div class="h-screen flex flex-col bg-slate-50 overflow-hidden">
    <!-- Header -->
    <header class="bg-white border-b border-slate-200 px-6 py-4 flex justify-between items-center shadow-sm z-10">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 bg-primary-600 rounded-xl flex items-center justify-center text-white shadow-lg shadow-primary-200">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
          </svg>
        </div>
        <h1 class="text-xl font-bold text-slate-800 tracking-tight">Gemini Chat</h1>
      </div>
      <div class="flex items-center gap-4">
        <div class="flex flex-col items-end">
          <span class="text-sm font-bold text-slate-700">{{ user.nickname }}</span>
          <span class="text-xs text-slate-400">{{ user.username }}</span>
        </div>
        <button @click="logout" class="px-4 py-2 text-sm font-semibold text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-cowhgdlors border border-transparent hover:border-red-100">
          로그아웃
        </button>
      </div>
    </header>

    <div class="flex-1 flex overflow-hidden">
      <!-- Sidebar -->
      <aside class="w-80 bg-white border-r border-slate-200 flex flex-col shadow-[4px_0_24px_rgba(0,0,0,0.02)]">
        <div class="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
          <h2 class="font-bold text-slate-800">채팅방 목록</h2>
          <button @click="createRoom" class="w-8 h-8 flex items-center justify-center bg-primary-600 hover:bg-primary-700 text-white rounded-lg shadow-md shadow-primary-100 transition-transform active:scale-90">
            <span class="text-xl">+</span>
          </button>
        </div>
        <div class="flex-1 overflow-y-auto p-3 space-y-2">
          <div 
            v-for="room in rooms" :key="room.roomId"
            @click="enterRoom(room)"
            :class="['group p-4 rounded-2xl cursor-pointer transition-all border border-transparent', 
                     currentRoom?.roomId === room.roomId ? 'bg-primary-50 border-primary-100 shadow-sm' : 'hover:bg-slate-50 hover:border-slate-100']"
          >
            <div class="flex justify-between items-start mb-1">
              <span :class="['font-bold truncate', currentRoom?.roomId === room.roomId ? 'text-primary-700' : 'text-slate-700']">
                {{ room.name }}
              </span>
              <span class="text-[10px] px-2 py-0.5 bg-green-100 text-green-600 rounded-full font-bold">
                {{ room.userCount }}명
              </span>
            </div>
            <p class="text-xs text-slate-400 truncate group-hover:text-slate-500">대화에 참여해보세요</p>
          </div>
        </div>
      </aside>

      <!-- Main Chat Area -->
      <section class="flex-1 flex flex-col bg-white relative">
        <div v-if="currentRoom" class="p-4 border-b border-slate-100 flex justify-between items-center bg-white/80 backdrop-blur-md sticky top-0 z-10">
          <div class="flex flex-col">
            <span class="font-bold text-slate-800">{{ currentRoom.name }}</span>
            <span class="text-[10px] text-slate-400">참여 중</span>
          </div>
          <button @click="leaveRoom" class="px-3 py-1.5 text-xs font-bold text-red-500 hover:bg-red-50 rounded-lg transition-colors">
            방 나가기
          </button>
        </div>

        <!-- Messages -->
        <div class="flex-1 overflow-y-auto p-6 space-y-6 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] bg-fixed" ref="messageBox">
          <div v-if="!currentRoom" class="h-full flex flex-col items-center justify-center text-slate-300">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-20 w-20 mb-4 opacity-20" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9a1.994 1.994 0 01-1.414-.586m0 0L11 14h4a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2v4l.586-.586z" />
            </svg>
            <p class="font-medium">대화를 시작할 채팅방을 선택해 주세요</p>
          </div>

          <template v-else>
            <div 
              v-for="(msg, idx) in messages" :key="idx"
              :class="['flex flex-col max-w-[80%]', 
                       msg.type === 'TALK' ? (msg.senderId === user.username ? 'ml-auto items-end' : 'mr-auto items-start') : 'mx-auto w-full items-center']"
            >
              <div v-if="msg.type === 'TALK'" class="flex flex-col gap-1 w-full" :class="msg.senderId === user.username ? 'items-end' : 'items-start'">
                <span v-if="msg.senderId !== user.username" class="text-[11px] font-bold text-slate-500 ml-1">{{ msg.sender }}</span>
                <div 
                  :class="['px-4 py-2.5 rounded-2xl text-sm leading-relaxed shadow-sm', 
                           msg.senderId === user.username ? 'bg-primary-600 text-white rounded-tr-none shadow-primary-100' : 'bg-slate-100 text-slate-700 rounded-tl-none']"
                >
                  {{ msg.message }}
                </div>
              </div>
              <div v-else class="system-msg bg-slate-200/50 text-slate-500 text-[10px] px-3 py-1 rounded-full font-bold uppercase tracking-wider my-2">
                {{ msg.message }}
              </div>
            </div>
          </template>
        </div>

        <!-- Input -->
        <div v-if="currentRoom" class="p-4 bg-white border-t border-slate-100">
          <div class="flex items-center gap-2 bg-slate-50 p-2 rounded-2xl border border-slate-200 focus-within:border-primary-400 focus-within:ring-4 focus-within:ring-primary-50 transition-all">
            <input 
              v-model="newMessage" @keypress.enter="sendMessage"
              class="flex-1 bg-transparent border-none outline-none px-3 py-2 text-sm placeholder:text-slate-400"
              placeholder="따뜻한 한마디를 건네보세요..."
            />
            <button 
              @click="sendMessage" :disabled="!newMessage.trim()"
              class="w-10 h-10 flex items-center justify-center bg-primary-600 hover:bg-primary-700 text-white rounded-xl shadow-md shadow-primary-100 disabled:opacity-30 disabled:shadow-none transition-all active:scale-90"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
              </svg>
            </button>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = ref({ username: '', nickname: '' })
const rooms = ref([])
const currentRoom = ref(null)
const messages = ref([])
const newMessage = ref('')
const messageBox = ref(null)

let stompClient = null
let subscription = null

const fetchUser = async () => {
  try {
    const res = await axios.get('/api/user/me')
    user.value = res.data
  } catch (err) {
    router.push('/login')
  }
}

const fetchRooms = async () => {
  try {
    const res = await axios.get('/chat/rooms')
    rooms.value = res.data
  } catch (err) {}
}

const connect = () => {
  const socket = new SockJS('/ws-stomp')
  stompClient = Stomp.over(socket)
  stompClient.debug = null // 디버그 로그 숨김
  stompClient.connect({}, (frame) => {
    console.log('STOMP Connected')
  })
}

const createRoom = async () => {
  const name = prompt('생성할 채팅방 이름을 입력하세요:')
  if (name) {
    await axios.post(`/chat/room?name=${encodeURIComponent(name)}`)
    fetchRooms()
  }
}

const enterRoom = async (room) => {
  if (currentRoom.value?.roomId === room.roomId) return
  if (subscription) subscription.unsubscribe()
  
  currentRoom.value = room
  messages.value = []
  
  try {
    const res = await axios.get(`/chat/room/${room.roomId}/messages`)
    messages.value = res.data.map(m => ({ ...m, type: 'TALK' }))
    
    subscription = stompClient.subscribe(`/sub/chat/room/${room.roomId}`, (payload) => {
      const msg = JSON.parse(payload.body)
      messages.value.push(msg)
      if (msg.type === 'ENTER' || msg.type === 'QUIT') fetchRooms()
      scrollToBottom()
    })

    stompClient.send('/pub/chat/message', {}, JSON.stringify({
      type: 'ENTER', roomId: room.roomId, sender: user.value.nickname, senderId: user.value.username
    }))
    scrollToBottom()
  } catch (err) {
    console.error('Room entry failed', err)
  }
}

const sendMessage = () => {
  if (!newMessage.value.trim() || !stompClient) return
  stompClient.send('/pub/chat/message', {}, JSON.stringify({
    type: 'TALK',
    roomId: currentRoom.value.roomId,
    sender: user.value.nickname,
    senderId: user.value.username,
    message: newMessage.value
  }))
  newMessage.value = ''
}

const leaveRoom = () => {
  if (stompClient && currentRoom.value) {
    stompClient.send('/pub/chat/message', {}, JSON.stringify({
      type: 'QUIT', roomId: currentRoom.value.roomId, sender: user.value.nickname, senderId: user.value.username
    }))
  }
  if (subscription) subscription.unsubscribe()
  currentRoom.value = null
  messages.value = []
}

const logout = async () => {
  try {
    await axios.post('/api/member/logout')
    router.push('/login')
  } catch (err) {
    console.error('Logout failed', err)
    // 에러가 나더라도 클라이언트 상태를 지우고 이동
    router.push('/login')
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageBox.value) messageBox.value.scrollTop = messageBox.value.scrollHeight
  })
}

onMounted(async () => {
  await fetchUser()
  connect()
  fetchRooms()
  setInterval(fetchRooms, 5000)
})
</script>
