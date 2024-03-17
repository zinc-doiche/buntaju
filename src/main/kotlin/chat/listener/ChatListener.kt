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
            val history = ArrayList(chats.map { it.toContent() }.toList())
            val part = Part("${member.nickname}: ${message.contentRaw}")
            history.add(Content("user", arrayOf(part)))

            val contents = if(history.size > 30) {
                Chat.deleteBefore(chats[1])
                history.subList(2, history.size)
            } else {
                history
            }

            RequestBuilder.builder()
                .model("gemini-pro")
                .request(
                    JSONRequest(contents.toTypedArray())//.apply { logger.info(this.toString()) }
                )
                .await().use { response ->
                    val body = response.body ?: return@runBlocking
                    val byteStream = body.byteStream()
                    val jsonResponse = json.readTree(byteStream).let {
                        if (it["error"] != null) {
                            logger.error(it.toString())
                            return@runBlocking
                        }
                        json.readValue<JSONResponse>(it.toString())
                    }

                    val text = jsonResponse.text ?: return@runBlocking
                    logger.info("text: {}", text)
                    logger.info(jsonResponse.status)
                    Chat.save(message)
                    textChannel.sendMessage(text).queue { message ->
                        runBlocking { Chat.save(message) }
                    }
                }
        }
    }
}