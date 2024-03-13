package zinc.doiche

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.AnnotatedEventManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.internal.JDAImpl
import okhttp3.OkHttpClient
import org.slf4j.Logger
import zinc.doiche.chat.listener.ChatListener
import zinc.doiche.command.FoundCommand
import zinc.doiche.command.ListCommand
import zinc.doiche.database.MongoDB
import zinc.doiche.lib.Config
import java.io.File
import java.time.Duration
import kotlin.jvm.Throws

internal lateinit var jda: JDA
    private set

internal lateinit var logger: Logger
    private set

@Throws(InterruptedException::class)
fun main() {
    val config = loadConfig()

    val discordToken = config.discordToken
    val databaseURI = config.database.getConnectionString()
    val databaseName = config.database.getName()
    //val openAIToken = args[3]

    jda = createJDA(discordToken)
    logger = JDAImpl.LOG

    MongoDB.register(databaseURI, databaseName)
    FoundCommand().register()
    ListCommand().register()

    logger.info("Buntaju is Online.")
}

private fun loadConfig(): Config {
    val objectMapper = ObjectMapper(YAMLFactory())
        .registerModule(KotlinModule.Builder()
            .build())
    val yamlFile = File("src/main/resources/config.yml")
    return objectMapper.readValue(yamlFile, Config::class.java)
}

private fun createJDA(token: String): JDA = JDABuilder.createDefault(token)
    .setEventManager(AnnotatedEventManager())
    .addEventListeners(
        ChatListener()
    )
    .setHttpClientBuilder(OkHttpClient.Builder()
        .callTimeout(Duration.ofSeconds(10)))
    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
    .build()
    .awaitReady()
