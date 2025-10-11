<!-- src/App.vue -->
<script setup>
import { ref } from 'vue'
import NavBar from './components/NavBar.vue'
import Hero from './components/Hero.vue'
import Features from './components/Features.vue'
import HowItWorks from './components/HowItWorks.vue'
import FAQ from './components/FAQ.vue'
import SiteFooter from './components/SiteFooter.vue'

const dark = ref(true)
function toggleTheme() { dark.value = !dark.value; document.documentElement.dataset.theme = dark.value ? 'dark' : 'light' }
</script>

<template>
    <div class="page">
        <NavBar :dark="dark" @toggle="toggleTheme" />
        <Hero />
        <Features />
        <HowItWorks />
        <FAQ />
        <SiteFooter />
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
body,
#app {
    height: 100%;
}

body {
    margin: 0;
    background: var(--bg);
    color: var(--fg);
    font: 16px/1.6 ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial;
}

.page {
    background:
        radial-gradient(1200px 600px at 20% -10%, rgba(92, 160, 255, 0.25), transparent 60%),
        radial-gradient(900px 500px at 100% 0%, rgba(90, 255, 200, 0.18), transparent 60%),
        var(--bg);
}

section {
    padding: 64px 20px;
}

.container {
    max-width: 1120px;
    margin: 0 auto;
}

.card {
    background: var(--glass);
    border: 1px solid var(--stroke);
    border-radius: 20px;
    backdrop-filter: saturate(140%) blur(10px);
}

h1,
h2,
h3 {
    margin: 0 0 12px
}

.muted {
    color: var(--muted);
}

.btn {
    display: inline-flex;
    gap: 10px;
    align-items: center;
    justify-content: center;
    padding: 14px 18px;
    border-radius: 14px;
    border: 1px solid var(--stroke);
    background: linear-gradient(180deg, var(--brand), #4d86ff);
    color: white;
    text-decoration: none;
    font-weight: 600;
    cursor: pointer;
    transition: transform .08s ease, filter .2s ease;
}

.btn:hover {
    filter: brightness(1.05);
}

.btn:active {
    transform: translateY(1px);
}

.tag {
    font-size: 12px;
    padding: 6px 10px;
    border-radius: 999px;
    background: var(--glass);
    border: 1px solid var(--stroke);
}

.grid {
    display: grid;
    gap: 20px;
}

@media (min-width: 900px) {
    .grid-2 {
        grid-template-columns: 1.2fr 1fr;
    }

    .grid-3 {
        grid-template-columns: repeat(3, 1fr);
    }
}
</style>
