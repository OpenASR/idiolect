package org.openasr.idear.recognizer.awslex

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder
import com.amazonaws.services.cognitoidentity.model.Credentials
import com.amazonaws.services.cognitoidentity.model.GetCredentialsForIdentityRequest
import com.amazonaws.services.cognitoidentity.model.GetIdRequest
import java.util.logging.Logger

class AwsUtils {
    companion object {
        private val logger = Logger.getLogger(AwsUtils::class.java.simpleName)
        val ACCOUNT_ID = "317105675130"
        val REGION = "us-east-1"
        val ID_POOL_ID = "us-east-1:212de8d8-ca7e-4c5e-9628-382c17a50cbc"

        private var _credentialsProvider: AWSCredentialsProvider? = null
        val credentialsProvider: AWSCredentialsProvider
            get() {
                if (_credentialsProvider == null) {
                    // TODO: use STSProfileCredentialsServiceProvider?  would let admins to add Slots etc by voice
                    val credentials = getCognitoCredentials()
                    _credentialsProvider = AWSStaticCredentialsProvider(BasicAWSCredentials(credentials.accessKeyId, credentials.secretKey))
                }
                return _credentialsProvider ?: throw AssertionError("Logged out by another thread")
            }

        fun getCognitoCredentials(): Credentials {
            val cognito = AmazonCognitoIdentityClientBuilder.standard().withRegion(REGION).build()
            // TODO: could allow users to provide Google, Facebook tokens:
            //     .withLogins(mapOf<String, String>("accounts.google.com" to "my.google.token")
            val id = cognito.getId(GetIdRequest().withAccountId(ACCOUNT_ID).withIdentityPoolId(ID_POOL_ID))
            return cognito.getCredentialsForIdentity(GetCredentialsForIdentityRequest().withIdentityId(id.identityId)).credentials
        }
    }
}
