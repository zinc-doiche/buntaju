package zinc.doiche.database

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.mongodb.reactivestreams.client.MongoClients
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.runInterruptible
import kotlin.coroutines.CoroutineContext

object MongoDB {
    lateinit var mongoClient: MongoClient
        private set

    lateinit var database: MongoDatabase
        private set

    val collections = mutableMapOf<String, MongoCollection<*>>()

    fun register(uri: String, name: String) {
        database = runBlocking {
            mongoClient = MongoClient.create(uri)
            mongoClient.getDatabase(name)
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> getCollection(name: String): MongoCollection<T> =
        collections[name] as? MongoCollection<T> ?: database.getCollection<T>(name).apply {
            collections[name] = this
        }
}