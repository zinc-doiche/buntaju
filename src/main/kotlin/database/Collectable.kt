package zinc.doiche.database

import com.mongodb.kotlin.client.coroutine.MongoCollection

interface Collectable<E: Any, T : Any> {
    val collection: MongoCollection<T>

    suspend fun findById(id: Long): T?

    suspend fun save(entity: E): T
}

interface UnaryCollectable<T : Any>: Collectable<T, T>