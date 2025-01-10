<template>
  <v-card>
    <v-layout>
      <!-- Навбар -->
      <v-app-bar color="red" prominent>
        <v-app-bar-nav-icon variant="text" @click.stop="drawer = !drawer"></v-app-bar-nav-icon>

        <v-toolbar-title>Grades</v-toolbar-title>

        <v-spacer></v-spacer>

        <v-btn text color="white" @click="redirectToHome">
          Home
        </v-btn>
      </v-app-bar>

      <!-- Основной контент -->
      <v-main>
        <v-container>
          <!-- Список курсов -->
          <v-row>
            <v-col v-for="(course, index) in courses" :key="index" cols="12" md="6">
              <v-card class="mb-4">
                <v-card-title>{{ course.subjectName }}</v-card-title>
                <v-card-subtitle>Преподаватели: {{ course.tutorList }}</v-card-subtitle>
                <v-card-text>
                  <v-list>
                    <v-list-item-group>
                      <v-list-item v-for="(exam, idx) in course.exams" :key="idx">
                        <v-list-item-content>
                          <v-row class="d-flex align-center mt-2">
                            <v-col cols="3">
                              <v-list-item-title>{{ exam.name }}</v-list-item-title>
                            </v-col>
                            <v-col cols="7" class="pl-0">
                              <v-progress-linear
                                :model-value="getProgress(exam.mark)"
                                :color="getProgressColor(exam.mark)"
                                bg-color="#f0f0f0"
                                height="12"
                                rounded
                              ></v-progress-linear>
                            </v-col>
                            <v-col cols="2" class="d-flex justify-end">
                              <div class="text-h6">{{ getProgress(exam.mark) }}%</div>
                            </v-col>
                          </v-row>
                        </v-list-item-content>
                      </v-list-item>
                    </v-list-item-group>
                  </v-list>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
      </v-main>
    </v-layout>
  </v-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Cookies from 'js-cookie'; // Импортируем библиотеку для работы с куками
import { useRouter } from 'vue-router';
import jwt_decode from 'jwt-decode';
import axios from 'axios';

const router = useRouter();
const drawer = ref(false);
const courses = ref([]);

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

// Получение данных курсов с сервера
const fetchGrades = async () => {
  console.log("Получение списка курсов...");
  try {
    const token = Cookies.get('auth_token'); // Получаем токен из Cookies

    if (!token || isTokenExpired(token)) {
      console.error("Токен отсутствует или истёк. Перенаправление на страницу входа.");
      router.push({ name: "LoginPage" });
      return;
    }

    const response = await axios.get('http://localhost:8081/user/getGrades', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    courses.value = response.data.map((item) => ({
      subjectName: item.subjectName,
      tutorList: item.tutorList,
      exams: item.exams
        .filter((exam) => exam.name && exam.mark)
        .filter((exam) => exam.name !== 'Ср.тек. 11' && exam.name !== 'Ср.тек. 22'),
    }));
  } catch (error) {
    console.error("Ошибка при получении данных:", error.message);
    router.push({ name: "LoginPage" });
  }
};

// Функция для редиректа на главную страницу
const redirectToHome = () => {
  router.push({ name: "UserPage" });
};

// Функция для прогресса (0-100%)
const getProgress = (mark) => {
  if (mark === '-' || mark === null || mark === 'Не задано') return 0;
  const markValue = parseFloat(mark);
  return isNaN(markValue) ? 0 : Math.min(Math.max(markValue, 0), 100);
};

// Функция для цвета прогресса
const getProgressColor = (mark) => {
  const progress = getProgress(mark);
  if (progress <= 25) return 'red';
  if (progress <= 50) return 'yellow';
  if (progress <= 75) return 'blue';
  return 'green';
};

// Загрузка данных при монтировании компонента
onMounted(fetchGrades);
</script>


<style scoped>
.v-card {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.v-card-title {
  font-weight: bold;
}

.v-card-subtitle {
  font-size: 0.9rem;
  color: gray;
}

.v-toolbar-title {
  font-size: 1.5rem;
  font-weight: bold;
}

.v-progress-linear {
  position: relative;
}

.pl-0 {
  padding-left: 0 !important;
}
</style>
