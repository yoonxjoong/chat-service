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

const routes = [
  { path: '/', component: Home },
  { path: '/stats', component: Stats },
  { path: '/feed', component: Feed },
  { path: '/settings', component: Settings },
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

export default router
