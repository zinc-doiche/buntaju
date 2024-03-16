package zinc.doiche.lib.database

import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.BsonObjectId

interface Collectable<E: Any, T : Any> {
    val collection: MongoCollection<T>

    suspend fun findById(id: BsonObjectId): T?

    suspend fun save(entity: E): T
}

interface UnaryCollectable<T : Any>: Collectable<T, T>