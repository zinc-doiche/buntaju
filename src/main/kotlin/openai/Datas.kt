package zinc.doiche.openai

import com.mongodb.kotlin.client.coroutine.MongoCollection
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import zinc.doiche.database.Collectable
import zinc.doiche.database.MongoDB
import zinc.doiche.jda

data class Server(
    val serverId: Long,
    val name: String,
    val channels: Map<String, Channel>
) {
    val guild: Guild?
        get() = jda.getGuildById(serverId)

    operator fun get(channelName: String): Channel? = channels[channelName]

    companion object : Collectable<Server> {
        override val collection: MongoCollection<Server> = MongoDB.getCollection<Server>("servers")
    }
}

data class Channel(
    val channelId: Long,
    val name: String,
) {
    val guildChannel: GuildChannel?
        get() = jda.getGuildChannelById(channelId)



    companion object : Collectable<Channel> {
        override val collection: MongoCollection<Channel> = MongoDB.getCollection<Channel>("channels")
    }
}

data class Feed(
    val sender: String,
    val messages: List<Message>
)

data class Message(
    val sender: String,
    val channelId: Long,
    val createdAt: Long,
    val message: String
) {
    companion object : Collectable<Message> {
        override val collection: MongoCollection<Message> = MongoDB.getCollection<Message>("messages")
    }
}