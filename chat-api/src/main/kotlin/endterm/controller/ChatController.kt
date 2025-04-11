package endterm.controller

import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.messaging.simp.SimpMessagingTemplate

@Controller
class ChatController(private val simpMessagingTemplate: SimpMessagingTemplate) {

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    fun sendMessage(message: String): String {
        return "New message: $message"
    }

    @MessageMapping("/sendPrivateMessage")
    fun sendPrivateMessage(message: String, @Header("simpSessionId") sessionId: String) {
        simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/messages", message)
    }
}