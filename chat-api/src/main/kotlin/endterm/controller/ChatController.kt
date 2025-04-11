package endterm.controller

import endterm.dto.ChatMessage
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class ChatController {

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    fun sendMessage(message: ChatMessage): ChatMessage {
        return message
    }
}
