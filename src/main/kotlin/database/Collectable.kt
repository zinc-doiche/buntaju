package zinc.doiche.database

import com.mongodb.kotlin.client.coroutine.MongoCollection

interface Collectable<T : Any> {
    val collection: MongoCollection<T>
}