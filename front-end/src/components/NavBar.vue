<template>
  <v-navigation-drawer
    app
    permanent
    class="navbar-drawer"
    width="200"
  >
  
    <div class="profile-section">
      <v-avatar size="64" class="mb-2">
        <img src="@/assets/profile.svg" alt="Profile" class="avatar-img"/>
      </v-avatar>
      <div class="username">{{ userName }}</div>
    </div>
   
    <v-list dense nav>
      <v-list-item
        link
        to="/userPage"
        class="list-item"
      >
        <v-list-item-icon>
          <v-icon>mdi-view-dashboard</v-icon>
        </v-list-item-icon>
        <v-list-item-title>Dashboard</v-list-item-title>
      </v-list-item>
      <v-list-item
        link
        to="/grades"
        class="list-item"
      >
        <v-list-item-icon>
          <v-icon>mdi-book-open-page-variant</v-icon>
        </v-list-item-icon>
        <v-list-item-title>Grades</v-list-item-title>
      </v-list-item>
      <v-list-item
        link
        to="/global-chat"
        class="list-item"
      >
        <v-list-item-icon>
          <v-icon>mdi-forum</v-icon>
        </v-list-item-icon>
        <v-list-item-title>Global Chat</v-list-item-title>
      </v-list-item>
    </v-list>
    <v-spacer />
    <!-- Logout -->
    <v-list dense nav class="logout-section">
      <v-list-item
        link
        @click="logout"
        class="list-item"
      >
        <v-list-item-icon>
          <v-icon>mdi-logout</v-icon>
        </v-list-item-icon>
        <v-list-item-title>Logout</v-list-item-title>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script setup lang="js">
import { ref, onMounted } from 'vue';
import Cookies from 'js-cookie';
import { useRouter } from 'vue-router';

const router = useRouter();
const userName = ref('');

const logout = () => {
  Cookies.remove('auth_token');
  localStorage.removeItem('userFullName');
  router.push({ name: 'AuthForm' });
};

const fetchUserName = async () => {
  
  const storedName = localStorage.getItem('userFullName');
  if (storedName) {
    userName.value = storedName;
  }
  
  
  const token = Cookies.get('auth_token');
  if (!token) return;
  
  try {
    const response = await fetch(
      `${process.env.VUE_APP_BASE_URL}/auth-api/user/getUser`,
      {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      }
    );
    
    if (response.ok) {
      const data = await response.json();
      userName.value = data.fullName;
      localStorage.setItem('userFullName', data.fullName);
    }
  } catch (err) {
    console.error('Failed to fetch user name', err);
  }
};

onMounted(fetchUserName);
</script>

<style scoped>
.navbar-drawer {
  background: linear-gradient(135deg, #665679, #47054b);
  color: #fff;
}

.profile-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.username {
  margin-top: 8px;
  font-weight: 500;
  text-align: center;
  font-size: 1rem;
}

.list-item {
  color: #fff;
  border-radius: 8px;
  margin: 4px 8px;
}

.list-item .v-icon {
  color: #fff;
}

.list-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.logout-section {
  margin-bottom: 20px;
}
</style>