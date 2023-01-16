package ai.sahaj.gurukul.apidesign.text_analysis

fun wordCount(sentence: String): Int {
    if (sentence.isEmpty()) return 0
    return sentence.trim().split("\\s+".toRegex()).size
}

fun lineCount(sentence: String): Int {
    if (sentence.isEmpty()) return 0
    return sentence.trim().split('.').size - 1
}

fun charCount(sentence: String): Int {
    if (sentence.isEmpty()) return 0
    return sentence.trim().length
}

fun charCountWithoutSpaces(sentence: String): Int {
    if (sentence.isEmpty()) return 0
    val spaces = sentence.trim().count{it == ' '}
    return charCount(sentence) - spaces
}

fun uniqueWordsCount(sentence: String): Int {
    if (sentence.isEmpty()) return 0
    // first remove all non-letter character and then split on spaces
    val words = sentence.trim().replace("[^a-zA-Z ]".toRegex(), "").split("\\s+".toRegex())
    return words.toHashSet().size
}
