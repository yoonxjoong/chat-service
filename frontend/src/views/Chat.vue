<template>
  <div class="h-screen flex flex-col bg-slate-50 overflow-hidden">
    <AppHeader />

    <div class="flex-1 flex overflow-hidden">
      <!-- Sidebar: Channel Switcher -->
      <aside class="w-20 md:w-64 bg-white border-r border-slate-200 flex flex-col shadow-[4px_0_24px_rgba(0,0,0,0.02)]">
        <div class="p-6 border-b border-slate-100 hidden md:block">
          <h2 class="font-black text-slate-800 tracking-tighter uppercase">Community</h2>
        </div>
        <div class="flex-1 overflow-y-auto p-3 space-y-4">
          <div 
            v-for="room in rooms" :key="room.roomId"
            @click="enterRoom(room)"
            :class="['group relative p-4 rounded-2xl cursor-pointer transition-all border flex items-center gap-3', 
                     currentRoom?.roomId === room.roomId ? 'bg-primary-600 border-primary-600 shadow-lg shadow-primary-100' : 'hover:bg-slate-50 border-transparent']"
          >
            <div :class="['w-10 h-10 rounded-xl flex items-center justify-center text-xl shrink-0', currentRoom?.roomId === room.roomId ? 'bg-white/20 text-white' : 'bg-slate-100 text-slate-500']">
              {{ room.roomId === 'today-swim-room' ? '🏊' : '💬' }}
            </div>
            <div class="hidden md:flex flex-col overflow-hidden">
              <span :class="['font-bold truncate', currentRoom?.roomId === room.roomId ? 'text-white' : 'text-slate-700']">{{ room.name }}</span>
              <span :class="['text-[10px]', currentRoom?.roomId === room.roomId ? 'text-white/70' : 'text-slate-400']">{{ room.userCount }}명 참여 중</span>
            </div>
            <!-- Unread Badge -->
            <span v-if="unreadCounts[room.roomId]" class="absolute -top-1 -right-1 flex items-center justify-center min-w-[18px] h-[18px] bg-red-500 text-white text-[9px] font-black rounded-full border-2 border-white">
              {{ unreadCounts[room.roomId] }}
            </span>
          </div>
        </div>
      </aside>

      <!-- Main Chat Area -->
      <section class="flex-1 flex flex-col bg-white relative">
        <div v-if="currentRoom" class="p-4 border-b border-slate-100 flex justify-between items-center bg-white/80 backdrop-blur-md sticky top-0 z-10">
          <div class="flex items-center gap-4">
            <div class="w-10 h-10 bg-slate-50 rounded-xl flex items-center justify-center text-xl">
              {{ currentRoom.roomId === 'today-swim-room' ? '🏊' : '💬' }}
            </div>
            <div class="flex flex-col">
              <span class="font-black text-slate-800 tracking-tight">{{ currentRoom.name }}</span>
              <div class="flex items-center gap-2">
                <span class="w-1.5 h-1.5 bg-green-500 rounded-full animate-pulse"></span>
                <span class="text-[10px] text-slate-400 font-bold uppercase tracking-widest">Live Channel</span>
              </div>
            </div>
          </div>
          
          <!-- Today's Stats in Header (Only for today-swim-room) -->
          <div v-if="currentRoom.roomId === 'today-swim-room'" class="hidden lg:flex items-center gap-6 px-6 py-2 bg-slate-50 rounded-2xl border border-slate-100">
            <div class="flex flex-col items-center">
              <span class="text-[9px] font-black text-slate-400 uppercase tracking-widest">Today's Team Distance</span>
              <span class="text-sm font-black text-primary-600">{{ todayTeamDistance.toLocaleString() }}{{ user.distanceUnit === 'YARD' ? 'yd' : 'm' }}</span>
            </div>
            <div class="w-px h-6 bg-slate-200"></div>
            <div class="flex flex-col items-center">
              <span class="text-[9px] font-black text-slate-400 uppercase tracking-widest">Active Today</span>
              <span class="text-sm font-black text-orange-500">{{ currentRoom.userCount }}명</span>
            </div>
          </div>

          <button @click="leaveRoom" class="p-2 text-slate-300 hover:text-red-500 transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" stroke-width="2" /></svg>
          </button>
        </div>

        <div class="flex-1 overflow-y-auto p-6 space-y-6 bg-[#F8FAFC]" ref="messageBox">
          <div v-if="!currentRoom" class="h-full flex flex-col items-center justify-center text-slate-300">
            <div class="w-24 h-24 bg-slate-100 rounded-[2rem] flex items-center justify-center text-4xl mb-6">👋</div>
            <p class="font-black text-slate-800 text-xl tracking-tighter">함께 수영할 준비가 되셨나요?</p>
            <p class="text-sm text-slate-400 mt-1">왼쪽 채널을 선택하여 대화를 시작해보세요.</p>
          </div>

          <template v-else>
            <div 
              v-for="(msg, idx) in messages" :key="idx"
              :class="['flex flex-col max-w-[85%]', 
                       (msg.senderId === 'system' || msg.type === 'ENTER' || msg.type === 'QUIT') ? 'mx-auto w-full items-center my-4' : (msg.senderId === user.username ? 'ml-auto items-end' : 'mr-auto items-start')]"
            >
              <!-- 1. Swimming Record Notification (Fancy Style) -->
              <div v-if="msg.senderId === 'system'" class="flex flex-col items-center gap-2">
                <div class="px-6 py-2 bg-white rounded-2xl border border-primary-100 shadow-sm flex items-center gap-3">
                  <span class="text-lg">✨</span>
                  <span class="text-xs font-black text-slate-700">{{ msg.message }}</span>
                </div>
              </div>

              <!-- 2. Entry/Quit Message (Simple Style - Restored) -->
              <div v-else-if="msg.type === 'ENTER' || msg.type === 'QUIT'" class="bg-slate-200/50 text-slate-500 text-[10px] px-3 py-1 rounded-full font-bold uppercase tracking-wider">
                {{ msg.message }}
              </div>

              <!-- 3. Regular Talk Message Style -->
              <div v-else class="flex flex-col gap-1 w-full" :class="msg.senderId === user.username ? 'items-end' : 'items-start'">
                <div v-if="msg.senderId !== user.username" class="flex items-center gap-2 mb-1 ml-1">
                  <div class="w-6 h-6 rounded-lg bg-primary-100 flex items-center justify-center text-[10px] font-bold text-primary-700 uppercase">
                    {{ msg.sender?.charAt(0) }}
                  </div>
                  <span class="text-[11px] font-black text-slate-500">{{ msg.sender }}</span>
                </div>
                <div 
                  :class="['px-4 py-3 rounded-2xl text-[13px] leading-relaxed shadow-sm transition-all', 
                           msg.senderId === user.username 
                            ? 'bg-primary-600 text-white rounded-tr-none hover:bg-primary-700' 
                            : 'bg-white text-slate-700 border border-slate-100 rounded-tl-none hover:border-slate-200']"
                >
                  {{ msg.message }}
                </div>
              </div>
            </div>
          </template>
        </div>

        <div v-if="currentRoom" class="p-4 bg-white border-t border-slate-100">
          <div class="flex items-center gap-2 bg-slate-50 p-2 rounded-2xl border border-slate-200 focus-within:border-primary-400 transition-all">
            <input 
              v-model="newMessage" @keypress.enter="sendMessage"
              class="flex-1 bg-transparent border-none outline-none px-3 py-2 text-sm placeholder:text-slate-400"
              placeholder="따뜻한 한마디를 건네보세요..."
            />
            <button @click="sendMessage" :disabled="!newMessage.trim()" class="w-10 h-10 flex items-center justify-center bg-primary-600 hover:bg-primary-700 text-white rounded-xl disabled:opacity-30">
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
import AppHeader from '../components/AppHeader.vue'

