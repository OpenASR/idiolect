package com.darkprograms.speech.recognizer.awslex

import com.amazonaws.services.lexruntime.model.PostContentResult
import com.darkprograms.speech.recognizer.RecognitionResult

class LexResponse(val result: PostContentResult) : RecognitionResult {
    override val response: String
        get() = result.inputTranscript

    override val isFinalResponse: Boolean
        get() {
            val state = result.dialogState
            return "Fulfilled" == state || "Failed" == state
        }
}
