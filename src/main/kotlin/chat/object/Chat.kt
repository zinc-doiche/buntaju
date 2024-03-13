package zinc.doiche.chat.`object`

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import net.dv8tion.jda.api.entities.Message
import zinc.doiche.database.Collectable
import zinc.doiche.database.MongoDB
import zinc.doiche.database.eq
import java.time.LocalDateTime

data class Chat(
    val senderId: Long,
    val channelId: Long,
    val createdDateTime: LocalDateTime,
    val content: String
) {
    companion object : Collectable<Message, Chat> {
        override val collection: MongoCollection<Chat> = MongoDB.getCollection<Chat>("messages")

        override suspend fun findById(id: Long): Chat? = collection.find(Chat::channelId eq id).firstOrNull()

        override suspend fun save(entity: Message): Chat = Chat(
            entity.author.idLong,
            entity.channel.idLong,
            entity.timeCreated.toLocalDateTime(),
            entity.contentRaw
        ).apply {
            collection.insertOne(this)
        }
    }
}