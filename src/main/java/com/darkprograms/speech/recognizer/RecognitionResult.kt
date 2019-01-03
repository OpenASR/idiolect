package com.darkprograms.speech.recognizer

interface RecognitionResult {
    /** @return String representation of what was said
     */
    val response: String?
    val isFinalResponse: Boolean
}
