package zinc.doiche

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.AnnotatedEventManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.internal.JDAImpl
import okhttp3.OkHttpClient
import org.slf4j.Logger
import zinc.doiche.chat.listener.ChatListener
import zinc.doiche.lib.command.CommandFactory
import zinc.doiche.command.foundCommand
import zinc.doiche.command.listCommand
import zinc.doiche.lib.database.MongoDB
import zinc.doiche.lib.Config
import zinc.doiche.lib.command.register
import java.io.File
import java.time.Duration
import kotlin.jvm.Throws

internal lateinit var jda: JDA
    private set

internal lateinit var logger: Logger
    private set

internal lateinit var aiToken: String
    private set

internal lateinit var json: ObjectMapper
    private set

@Throws(InterruptedException::class)
fun main() {
    logger = JDAImpl.LOG

    val config = loadConfig()
    val discordToken = config.discordToken
    val databaseURI = config.database.getConnectionString()
    val databaseName = config.database.getName()

    aiToken = config.aiToken
    jda = createJDA(discordToken)
    json = jacksonObjectMapper()

    MongoDB.register(databaseURI, databaseName)

    foundCommand().register()
    listCommand().register()

    logger.info("Buntaju is Online.")
}

private fun loadConfig(): Config {
    val objectMapper = ObjectMapper(YAMLFactory())
        .registerModule(KotlinModule.Builder()
            .build())

    val yamlFile = File("./config.yml").apply {
        if(!exists()) {
            Thread.currentThread().contextClassLoader.getResource("config.yml")?.let {
                createNewFile()
                val default = objectMapper.readValue<Config>(File(it.path))
                objectMapper.writeValue(this, default)
            }
        }
    }
    return objectMapper.readValue(yamlFile, Config::class.java)
}

private fun createJDA(token: String): JDA = JDABuilder.createDefault(token)
    .setEventManager(AnnotatedEventManager())
    .addEventListeners(
        ChatListener(),
        CommandFactory()
    )
    .setHttpClientBuilder(OkHttpClient.Builder()
        .callTimeout(Duration.ofSeconds(10)))
    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
    .build()
    .awaitReady()
