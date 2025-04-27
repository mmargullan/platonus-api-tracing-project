<script setup lang="js">
import NavBar from './NavBar.vue'
import { ref } from 'vue'
import axios from 'axios'
import Cookies from 'js-cookie'

const base64Data = ref('') // сюда будет падать base64

const generateResume = async () => {
  const token = Cookies.get('auth_token')
  try {
    const response = await axios.post(
      `${process.env.VUE_APP_BASE_URL}/api/docs-api/document/generate?nationality=Kazakh`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      }
    )
    base64Data.value = response.data
  } catch (error) {
    console.error('Error generating resume:', error)
  }
}
</script>

<template>
  <div>
    <NavBar />
    <v-main class="content-wrapper">
      <div class="generate-resume-container">
        <v-btn color="primary" @click="generateResume">
          Generate Resume
        </v-btn>
        <div v-if="base64Data" class="pdf-container">
          <iframe
            :src="`data:application/pdf;base64,${base64Data}`"
            width="100%"
            height="800px"
            style="border: none;"
          />
        </div>
      </div>
    </v-main>
  </div>
</template>

<style scoped>
.content-wrapper {
  padding-left: 200px; /* Ширина вашего NavBar */
}

.generate-resume-container {
  width: 100%;
  height: 100vh;
  padding: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.pdf-container {
  margin-top: 40px;
  width: 80%;
  max-width: 900px;
  height: 800px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  border-radius: 12px;
  overflow: hidden;
}
</style>