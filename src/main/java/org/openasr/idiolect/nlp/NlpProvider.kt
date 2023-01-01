package org.openasr.idiolect.nlp

import org.openasr.idiolect.settings.ConfigurableExtension

interface NlpProvider : ConfigurableExtension {
    fun processNlpRequest(nlpRequest: NlpRequest)
}
