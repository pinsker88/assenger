import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import mkcert from 'vite-plugin-mkcert'



export default defineConfig({
    server: {
        https: true,   // enable https
        host: true,    // listen on LAN so your iPhone can reach it
        port: 5173     // or your port
    },
    plugins: [vue(), mkcert()]
})