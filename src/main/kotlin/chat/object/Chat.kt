package zinc.doiche.chat.`object`

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import net.dv8tion.jda.api.entities.Message
import zinc.doiche.lib.database.Collectable
import zinc.doiche.lib.database.MongoDB
import zinc.doiche.lib.database.eq
import java.time.LocalDateTime

data class Chat(
    val senderId: Long,
    val senderName: String,
    val channelId: Long,
    val createdDateTime: LocalDateTime,
    val content: String
) {
    val requestText = "$senderName: $content"

    companion object : Collectable<Message, Chat> {
        override val collection: MongoCollection<Chat> = MongoDB.getCollection<Chat>("messages")

        override suspend fun findById(id: Long): Chat? = collection.find(Chat::channelId eq id).firstOrNull()

        override suspend fun save(entity: Message): Chat = Chat(
            entity.author.idLong,
            entity.member?.nickname ?: entity.author.name,
            entity.channel.idLong,
            entity.timeCreated.toLocalDateTime(),
            entity.contentRaw
        ).apply {
            collection.insertOne(this)
        }

        suspend fun save(chat: Chat) = collection.insertOne(chat)

        fun findAllByChannelId(channelId: Long) = collection.find(Chat::channelId eq channelId)
    }
}