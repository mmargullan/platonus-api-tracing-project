<template class>
  <div>
    <NavBar />

    <div class="stars-background">
      <div
          v-for="(star, i) in stars"
          :key="i"
          class="star"
          :style="star.style"
      ></div>
    </div>

    <div class="chat-container">
      <div class="chat-header">
        <h2 class="chat-title">Global Chat</h2>
      </div>

      <div class="chat-messages">
        <div
            v-for="(msg, index) in messages"
            :key="index"
            class="chat-message"
            :class="{ 'self-message': msg.from === userName }"
        >
          <div class="message-header">
            <span class="message-author">{{ msg.from }}</span>
            <span v-if="msg.date" class="message-time">{{ formatDate(msg.date) }}</span>
          </div>
          <div class="message-content">{{ msg.text }}</div>
        </div>
      </div>

      <div class="chat-input">
        <input
            v-model="messageInput"
            @keydown="handleKeyDown"
            placeholder="Type your message..."
        />
        <button @click="sendMessage">
          <v-icon color="white">mdi-send</v-icon>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import axios from 'axios';
import NavBar from '@/components/NavBar.vue';

const stompClient = ref(null);
const messages = ref([]);
const messageInput = ref('');
const userName = ref(localStorage.getItem('userFullName') || 'Anonymous');
const stars = ref([]);

const updateUserName = (name) => {
  userName.value = name;
};

const connectWebSocket = () => {
  const socket = new SockJS(`${process.env.VUE_APP_BASE_URL}/chat-api/chat`);

  stompClient.value = new Client({
    webSocketFactory: () => socket,
    debug: (str) => {
      console.log(`[STOMP] ${str}`);
    },
    reconnectDelay: 5000,
    onConnect: () => {
      console.log("✅ WebSocket connected");
      stompClient.value.subscribe("/topic/messages", (message) => {
        const data = JSON.parse(message.body);
        console.log("Received message:", data);
        messages.value.push({
          from: data.from,
          text: data.text,
          date: data.date || new Date().toISOString()
        });
        // Auto scroll to bottom when new message arrives
        setTimeout(() => scrollToBottom(), 50);
      });
    },
    onStompError: (frame) => {
      console.error("❌ STOMP error", frame.headers['message'], frame.body);
    }
  });
  stompClient.value.activate();
};

const formatDate = (dateString) => {
  if (!dateString) return "";

  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
      console.error("Invalid date:", dateString);
      return "";
    }
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  } catch (error) {
    console.error("Error formatting date:", error);
    return "";
  }
};

const sendMessage = () => {
  if (!stompClient.value || !stompClient.value.connected || messageInput.value.trim() === "") return;

  const currentDate = new Date().toISOString();

  const message = {
    from: userName.value,
    text: messageInput.value,
    date: currentDate
  };

  stompClient.value.publish({
    destination: "/app/sendMessage",
    body: JSON.stringify(message)
  });

  messageInput.value = "";
};

const handleKeyDown = (event) => {
  if (event.key === "Enter") sendMessage();
};

const scrollToBottom = () => {
  const chatMessages = document.querySelector('.chat-messages');
  if (chatMessages) {
    chatMessages.scrollTop = chatMessages.scrollHeight;
  }
};

const generateStars = () => {
  for (let i = 0; i < 200; i++) {
    stars.value.push({
      style: {
        top: Math.random() * 100 + '%',
        left: Math.random() * 100 + '%',
        width: Math.random() * 3 + 'px',
        height: Math.random() * 3 + 'px',
        animationDelay: Math.random() * 5 + 's',
      },
    });
  }
};

onMounted(async () => {
  updateUserName(userName.value);
  generateStars();

  try {
    const response = await axios.get(`${process.env.VUE_APP_BASE_URL}/chat-api/message/getAll`);
    console.log("📥 История сообщений:", response.data);

    const history = response.data;

    if (Array.isArray(history)) {
      messages.value = history.map(msg => {
        if (msg && typeof msg === 'object' && 'from' in msg && 'text' in msg) {
          return {
            from: msg.from,
            text: msg.text,
            date: msg.date || new Date().toISOString()
          };
        }
        return {
          from: 'System',
          text: '[неизвестное сообщение]',
          date: new Date().toISOString()
        };
      });
    }

    // Scroll to bottom once messages are loaded
    setTimeout(() => scrollToBottom(), 100);
  } catch (err) {
    console.error("❌ Ошибка при загрузке истории сообщений:", err);
  }

  connectWebSocket();
});

onBeforeUnmount(() => {
  if (stompClient.value && stompClient.value.connected) {
    stompClient.value.deactivate();
  }
});
</script>

<style scoped>
.stars-background {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}

.star {
  position: absolute;
  background: white;
  border-radius: 50%;
  animation: twinkle 3s infinite ease-in-out;
}

@keyframes twinkle {
  0%, 100% {
    opacity: 0.3;
  }
  50% {
    opacity: 1;
  }
}

body {
  overflow-x: hidden;
}

.chat-container {
  position: relative;
  z-index: 1;
  max-width: 100vw;
  height: 95vh;
  margin: 20px auto;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  margin-left: 200px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  background: linear-gradient(135deg, #665679, #47054b);
}

.chat-container:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.2);
}

.chat-header {
  background: #fff;
  padding: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.chat-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #47054b;
  margin: 0;
  text-align: center;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  background-color: rgba(255, 255, 255, 0.1);
}

.chat-messages::-webkit-scrollbar {
  display: none;
}

.chat-messages {
  scrollbar-width: none; /* Firefox */
}

.self-message {
  align-self: flex-end;
  background-color: #f5f0f7;
  border-left: 3px solid #47054b;
}

.chat-message {
  background-color: #fff;
  padding: 15px;
  border-radius: 12px;
  max-width: 80%;
  align-self: flex-start;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}


.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 5px;
}

.message-author {
  font-weight: 600;
  color: #47054b;
}

.message-time {
  font-size: 0.8rem;
  color: #666;
  margin-left: 10px;
}

.message-content {
  line-height: 1.4;
  color: #333;
  word-wrap: break-word;
  white-space: normal;
}

.chat-input {
  background: #fff;
  padding: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-top: 2px solid #f0f0f0;
}

.chat-input input {
  flex: 1;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 25px;
  font-size: 14px;
  transition: border-color 0.3s;
  outline: none;
}

.chat-input input:focus {
  border-color: #47054b;
}

.chat-input button {
  background-color: #47054b;
  color: white;
  border: none;
  border-radius: 50%;
  width: 45px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.3s;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.chat-input button:hover {
  background-color: #5d1870;
}

/* Responsive adjustments */
@media (max-width: 1200px) {
  .chat-container {
    margin-left: auto;
    margin-right: auto;
    width: 90%;
  }
}

@media (max-width: 768px) {
  .chat-container {
    width: 95%;
    height: 85vh;
  }

  .chat-message {
    max-width: 90%;
  }

  .chat-title {
    font-size: 1.5rem;
  }
}
</style>