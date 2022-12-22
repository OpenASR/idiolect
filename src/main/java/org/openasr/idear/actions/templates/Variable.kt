package org.openasr.idear.actions.templates

/** The display name can also provide localized variants by specifying key and resource-bundle attributes additionally */
data class Variable(val name: String, val expression: String, val defaultValue: String, val alwaysStopAt: Boolean) {

}
