import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      '/api': { // 获取路径中包含‘api’的请求
        target: 'http://localhost:8080', // 代理的目标地址
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''), // 重写路径，用空字符代替‘api’
      }
    },
    host: true, // 必须开启 host 才能外部访问
    port: 5173, // 你的端口
    allowedHosts: ['.ngrok-free.app'] // 允许 ngrok 的域名访问
  }
})
