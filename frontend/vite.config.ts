import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
      '/chat': 'http://localhost:8080',
      '/ws-stomp': {
        target: 'ws://localhost:8080',
        ws: true
      },
      '/login.html': 'http://localhost:8080',
      '/logout': 'http://localhost:8080'
    }
  }
})
