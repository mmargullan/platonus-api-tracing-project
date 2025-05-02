<script setup lang="js">
import { ref, onMounted} from "vue";
import { useRouter } from "vue-router";
import Cookies from "js-cookie";

const login = ref("");
const password = ref("");
const isLoading = ref(false);
const snackbar = ref(false);
const snackbarMessage = ref("");
const router = useRouter();

onMounted(() => {
  // Create stars
  createStars();
});

const createStars = () => {
  const starsContainer = document.querySelector('.stars-background');
  if (!starsContainer) return;
  
  // Clear existing stars if any
  starsContainer.innerHTML = '';
  
  // Create 200 stars with random positions
  for (let i = 0; i < 200; i++) {
    const star = document.createElement('div');
    star.className = 'star';
    
    // Random position and properties
    const leftPos = Math.random() * 100;
    const topPos = Math.random() * 100;
    const size = Math.random() * 3 + 1;
    const delay = Math.random() * 5;
    const duration = Math.random() * 3 + 2;
    const opacity = Math.random() * 0.7 + 0.3;
    
    star.style.left = `${leftPos}%`;
    star.style.top = `${topPos}%`;
    star.style.width = `${size}px`;
    star.style.height = `${size}px`;
    star.style.opacity = opacity;
    star.style.animationDelay = `${delay}s`;
    star.style.animationDuration = `${duration}s`;
    
    starsContainer.appendChild(star);
  }
};

const handleSubmit = async () => {
  const requestBody = {
    login: login.value,
    password: password.value,
  };

  isLoading.value = true;

  try {
    const response = await fetch(`${process.env.VUE_APP_BASE_URL}/auth-api/user/login`, {
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

    const token = `${data.token}`;
    Cookies.set("auth_token", token, {
      expires: 7,
      sameSite: "Strict",
      secure: false, // change to true in production
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
</script>

<template>
  <v-app>
    <!-- Stars background - fixed position -->
    <div class="stars-background"></div>
    
    <v-container fluid class="login-container">
      <v-row no-gutters justify="center">
        <v-col cols="12" md="10">
          <v-row class="rounded-card">
            <!-- Left Block -->
            <v-col cols="12" md="6" class="intro-section">
              <div class="intro-content">
                <div class="logo-placeholder">
                  <!-- Placeholder for UI elements (logo, image, etc.) -->
                  <v-icon size="64" color="#665679">mdi-school</v-icon>
                </div>
                <h1>Welcome back!</h1>
                <p>
                  This platform is designed specifically for IITU students.
                  Stay updated with the latest news, events, and opportunities tailored to your journey.
                </p>
              </div>
            </v-col>
            <!-- Right Block: Sign In Form inside -->
            <v-col cols="12" md="6" class="form-section">
              <div class="sign-in-form">
                <h2>Sign In</h2>
                <v-form class="custom-form">
                  <v-text-field 
                    label="Email" 
                    v-model="login" 
                    variant="outlined" 
                    placeholder="123456@iitu.edu.kz" 
                    prepend-inner-icon="mdi-email"
                    class="rounded-input"
                    outlined
                  />
                  <v-text-field 
                    label="Password" 
                    v-model="password" 
                    type="password"
                    variant="outlined" 
                    hint="Enter your university email password" 
                    prepend-inner-icon="mdi-lock"
                    class="rounded-input"
                    outlined
                  />
                  <v-btn 
                    elevation="0" 
                    class="custom-button" 
                    block
                    :loading="isLoading"
                    @click="handleSubmit"
                  >
                    Sign In
                  </v-btn>
                </v-form>
              </div>
            </v-col>
          </v-row>
        </v-col>
      </v-row>
    </v-container>

    <v-snackbar
      v-model="snackbar"
      :timeout="3000"
      color="success"
    >
      {{ snackbarMessage }}
    </v-snackbar>
  </v-app>
</template>

<style>
/* Use non-scoped style to ensure stars are visible */
.stars-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  background: linear-gradient(135deg, #665679, #47054b);
  overflow: hidden;
}

.star {
  position: absolute;
  background-color: #ffffff;
  border-radius: 50%;
  z-index: 1;
  animation: twinkle ease-in-out infinite, floating linear infinite;
}

@keyframes twinkle {
  0% {
    opacity: 0.3;
    transform: scale(0.8);
  }
  50% {
    opacity: 1;
    transform: scale(1);
  }
  100% {
    opacity: 0.3;
    transform: scale(0.8);
  }
}

@keyframes floating {
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(20px);
  }
  100% {
    transform: translateY(0px);
  }
}
</style>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
}

.rounded-card {
  background-color: rgba(255, 255, 255, 0.85);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 3;
  backdrop-filter: blur(5px);
}

.intro-section {
  background-color: rgba(255, 255, 255, 0.7);
  padding: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.intro-content {
  max-width: 500px;
  color: #333;
  text-align: center;
}

.intro-content h1 {
  font-size: 32px;
  margin-top: 20px;
  margin-bottom: 15px;
}

.intro-content p {
  font-size: 16px;
  line-height: 1.5;
}

.logo-placeholder {
  margin-bottom: 20px;
}

.form-section {
  background-color: rgba(245, 245, 245, 0.7);
  padding: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sign-in-form {
  width: 100%;
  max-width: 400px;
  padding: 20px;
  border-radius: 16px;
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.sign-in-form h2 {
  text-align: center;
  margin-bottom: 20px;
  font-size: 26px;
  color: #333;
}

.custom-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.rounded-input .v-input__control {
  border-radius: 12px;
}

.custom-button {
  margin-top: 16px;
  text-transform: none;
  border-radius: 12px;
  padding: 12px;
  background-color: transparent;
  border: 2px solid #665679;
  color: #665679;
  transition: all 0.3s ease;
}

.custom-button:hover {
  background-color: #665679;
  color: #fff;
}
</style>
