import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Chat from '../views/Chat.vue'
import Stats from '../views/Stats.vue'
import Feed from '../views/Feed.vue'
import Settings from '../views/Settings.vue'
import PrivacyPolicy from '../views/PrivacyPolicy.vue'
import TermsOfService from '../views/TermsOfService.vue'

import axios from 'axios'

const routes = [
  { path: '/', component: Home, meta: { requiresAuth: true } },
  { path: '/stats', component: Stats, meta: { requiresAuth: true } },
  { path: '/feed', component: Feed, meta: { requiresAuth: true } },
  { path: '/settings', component: Settings, meta: { requiresAuth: true } },
  { path: '/privacy', component: PrivacyPolicy },
  { path: '/terms', component: TermsOfService },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  // { path: '/chat', component: Chat }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  // 로그인 여부 체크
  let isAuthenticated = false
  try {
    const res = await axios.get('/api/user/me')
    isAuthenticated = !!res.data.username
  } catch (err) {
    isAuthenticated = false
  }

  // 인증이 필요한 페이지에 접근할 때
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } 
  // 이미 로그인된 유저가 로그인/가입 페이지에 접근할 때
  else if ((to.path === '/login' || to.path === '/register') && isAuthenticated) {
    next('/')
  }
  else {
    next()
  }
})

export default router
