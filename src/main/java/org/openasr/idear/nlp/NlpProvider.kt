package org.openasr.idear.nlp

import org.openasr.idear.settings.ConfigurableExtension

interface NlpProvider : ConfigurableExtension {
    fun processNlpRequest(nlpRequest: NlpRequest)
}
