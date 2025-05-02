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

    <div class="search-page-container">
      <div class="block search-block">
        <h2 class="block-title">Search Students</h2>

        <v-text-field
            v-model="searchQuery"
            label="Search by name"
            append-icon="mdi-magnify"
            clearable
            class="search-input"
        />

        <v-text-field
            v-model="groupName"
            label="Group Name"
            clearable
            class="search-input"
        />

        <v-text-field
            v-model.number="courseNumber"
            label="Course Number"
            type="number"
            clearable
            class="search-input"
        />

        <v-btn @click="fetchUsers" class="search-button" color="primary">Search</v-btn>

        <v-list v-if="users.length" class="search-results">
          <v-list-item
              v-for="(user, index) in users"
              :key="index"
              class="search-item"
          >
            <v-list-item-avatar>
              <v-avatar color="purple lighten-1">
                <span class="white--text">{{ user.fullName?.charAt(0) }}</span>
              </v-avatar>
            </v-list-item-avatar>
            <v-list-item-content>
              <v-list-item-title>{{ user.fullName }}</v-list-item-title>
              <v-list-item-subtitle>{{ user.groupName }} - {{ user.specializationName }}</v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
        </v-list>

        <div v-else class="no-results">No users found.</div>

        <div class="pagination-controls">
          <v-btn :disabled="offset === 0" @click="prevPage" color="secondary">Prev</v-btn>
          <v-btn :disabled="users.length < 10" @click="nextPage" color="secondary">Next</v-btn>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="js">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import Cookies from 'js-cookie'
import NavBar from "@/components/NavBar.vue"

const searchQuery = ref('')
const groupName = ref('')
const courseNumber = ref(null)
const offset = ref(0)
const users = ref([])

const stars = ref([])

const fetchUsers = async () => {
  const token = Cookies.get('auth_token')

  const filter = {
    name: searchQuery.value,
    size: 10,
    offset: offset.value,
    limit: 10,
    groupName: groupName.value,
    courseNumber: courseNumber.value,
  }

  try {
    const response = await axios.post(
        `${process.env.VUE_APP_BASE_URL}/auth-api/user/getUsersByFilter`,
        filter,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
    )
    users.value = response.data.users
  } catch (error) {
    console.error('Error fetching users:', error)
  }
}

const nextPage = () => {
  offset.value += 10
  fetchUsers()
}

const prevPage = () => {
  if (offset.value >= 10) {
    offset.value -= 10
    fetchUsers()
  }
}

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
  0%, 100% { opacity: 0.3; }
  50% { opacity: 1; }
}

.search-page-container {
  position: relative;
  z-index: 1;
  padding: 40px;
  display: flex;
  justify-content: center;
  background: linear-gradient(135deg, #665679, #47054b);
  min-height: 100vh;
}

.block {
  background: #fff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  width: 100%;
  max-width: 700px;
  display: flex;
  flex-direction: column;
}

.block-title {
  font-size: 1.6rem;
  font-weight: 600;
  color: #47054b;
  margin-bottom: 20px;
  text-align: center;
}

.search-input {
  margin-bottom: 16px;
}

.search-button {
  align-self: center;
  margin-bottom: 20px;
  font-weight: 600;
}

.search-results {
  background: transparent !important;
}

.search-item {
  border-radius: 12px;
  transition: background-color 0.2s;
}

.search-item:hover {
  background-color: #f0f0ff;
}

.no-results {
  text-align: center;
  color: #888;
  font-size: 1rem;
  margin-top: 20px;
}

.pagination-controls {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}
</style>
