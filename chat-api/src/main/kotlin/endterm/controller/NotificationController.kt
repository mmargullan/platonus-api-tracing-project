package endterm.controller

import endterm.dto.ChatMessage
import endterm.service.ChatService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/notification")
class NotificationController(
    private val chatService: ChatService
) {

    @PostMapping("/add")
    fun sendNotification(@RequestBody notificationMessage: ChatMessage) {
        chatService.saveMessage(notificationMessage)
    }

    @GetMapping("/getByUserId")
    fun getUserMessages(@RequestParam userId: String): List<ChatMessage> {
        return chatService.getMessagesByUserId(userId)
    }

}
