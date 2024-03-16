package zinc.doiche.openai

import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import zinc.doiche.jda
import zinc.doiche.json
import zinc.doiche.aiToken

private const val API_URL = "https://generativelanguage.googleapis.com/v1beta/models/"

class RequestBuilder private constructor() {
    private var model: String? = null

    fun model(model: String): RequestBuilder {
        this.model = model
        return this
    }

    fun request(obj: Any): Deferred<Response> = runBlocking {
        async {
            Request.Builder()
                .method(
                    "POST", json.writeValueAsString(obj)
                        .toRequestBody("application/json".toMediaType())
                )
                .url("$API_URL$model:generateContent?key=$aiToken")
                .addHeader("Content-Type", "application/json")
                .build().let {
                    jda.httpClient.newCall(it).execute()
                }
        }
    }

    companion object {
        fun builder(): RequestBuilder {
            return RequestBuilder()
        }
    }
}