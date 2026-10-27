import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      // 后端 context-path 就是 /api，所以这里原样转发，不做 rewrite
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})
