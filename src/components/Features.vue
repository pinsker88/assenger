<template>
    <section id="features" class="container">
        <h2>Why Assenger?</h2>
        <p class="muted">Private by default. Simple by design.</p>
        <div class="grid grid-3" style="margin-top: 16px;">
            <article class="card feat">
                <h3>Anonymous rooms</h3>
                <p>No signup or phone numbers. Share a one-time link and you’re in.</p>
            </article>
            <article class="card feat">
                <h3>Video or text</h3>
                <p>Jump into video instantly or keep it to low-friction text chat.</p>
            </article>
            <article class="card feat">
                <h3>Privacy-first</h3>
                <p>Ephemeral by default. Your conversations aren’t content.</p>
            </article>
        </div>
    </section>
</template>

<style scoped>
h2 {
    font-size: clamp(22px, 3vw, 32px)
}

.feat {
    padding: 18px;
    border-radius: 16px;
}

.feat h3 {
    margin: 4px 0 6px;
    font-size: 18px;
}
</style>
