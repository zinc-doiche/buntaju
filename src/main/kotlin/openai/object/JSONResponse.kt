package zinc.doiche.openai.`object`


data class JSONResponse(
    val candidates: ArrayList<Candidate>? = null,
    val promptFeedback: PromptFeedback? = null
) {
    val text: String? = candidates?.get(0)?.content?.parts?.get(0)?.text
    override fun toString(): String {
        return "JSONResponse(candidates=$candidates, promptFeedback=$promptFeedback, text=$text)"
    }
}

data class Candidate(
    val content: Content? = null,
    val finishReason: String? = null,
    val index: Int = 0,
    val safetyRatings: ArrayList<SafetyRating>? = null
) {
    override fun toString(): String {
        return "Candidate(content=$content, finishReason=$finishReason, index=$index, safetyRatings=$safetyRatings)"
    }
}

data class PromptFeedback(
    val safetyRatings: ArrayList<SafetyRating>?
) {
    override fun toString(): String {
        return "PromptFeedback(safetyRatings=$safetyRatings)"
    }
}

data class SafetyRating(
    val category: Category?,
    val probability: Probability?
) {
    override fun toString(): String {
        return "SafetyRating(category=$category, probability=$probability)"
    }
}

enum class Category {
    HARM_CATEGORY_HARASSMENT,
    HARM_CATEGORY_HATE_SPEECH,
    HARM_CATEGORY_SEXUALLY_EXPLICIT,
    HARM_CATEGORY_DANGEROUS_CONTENT;

    companion object {
        fun defaultSettings() = arrayOf(
            SafetySetting(HARM_CATEGORY_HARASSMENT, Threshold.BLOCK_NONE),
            SafetySetting(HARM_CATEGORY_HATE_SPEECH, Threshold.BLOCK_NONE),
            SafetySetting(HARM_CATEGORY_SEXUALLY_EXPLICIT, Threshold.BLOCK_NONE),
            SafetySetting(HARM_CATEGORY_DANGEROUS_CONTENT, Threshold.BLOCK_NONE),
//            SafetySetting(HARM_CATEGORY_UNSPECIFIED, Threshold.BLOCK_NONE),
//            SafetySetting(HARM_CATEGORY_DEROGATORY, Threshold.BLOCK_NONE),
//            SafetySetting(HARM_CATEGORY_TOXICITY, Threshold.BLOCK_NONE),
//            SafetySetting(HARM_CATEGORY_VIOLENCE, Threshold.BLOCK_NONE),
//            SafetySetting(HARM_CATEGORY_SEXUAL, Threshold.BLOCK_NONE),
//            SafetySetting(HARM_CATEGORY_MEDICAL, Threshold.BLOCK_NONE),
//            SafetySetting(HARM_CATEGORY_DANGEROUS, Threshold.BLOCK_NONE)
        )
    }
}

enum class Probability {
    NEGLIGIBLE,
    LOW,
    MEDIUM,
    HIGH
}