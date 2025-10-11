<template>
    <section id="faq" class="container card" style="padding: 20px;">
        <h2>FAQ</h2>
        <details>
            <summary>Do I need an account?</summary>
            <p>No. Rooms are anonymous—share a link to join.</p>
        </details>
        <details>
            <summary>Is it encrypted?</summary>
            <p>We design for end-to-end media paths (WebRTC). More details will be in our whitepaper.</p>
        </details>
        <details>
            <summary>Does it store messages?</summary>
            <p>Default is ephemeral. We’ll offer “burn after reading” modes.</p>
        </details>
    </section>
</template>

<style scoped>
details {
    margin: 10px 0;
    padding: 10px 0;
    border-top: 1px dashed var(--stroke);
}

details:first-of-type {
    border-top: 0
}

summary {
    cursor: pointer;
    font-weight: 600
}
</style>
