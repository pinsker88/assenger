<template>
    <section id="how" class="container">
        <h2>How it works</h2>
        <ol class="steps">
            <li><span class="num">1</span> Click “Start anonymous room”.</li>
            <li><span class="num">2</span> Share the link with your friend.</li>
            <li><span class="num">3</span> Allow camera/mic and you’re talking.</li>
        </ol>
    </section>
</template>

<style scoped>
.steps {
    list-style: none;
    padding: 0;
    margin: 12px 0 0;
    display: grid;
    gap: 12px
}

.num {
    display: inline-grid;
    place-items: center;
    width: 24px;
    height: 24px;
    border-radius: 999px;
    background: var(--brand);
    color: #fff;
    font-weight: 700;
    margin-right: 10px;
}
</style>
