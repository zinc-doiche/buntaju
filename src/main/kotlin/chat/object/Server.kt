package zinc.doiche.chat.`object`

import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import net.dv8tion.jda.api.entities.Guild
import org.bson.BsonObjectId
import zinc.doiche.lib.database.Collectable
import zinc.doiche.lib.database.MongoDB
import zinc.doiche.lib.database.eq
import zinc.doiche.jda

data class Server(
    val id: BsonObjectId,
    val guildId: Long,
    val name: String
) {
    constructor(guildId: Long, name: String): this(BsonObjectId(), guildId, name)

    val channels: FindFlow<Channel>
        get() = Channel.collection.find(Channel::guildId eq this.guildId)

    val guild: Guild?
        get() = jda.getGuildById(guildId)

    suspend fun getChannel(channelId: Long): Channel? = Channel.collection.find(Channel::guildId eq channelId).firstOrNull()

    companion object : Collectable<Guild, Server> {
        override val collection: MongoCollection<Server> = MongoDB.getCollection<Server>("servers")

        override suspend fun findById(id: BsonObjectId) = collection.find(Server::id eq id).firstOrNull()
        suspend fun findByGuildId(guildId: Long) = collection.find(Server::guildId eq guildId).firstOrNull()

        override suspend fun save(entity: Guild): Server = Server(entity.idLong, entity.name).apply {
            collection.insertOne(this)
        }
    }
}