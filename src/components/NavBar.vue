<script setup>
const props = defineProps({ dark: Boolean })
const emit = defineEmits(['toggle'])
</script>

<template>
    <header class="nav container card">
        <div class="left">
            <img src="/logo.svg" alt="Assenger" class="logo" />
            <span class="brand">Assenger</span>
            <span class="tag">Anonymous • Private</span>
        </div>
        <nav class="right">
            <a href="#features">Features</a>
            <a href="#how">How it works</a>
            <a href="#faq">FAQ</a>
            <button class="toggle" @click="$emit('toggle')">{{ dark ? 'Light' : 'Dark' }}</button>
        </nav>
    </header>
</template>

<style scoped>
.nav {
    margin: 16px auto;
    padding: 12px 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.left {
    display: flex;
    align-items: center;
    gap: 10px;
}

.logo {
    width: 28px;
    height: 28px;
}

.brand {
    font-weight: 800;
    letter-spacing: 0.2px;
}

.right {
    display: flex;
    align-items: center;
    gap: 16px;
}

.right a {
    color: var(--fg);
    text-decoration: none;
    opacity: .85
}

.right a:hover {
    opacity: 1
}

.toggle {
    all: unset;
    padding: 8px 10px;
    border-radius: 10px;
    cursor: pointer;
    background: var(--glass);
    border: 1px solid var(--stroke)
}
</style>
