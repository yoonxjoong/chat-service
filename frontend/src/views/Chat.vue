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
        <h1 class="text-xl font-bold text-slate-800 tracking-tight mr-4">Gemini Chat</h1>
        
        <div class="hidden md:flex gap-1 bg-slate-100 p-1 rounded-xl">
          <router-link to="/" class="px-4 py-1.5 text-xs font-bold rounded-lg text-slate-500 hover:text-slate-700 transition-all">대시보드</router-link>
          <router-link to="/stats" class="px-4 py-1.5 text-xs font-bold rounded-lg text-slate-500 hover:text-slate-700 transition-all">통계</router-link>
          <router-link to="/chat" class="px-4 py-1.5 text-xs font-bold rounded-lg bg-white text-primary-600 shadow-sm transition-all">채팅</router-link>
        </div>
      </div>
      
      <div class="flex items-center gap-4">
        <!-- User Profile Trigger -->
        <div class="flex items-center gap-3 cursor-pointer group px-3 py-1.5 rounded-xl hover:bg-slate-50 transition-colors" @click="openProfileModal">
          <div class="flex flex-col items-end">
            <span class="text-sm font-bold text-slate-700 group-hover:text-primary-600 transition-colors">{{ user.nickname }}</span>
            <span class="text-[10px] text-slate-400">내 프로필 설정</span>
          </div>
          <!-- Avatar Display -->
          <div class="w-10 h-10 rounded-full border-2 border-white shadow-sm overflow-hidden flex items-center justify-center bg-primary-100">
            <img v-if="user.profileImageUrl" :src="user.profileImageUrl" class="w-full h-full object-cover" />
            <span v-else class="text-primary-700 font-bold text-sm">{{ user.nickname?.charAt(0) }}</span>
          </div>
        </div>
        
        <div class="h-8 w-px bg-slate-200 mx-1"></div>
        
        <button @click="logout" class="px-4 py-2 text-sm font-semibold text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors">
          로그아웃
        </button>
      </div>
    </header>

    <div class="flex-1 flex overflow-hidden">
      <!-- Sidebar -->
      <aside class="w-80 bg-white border-r border-slate-200 flex flex-col shadow-[4px_0_24px_rgba(0,0,0,0.02)]">
        <div class="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
          <h2 class="font-bold text-slate-800">채팅방 목록</h2>
          <button @click="createRoom" class="w-8 h-8 flex items-center justify-center bg-primary-600 hover:bg-primary-700 text-white rounded-lg shadow-md">
            <span class="text-xl">+</span>
          </button>
        </div>
        <div class="flex-1 overflow-y-auto p-3 space-y-2">
          <div 
            v-for="room in rooms" :key="room.roomId"
            @click="enterRoom(room)"
            :class="['group p-4 rounded-2xl cursor-pointer transition-all border border-transparent flex flex-col gap-1', 
                     currentRoom?.roomId === room.roomId ? 'bg-primary-50 border-primary-100 shadow-sm' : 'hover:bg-slate-50 hover:border-slate-100']"
          >
            <div class="flex justify-between items-center">
              <span :class="['font-bold truncate max-w-[150px]', currentRoom?.roomId === room.roomId ? 'text-primary-700' : 'text-slate-700']">
                {{ room.name }}
              </span>
              <div class="flex items-center gap-2">
                <span v-if="unreadCounts[room.roomId]" class="flex items-center justify-center min-w-[20px] h-5 px-1.5 bg-red-500 text-white text-[10px] font-bold rounded-full shadow-sm animate-bounce">
                  {{ unreadCounts[room.roomId] }}
                </span>
                <span class="text-[10px] px-2 py-0.5 bg-green-100 text-green-600 rounded-full font-bold">
                  {{ room.userCount }}명
                </span>
              </div>
            </div>
            <p class="text-xs text-slate-400 truncate">대화에 참여해보세요</p>
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
          <button @click="leaveRoom" class="px-3 py-1.5 text-xs font-bold text-red-500 hover:bg-red-50 rounded-lg">
            방 나가기
          </button>
        </div>

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

    <!-- Profile Edit Modal -->
    <div v-if="showProfileModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div class="bg-white w-full max-w-md rounded-3xl shadow-2xl overflow-hidden">
        <div class="p-8 space-y-6">
          <div class="flex justify-between items-center">
            <h3 class="text-2xl font-bold text-slate-800">프로필 편집</h3>
            <button @click="showProfileModal = false" class="text-slate-400 hover:text-slate-600">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
            </button>
          </div>

          <div class="flex flex-col items-center gap-4">
            <div class="w-24 h-24 rounded-full border-4 border-slate-50 shadow-inner overflow-hidden bg-primary-100 flex items-center justify-center">
              <img v-if="profileEdit.profileImageUrl" :src="profileEdit.profileImageUrl" class="w-full h-full object-cover" />
              <span v-else class="text-primary-700 font-bold text-3xl">{{ profileEdit.nickname?.charAt(0) }}</span>
            </div>
            <p class="text-xs text-slate-400 italic">ID: {{ user.username }}</p>
          </div>

          <div class="space-y-4">
            <div>
              <label class="block text-sm font-bold text-slate-700 mb-1">닉네임</label>
              <input v-model="profileEdit.nickname" class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:border-primary-500 outline-none transition-all" />
            </div>
            <div>
              <label class="block text-sm font-bold text-slate-700 mb-1">프로필 이미지 URL</label>
              <input v-model="profileEdit.profileImageUrl" placeholder="https://..." class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:border-primary-500 outline-none transition-all" />
            </div>
          </div>

          <button @click="updateProfile" :disabled="isUpdating" class="w-full py-4 bg-primary-600 hover:bg-primary-700 text-white rounded-2xl font-bold transition-all active:scale-95 disabled:opacity-50 shadow-lg shadow-primary-100">
            {{ isUpdating ? '저장 중...' : '변경 내용 저장' }}
          </button>
        </div>
      </div>
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
const user = ref({ username: '', nickname: '', profileImageUrl: '' })
const rooms = ref([])
const currentRoom = ref(null)
const messages = ref([])
const newMessage = ref('')
const messageBox = ref(null)
const unreadCounts = ref({})

