package ai.sahaj.gurukul.apidesign.model

data class TextAnalysisOutput(
        val word_count: Int,
        val character_count_with_spaces: Int,
        val character_count_without_spaces: Int,
        val line_count: Int,
        val unique_words: Int)
