<template>
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

    <div class="dashboard-container">
      
      <div class="block block-1">
        <h2 class="greeting">Hello, {{ firstName }}!</h2>
        <div class="profile-info">
          <div class="info-item">
            <v-icon color="purple" class="info-icon">mdi-account</v-icon>
            <span class="info-label">Full Name:</span>
            <span class="info-value">{{ userInfo.fullName }}</span>
          </div>
          <div class="info-item">
            <v-icon color="purple" class="info-icon">mdi-school</v-icon>
            <span class="info-label">GPA:</span>
            <span class="info-value">{{ userInfo.gpa }}</span>
          </div>
          <div class="info-item">
            <v-icon color="purple" class="info-icon">mdi-book-open-variant</v-icon>
            <span class="info-label">Specialization:</span>
            <span class="info-value">{{ userInfo.specializationName }}</span>
          </div>
          <div class="info-item">
            <v-icon color="purple" class="info-icon">mdi-notebook</v-icon>
            <span class="info-label">Course:</span>
            <span class="info-value">{{ userInfo.courseNumber }}</span>
          </div>
          <div class="info-item">
            <v-icon color="purple" class="info-icon">mdi-account-group</v-icon>
            <span class="info-label">Group:</span>
            <span class="info-value">{{ userInfo.group?.name }}</span>
          </div>
          <div class="info-item">
            <v-icon color="purple" class="info-icon">mdi-account-multiple</v-icon>
            <span class="info-label">Students in group:</span>
            <span class="info-value">{{ userInfo.group?.studentCount || 'N/A' }}</span>
          </div>
        </div>
        
      </div>
      
      
      <div class="block block-2">
        <h3 class="block-title">Group Rating</h3>
        <div class="rating-container">
          <div class="rating-circle">
            <template v-if="groupRating !== null">
              <span class="rating-number">#{{ groupRating }}</span>
              <span class="rating-label">your place</span>
            </template>
            <template v-else>
              <span class="rating-label">No data</span>
            </template>
          </div>
          <div class="gpa-display">
            <div class="gpa-label">Your GPA</div>
            <div class="gpa-value">{{ userInfo.gpa || 'N/A' }}</div>
          </div>
        </div>
      </div>
      
  
      <div class="block block-3">
        <h3 class="block-title">Subjects & Teachers</h3>
        <v-list class="subject-list">
          <v-list-item v-for="(subject, index) in subjects" :key="index" class="subject-item">
            <v-list-item-avatar>
              <v-avatar color="purple lighten-1" size="40">
                <span class="white--text">{{ subject.name.charAt(0) }}</span>
              </v-avatar>
            </v-list-item-avatar>
            <v-list-item-content>
              <v-list-item-title>{{ subject.name }}</v-list-item-title>
              <v-list-item-subtitle>{{ subject.teacher }}</v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </div>
      
    
      <div class="block block-4">
        <h3 class="block-title">Academic Calendar</h3>
        
       
        <div class="hardcoded-calendar">
          <div class="calendar-header">
            <div class="month-display">April 2025</div>
          </div>
          
          <div class="calendar-days">
            <div class="day-header">Mon</div>
            <div class="day-header">Tue</div>
            <div class="day-header">Wed</div>
            <div class="day-header">Thu</div>
            <div class="day-header">Fri</div>
            <div class="day-header">Sat</div>
            <div class="day-header">Sun</div>
          </div>
          
          <div class="calendar-grid">
            <!-- Week 1 -->
            <div class="day disabled">31</div>
            <div class="day">1</div>
            <div class="day">2</div>
            <div class="day">3</div>
            <div class="day">4</div>
            <div class="day">5</div>
            <div class="day">6</div>
            
            <!-- Week 2 -->
            <div class="day">7</div>
            <div class="day">8</div>
            <div class="day">9</div>
            <div class="day">10</div>
            <div class="day">11</div>
            <div class="day">12</div>
            <div class="day">13</div>
            
            <!-- Week 3 -->
            <div class="day">14</div>
            <div class="day event">15</div>
            <div class="day">16</div>
            <div class="day current">17</div>
            <div class="day">18</div>
            <div class="day">19</div>
            <div class="day">20</div>
            
            <!-- Week 4 -->
            <div class="day">21</div>
            <div class="day event">22</div>
            <div class="day">23</div>
            <div class="day">24</div>
            <div class="day">25</div>
            <div class="day">26</div>
            <div class="day">27</div>
            
            <!-- Week 5 -->
            <div class="day">28</div>
            <div class="day">29</div>
            <div class="day">30</div>
            <div class="day disabled">1</div>
            <div class="day disabled">2</div>
            <div class="day disabled">3</div>
            <div class="day disabled">4</div>
          </div>
        </div>
        
        <div class="events-list">
          <div class="event-item" v-for="(event, index) in upcomingEvents" :key="index">
            <div class="event-date">{{ event.date }}</div>
            <div class="event-title">{{ event.title }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="js">
import { ref, onMounted, computed } from 'vue';
import Cookies from 'js-cookie';
import { useRouter } from 'vue-router';
import jwt_decode from 'jwt-decode';
import NavBar from '@/components/NavBar.vue';

const router = useRouter();
const stars = ref([]);

const userInfo = ref({
  fullName: '',
  gpa: '',
  specializationName: '',
  courseNumber: '',
  group: {
    id: null,
    name: '',
    studentCount: ''
  },
});

const firstName = computed(() => {
  if (userInfo.value.fullName) {
    return userInfo.value.fullName.split(' ')[0];
  }
  return '';
});

const groupRating = ref(null);

// Dummy data for demonstration
const subjects = ref([
  { name: 'Java Springboot', teacher: 'Prof. Gayni' },
  { name: 'Android Kotlin', teacher: 'Prof. Zamir' },
  { name: 'Operation System', teacher: 'Dr. Bezgeyev' },
  { name: 'Economics', teacher: 'Ms. Hoely' }
]);

const upcomingEvents = ref([
  { title: 'Midterm Exams', date: 'April 15, 2025' },
  { title: 'Project Submission', date: 'April 22, 2025' },
  { title: 'Final Exams Begin', date: 'May 10, 2025' }
]);


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

onMounted(() => {
  fetchUserInfo();
  generateStars();
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


.dashboard-container {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 20px;
  padding: 20px;
  margin-left: 200px;
  min-height: 100vh;
  background: linear-gradient(135deg, #665679, #47054b);
}


.block {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  transition: transform 0.3s, box-shadow 0.3s;
  overflow: hidden;
}

.block:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.2);
}

.block-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #47054b;
  margin-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 10px;
}


.greeting {
  font-size: 1.8rem;
  font-weight: 700;
  color: #47054b;
  margin-bottom: 20px;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-icon {
  margin-right: 10px;
}

.info-label {
  font-weight: 600;
  color: #555;
  width: 130px;
}

.info-value {
  font-weight: 500;
  color: #47054b;
}


.rating-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.rating-circle {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background: linear-gradient(135deg, #665679, #47054b);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  box-shadow: 0 6px 15px rgba(71, 5, 75, 0.3);
}

.rating-number {
  font-size: 2.5rem;
  font-weight: bold;
}

.rating-label {
  font-size: 0.9rem;
}

.gpa-display {
  text-align: center;
}

.gpa-label {
  font-size: 1.1rem;
  font-weight: 600;
  color: #555;
}

.gpa-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #47054b;
}


.subject-list {
  background-color: transparent !important;
}

.subject-item {
  border-radius: 8px;
  margin-bottom: 8px;
  transition: background-color 0.2s;
}

.subject-item:hover {
  background-color: #f5f0f7;
}


.hardcoded-calendar {
  width: 100%;
  max-width: 350px;
  margin: 0 auto 20px;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.calendar-header {
  background-color: #47054b;
  color: white;
  padding: 12px 16px;
  text-align: center;
}

.month-display {
  font-size: 1.2rem;
  font-weight: 600;
}

.calendar-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  padding: 8px 0;
  background-color: #f5f0f7;
  border-bottom: 1px solid #e0e0e0;
}

.day-header {
  font-weight: 600;
  color: #666;
  font-size: 0.85rem;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-template-rows: repeat(5, 1fr);
  gap: 2px;
  padding: 8px;
}

.day {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 36px;
  border-radius: 50%;
  margin: 2px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.day:hover:not(.disabled) {
  background-color: #f0f0f0;
}

.day.disabled {
  color: #ccc;
  cursor: default;
}

.day.current {
  background-color: #47054b;
  color: white;
}

.day.event {
  background-color: #f5f0f7;
  border: 2px solid #47054b;
  color: #47054b;
  font-weight: 600;
}

.events-list {
  margin-top: 15px;
  overflow-y: auto;
  max-height: 150px;
}

.event-item {
  display: flex;
  flex-direction: column;
  padding: 8px 12px;
  margin-bottom: 8px;
  background-color: #f5f0f7;
  border-radius: 6px;
  border-left: 3px solid #47054b;
}

.event-date {
  font-size: 0.85rem;
  color: #666;
}

.event-title {
  font-weight: 600;
  color: #47054b;
}

/* Responsive adjustments */
@media (max-width: 1200px) {
  .dashboard-container {
    grid-template-columns: 1fr;
    grid-template-rows: repeat(4, auto);
  }
}

@media (max-width: 600px) {
  .rating-circle {
    width: 120px;
    height: 120px;
  }
  
  .greeting {
    font-size: 1.5rem;
  }
}
</style>