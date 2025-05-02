<template>
  <div>
    <!-- Подключаем навбар -->
    <NavBar />

    <div class="grades-content">
      <v-card class="grades-card">
        <v-layout>
          <!-- Верхний бар с выбором года, семестра и кнопкой Home -->
          <v-app-bar prominent class="custom-app-bar">

            <v-app-bar-nav-icon variant="text" @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
            <v-toolbar-title>Grades</v-toolbar-title>
            <v-spacer></v-spacer>
            <!-- Кнопки выбора года и семестра -->
            <v-btn text color="white" @click="selectYear">
              Year: {{ selectedYear }}
            </v-btn>
            <v-btn text color="white" @click="selectSemester">
              Semester: {{ selectedSemester }}
            </v-btn>
            
          </v-app-bar>

          <!-- Основной контент -->
          <v-main>
            <v-container>
              <!-- Список курсов -->
              <v-row>
                <v-col
                  v-for="(course, index) in courses"
                  :key="index"
                  cols="12"
                  md="6"
                >
                  <v-card class="mb-4">
                    <v-card-title>{{ course.subjectName }}</v-card-title>
                    <v-card-subtitle>
                      Преподаватели: {{ course.tutorList }}
                    </v-card-subtitle>
                    <v-card-text>
                      <v-list>
                        <v-list-item-group>
                          <v-list-item
                            v-for="(exam, idx) in course.exams"
                            :key="idx"
                          >
                            <v-list-item-content>
                              <v-row class="d-flex align-center mt-2">
                                <v-col cols="3">
                                  <v-list-item-title>
                                    {{ exam.name }}
                                  </v-list-item-title>
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
                                <v-col
                                  cols="2"
                                  class="d-flex justify-end"
                                >
                                  <div class="text-h6">
                                    {{ getProgress(exam.mark) }}%
                                  </div>
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
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Cookies from 'js-cookie';
import { useRouter } from 'vue-router';
import jwt_decode from 'jwt-decode';
import axios from 'axios';
import NavBar from '@/components/NavBar.vue';

const router = useRouter();
const drawer = ref(false);
const courses = ref([]);

// Выбранные год и семестр (дефолтно можно задать текущие значения)
const selectedYear = ref(2024);
const selectedSemester = ref(1);

// Функция для выбора года (например, через prompt)
const selectYear = () => {
  const newYear = prompt("Enter year:", selectedYear.value);
  if(newYear) {
    selectedYear.value = parseInt(newYear);
    fetchGrades();
  }
};

// Функция для выбора семестра (1 или 2)
const selectSemester = () => {
  const newSem = prompt("Enter semester (1 or 2):", selectedSemester.value);
  if(newSem && (newSem === "1" || newSem === "2")) {
    selectedSemester.value = parseInt(newSem);
    fetchGrades();
  }
};

// Функция для получения предыдущего семестра
const getPreviousSemester = (year, semester) => {
  if(semester === 1) {
    return { year: year - 1, semester: 2 };
  } else {
    return { year: year, semester: 1 };
  }
};

// Проверка истечения токена
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

// Получение данных курсов с сервера с логикой выбора семестра
const fetchGrades = async () => {
  console.log("Получение списка курсов для Year:", selectedYear.value, "Semester:", selectedSemester.value);
  try {
    const token = Cookies.get('auth_token');
    if (!token || isTokenExpired(token)) {
      console.error("Токен отсутствует или истёк. Перенаправление на страницу входа.");
      router.push({ name: "LoginPage" });
      return;
    }

    const url = `${process.env.VUE_APP_BASE_URL}/api/auth-api/grades/getGrades?year=${selectedYear.value}&semester=${selectedSemester.value}`;
    const response = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.data.length === 0) {
      console.warn("Оценок не найдено для текущего семестра, переключаемся на предыдущий семестр.");
      const prev = getPreviousSemester(selectedYear.value, selectedSemester.value);
      selectedYear.value = prev.year;
      selectedSemester.value = prev.semester;
      // Рекурсивно вызываем fetchGrades для предыдущего семестра
      return fetchGrades();
    }

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

// Функция для расчёта прогресса (0-100%)
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
  return 'deep-purple'; // Изменил на фиолетовый для хорошей оценки
};

onMounted(fetchGrades);
</script>

<style scoped>
/* Стили для размещения контента справа от навбара */
.grades-content {
  margin-left: 200px; /* Ширина навбара */
  background-color: #f7f2ff; /* Светло-фиолетовый фон */
  min-height: 100vh;
  padding: 16px;
}

.grades-card {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  background-color: white;
}

.v-card {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.v-card-title {
  font-weight: bold;
  color: #47054b; /* Темно-фиолетовый цвет для заголовков */
}

.v-card-subtitle {
  font-size: 0.9rem;
  color: #47054b; /* Фиолетовый цвет для подзаголовков */
}

.v-toolbar-title {
  font-size: 1.5rem;
  font-weight: bold;
}

.custom-app-bar {
  background: linear-gradient(135deg, #665679, #47054b) !important;
  color: white;
}

.v-progress-linear {
  position: relative;
}

.pl-0 {
  padding-left: 0 !important;
}
</style>


