package org.openasr.idear.recognizer.awslex

import com.amazonaws.auth.*
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder
import com.amazonaws.services.cognitoidentity.model.*

object AwsUtils {
    val ACCOUNT_ID = "317105675130"
    val REGION = "us-east-1"
    val ID_POOL_ID = "us-east-1:212de8d8-ca7e-4c5e-9628-382c17a50cbc"

    private var _credentialsProvider: AWSCredentialsProvider? = null
    val credentialsProvider: AWSCredentialsProvider
        get() {
            if (_credentialsProvider == null) {
                // TODO: use STSProfileCredentialsServiceProvider?  would let admins to add Slots etc by voice
                val credentials = getCognitoCredentials()
                _credentialsProvider = AWSStaticCredentialsProvider(BasicSessionCredentials(credentials.accessKeyId, credentials.secretKey, credentials.sessionToken))
            }
            return _credentialsProvider ?: throw AssertionError("Logged out by another thread")
        }

    private fun getCognitoCredentials(): Credentials {
        val cognito = AmazonCognitoIdentityClientBuilder.standard().withRegion(REGION).build()
        // TODO: could allow users to provide Google, Facebook tokens:
        //     .withLogins(mapOf<String, String>("accounts.google.com" to "my.google.token")
        val id = cognito.getId(GetIdRequest().withAccountId(ACCOUNT_ID).withIdentityPoolId(ID_POOL_ID))
        return cognito.getCredentialsForIdentity(GetCredentialsForIdentityRequest().withIdentityId(id.identityId)).credentials
    }
}
