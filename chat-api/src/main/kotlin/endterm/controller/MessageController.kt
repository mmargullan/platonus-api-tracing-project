package endterm.controller

import endterm.dto.ChatMessage
import endterm.service.ChatService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/message")
class MessageController(
    private val chatService: ChatService
) {

    @GetMapping("/getAll")
    fun getHistory(): List<ChatMessage> {
        return chatService.getMessages()
    }

}