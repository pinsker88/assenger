<template>
  <section class="heart-wrapper no-select">
    <div
      class="heart-shell"
      role="button"
      tabindex="0"
      @click="startLocal"
      @keydown.enter.prevent="startLocal"
      @keydown.space.prevent="startLocal"
      aria-label="Start camera"
    >
      <img src="/logo.svg" alt="Heart" class="heart" />
    </div>

    <!-- Local preview (required on iOS) -->
    <video
      id="localVideo"
      ref="localVideoEl"
      autoplay
      playsinline
      muted
      style="position: fixed; bottom: 16px; right: 16px; width: 160px; border-radius: 12px"
    ></video>
  </section>

  <section class="webrtc-panel">
    <video
      id="remoteVideo"
      ref="remoteVideoEl"
      autoplay
      playsinline
      style="width: 100%; max-width: 720px; border-radius: 12px"
    ></video>

    <div class="controls">
      <button @click="createOffer" :disabled="!localReady || hasLocalDesc">Create Offer</button>
      <button @click="acceptOfferAndMakeAnswer" :disabled="!localReady">Accept Offer & Create Answer</button>
      <button @click="acceptAnswer" :disabled="!localReady || !pc">Accept Answer</button>
      <button @click="addCandidate" :disabled="!localReady || !pc">Add Candidate</button>
    </div>

    <div class="areas">
      <label>
        Local (copy this out)
        <textarea v-model="localSignal" readonly spellcheck="false"></textarea>
      </label>
      <label>
        Remote (paste here)
        <textarea v-model="remoteSignal" spellcheck="false" placeholder="Paste JSON offer/answer/candidate here"></textarea>
      </label>
    </div>
  </section>
</template>

<script setup>
import { ref } from "vue";

let pc = null;
let localStream = null;

const localVideoEl = ref(null);
const remoteVideoEl = ref(null);

const localReady = ref(false);
const hasLocalDesc = ref(false);

const localSignal = ref("");   // JSON you copy out
const remoteSignal = ref("");  // JSON you paste in

const rtcConfig = {
  iceServers: [
    // Works on LANs. For internet/NAT traversal, add a TURN server here.
    { urls: "stun:stun.l.google.com:19302" },
  ],
};

const startLocal = async () => {
  try {
    if (!navigator.mediaDevices?.getUserMedia) {
      alert("Camera not supported in this browser.");
      return;
    }
    if (!localVideoEl.value) return;

    localStream = await navigator.mediaDevices.getUserMedia({
      video: true,
      audio: true, // set false if you don't need microphone
    });

    localVideoEl.value.srcObject = localStream;
    await localVideoEl.value.play().catch(() => {});
    localReady.value = true;

    ensurePeer();
  } catch (err) {
    console.error("getUserMedia error:", err);
    alert("Could not access camera/microphone. Check permissions and HTTPS.");
  }
};

function ensurePeer() {
  if (pc) return;

  pc = new RTCPeerConnection(rtcConfig);

  // When remote tracks arrive, show them.
  pc.ontrack = (evt) => {
    if (!remoteVideoEl.value) return;
    const [stream] = evt.streams;
    if (stream) {
      remoteVideoEl.value.srcObject = stream;
    } else {
      // Fallback if streams array is empty
      const remote = remoteVideoEl.value.srcObject || new MediaStream();
      remote.addTrack(evt.track);
      remoteVideoEl.value.srcObject = remote;
    }
  };

  // Optional: show ICE state in console
  pc.oniceconnectionstatechange = () => {
    console.log("ICE state:", pc.iceConnectionState);
  };

  // Gather ICE candidates (baked into SDP on most browsers after setLocalDescription).
  pc.onicecandidate = (e) => {
    // If your browser emits trickle candidates, you can copy them separately:
    if (e.candidate) {
      localSignal.value = JSON.stringify({ candidate: e.candidate }, null, 2);
    }
  };

  // Add local tracks
  if (localStream) {
    for (const track of localStream.getTracks()) {
      pc.addTrack(track, localStream);
    }
  }
}

// Caller flow
const createOffer = async () => {
  try {
    ensurePeer();
    const offer = await pc.createOffer({
      offerToReceiveAudio: true,
      offerToReceiveVideo: true,
    });
    await pc.setLocalDescription(offer);
    hasLocalDesc.value = true;

    // Wait for ICE gathering to complete to produce a "complete" SDP for easy copy/paste.
    await waitForIceGatheringComplete(pc);
    localSignal.value = JSON.stringify({ sdp: pc.localDescription }, null, 2);
  } catch (e) {
    console.error(e);
    alert("Failed to create offer.");
  }
};

