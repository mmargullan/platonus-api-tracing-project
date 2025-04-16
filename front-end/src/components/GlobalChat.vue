<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

const stompClient = ref(null)
const messages = ref([])
const messageInput = ref("")
const userName = ref(localStorage.getItem('userFullName') || 'Anonymous')

const updateUserName = (name) => {
  userName.value = name
}

const connectWebSocket = () => {
  const socket = new SockJS(`${process.env.VUE_APP_BASE_URL}/chat`);

  stompClient.value = new Client({
    webSocketFactory: () => socket,
    debug: (str) => {
      console.log(`[STOMP] ${str}`)
    },
    reconnectDelay: 5000,
    onConnect: () => {
      console.log("✅ WebSocket connected")
      stompClient.value.subscribe("/topic/messages", (message) => {
        const data = JSON.parse(message.body)
        messages.value.push(`${data.from}: ${data.text}`)
      })
    },
    onStompError: (frame) => {
      console.error("❌ STOMP error", frame.headers['message'], frame.body)
    }
  })
  stompClient.value.activate()
}

const sendMessage = () => {
    console.log(userName.value)
  if (!stompClient.value || !stompClient.value.connected || messageInput.value.trim() === "") return

  const message = {
    from: userName.value, 
    text: messageInput.value
  }

  stompClient.value.publish({
    destination: "/app/sendMessage",
    body: JSON.stringify(message)
  })

  messageInput.value = ""
}

const handleKeyDown = (event) => {
  if (event.key === "Enter") sendMessage()
}

onMounted(() => {
  updateUserName(userName.value)
  connectWebSocket()
})

onBeforeUnmount(() => {
  if (stompClient.value && stompClient.value.connected) {
    stompClient.value.deactivate()
  }
})
</script>



<template>
    <main class="page-wrapper">
      <div class="chat-container">
        <div class="chat-header">Global Chat</div>
        <div class="chat-messages">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            class="chat-message"
          >
            {{ msg }}
          </div>
        </div>
        <div class="chat-input">
          <input
            v-model="messageInput"
            @keydown="handleKeyDown"
            placeholder="Type your message..."
          />
          <button @click="sendMessage">Send</button>
        </div>
      </div>
    </main>
  </template>
  

  <style scoped>
  .page-wrapper {
    height: 100vh;
    width: 100vw;
    background: radial-gradient(ellipse at top left, #1c0f2d, #120820 60%);
    background-repeat: no-repeat;
    background-size: cover;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
    overflow: hidden;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  }
  
  .chat-container {
    background-color: rgba(35, 20, 50, 0.85);
    color: #f4eaff;
    width: 100%;
    max-width: 720px;
    height: 100%;
    max-height: 90vh;
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 0 20px rgba(170, 95, 255, 0.5);
    display: flex;
    flex-direction: column;
    border: 1px solid #3a1a5e;
    backdrop-filter: blur(8px);
  }
  
  .chat-header {
    padding: 20px;
    font-size: 24px;
    background: linear-gradient(to right, #681a9c, #a637c1);
    color: #fceaff;
    font-weight: bold;
    text-align: center;
    border-bottom: 1px solid #3d235a;
    letter-spacing: 1px;
  }
  
  .chat-messages {
    padding: 16px;
    flex: 1;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 10px;
    background-color: transparent;
  }
  
  .chat-message {
    background-color: rgba(110, 58, 150, 0.3);
    padding: 10px 14px;
    border-radius: 12px;
    word-break: break-word;
    transition: background-color 0.3s;
    border: 1px solid rgba(255, 255, 255, 0.05);
  }
  
  .chat-message:hover {
    background-color: rgba(139, 76, 184, 0.4);
  }
  
  .chat-input {
    display: flex;
    border-top: 1px solid #4a2e70;
    background-color: rgba(44, 24, 68, 0.9);
    padding: 12px;
  }
  
  .chat-input input {
    flex: 1;
    padding: 12px 14px;
    border: none;
    background: #2f1e4a;
    color: #f8eaff;
    font-size: 15px;
    border-radius: 8px;
    outline: none;
    caret-color: #d8a6ff;
    margin-right: 8px;
  }
  
  .chat-input input::placeholder {
    color: #b99ccf;
  }
  
  .chat-input button {
    background-color: #9b34cc;
    border: none;
    padding: 12px 16px;
    color: white;
    font-weight: bold;
    border-radius: 8px;
    cursor: pointer;
    transition: background 0.3s ease;
  }
  
  .chat-input button:hover {
    background-color: #b455e1;
  }
  </style>  