<script setup lang="js">
import { ref } from 'vue';
import NavBar from './NavBar.vue';

const userId = ref('');
const text = ref('');

const sendNotification = async () => {
  const notificationData = {
    userId: userId.value,
    text: text.value,
  };

  try {
    const response = await fetch('https://test-student-forum.serveo.net/chat-api/notification/add', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(notificationData),
    });

    if (response.ok) {
      alert('Notification sent successfully!');
      userId.value = '';
      text.value = '';
    } else {
      alert('Failed to send notification');
    }
  } catch (error) {
    console.error('Error sending notification:', error);
    alert('An error occurred while sending the notification');
  }
};
</script>

<template>
  <NavBar />

  <div class="notification-form">
    <h3>Send Notification</h3>
    <div>
      <label for="userId">User ID</label>
      <input type="text" id="userId" v-model="userId" placeholder="Enter user ID" />
    </div>
    <div>
      <label for="text">Text</label>
      <input type="text" id="text" v-model="text" placeholder="Enter notification text" />
    </div>
    <button @click="sendNotification">Send Notification</button>
  </div>
</template>

<style scoped>
.notification-form {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  max-width: 400px;
  margin: 20px auto;
}

.notification-form h3 {
  margin-bottom: 10px;
  font-size: 1.5rem;
}

.notification-form label {
  font-weight: bold;
  display: block;
  margin-bottom: 5px;
}

.notification-form input {
  width: 100%;
  padding: 8px;
  margin-bottom: 15px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.notification-form button {
  width: 100%;
  padding: 10px;
  background-color: #665679;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.notification-form button:hover {
  background-color: #47054b;
}
</style>
