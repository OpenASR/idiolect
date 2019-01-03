package com.darkprograms.speech.recognizer.google

import com.darkprograms.speech.recognizer.RecognitionResult
import java.util.*

/******************************************************************************
 * Class that holds the response and confidence of a Google recognizer request
 *
 * @author Luke Kuza, Duncan Jauncey, Aaron Gokaslan
 */
/**
 * Constructor
 */
class GoogleResponse : RecognitionResult {

    /**
     * Variable that holds the response
     */
    /**
     * Gets the response text of what was said in the submitted Audio to Google
     *
     * @return String representation of what was said
     */
    /**
     * Set the response
     *
     * @param response The response
     */
    override var response: String? = null
    /**
     * Variable that holds the confidence score
     */
    /**
     * Gets the confidence score for the specific request
     *
     * @return The confidence score, ex .922343324323
     */
    /**
     * Set the confidence score for this request
     *
     * @param confidence The confidence score
     */
    var confidence: String? = null

    /**
     * List that holds other possible responses for this request.
     */
    private val otherPossibleResponses = ArrayList<String>(20)


    override var isFinalResponse = true

    /**
     * Get other possible responses for this request.
     * @return other possible responses
     */
    fun getOtherPossibleResponses(): List<String> {
        return otherPossibleResponses
    }
}
