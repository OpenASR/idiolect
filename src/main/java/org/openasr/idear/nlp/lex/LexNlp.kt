package org.openasr.idear.nlp.lex

import com.amazonaws.services.lexruntime.AmazonLexRuntime
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder
import com.amazonaws.services.lexruntime.model.PostTextRequest

class LexNlp {
    private lateinit var lex: AmazonLexRuntime;
//    private lateinit var lexAsync: AmazonLexRuntimeAsync
    private lateinit var userId: String

    init {
//        lexAsync = AmazonLexRuntimeAsyncClientBuilder.standard().build()
        lex = AmazonLexRuntimeClientBuilder.standard()
//                .withClientConfiguration()
//                .withCredentials()
//                .withRegion()
                .build()
    }

    fun processUtterance(utterance: String, sessionAttributes: Map<String, String>? = null) {
        val request = PostTextRequest()
                .withBotName("idear")
                .withBotAlias("PROD")
                .withInputText(utterance)
                .withUserId(userId)
                .withSessionAttributes(sessionAttributes)

        val response = lex.postText(request)
        println(response)

        // ElicitIntent, ConfirmIntent, ElicitSlot, Fulfilled, ReadyForFulfillment, Failed
        response.dialogState
        // ApplyInspection | ApplyFile/Code/LiveTemplate | ApplyAction | ExecuteUserScript
        response.intentName
        response.message
        /* {genericAttachments:[
                {
                    title:"templateName",
                    subTitle:"templateGroup or contexts?",
                    imageUrl:"?",
                    attachmentLinkUrl:"url to template source",
                    buttons:[{text:"variableName", value:"value"}...]
        }]}*/
        response.responseCard
        response.slotToElicit
        // {className:"MyComponent", fileName:"MyComponent.tsx", functionName:...}
        response.slots
        // {ext:html,lang:js,context:{}, scope:{},toolWindow:"Terminal" ,PreviousAction:{},...}
        response.sessionAttributes

//        response.dialogState
    }
}

fun main(args: Array<String>) {
    val lex = LexNlp()
    lex.processUtterance("create a new class")
}