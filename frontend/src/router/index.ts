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
  if (to.meta.requiresAuth) {
    try {
      await axios.get('/api/user/me')
      next()
    } catch (err) {
      next('/login')
    }
  } else {
    next()
  }
})

export default router