// Profile Modal State
const showProfileModal = ref(false)
const isUpdating = ref(false)
const profileEdit = ref({ nickname: '', profileImageUrl: '' })

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

const openProfileModal = () => {
  profileEdit.value = { 
    nickname: user.value.nickname, 
    profileImageUrl: user.value.profileImageUrl || '' 
  }
  showProfileModal.value = true
}

const updateProfile = async () => {
  if (!profileEdit.value.nickname.trim()) return
  isUpdating.value = true
  try {
    await axios.put('/api/user/profile', profileEdit.value)
    await fetchUser() 
    showProfileModal.value = false
    alert('프로필이 수정되었습니다.')
  } catch (err) {
    alert('프로필 수정 중 오류가 발생했습니다.')
  } finally {
    isUpdating.value = false
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
  stompClient.debug = null
  stompClient.connect({}, (frame) => {
    isConnected = true
    console.log('STOMP Connected')
    subscribeGlobalNotifications()
  }, (err) => {
    console.error('STOMP Connection Error', err)
    isConnected = false
    // 재연결 로직 (선택사항)
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
  console.log('Entering room:', room.name)
  if (currentRoom.value?.roomId === room.roomId) return
  
  // 상태 초기화
  unreadCounts.value[room.roomId] = 0
  if (subscription) {
    subscription.unsubscribe()
    subscription = null
  }
  
  currentRoom.value = room
  messages.value = []
  
  try {
    // 히스토리 로드
    const res = await axios.get(`/chat/room/${room.roomId}/messages`)
    messages.value = res.data.map(m => ({ ...m, type: 'TALK' }))
    
    if (isConnected && stompClient) {
      // 구독 설정
      subscription = stompClient.subscribe(`/sub/chat/room/${room.roomId}`, (payload) => {
        const msg = JSON.parse(payload.body)
        if (msg.roomId === currentRoom.value?.roomId) {
          messages.value.push(msg)
          scrollToBottom()
        }
        if (msg.type === 'ENTER' || msg.type === 'QUIT') fetchRooms()
      })

      // 입장 메시지 전송
      stompClient.send('/pub/chat/message', {}, JSON.stringify({
        type: 'ENTER', roomId: room.roomId, sender: user.value.nickname, senderId: user.value.username
      }))
    }
    
    scrollToBottom()
  } catch (err) {
    console.error('Room entry failed', err)
  }
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

const logout = async () => {
  try {
    await axios.post('/api/member/logout')
    router.push('/login')
  } catch (err) {
    router.push('/login')
  }
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
    fetchRooms()
    setInterval(fetchRooms, 5000)
  } catch (err) {}
})
</script>
