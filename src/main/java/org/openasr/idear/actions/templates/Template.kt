package org.openasr.idear.actions.templates

import com.intellij.codeInsight.template.TemplateContextType

/**
 * @param name
 * @param value - the template body
 * @param description
 * @param toReformat
 * @param toShortenFQNames
 *
 * @see https://plugins.jetbrains.com/docs/intellij/template-support.html
 */
class Template(val name: String, val value: String, val description: String, val shortcut: String?,
               val toReformat: Boolean, val toShortenFQNames: Boolean) {

//    val c = TemplateContextType
    fun addVariable(variable: Variable) {}
}
