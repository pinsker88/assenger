<script setup>
function randomRoom() {
  // Simple pseudo room path. Replace later with real backend-generated IDs.
  const id = Math.random().toString(36).slice(2, 8);
  window.location.href = `/room/${id}`; // for now this will 404; it's just CTA wiring
}
</script>

<template>
  <section class="container hero">
    <div class="grid grid-2">
      <div class="copy">
        <h1>Start a private call in seconds.</h1>
        <p class="muted">
          Assenger lets you launch anonymous video chats or text rooms. No phone number.
          No signup. Share a link, you’re in.
        </p>
        <div style="display: flex; gap: 12px; align-items: center; margin: 16px 0 6px">
          <button class="btn" @click="randomRoom">
            <span>Start anonymous room</span>
          </button>
          <a
            class="btn ghost"
            href="#features"
            style="background: transparent; color: var(--fg)"
            >See features</a
          >
        </div>
        <div class="mini">
          <span class="tag">End-to-end mindset</span>
          <span class="tag">No phone number</span>
          <span class="tag">One-time links</span>
        </div>
      </div>

      <!-- Camera tile mock (FaceTime-like) -->
      <div class="tile card">
        <div class="status">
          <span class="dot"></span>
          <span>Camera preview</span>
        </div>
        <div class="video-placeholder">
          <div class="bubble"></div>
          <div class="bubble small"></div>
        </div>
        <div class="controls">
          <button title="Mute" class="ctl">🎙️</button>
          <button title="Camera" class="ctl">🎥</button>
          <button title="Share" class="ctl">🖥️</button>
          <button title="End" class="ctl end">⛔</button>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.hero {
  padding-top: 32px;
  padding-bottom: 32px;
}
.copy h1 {
  font-size: clamp(28px, 4vw, 48px);
  line-height: 1.15;
  margin-bottom: 12px;
}
.copy .mini {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 10px;
}
.btn.ghost {
  border: 1px solid var(--stroke);
}
.tile {
  position: relative;
  aspect-ratio: 16/10;
  overflow: hidden;
}
.status {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--glass);
  border: 1px solid var(--stroke);
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--accent);
  box-shadow: 0 0 10px var(--accent);
}
.video-placeholder {
  position: absolute;
  inset: 0;
  background: radial-gradient(
      800px 400px at 0% 0%,
      rgba(110, 255, 195, 0.12),
      transparent 60%
    ),
    radial-gradient(800px 400px at 100% 100%, rgba(106, 168, 255, 0.2), transparent 60%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.06), rgba(0, 0, 0, 0.08));
}
.bubble,
.bubble.small {
  position: absolute;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid var(--stroke);
  backdrop-filter: blur(6px) saturate(140%);
  animation: float 6s ease-in-out infinite;
}
.bubble {
  width: 180px;
  height: 120px;
  right: 8%;
  bottom: 12%;
}
.bubble.small {
  width: 120px;
  height: 80px;
  left: 6%;
  top: 18%;
  animation-duration: 7.5s;
}
@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}
.controls {
  position: absolute;
  left: 50%;
  bottom: 14px;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  padding: 8px;
  border-radius: 999px;
  background: var(--glass);
  border: 1px solid var(--stroke);
}
.ctl {
  all: unset;
  cursor: pointer;
  width: 38px;
  height: 38px;
  display: grid;
  place-items: center;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid var(--stroke);
}
.ctl.end {
  background: #ff5b5b;
  color: #fff;
  border-color: rgba(0, 0, 0, 0.15);
}
</style>
