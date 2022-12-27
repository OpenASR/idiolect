package org.openasr.idear.asr

import org.openasr.idear.asr.ListeningState.Status.*
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object ListeningState {
    private val status = AtomicReference(INIT)
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    enum class Status { INIT, ACTIVE, STANDBY, TERMINATED }

    private fun setStatus(s: Status) {
        lock.withLock {
            status.set(s)
            condition.signal()
        }
    }

    fun getStatus() = status.get()

    val isTerminated: Boolean
        get() = getStatus() == TERMINATED

    val isInit: Boolean
        get() = getStatus() == INIT

    val isActive: Boolean
        get() = getStatus() == ACTIVE

    fun waitIfStandby() =
        lock.withLock { if (status.get() == STANDBY) condition.await() }

    fun standBy() = setStatus(STANDBY)

    fun activate() = setStatus(ACTIVE)

    fun terminate() = setStatus(TERMINATED)
}
