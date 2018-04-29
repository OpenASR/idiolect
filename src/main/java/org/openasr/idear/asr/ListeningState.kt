package org.openasr.idear.asr

import org.openasr.idear.asr.ListeningState.Status.*
import java.util.concurrent.atomic.AtomicReference

object ListeningState {
    private val status = AtomicReference(INIT)

    enum class Status { INIT, ACTIVE, STANDBY, TERMINATED }

    private fun setStatus(s: Status) = status.getAndSet(s)

    fun getStatus() = status.get()

    val isTerminated: Boolean
        get() = getStatus() == TERMINATED

    val isInit: Boolean
        get() = getStatus() == INIT

    val isActive: Boolean
        get() = getStatus() == ACTIVE

    fun standBy() = STANDBY == setStatus(STANDBY)

    fun activate() = ACTIVE == setStatus(ACTIVE)

    fun terminate() = setStatus(TERMINATED)
}
