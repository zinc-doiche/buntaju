package zinc.doiche.chat.`object`

import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import org.bson.BsonObjectId
import org.bson.codecs.pojo.annotations.BsonId
import zinc.doiche.lib.database.Collectable
import zinc.doiche.lib.database.MongoDB
import zinc.doiche.lib.database.eq
import zinc.doiche.jda

data class Channel(
    @BsonId
    val id: BsonObjectId,
    val channelId: Long,
    val guildId: Long,
    val name: String
) {

    constructor(channelId: Long, guildId: Long, name: String): this(BsonObjectId(), channelId, guildId, name)

    val messages: FindFlow<Chat>
        get() = Chat.collection.find(Chat::channelId eq this.guildId)

    val guildChannel: TextChannel?
        get() = jda.getGuildById(guildId)?.getTextChannelById(channelId)

    suspend fun getMessage(messageId: Long): Chat? = Chat.collection.find(Chat::channelId eq messageId).firstOrNull()

    companion object : Collectable<TextChannel, Channel> {
        override val collection: MongoCollection<Channel> = MongoDB.getCollection<Channel>("channels")

        override suspend fun findById(id: BsonObjectId): Channel? = collection.find(Channel::id eq id).firstOrNull()

        suspend fun findByChannelId(channelId: Long): Channel? = collection.find(Channel::channelId eq channelId).firstOrNull()

        override suspend fun save(entity: TextChannel): Channel = Channel(
            entity.idLong,
            entity.guild.idLong,
            entity.name
        ).apply {
            collection.insertOne(this)
        }
    }
}