package com.jetbrains.idear.tts

interface TTSProvider {
    fun say(text: String?)
    fun dispose()
}