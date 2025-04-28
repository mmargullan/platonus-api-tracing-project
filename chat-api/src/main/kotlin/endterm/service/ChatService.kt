package endterm.service

import endterm.dto.ChatMessage
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ChatService(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    val logger = LoggerFactory.getLogger(ChatService::class.java)

    fun saveMessage(message: ChatMessage) {
        val key = getTodayKey()
        val ops = redisTemplate.opsForList()
        ops.rightPush(key, message)
        redisTemplate.expire(key, Duration.ofDays(1))
    }

    fun getMessages(): List<ChatMessage> {
        val key = getTodayKey()
        val ops = redisTemplate.opsForList()
        val size = ops.size(key) ?: 0
        return ops.range(key, 0, size)?.map { it as ChatMessage } ?: emptyList()
    }

    private fun getTodayKey(): String {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        return "chat:messages:$today"
    }

    fun getMessagesByUserId(userId: String): List<ChatMessage> {
        val key = getTodayKey()
        val ops = redisTemplate.opsForList()
        val size = ops.size(key) ?: 0
        val allMessages = ops.range(key, 0, size)?.map { it as ChatMessage } ?: emptyList()
        return allMessages.filter { it.userId == userId }
    }

}