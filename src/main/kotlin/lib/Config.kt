package zinc.doiche.lib

data class Config(
    val discordToken: String,
    val openAIToken: String,
    val database: Database
)

data class Database(
    private val user: String? = null,
    private val password: String? = null,
    private val uri: String,
    private val port: Int,
    private val database: String,
    private val options: Map<String, String>
) {
    fun getName() = database

    fun getConnectionString(): String {
        val option = if(options.isNotEmpty()) {
            options.map { (key, value) -> "$key=$value" }.joinToString("&")
        } else ""

        return if(user == null || password == null) {
            "mongodb://$uri:$port/$database$option"
        } else {
            "mongodb://$user:$password@$uri:$port/$database$option"
        }
    }
}

