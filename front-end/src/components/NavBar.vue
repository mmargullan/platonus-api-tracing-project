<template>
    <v-navigation-drawer app permanent>
      <v-list dense>
        <!-- Ссылки навигации -->
        <v-list-item>
          <router-link to="/userPage" class="nav-link">Profile</router-link>
        </v-list-item>
        <v-list-item>
          <router-link to="/grades" class="nav-link">Grades</router-link>
        </v-list-item>
      </v-list>
      <v-spacer></v-spacer>
      <!-- Кнопка Logout размещается в нижней части -->
      <v-list dense>
        <v-list-item>
          <router-link to="/" class="nav-link" @click="logout">
            <v-btn text>Logout</v-btn>
          </router-link>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
  </template>
  
  <script setup>

  
  const logout = () => {
    console.log("Пользователь вышел");
  }
  </script>
  
  <style scoped>
  .nav-link {
    text-decoration: none;
    color: inherit;
  }
  </style>
  