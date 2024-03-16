package zinc.doiche.chat.`object`

import com.mongodb.client.model.DeleteOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import net.dv8tion.jda.api.entities.Message
import org.bson.BsonObjectId
import org.bson.codecs.pojo.annotations.BsonId
import zinc.doiche.jda
import zinc.doiche.lib.database.*
import zinc.doiche.lib.database.eq
import zinc.doiche.lib.database.set
import zinc.doiche.openai.`object`.Content
import zinc.doiche.openai.`object`.Part
import java.time.LocalDateTime

data class Chat(
    @BsonId
    val id: BsonObjectId,
    val senderId: Long,
    val senderName: String,
    val channelId: Long,
    var createdDateTime: LocalDateTime,
    var content: String
) {
    constructor(
        senderId: Long,
        senderName: String,
        channelId: Long,
        createdDateTime: LocalDateTime,
        content: String
    ): this(BsonObjectId(), senderId, senderName, channelId, createdDateTime, content)

    val isUser = senderId != jda.selfUser.idLong

    fun toContent() = Content(if(isUser) "user" else "model", arrayOf(Part("$senderName: $content")))

    companion object : Collectable<Message, Chat> {
        override val collection: MongoCollection<Chat> = MongoDB.getCollection<Chat>("messages")

        override suspend fun findById(id: BsonObjectId): Chat? = collection.find(Chat::id eq id).firstOrNull()

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

        suspend fun update(chat: Chat) = collection.updateOne(Chat::id eq chat.id, set(chat))

        suspend fun delete(chat: Chat) = collection.deleteOne(Chat::id eq chat.id)

        suspend fun deleteBefore(chat: Chat) = collection.deleteMany(Chat::id lte chat.id)
    }
}