const router = useRouter()
const user = ref({ username: '', nickname: '', profileImageUrl: '' })
const rooms = ref([])
const currentRoom = ref(null)
const messages = ref([])
const newMessage = ref('')
const messageBox = ref(null)
const unreadCounts = ref({})
const todayTeamDistance = ref(0)

let stompClient = null
let subscription = null
let isConnected = false

const fetchUser = async () => {
  try {
    const res = await axios.get('/api/user/me')
    user.value = res.data
    return res.data
  } catch (err) {
    router.push('/login')
    throw err
  }
}

const fetchRooms = async () => {
  try {
    const res = await axios.get('/chat/rooms')
    rooms.value = res.data
  } catch (err) {}
}

const fetchTodayTeamDistance = async () => {
  try {
    const res = await axios.get('/api/swimming/today-total')
    let dist = res.data.totalDistance || 0
    if (user.value.distanceUnit === 'YARD') {
      dist = Math.round(dist * 1.09361)
    }
    todayTeamDistance.value = dist
  } catch (err) {}
}

const connect = () => {
  const socket = new SockJS('/ws-stomp')
  stompClient = Stomp.over(socket)
  stompClient.debug = null
  stompClient.connect({}, (frame) => {
    isConnected = true
    console.log('STOMP Connected')
    subscribeGlobalNotifications()
  }, (err) => {
    console.error('STOMP Connection Error', err)
    isConnected = false
    setTimeout(connect, 5000)
  })
}

