package zinc.doiche.openai

import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import zinc.doiche.jda
import zinc.doiche.json
import zinc.doiche.openAIToken

private const val OPENAI_API_URL = "https://api.openai.com/v1/"

class RequestBuilder private constructor() {
    private var endpoint: String? = null

    fun endpoint(endpoint: String): RequestBuilder {
        this.endpoint = endpoint
        return this
    }

    fun request(obj: Any): Deferred<Response> = runBlocking {
        async {
            Request.Builder()
                .method(
                    "get", json.writeValueAsString(obj)
                        .toRequestBody("application/json".toMediaType())
                )
                .url(OPENAI_API_URL)
//            .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $openAIToken")
                .build().let {
                    jda.httpClient.newCall(it).execute()
                }
        }
    }

    companion object {
        fun create(): RequestBuilder {
            return RequestBuilder()
        }
    }
}