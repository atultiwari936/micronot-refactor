package ai.sahaj.gurukul.apidesign.service

import ai.sahaj.gurukul.apidesign.model.*
import ai.sahaj.gurukul.apidesign.text_analysis.*
import jakarta.inject.Singleton

@Singleton
class TextService {
    fun analyze(body: TextObjectInput): TextAnalysisOutput {
        val text = body.content
        return TextAnalysisOutput(wordCount(text), charCount(text), charCountWithoutSpaces(text), lineCount(text), uniqueWordsCount(text))
    }
}
