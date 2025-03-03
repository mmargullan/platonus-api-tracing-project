<template>
  <v-container fluid class="main-container">
    <!-- Background Material Geometry -->
    <div class="background-shapes"></div>
    <div class="glitter-container">
      <div v-for="n in 100" :key="n" class="star" :style="getStarStyle()"></div>
    </div>

    <!-- User Information Card -->
    <div class="info-card">
      <div class="info-section">
        <h2 class="title animated-text">{{ userInfo.fullname || "Loading..." }}</h2>
        <p class="detail animated-text"><strong>GPA:</strong> <span>{{ userInfo.gpa || "Loading..." }}</span></p>
        <p class="detail animated-text"><strong>Specialization:</strong> <span>{{ userInfo.specialization || "Loading..." }}</span></p>
        <p class="detail animated-text"><strong>Course:</strong> <span>{{ userInfo.course || "Loading..." }}</span></p>
      </div>
      <div class="welcome-section">
        <h2 class="welcome-title">Welcome!</h2>
        <p class="message">Here you can view all your academic records and performance details.</p>
        <a href="#" @click.prevent="viewGrades" class="view-grades">VIEW ALL GRADES</a>
      </div>
    </div>
  </v-container>
</template>

<script setup lang="js">
import { ref, onMounted } from 'vue';
import Cookies from 'js-cookie'; // Импортируем библиотеку для работы с   ми
import { useRouter } from 'vue-router';
import jwt_decode from 'jwt-decode';

const router = useRouter();
const userInfo = ref({
  fullname: '',
  gpa: '',
  specialization: '',
  course: ''
});

// Переход на страницу с оценками
const viewGrades = () => {
  console.log("Переход на страницу с оценками...");
  router.push({ name: "GradesPage" });
};

// Генерация случайного положения и анимации звезд
const getStarStyle = () => {
  const randomPosition = () => Math.random() * 100;
  const randomAnimationDelay = () => Math.random() * 5;
  const randomSize = () => Math.random() * 3 + 1;

  return {
    top: `${randomPosition()}%`,
    left: `${randomPosition()}%`,
    width: `${randomSize()}px`,
    height: `${randomSize()}px`,
    animationDelay: `${randomAnimationDelay()}s`,
  };
};

// Проверка истечения токена
const isTokenExpired = (token) => {
  try {
    const decoded = jwt_decode(token); // Декодируем токен
    const currentTime = Math.floor(Date.now() / 1000); // Текущее время в секундах
    return decoded.exp < currentTime; // Проверяем, истек ли токен
  } catch (error) {
    console.error("Ошибка при проверке истечения токена:", error.message);
    return true;
  }
};

// Функция получения информации о пользователе
const fetchUserInfo = async () => {
  console.log("Получение информации о пользователе...");
  try {
    const token = Cookies.get('auth_token'); // Получаем токен из Cookies

    if (!token || isTokenExpired(token)) {
      console.error("Токен отсутствует или истёк. Перенаправление на страницу входа.");
      router.push({ name: "AuthForm" });
      return;
    }

    const response = await fetch(`${process.env.VUE_APP_BASE_URL}/api/auth-api/user/getUser`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`Ошибка сервера: ${response.status}`);
    }

    const data = await response.json();
    console.log("Ответ от сервера получен:", data);
    userInfo.value = data;

    // Запуск анимации текста
    const elements = document.querySelectorAll('.animated-text');
    elements.forEach((el, index) => {
      setTimeout(() => {
        el.classList.add('fade-in');
      }, index * 200);
    });
  } catch (error) {
    console.error("Ошибка при получении данных пользователя:", error.message);
    router.push({ name: "AuthForm" });
  }
};

// Получение данных при загрузке страницы
onMounted(() => {
  fetchUserInfo();
});
</script>

<style scoped>
/* Main Container */
.main-container {
  position: relative;
  height: 100vh;
  background: linear-gradient(120deg, #5fc2d4, #1b5787);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

/* Glitter Container */
.glitter-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
  z-index: 1;
}

.star {
  position: absolute;
  background: white;
  border-radius: 50%;
  box-shadow: 0 0 6px rgba(255, 255, 255, 0.8);
  animation: twinkle 2s infinite ease-in-out;
}

/* Keyframes for twinkle effect */
@keyframes twinkle {
  0%, 100% {
    opacity: 0.5;
  }
  50% {
    opacity: 1;
  }
}

/* Background Shapes */
.background-shapes::before,
.background-shapes::after {
  content: "";
  position: absolute;
  border-radius: 50%;
  z-index: 0;
}

.background-shapes::before {
  width: 300px;
  height: 300px;
  background: rgba(33, 150, 243, 0.2);
  top: 10%;
  left: 10%;
}

.background-shapes::after {
  width: 500px;
  height: 500px;
  background: rgba(3, 169, 244, 0.15);
  bottom: -10%;
  right: -5%;
  border-radius: 20%;
  transform: rotate(25deg);
}

/* Info Card */
.info-card {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 90%;
  max-width: 850px;
  background: #ffffff;
  border-radius: 8px;
  padding: 30px;
  z-index: 3;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

/* Typography */
.title {
  font-size: 2rem;
  font-weight: bold;
  color: #0f1317;
}

.welcome-title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #1c1e21;
}

.detail {
  font-size: 1rem;
  color: #2c2d30;
  margin: 8px 0;
  font-weight: 600;
}

.detail strong {
  color: #0288d1;
}

.message {
  color: #252529;
  margin: 5px 0;
  font-size: 1rem;
}

/* Text Animation */
.animated-text {
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.8s ease, transform 0.8s ease;
}

.animated-text.fade-in {
  opacity: 1;
  transform: translateY(0);
}

/* Button */
.view-grades {
  text-decoration: none;
  background: #129ddb;
  color: #f0f3f5;
  padding: 12px 24px;
  border-radius: 4px;
  font-weight: bold;
  font-size: 1rem;
  display: inline-block;
  margin-top: 20px;
  transition: background 0.3s ease;
}

.view-grades:hover {
  background: #0288d1;
}
</style>
