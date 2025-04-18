import { createRouter, createWebHistory } from "vue-router";
import AuthForm from "./components/AuthForm.vue";
import UserPage from "./components/UserPage.vue";
import GradesPage from "./components/GradesPage.vue";
import Chat from "./components/GlobalChat.vue"

const routes = [
    {
        path: "/",
        name: "AuthForm",
        component: AuthForm,
        meta: { title: "Authorization" }
    },
    {
        path: "/userPage",
        name: "UserPage",
        component: UserPage,
        meta: { title: "Dashboard" }
    },
    {
        path: "/grades",
        name: "GradesPage",
        component: GradesPage,
        meta: { title: "Grades" }
    },
    {
        path: "/global-chat",
        name: "Chat",
        component: Chat,
        meta: { title: "Chat" }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const defaultTitle = "Моё приложение";
    document.title = to.meta.title || defaultTitle;
    next();
});

export default router