// Answerer flow: takes a remote offer string from textarea, sets it, then creates an answer.
const acceptOfferAndMakeAnswer = async () => {
  try {
    ensurePeer();
    const payload = safeParse(remoteSignal.value);
    if (!payload?.sdp) {
      alert("Paste a valid OFFER (JSON with sdp).");
      return;
    }
    await pc.setRemoteDescription(payload.sdp);

    const answer = await pc.createAnswer();
    await pc.setLocalDescription(answer);

    await waitForIceGatheringComplete(pc);
    localSignal.value = JSON.stringify({ sdp: pc.localDescription }, null, 2);
  } catch (e) {
    console.error(e);
    alert("Failed to accept offer & create answer.");
  }
};

// Caller takes answer from textarea and sets as remote description.
const acceptAnswer = async () => {
  try {
    const payload = safeParse(remoteSignal.value);
    if (!payload?.sdp) {
      alert("Paste a valid ANSWER (JSON with sdp).");
      return;
    }
    await pc.setRemoteDescription(payload.sdp);
  } catch (e) {
    console.error(e);
    alert("Failed to accept answer.");
  }
};

// Optional: if your browser trickles ICE candidates after the SDP step
const addCandidate = async () => {
  try {
    const payload = safeParse(remoteSignal.value);
    if (!payload?.candidate) {
      alert("Paste a valid CANDIDATE object.");
      return;
    }
    await pc.addIceCandidate(payload.candidate);
  } catch (e) {
    console.error(e);
  }
};

function safeParse(txt) {
  try { return JSON.parse(txt); } catch { return null; }
}

function waitForIceGatheringComplete(pc) {
  if (pc.iceGatheringState === "complete") return Promise.resolve();
  return new Promise((resolve) => {
    const check = () => {
      if (pc.iceGatheringState === "complete") {
        pc.removeEventListener("icegatheringstatechange", check);
        resolve();
      }
    };
    pc.addEventListener("icegatheringstatechange", check);
  });
}
</script>

<style scoped>
.no-select {
  user-select: none;
  -webkit-user-drag: none;
}

/* Center the heart */
.heart-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50vh;
  width: 100vw;
  margin: 0;
  padding: 0;
}

/* Container for the heart + waves */
.heart-shell {
  position: relative;
  display: grid;
  place-items: center;
  width: 15vw;
  max-width: 200px;
  min-width: 100px;

  touch-action: manipulation;
  -webkit-tap-highlight-color: transparent;
}

/* Heart image */
.heart {
  width: 100%;
  display: block;
  background: transparent;
  filter: drop-shadow(0 0 15px #1e90ff);
  animation: heartbeat 1.2s infinite cubic-bezier(0.215, 0.61, 0.355, 1);
  will-change: transform, filter;
  cursor: pointer;
  user-select: none;
  outline: none;
}

/* Ripple waves */
.heart-shell::before,
.heart-shell::after {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 2px solid rgba(30, 144, 255, 0.8);
  box-shadow: 0 0 18px rgba(30, 144, 255, 0.6);
  transform: scale(1);
  opacity: 0;
  pointer-events: none;
  animation: ripple 1.2s infinite cubic-bezier(0.215, 0.61, 0.355, 1);
  will-change: transform, opacity;
}
.heart-shell::after { animation-delay: 0.6s; }

@keyframes heartbeat {
  0% { transform: scale(0.95); filter: drop-shadow(0 0 8px #1e90ff); }
  5% { transform: scale(1.1); filter: drop-shadow(0 0 20px #1e90ff); }
  39% { transform: scale(0.85); filter: drop-shadow(0 0 5px #1e90ff); }
  45% { transform: scale(1); filter: drop-shadow(0 0 15px #1e90ff); }
  60% { transform: scale(0.95); filter: drop-shadow(0 0 10px #1e90ff); }
  100% { transform: scale(0.9); filter: drop-shadow(0 0 6px #1e90ff); }
}

@keyframes ripple {
  0% { transform: scale(1); opacity: 0.55; border-width: 10px; }
  40% { opacity: 0.35; }
  70% { opacity: 0.18; }
  100% { transform: scale(5); opacity: 0; border-width: 1px; }
}

/* WebRTC helper UI */
.webrtc-panel {
  max-width: 760px;
  margin: 24px auto;
  padding: 16px;
  display: grid;
  gap: 12px;
}
.controls {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.controls button {
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
}
.controls button:disabled {
  opacity: 0.5; cursor: not-allowed;
}
.areas {
  display: grid;
  gap: 12px;
}
.areas textarea {
  width: 100%;
  min-height: 140px;
  border-radius: 8px;
  border: 1px solid #ddd;
  padding: 8px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
  font-size: 12px;
  line-height: 1.4;
}
.areas label { display: grid; gap: 6px; }
</style>
