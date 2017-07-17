package org.openasr.idear.nlp.lex

import com.amazonaws.services.lexruntime.AmazonLexRuntime
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder
import com.amazonaws.services.lexruntime.model.DialogState
import com.amazonaws.services.lexruntime.model.PostTextRequest
import org.openasr.idear.nlp.NlpProvider
import org.openasr.idear.nlp.NlpResultListener
import org.openasr.idear.nlp.NlpResultListener.Companion.Verbosity
import org.openasr.idear.recognizer.awslex.AwsUtils


/**
 * Posts an utterance (String) to Lex to be processed into actions
 */
class LexNlp(val listener: NlpResultListener) : NlpProvider {
    private lateinit var lex: AmazonLexRuntime;
    private lateinit var userId: String
    private val sessionAttributes: MutableMap<String, String> = HashMap<String, String>()

    init {
        // TODO: get userId from Cognito
        userId = "anonymous"
        lex = AmazonLexRuntimeClientBuilder.standard()
                .withCredentials(AwsUtils.credentialsProvider)
                .withRegion(AwsUtils.REGION)
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
    override fun processUtterance(utterance: String) {
        val request = PostTextRequest()
            .withBotName("idear")
            .withBotAlias("PROD")
            .withInputText(utterance)
            .withUserId(userId)
            .withSessionAttributes(sessionAttributes)

        val response = lex.postText(request)
//        println(response)

        /*// Close      - Fulfilled or Failed   (ReadyForFulfillment?)
        // Incomplete - ElicitIntent, ConfirmIntent, ElicitSlot
        response.dialogState
        // ApplyInspection | ApplyFile/Code/LiveTemplate | InvokeAction | ExecuteUserScript
        //
        response.intentName
        response.message
        *//* {genericAttachments:[
                {
                    title:"templateName",
                    subTitle:"templateGroup or contexts?",
                    imageUrl:"?",
                    attachmentLinkUrl:"url to template source",
                    buttons:[{text:"variableName", value:"value"}...]
        }]}*//*
        response.responseCard
        response.slotToElicit
        // {className:"MyComponent", fileName:"MyComponent.tsx", functionName:...}
        response.slots
        // {ext:html,lang:js,context:{},ide:intellij, scope:{},toolWindow:"Terminal" ,PreviousAction:{},...}
        response.sessionAttributes*/


        if (response.dialogState == DialogState.Fulfilled.name) {
            listener.onFulfilled(response.intentName, response.sessionAttributes)
        } else {
//            for (card in response.responseCard.genericAttachments) {
//                listener.onMessage()
//            }
        }
        listener.onMessage(response.message, Verbosity.valueOf(response.sessionAttributes["Verbosity"] ?: "ALL"))
//        sessionAttributes.remove("Verbosity")

        /*when (response.dialogState) {
            DialogState.Fulfilled.name, DialogState.ReadyForFulfillment.name -> listener.onFulfilled()
            DialogState.Failed.name -> listener.onFailure()
            else -> listener.onIncomplete()
        }*/
    }

    /*companion object {
        /** PostTextResult and PostContent differ slightly:
          *  - PostTextResult has ressponseCard (and Maps for context and sesssionAttributes)
          *  - PostContentResult has inputTranscript and audioStream
          */
        fun dispatchNlpResult(nlpListener: NlpResultListener,
                              intentName: String,
                              slots: Map<String, String>,
                              sessionAttributes: Map<String, String>,
                              message: String,
                              dialogState: String,
                              slotToElicit: String) {
        }
    }*/
}
