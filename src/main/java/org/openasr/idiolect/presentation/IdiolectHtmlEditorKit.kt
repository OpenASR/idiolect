package org.openasr.idiolect.presentation

import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.StyleSheet

class IdiolectHtmlEditorKit : HTMLEditorKit() {
    private var styleSheet: StyleSheet? = null
    override fun getStyleSheet(): StyleSheet {
        return styleSheet ?: super.getStyleSheet()
    }

    override fun setStyleSheet(s: StyleSheet?) {
        styleSheet = s
    }

    /** Prevents styles leaking out onto the master application-wide style sheet */
    fun withStyle(style: String): HTMLEditorKit {
        this.styleSheet = StyleSheet().apply {
            addStyleSheet(defaultStyleSheet)
            addRule(style)
        }

        return this
    }

    fun withStyles(styles: List<String>): HTMLEditorKit {
        this.styleSheet = StyleSheet().apply {
            addStyleSheet(defaultStyleSheet)
            styles.forEach {
                addRule(it)
            }
        }

        return this
    }

    var defaultStyleSheet: StyleSheet
        get() = super.getStyleSheet()
        set(s) {
            super.setStyleSheet(s)
        }
}
