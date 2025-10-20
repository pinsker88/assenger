<template>
  <!-- Start screen with beating heart -->
  <section v-if="!callStarted" class="start-screen no-select">
    <div
      class="heart-shell"
      role="button"
      tabindex="0"
      @click="startHostCall"
      @keydown.enter.prevent="startHostCall"
      @keydown.space.prevent="startHostCall"
      aria-label="Start call"
    >
      <img src="/logo.svg" alt="Heart" class="heart" />
    </div>
  </section>

  <!-- Call screen with draggable video panels -->
  <section v-else class="call-screen">
    <!-- Remote video window -->
    <div
      class="video-container remote-video"
      :style="{ top: remotePos.y + 'px', left: remotePos.x + 'px' }"
      @mousedown.prevent="startDrag('remote', $event)"
    >
      <video ref="remoteVideoEl" autoplay playsinline class="video"></video>
    </div>
    <!-- Local video thumb -->
    <div
      class="video-container local-video"
      :style="{ top: localPos.y + 'px', left: localPos.x + 'px' }"
      @mousedown.prevent="startDrag('local', $event)"
    >
      <video ref="localVideoEl" autoplay playsinline muted class="video"></video>
    </div>
    <!-- Share link bar -->
    <div class="share-link">
      <span>Share this link:</span>
      <input type="text" :value="shareLink" readonly @click="copyLink" />
    </div>
  </section>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";

// Flag to toggle start screen vs call UI
const callStarted = ref(false);

// Room ID and shareable URL
const roomId = ref("");
const shareLink = ref("");

// WebRTC variables
const pc = ref(null);
const ws = ref(null);
const localStream = ref(null);
const remoteStream = ref(new MediaStream());

// DOM refs for video elements
const localVideoEl = ref(null);
const remoteVideoEl = ref(null);

// Draggable positions for video windows
const localPos = reactive({ x: 20, y: 20 });
const remotePos = reactive({ x: 80, y: 80 });
const dragState = reactive({ which: null, offsetX: 0, offsetY: 0 });

// STUN servers for NAT traversal; add TURN here if needed
const stunServers = [{ urls: "stun:stun.l.google.com:19302" }];

/**
 * Generate a pseudo‑random room identifier. This is used when the host
 * starts a call via the heart. A simple alphanumeric string suffices
 * for demonstration purposes.
 */
function generateRoomId() {
  return Math.random().toString(36).substring(2, 10);
}

/**
 * Start a call as the host: obtain media, set up a peer connection,
 * create a new room, and connect to the signaling server. This hides
 * the start screen and shows the call UI with draggable video windows.
 */
async function startHostCall() {
  // Transition to call UI
  callStarted.value = true;

  // Generate a unique room ID and set the share URL
  roomId.value = generateRoomId();
  shareLink.value = `${location.origin}/room/${roomId.value}`;

  // Acquire local media
  await getLocalMedia();

  // Prepare the peer connection and WebSocket signaling
  initPeerConnection();
  connectWebSocket();
}

/**
 * If the user navigates directly to /room/:id, join that room
 * automatically by starting media and connecting to the signaling server.
 * This runs only once on mount.
 */
onMounted(async () => {
  const parts = location.pathname.split("/").filter(Boolean);
  if (parts[0] === "room" && parts[1]) {
    callStarted.value = true;
    roomId.value = parts[1];
    shareLink.value = `${location.origin}${location.pathname}`;
    await getLocalMedia();
    initPeerConnection();
    connectWebSocket();
  }
});

/**
 * Acquire the user's camera and microphone. Attach the resulting
 * MediaStream to the local video element. Prompt for permissions if
 * necessary. If audio is not desired, set audio: false.
 */
async function getLocalMedia() {
  if (!navigator.mediaDevices?.getUserMedia) {
    alert("Camera not supported in this browser.");
    return;
  }
  const stream = await navigator.mediaDevices.getUserMedia({
    video: true,
    audio: true,
  });
  localStream.value = stream;
  if (localVideoEl.value) {
    localVideoEl.value.srcObject = stream;
    await localVideoEl.value.play().catch(() => {});
  }
}

/**
 * Initialize the RTCPeerConnection, add local tracks, and set up
 * handlers for incoming tracks and ICE candidates.
 */
function initPeerConnection() {
  const config = { iceServers: stunServers };
  pc.value = new RTCPeerConnection(config);
  // Add local media tracks
  if (localStream.value) {
    localStream.value.getTracks().forEach((t) =>
      pc.value.addTrack(t, localStream.value)
    );
  }
  // When remote tracks arrive, attach them to the remote MediaStream
  pc.value.ontrack = (event) => {
    event.streams[0].getTracks().forEach((t) => {
      remoteStream.value.addTrack(t);
    });
    if (remoteVideoEl.value) {
      remoteVideoEl.value.srcObject = remoteStream.value;
    }
  };
  // Forward ICE candidates to the signaling server
  pc.value.onicecandidate = (event) => {
    if (event.candidate) {
      sendMessage({ type: "ice", candidate: event.candidate });
    }
  };
}

/**
 * Establish a WebSocket connection to the Spring Boot signaling server,
 * join the current room, and handle incoming signaling messages. The
 * server will send a peer-join message when a second client arrives.
 */
