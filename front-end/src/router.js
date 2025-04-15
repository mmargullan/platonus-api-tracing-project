import { createRouter, createWebHistory } from "vue-router";
import AuthForm from "./components/AuthForm.vue";
import UserPage from "./components/UserPage.vue";
import GradesPage from "./components/GradesPage.vue";
import Chat from "./components/GlobalChat.vue"

const routes = [
    {path: "/", name:"AuthForm", component: AuthForm},
    {path: "/userPage", name:"UserPage", component: UserPage},
    {path: "/grades", name:"GradesPage", component: GradesPage},
    {path: "/chat", name:"Chat", component: Chat}
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router