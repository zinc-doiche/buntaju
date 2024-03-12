package zinc.doiche.database

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking

object MongoDB {
    lateinit var database: MongoDatabase
        private set

    fun register(uri: String, name: String) {
        database = runBlocking {
            val mongoClient = MongoClient.create(uri)
            mongoClient.getDatabase(name)
        }
    }

    inline fun <reified T : Any> getCollection(name: String): MongoCollection<T> {
        return database.getCollection<T>(name)
    }
}