package zinc.doiche.chat.listener

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import zinc.doiche.chat.`object`.Channel
import zinc.doiche.chat.`object`.Chat
import zinc.doiche.json
import zinc.doiche.logger
import zinc.doiche.openai.`object`.Content
import zinc.doiche.openai.`object`.JSONRequest
import zinc.doiche.openai.`object`.Part
import zinc.doiche.openai.RequestBuilder
import zinc.doiche.openai.`object`.JSONResponse

class ChatListener {

    @SubscribeEvent
    fun onMessageReceived(event: MessageReceivedEvent) = runBlocking {
        if (event.author.isBot || isThinking) {
            return@runBlocking
        }
        withLocking {
            val member = event.member ?: return@runBlocking
            val textChannel = event.channel as? TextChannel ?: return@runBlocking
            val message = event.message.takeUnless { it.contentRaw.isEmpty() } ?: return@runBlocking
            Channel.findById(textChannel.idLong) ?: return@runBlocking

            textChannel.sendTyping().queue()

            RequestBuilder.builder()
                .model("gemini-pro")
                .request(
                    JSONRequest(
                        Content(
                            Part("${member.nickname}: ${message.contentRaw}")
                        )
                    ).apply { logger.info(this.toString()) }
                )
                .await().use { response ->
                    val body = response.body ?: return@runBlocking
                    val byteStream = body.byteStream()

                    val jsonResponse = json.readTree(byteStream).let {
                        if(it["error"] != null) {
                            logger.error(it.toString())
                            return@runBlocking
                        }

                        json.readValue<JSONResponse>(it.toString())
                    }

                    val text = jsonResponse.text ?: return@runBlocking

                    logger.info(jsonResponse.toString())

                    Chat.save(message)
                    textChannel.sendMessage(text).queue()
                }
        }
    }

    private inline fun CoroutineScope.withLocking(block: () -> Unit) {
        runCatching {
            isThinking = true
            block()
            isThinking = false
        }.recover {
            logger.error("메세지 처리 중 오류가 발생했어요.", it)
            isThinking = false
        }
    }

    companion object {
        private var isThinking = false;
    }
}