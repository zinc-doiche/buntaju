package zinc.doiche.openai.`object`

data class JSONRequest(
    val contents: Array<Content>,
    val safetySettings: Array<SafetySetting> = Category.defaultSettings(),
    val generationConfig: GenerationConfig = GenerationConfig()
) {
    constructor(vararg contents: Content): this(contents = arrayOf(*contents))

    override fun toString(): String {
        return "JSONRequest(contents=${contents.contentToString()})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JSONRequest

        if (!contents.contentEquals(other.contents)) return false
        if (!safetySettings.contentEquals(other.safetySettings)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = contents.contentHashCode()
        result = 31 * result + safetySettings.contentHashCode()
        return result
    }
}

data class Content(
    val role: String? = "user",
    val parts: Array<Part>
) {
    constructor(vararg parts: Part): this(parts = arrayOf(*parts))

    override fun toString(): String {
        return "Content(role=$role, parts=${parts.contentToString()})"
    }
}

data class Part(
    val text: String
) {
    override fun toString(): String {
        return "Part(text='$text')"
    }
}

data class SafetySetting(
    val category: Category,
    val threshold: Threshold
) {
    override fun toString(): String {
        return "SafetySetting(category=$category, threshold=$threshold)"
    }
}

enum class Threshold {
    BLOCK_NONE,
    BLOCK_ONLY_HIGH,
    BLOCK_MEDIUM_AND_ABOVE,
    BLOCK_LOW_AND_ABOVE,
    HARM_BLOCK_THRESHOLD_UNSPECIFIED
}

data class GenerationConfig(
    val stopSequences: Array<String> = arrayOf("Title"),
    val temperature: Double = 0.9,
    val maxOutputTokens: Int = 800,
    val topP: Double = 0.8,
    val topK: Double = 10.0
) {
    override fun toString(): String {
        return "GenerationConfig(stopSequences=${stopSequences.contentToString()}, temperature=$temperature, maxOutputTokens=$maxOutputTokens, topP=$topP, topK=$topK)"
    }
}