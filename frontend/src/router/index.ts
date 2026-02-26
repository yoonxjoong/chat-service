import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Chat from '../views/Chat.vue'
import Stats from '../views/Stats.vue'
import Settings from '../views/Settings.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/stats', component: Stats },
  { path: '/settings', component: Settings },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/chat', component: Chat }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
