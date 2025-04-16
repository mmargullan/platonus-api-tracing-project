package endterm.controller

import endterm.dto.ChatMessage
import endterm.service.ChatService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class ChatController(
    private val chatService: ChatService
) {

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    fun sendMessage(message: ChatMessage): ChatMessage {
        chatService.saveMessage(message)
        return message
    }

}
