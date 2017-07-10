package org.openasr.idear.nlp.lex

import com.amazonaws.services.lexruntime.AmazonLexRuntime
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder
import com.amazonaws.services.lexruntime.model.PostTextRequest

class LexNlp {
    private lateinit var lex: AmazonLexRuntime;
//    private lateinit var lexAsync: AmazonLexRuntimeAsync
    private lateinit var userId: String

    init {
        // TODO: get userId from Cognito
        userId = "TODO"
//        lexAsync = AmazonLexRuntimeAsyncClientBuilder.standard().build()
        lex = AmazonLexRuntimeClientBuilder.standard()
//                .withClientConfiguration()
//                .withCredentials()
//                .withRegion()
                .build()
    }

    /**
     * @param utterance - what the user said
     * @param sessionAttributes - **new** context.
     *                  eg: {ext:html,lang:js,ide:intellij, context:{}, scope:{}, toolWindow:"Terminal", PreviousAction:{}, ...}
     *
     * @throws NotFoundException
     *         The resource (such as the Amazon Lex bot or an alias) that is referred to is not found.
     * @throws BadRequestException
     *         Request validation failed, there is no usable message in the context, or the bot build failed.
     * @throws LimitExceededException
     *         Exceeded a limit.
     * @throws InternalFailureException
     *         Internal service error. Retry the call.
     * @throws ConflictException
     *         Two clients are using the same AWS account, Amazon Lex bot, and user ID.
     * @throws DependencyFailedException
     *         One of the downstream dependencies, such as AWS Lambda or Amazon Polly, threw an exception. For example,
     *         if Amazon Lex does not have sufficient permissions to call a Lambda function, it results in Lambda
     *         throwing an exception.
     * @throws BadGatewayException
     *         Either the Amazon Lex bot is still building, or one of the dependent services (Amazon Polly, AWS Lambda)
     *         failed with an internal service error.
     * @throws LoopDetectedException
     *         Lambda fulfilment function returned <code>DelegateDialogAction</code> to Amazon Lex without changing any
     *         slot values.
     */
    fun processUtterance(utterance: String, sessionAttributes: Map<String, String>? = null) {
        val request = PostTextRequest()
                .withBotName("idear")
                .withBotAlias("PROD")
                .withInputText(utterance)
                .withUserId(userId)
                .withSessionAttributes(sessionAttributes)

        val response = lex.postText(request)
        println(response)

        // Close      - Fulfilled or Failed   (ReadyForFulfillment?)
        // Incomplete - ElicitIntent, ConfirmIntent, ElicitSlot
        response.dialogState
        // ApplyInspection | ApplyFile/Code/LiveTemplate | InvokeAction | ExecuteUserScript
        //
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
        // {ext:html,lang:js,context:{},ide:intellij, scope:{},toolWindow:"Terminal" ,PreviousAction:{},...}
        response.sessionAttributes


        //
    }
}

fun main(args: Array<String>) {
    val lex = LexNlp()
    lex.processUtterance("create a new class")
}