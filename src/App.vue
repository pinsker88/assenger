<script setup>
import { ref } from 'vue'
const count = ref(0)
</script>


<template>
    <main
        style="max-width:720px;margin:64px auto;font:16px/1.6 system-ui, -apple-system, Segoe UI, Roboto, Ubuntu, Cantarell, Noto Sans, Helvetica, Arial, 'Apple Color Emoji', 'Segoe UI Emoji';">
        <h1 style="font-size:32px;margin:0 0 8px;">Hello from Vue + Nginx 🚀</h1>
        <p style="margin:0 0 24px;">This site is built with Vue 3 & served by Nginx in Docker.</p>


        <button style="padding:10px 16px;border-radius:10px;border:1px solid #ddd;cursor:pointer;" @click="count++">
            You clicked {{ count }} times
        </button>


        <p style="margin-top:24px;color:#666;">Edit <code>src/App.vue</code> and rebuild to see changes.</p>
    </main>
</template>


<style>
html,
body {
    margin: 0;
}

button:hover {
    filter: brightness(0.95);
}
</style>