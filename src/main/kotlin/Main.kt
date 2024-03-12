package zinc.doiche

import kotlinx.coroutines.flow.forEach
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.AnnotatedEventManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.internal.JDAImpl
import okhttp3.OkHttpClient
import org.slf4j.Logger
import zinc.doiche.chat.listener.ChatListener
import zinc.doiche.database.MongoDB
import zinc.doiche.openai.Channel
import kotlin.jvm.Throws

internal lateinit var jda: JDA
    private set

internal lateinit var logger: Logger
    private set

@Throws(InterruptedException::class)
fun main(args: Array<out String>) {
    val token = args[0]
    val uri = args[1]
    val name = args[2]

    jda = createJDA(token)
    logger = JDAImpl.LOG

    MongoDB.register(uri, name)

    logger.info("Buntaju is Online.")
}

private fun createJDA(token: String): JDA = JDABuilder.createDefault(token)
    .setEventManager(AnnotatedEventManager())
    .addEventListeners(
        ChatListener()
    )
    .setHttpClientBuilder(OkHttpClient.Builder())
    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
    .build()
    .awaitReady()