const subscribeGlobalNotifications = () => {
  if (!stompClient) return
  stompClient.subscribe('/sub/chat/all/notifications', (payload) => {
    const msg = JSON.parse(payload.body)
    if (msg.senderId === user.value.username) return
    if (!currentRoom.value || currentRoom.value.roomId !== msg.roomId) {
      unreadCounts.value[msg.roomId] = (unreadCounts.value[msg.roomId] || 0) + 1
      showBrowserNotification(msg)
    } else if (document.hidden) {
      showBrowserNotification(msg)
    }
  })
}

const showBrowserNotification = (msg) => {
  if (Notification.permission === 'granted') {
    const roomName = rooms.value.find(r => r.roomId === msg.roomId)?.name || '새 메시지'
    new Notification(`[${roomName}] ${msg.sender}`, { body: msg.message })
  }
}

const createRoom = async () => {
  const name = prompt('생성할 채팅방 이름을 입력하세요:')
  if (name) {
    try {
      await axios.post(`/chat/room?name=${encodeURIComponent(name)}`)
      fetchRooms()
    } catch (err) {}
  }
}

const enterRoom = async (room) => {
  if (currentRoom.value?.roomId === room.roomId) return
  unreadCounts.value[room.roomId] = 0
  if (subscription) {
    subscription.unsubscribe()
    subscription = null
  }
  currentRoom.value = room
  messages.value = []
  try {
    const res = await axios.get(`/chat/room/${room.roomId}/messages`)
    messages.value = res.data.map(m => ({ ...m, type: 'TALK' }))
    if (isConnected && stompClient) {
      subscription = stompClient.subscribe(`/sub/chat/room/${room.roomId}`, (payload) => {
        const msg = JSON.parse(payload.body)
        if (msg.roomId === currentRoom.value?.roomId) {
          messages.value.push(msg)
          scrollToBottom()
        }
        if (msg.type === 'ENTER' || msg.type === 'QUIT') fetchRooms()
      })
      stompClient.send('/pub/chat/message', {}, JSON.stringify({
        type: 'ENTER', roomId: room.roomId, sender: user.value.nickname, senderId: user.value.username
      }))
    }
    scrollToBottom()
  } catch (err) {}
}

const sendMessage = () => {
  if (!newMessage.value.trim() || !isConnected || !stompClient || !currentRoom.value) return
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
  if (isConnected && stompClient && currentRoom.value) {
    stompClient.send('/pub/chat/message', {}, JSON.stringify({
      type: 'QUIT', roomId: currentRoom.value.roomId, sender: user.value.nickname, senderId: user.value.username
    }))
  }
  if (subscription) {
    subscription.unsubscribe()
    subscription = null
  }
  currentRoom.value = null
  messages.value = []
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageBox.value) messageBox.value.scrollTop = messageBox.value.scrollHeight
  })
}

onMounted(async () => {
  if (Notification.permission !== 'granted') Notification.requestPermission()
  try {
    await fetchUser()
    connect()
    await fetchRooms()
    
    // URL에 roomId가 있으면 자동 입장
    const urlParams = new URLSearchParams(window.location.search)
    const rid = urlParams.get('roomId')
    if (rid) {
      const room = rooms.value.find(r => r.roomId === rid)
      if (room) enterRoom(room)
    }

    setInterval(fetchRooms, 5000)
    setInterval(fetchTodayTeamDistance, 10000)
    fetchTodayTeamDistance()
  } catch (err) {}
})
</script>