function connectWebSocket() {
  const protocol = location.protocol === "https:" ? "wss" : "ws";
  ws.value = new WebSocket(
    `${protocol}://${location.host}/ws?room=${encodeURIComponent(
      roomId.value
    )}`
  );
  ws.value.onopen = () => {
    // No action needed; server will broadcast peer-join when a second peer connects
  };
  ws.value.onmessage = async (event) => {
    const msg = JSON.parse(event.data);
    switch (msg.type) {
      case "peer-join":
        // As the existing peer, create and send an offer
        {
          const offer = await pc.value.createOffer();
          await pc.value.setLocalDescription(offer);
          sendMessage({ type: "offer", offer });
        }
        break;
      case "offer":
        // As the joining peer, set remote description and send an answer
        {
          await pc.value.setRemoteDescription(
            new RTCSessionDescription(msg.offer)
          );
          const answer = await pc.value.createAnswer();
          await pc.value.setLocalDescription(answer);
          sendMessage({ type: "answer", answer });
        }
        break;
      case "answer":
        // Finalize handshake by setting remote description
        await pc.value.setRemoteDescription(
          new RTCSessionDescription(msg.answer)
        );
        break;
      case "ice":
        // Append remote ICE candidate
        try {
          await pc.value.addIceCandidate(msg.candidate);
        } catch {
          // Silently ignore errors
        }
        break;
    }
  };
  ws.value.onerror = (e) => {
    console.warn("WebSocket error", e);
  };
}

/**
 * Send a signaling payload over the WebSocket if the connection is open.
 */
function sendMessage(payload) {
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    ws.value.send(JSON.stringify(payload));
  }
}

/**
 * Copy the generated share link to the clipboard when the user clicks
 * inside the text input. If clipboard access fails, silently ignore.
 */
function copyLink() {
  navigator.clipboard.writeText(shareLink.value).catch(() => {});
}

/**
 * Begin dragging a video window. The `which` argument denotes whether
 * the local or remote element is being moved. Record the pointer offset
 * to maintain relative positioning during the drag.
 */
function startDrag(which, event) {
  dragState.which = which;
  const pos = which === "local" ? localPos : remotePos;
  dragState.offsetX = event.clientX - pos.x;
  dragState.offsetY = event.clientY - pos.y;
  window.addEventListener("mousemove", onDrag);
  window.addEventListener("mouseup", endDrag);
}

/**
 * Update the position of the currently dragged element based on the
 * pointer coordinates. This function is bound to the window's mousemove
 * event.
 */
function onDrag(event) {
  if (!dragState.which) return;
  const pos = dragState.which === "local" ? localPos : remotePos;
  pos.x = event.clientX - dragState.offsetX;
  pos.y = event.clientY - dragState.offsetY;
}

/**
 * End a dragging operation and remove the global listeners. This function
 * is bound to the window's mouseup event.
 */
function endDrag() {
  dragState.which = null;
  window.removeEventListener("mousemove", onDrag);
  window.removeEventListener("mouseup", endDrag);
}
</script>

<style scoped>
/* Prevent text selection and dragging of elements */
.no-select {
  user-select: none;
  -webkit-user-drag: none;
}

/* Start screen occupies the full viewport to center the heart */
.start-screen {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 100vh;
  margin: 0;
  padding: 0;
}

/* Heart container: retain size and ripple effect */
.heart-shell {
  position: relative;
  display: grid;
  place-items: center;
  width: 15vw;
  max-width: 200px;
  min-width: 100px;
  touch-action: manipulation;
  -webkit-tap-highlight-color: transparent;
  cursor: pointer;
}

/* Heart image with beating animation */
.heart {
  width: 100%;
  display: block;
  background: transparent;
  filter: drop-shadow(0 0 15px #1e90ff);
  animation: heartbeat 1.2s infinite cubic-bezier(0.215, 0.61, 0.355, 1);
  will-change: transform, filter;
  user-select: none;
  outline: none;
}

/* Ripple waves behind the heart */
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
.heart-shell::after {
  animation-delay: 0.6s;
}

@keyframes heartbeat {
  0% {
    transform: scale(0.95);
    filter: drop-shadow(0 0 8px #1e90ff);
  }
  5% {
    transform: scale(1.1);
    filter: drop-shadow(0 0 20px #1e90ff);
  }
  39% {
    transform: scale(0.85);
    filter: drop-shadow(0 0 5px #1e90ff);
  }
  45% {
    transform: scale(1);
    filter: drop-shadow(0 0 15px #1e90ff);
  }
  60% {
    transform: scale(0.95);
    filter: drop-shadow(0 0 10px #1e90ff);
  }
  100% {
    transform: scale(0.9);
    filter: drop-shadow(0 0 6px #1e90ff);
  }
}

@keyframes ripple {
  0% {
    transform: scale(1);
    opacity: 0.55;
    border-width: 10px;
  }
  40% {
    opacity: 0.35;
  }
  70% {
    opacity: 0.18;
  }
  100% {
    transform: scale(5);
    opacity: 0;
    border-width: 1px;
  }
}

/* Call screen occupies the full viewport and positions elements absolutely */
.call-screen {
  position: relative;
  width: 100vw;
  height: 100vh;
  background: #000;
  overflow: hidden;
}

/* Base style for draggable video containers */
.video-container {
  position: absolute;
  border-radius: 10px;
  overflow: hidden;
  cursor: move;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
}

/* Remote video is larger; constrain maximum size for responsiveness */
.remote-video {
  width: 70vw;
  height: 60vh;
  max-width: 960px;
  max-height: 540px;
}

/* Local video is smaller; appears as a thumbnail */
.local-video {
  width: 20vw;
  height: 15vh;
  max-width: 320px;
  max-height: 240px;
}

/* Video element fills its container and maintains aspect ratio */
.video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: #000;
}

/* Share link bar anchored near the bottom center */
.share-link {
  position: absolute;
  left: 50%;
  bottom: 20px;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.85);
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 14px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.share-link input {
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
  width: 200px;
  cursor: pointer;
}
</style>
