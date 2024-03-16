package zinc.doiche.chat.`object`

import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import zinc.doiche.lib.database.Collectable
import zinc.doiche.lib.database.MongoDB
import zinc.doiche.lib.database.eq
import zinc.doiche.jda

data class Channel(
    val channelId: Long,
    val guildId: Long,
    val name: String
) {
    val messages: FindFlow<Chat>
        get() = Chat.collection.find(Chat::channelId eq this.guildId)

    val guildChannel: TextChannel?
        get() = jda.getGuildById(guildId)?.getTextChannelById(channelId)

    suspend fun getMessage(messageId: Long): Chat? = Chat.collection.find(Chat::channelId eq messageId).firstOrNull()

    companion object : Collectable<TextChannel, Channel> {
        override val collection: MongoCollection<Channel> = MongoDB.getCollection<Channel>("channels")

        override suspend fun findById(id: Long): Channel? = collection.find(Channel::channelId eq id).firstOrNull()

        override suspend fun save(entity: TextChannel): Channel = Channel(
            entity.idLong,
            entity.guild.idLong,
            entity.name
        ).apply {
            collection.insertOne(this)
        }
    }
}