package zinc.doiche.chat.listener

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import zinc.doiche.chat.`object`.Channel
import zinc.doiche.chat.`object`.Chat
import zinc.doiche.json
import zinc.doiche.logger
import zinc.doiche.openai.`object`.Content
import zinc.doiche.openai.`object`.JSONRequest
import zinc.doiche.openai.`object`.Part
import zinc.doiche.openai.RequestBuilder
import zinc.doiche.openai.`object`.JSONResponse

private const val DELAY = 3000L

class ChatListener {
    private var lastMessageTime = 0L

    @SubscribeEvent
    fun onMessageReceived(event: MessageReceivedEvent) {
        runBlocking {
            if (event.author.isBot) {
                return@runBlocking
            }
            val member = event.member ?: return@runBlocking
            val textChannel = event.channel as? TextChannel ?: return@runBlocking
            val message = event.message.takeUnless { it.contentRaw.isEmpty() } ?: return@runBlocking
            Channel.findByChannelId(textChannel.idLong) ?: return@runBlocking

            val current = System.currentTimeMillis()
            if (current - lastMessageTime < DELAY) {
                message.delete().queue()
                textChannel.sendMessage("조금만 천천히 보내주세요...").mentionRepliedUser(true).queue()
                return@runBlocking
            }
            lastMessageTime = current

            textChannel.sendTyping().queue()

            val chats = Chat.findLatest40ByChannelId(textChannel.idLong).toList().reversed()
            val history = ArrayList(chats.map { it.toMessage() }.toList())
            val messageObject = Message("user", "${member.nickname}: ${message.contentRaw}")
            history.add(messageObject)

            val contents = if(history.size > 10) {
                Chat.deleteBefore(chats[1])
                history.subList(2, history.size)
            } else {
                history
            }
            contents.addAll(0, listOf(
                Message("system", "당신은 무협 세계관 속 거지들의 집단인 개방파의 분타주이다. 분타란 어떤 한 지역의 개방 지부를 말한다. 이곳에서 당신은 당신을 찾아오는 자들에게 정보를 제공해야 한다. 당신의 말투는 거지와 유사하며, 옛스러운 표현을 많이 사용한다. 또한, 절대 존대를 하지 않는다.")
            ))
            RequestBuilder.builder()
//                .model("gemini-pro")
//                .requestGemini(
//                    JSONRequest(contents.toTypedArray())//.apply { logger.info(this.toString()) }
//                )
                .requestGPT(mapOf(
                    "model" to "gpt-3.5-turbo",
                    "messages" to contents
                ))
                .await().use { response ->
                    val body = response.body ?: return@runBlocking
                    val byteStream = body.byteStream()
                    val jsonResponse = json.readTree(byteStream).let {
//                        if (it["error"] != null) {
//                            logger.error(it.toString())
//                            return@runBlocking
//                        }
//                        json.readValue<JSONResponse>(it.toString())
                        val content = it.toString()
                        logger.info(content)
                        json.readValue<ChatCompletion>(content)
                    }
//                    val text = jsonResponse.text ?: return@runBlocking
                    val text = jsonResponse.choices?.get(0)?.message?.content ?: return@runBlocking
//                    logger.info("text: {}", text)
//                    logger.info(jsonResponse.status)
//                    logger.info(jsonResponse.toString())
                    Chat.save(message)
                    textChannel.sendMessage(text).queue { message ->
                        runBlocking { Chat.save(message) }
                    }
                }
        }
    }

    data class Message(
        val role: String,
        val content: String
    )

    enum class Role {
        USER, SYSTEM, ASSISTANT, TOOL;


    }

//    {
//        "id": "chatcmpl-123",
//        "object": "chat.completion",
//        "created": 1677652288,
//        "model": "gpt-3.5-turbo-0125",
//        "system_fingerprint": "fp_44709d6fcb",
//        "choices": [{
//            "index": 0,
//            "message": {
//                "role": "assistant",
//                "content": "\n\nHello there, how may I assist you today?",
//            },
//            "logprobs": null,
//            "finish_reason": "stop"
//        }],
//        "usage": {
//            "prompt_tokens": 9,
//            "completion_tokens": 12,
//            "total_tokens": 21
//        }
//    }
    data class ChatCompletion(
        val id: String?,
        val `object`: String?,
        val created: Long?,
        val model: String?,
        val system_fingerprint: String?,
        val choices: List<Choice>?,
        val usage: Usage?
    )

    data class Choice(
        val index: Int?,
        val message: Message?,
        val logprobs: Any?,
        val finish_reason: String?
    )

    data class Usage(
        val prompt_tokens: Int?,
        val completion_tokens: Int?,
        val total_tokens: Int?
    )
}