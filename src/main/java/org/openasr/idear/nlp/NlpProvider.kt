package org.openasr.idear.nlp

import org.openasr.idear.settings.ConfigurableExtension

interface NlpProvider : ConfigurableExtension {
    fun processUtterance(utterance: String)
}
