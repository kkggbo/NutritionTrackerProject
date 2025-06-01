import './assets/main.scss'

import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from '@/router'
import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-persistedstate-plugin'

import App from './App.vue'

const app = createApp(App)
const persist = createPersistedState()
const pinia = createPinia()
pinia.use(persist)
app.use(pinia)
app.use(ElementPlus)
app.use(router)
app.mount('#app')
