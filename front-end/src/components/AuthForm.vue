<script setup lang="js">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import Cookies from "js-cookie";

const login = ref("");
const password = ref("");
const isLoading = ref(false);
const snackbar = ref(false);
const snackbarMessage = ref("");
const router = useRouter();
const formVisible = ref(false);
const stars = ref([]);

const handleSubmit = async () => {
  const requestBody = {
    login: login.value,
    password: password.value,
  };

  isLoading.value = true;

  try {
    const response = await fetch("https://2bbc-37-99-48-152.ngrok-free.app/api/auth-api/user/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(requestBody),
    });

    if (!response.ok) {
      throw new Error("Login failed");
    }

    const data = await response.json();
    console.log("Login successful:", data);

    const token = `${data.token}`; // Формируем корректный токен
    Cookies.set("auth_token", token, {
      expires: 7,
      sameSite: "Strict", // Изменено на "Strict" для большей безопасности
      secure: false, // NEED TO BE CHANGED TO TRUE IN PRODUCTION
    });

    snackbarMessage.value = "Login successful!";
    snackbar.value = true;
    router.push({ name: "UserPage" });
  } catch (error) {
    snackbarMessage.value = "Error: " + error.message;
    snackbar.value = true;
    console.error("Error:", error.message);
  } finally {
    isLoading.value = false;
  }
};

const scrollToLogin = () => {
  const loginForm = document.getElementById("login-form");
  if (loginForm) {
    loginForm.scrollIntoView({ behavior: "smooth" });
  }
};

onMounted(() => {
  // Генерация "звезд"
  for (let i = 0; i < 500; i++) {
    const randomX = Math.random() * window.innerWidth;
    const randomY = Math.random() * window.innerHeight;
    const randomDelay = Math.random() * 2 + "s";

    stars.value.push({
      style: {
        transform: `translate(${randomX}px, ${randomY}px)`,
        animationDelay: randomDelay,
      },
    });
  }

  // Наблюдение за видимостью формы
  const observer = new IntersectionObserver(
    ([entry]) => {
      formVisible.value = entry.isIntersecting;
    },
    { threshold: 0.5 }
  );

  const formElement = document.querySelector("#login-form");
  if (formElement) {
    observer.observe(formElement);
  }
});
</script>


<template>
  <v-app class="container">

    <v-parallax class="parallax" gradient="to top right, rgba(1000,115,201,.33), rgba(100,50,250,.7)" src="https://cdn.vuetifyjs.com/images/parallax/material.jpg">
      <v-container class="overlay-text-container">
        <v-row justify="center">
          <v-col class="text-center">
            <h1 class="overlay-text">Welcome!</h1>
            <div class="image-text-1">This is a simple interface to interact with Platonus for IITU students!</div>
            <div class="image-text-2">It is way better and faster than the original Platonus web-application</div>
            <div @click="scrollToLogin" class="image-text-3">Login</div>
          </v-col>
        </v-row>
      </v-container>
    </v-parallax>

    <v-container fluid class="layout-container" id="login-form">
      <div class="stars-background">
        <div v-for="(star, index) in stars" 
          :key="index" 
          class="ster"  
          :style="star.style">
        </div>
      </div>
      <div id="moon"></div>
      <v-card :class="{ visible: formVisible }" class="auth-card" elevation="6">
        <v-card-title class="title">Sign In</v-card-title>
        <v-card-text>
          <v-form ref="form" class="custom-form">
            <v-text-field 
              label="Email" 
              v-model="login" 
              variant="outlined" 
              placeholder="123456@iitu.edu.kz" 
              prepend-inner-icon="mdi-email"
              outlined
            />
            <v-text-field 
              label="Password" 
              v-model="password" 
              type="password"
              variant="outlined" 
              hint="Enter your university mail password" 
              prepend-inner-icon="mdi-lock"
              outlined
            />
            <v-btn 
              elevation="8" 
              class="custom-button" 
              color="primary" 
              block
              :loading="isLoading"
              @click="handleSubmit"
            >
              Sign In
            </v-btn>
          </v-form>
        </v-card-text>
      </v-card>
    </v-container>

    <v-progress-linear
      v-if="isLoading"
      color="primary"
      indeterminate
      absolute
      top
    />

    <v-snackbar
      v-model="snackbar"
      :timeout="3000"
      color="success"
    >
      {{ snackbarMessage }}
    </v-snackbar>
  </v-app>
</template>

<style lang="css" scoped>

.layout-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 100vh;
  gap: 40px;
  z-index: 3;
  overflow: hidden;
}

.auth-card {
  width: 400px;
  padding: 20px;
  border-radius: 12px;
  z-index: 4;
  opacity: 0;
  transition: opacity 0.5s ease-out;
}

.auth-card.visible {
  opacity: 1;
}

.title {
  text-align: center;
  font-weight: bold;
  font-size: 24px;
}

.custom-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.custom-button {
  margin-top: 16px;
  text-transform: none;
}

.overlay-text{
  font-size: 52px;
  padding: 60px;
  opacity: 0;
  animation: fadeIn 0.5s forwards 0.4s;
}

.image-text-1{
  font-size: 34px;
  padding: 25px;
  opacity: 0;
  transform: translateX(-20%);
  animation: fadeIn 0.5s forwards 0.4s;
}

.image-text-2{
  font-size: 30px;
  padding: 35px;
  opacity: 0;
  transform: translateX(-20%);
  animation: fadeIn 0.5s forwards 0.8s;
}

.image-text-3{
  font-size: 42px;
  padding: 20px;
  cursor: pointer;
  text-decoration: underline;
  opacity: 0;
  transform: translateX(-20%);
  animation: fadeIn 0.5s forwards 1.2s;
}

.parallax{
  z-index: 2;
}

@keyframes fadeIn{
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideUp{
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

@keyframes twinkle {
  0% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
}

.ster {
  position: absolute;
  background: white;
  width: 1.16px;
  height: 1.16px;
  border-radius: 50%;
  animation: twinkle 1s infinite ease-in-out;
  z-index: 1;
}

.stars-background {
  background: linear-gradient(45deg, rgb(1, 12, 32) 5%, rgb(2, 28, 62) 55%, rgb(40, 40, 81) 80%);
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  z-index: 1;
  pointer-events: none;
  opacity: 1;
}

#moon {
  position: absolute;
  background-color: white;
  top: 20%;
  left: 20%;
  margin-left: -38px;
  width: 76px;
  height: 76px;
  border-radius: 50%;
  box-shadow: 0 0 10px white;
  z-index: 2;
}

</style>
