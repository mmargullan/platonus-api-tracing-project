<template>
  <div>
    <!-- Подключаем навбар -->
    <NavBar />

    <!-- Основной контейнер страницы -->
    <v-container fluid class="main-container">
      <!-- Background Material Geometry -->
      <div class="background-shapes"></div>
      <div class="glitter-container">
        <div v-for="n in 100" :key="n" class="star" :style="getStarStyle()"></div>
      </div>

      <!-- Информационная карточка с табами -->
      <v-card class="info-card">
        <v-tabs v-model="tab" background-color="primary" dark>
          <v-tab value="profile">Profile</v-tab>
          <v-tab value="rating">Group Rating</v-tab>
        </v-tabs>
        <v-divider></v-divider>
        <v-tabs-window v-model="tab">
          <!-- Таб Profile: информация о пользователе -->
          <v-tabs-window-item value="profile">
            <v-card-text>
              <div class="info-section">
                <h2 class="title animated-text">{{ userInfo.fullName }}</h2>
                <p class="detail animated-text">
                  <strong>GPA:</strong> <span>{{ userInfo.gpa }}</span>
                </p>
                <p class="detail animated-text">
                  <strong>Specialization:</strong> <span>{{ userInfo.specializationName }}</span>
                </p>
                <p class="detail animated-text">
                  <strong>Course:</strong> <span>{{ userInfo.courseNumber }}</span>
                </p>
                <!-- Информация о группе -->
                <p class="detail animated-text">
                  <strong>Group:</strong> <span>{{ userInfo.group?.name }}</span>
                </p>
                <p class="detail animated-text">
                  <strong>Students in group:</strong> <span>{{ userInfo.group?.studentCount }}</span>
                </p>
              </div>
              <div class="welcome-section">
                <h2 class="welcome-title">Welcome!</h2>
                <p class="message">
                  Here you can view all your academic records and performance details.
                </p>
                <a href="#" @click.prevent="viewGrades" class="view-grades">VIEW ALL GRADES</a>
              </div>
            </v-card-text>
          </v-tabs-window-item>

          <!-- Таб Group Rating: отображение рейтинга группы -->
          <v-tabs-window-item value="rating">
            <v-card-text>
              <div class="info-section">
                <h2 class="title">Group Rating</h2>
                <p v-if="groupRating !== null" class="detail">
                  <strong>Rating:</strong> <span>№{{ groupRating }}</span>
                </p>
                <p v-else class="detail">
                  No rating data available.
                </p>
                <p class="detail">
                  <strong>Your GPA:</strong> <span>{{ userInfo.gpa }}</span>
                </p>
              </div>
            </v-card-text>
          </v-tabs-window-item>
        </v-tabs-window>
      </v-card>
    </v-container>
  </div>
</template>

<script setup lang="js">
import { ref, onMounted } from 'vue';
import Cookies from 'js-cookie';
import { useRouter } from 'vue-router';
import jwt_decode from 'jwt-decode';
import NavBar from '@/components/NavBar.vue'; // Импорт компонента NavBar

const router = useRouter();
const tab = ref("profile");

const userInfo = ref({
  fullName: '',
  gpa: '',
  specializationName: '',
  courseNumber: '',
  group: {
    id: null,
    name: '',
    studentCount: ''
  }
});
const groupRating = ref(null);

const viewGrades = () => {
  console.log("Переход на страницу с оценками...");
  router.push({ name: "GradesPage" });
};

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

const isTokenExpired = (token) => {
  try {
    const decoded = jwt_decode(token);
    const currentTime = Math.floor(Date.now() / 1000);
    return decoded.exp < currentTime;
  } catch (error) {
    console.error("Ошибка при проверке истечения токена:", error.message);
    return true;
  }
};

const fetchUserInfo = async () => {
  console.log("Получение информации о пользователе...");
  try {
    const token = Cookies.get('auth_token');
    if (!token || isTokenExpired(token)) {
      console.error("Токен отсутствует или истёк. Перенаправление на страницу входа.");
      router.push({ name: "AuthForm" });
      return;
    }
    const response = await fetch(`${process.env.VUE_APP_BASE_URL}/auth-api/user/getUser`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });
    if (!response.ok) throw new Error(`Ошибка сервера: ${response.status}`);
    const data = await response.json();
    console.log("Информация о пользователе получена:", data);
    userInfo.value = data;

    localStorage.setItem('userFullName', data.fullName);

    if (userInfo.value.group && userInfo.value.group.id) {
      fetchGroupRating(userInfo.value.group.id);
    }
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

const fetchGroupRating = async (groupId) => {
  console.log("Получение рейтинга группы для groupId:", groupId);
  try {
    const token = Cookies.get('auth_token');
    const response = await fetch(`${process.env.VUE_APP_BASE_URL}/auth-api/group/getStudentRating/${groupId}`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });
    if (!response.ok) throw new Error(`Ошибка сервера: ${response.status}`);
    const data = await response.json();
    console.log("Данные рейтинга группы:", data);
    groupRating.value = data;
  } catch (error) {
    console.error("Ошибка при получении рейтинга группы:", error.message);
  }
};

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
