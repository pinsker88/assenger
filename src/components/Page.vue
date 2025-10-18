<template>
  <section class="heart-wrapper">
    <div class="heart-shell">
      <img src="/logo.svg" alt="Heart" class="heart" />
    </div>
  </section>
</template>

<style scoped>
/* Center the heart */
.heart-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  margin: 0;
  padding: 0;
}

/* Container for the heart + waves */
.heart-shell {
  position: relative;
  display: grid;
  place-items: center;
  /* This controls the base heart size responsively */
  width: 15vw;
  max-width: 200px;
  min-width: 100px;
}

/* Heart image */
.heart {
  width: 100%;
  display: block;
  background: transparent;
  filter: drop-shadow(0 0 15px #1E90FF);
  animation: heartbeat 1.2s infinite cubic-bezier(0.215, 0.61, 0.355, 1);
  will-change: transform, filter;
}

/* Ripple waves: two rings, staggered, expanding + fading */
.heart-shell::before,
.heart-shell::after {
  content: "";
  position: absolute;
  inset: 0;                /* starts same size as shell (i.e., heart) */
  border-radius: 50%;
  border: 2px solid rgba(30, 144, 255, 0.8); /* #1E90FF */
  box-shadow: 0 0 18px rgba(30, 144, 255, 0.6);
  transform: scale(1);
  opacity: 0;
  pointer-events: none;
  animation: ripple 1.2s infinite cubic-bezier(0.215, 0.61, 0.355, 1);
  will-change: transform, opacity;
}

/* second wave launches halfway through, for continuous feel */
.heart-shell::after {
  animation-delay: 0.6s; /* half of 1.2s heartbeat */
}

/* Heartbeat animation (unchanged, but kept for sync) */
@keyframes heartbeat {
  0%   { transform: scale(0.95); filter: drop-shadow(0 0 8px #1E90FF); }
  5%   { transform: scale(1.1);  filter: drop-shadow(0 0 20px #1E90FF); }
  39%  { transform: scale(0.85); filter: drop-shadow(0 0 5px #1E90FF); }
  45%  { transform: scale(1.0);  filter: drop-shadow(0 0 15px #1E90FF); }
  60%  { transform: scale(0.95); filter: drop-shadow(0 0 10px #1E90FF); }
  100% { transform: scale(0.9);  filter: drop-shadow(0 0 6px #1E90FF); }
}

/* Ripple rings: start at heart edge, expand and wane */
@keyframes ripple {
  0% {
    transform: scale(1);      /* start at heart size */
    opacity: 0.55;
    border-width: 2px;
  }
  40% {
    opacity: 0.35;
  }
  70% {
    opacity: 0.18;
  }
  100% {
    transform: scale(3.2);    /* how far the wave travels */
    opacity: 0;
    border-width: 1px;        /* subtle thinning as it fades */
  }
}
</style>
