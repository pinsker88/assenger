<!-- src/App.vue -->
<script setup>
import { ref } from 'vue'
import NavBar from './components/NavBar.vue'
import Hero from './components/Hero.vue'
import Features from './components/Features.vue'
import HowItWorks from './components/HowItWorks.vue'
import FAQ from './components/FAQ.vue'
import SiteFooter from './components/SiteFooter.vue'
import Page from './components/Page.vue'

const dark = ref(true)
function toggleTheme() { dark.value = !dark.value; document.documentElement.dataset.theme = dark.value ? 'dark' : 'light' }
</script>

<template>
    <div class="page">
        <Page />
    </div>
</template>

<style>
:root {
    --bg: #0b0f14;
    --fg: #e8eef6;
    --muted: #a7b3c4;
    --brand: #6aa8ff;
    --accent: #6effc3;
    --glass: rgba(255, 255, 255, 0.08);
    --stroke: rgba(255, 255, 255, 0.12);
}

:root[data-theme="light"] {
    --bg: #f7fbff;
    --fg: #0c1624;
    --muted: #445066;
    --brand: #2368ff;
    --accent: #12b886;
    --glass: rgba(255, 255, 255, 0.7);
    --stroke: rgba(0, 0, 0, 0.08);
}

html,
#app {
    height: 100%;
    overflow: hidden;
}

body {
    overflow: hidden;
    height: 100%;
    margin: 0;
    background: var(--bg);
    color: var(--fg);
    font: 16px/1.6 ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial;
}

.page {
    height: 100%;
    background:
        radial-gradient(1200px 800px at 20% -10%, rgba(92, 160, 255, 0.25), transparent 60%),
        radial-gradient(900px 500px at 100% 0%, rgba(90, 255, 200, 0.18), transparent 60%),
        rgb(28, 10, 10);
}

</style>
