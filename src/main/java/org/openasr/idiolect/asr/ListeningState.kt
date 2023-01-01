package org.openasr.idiolect.asr

import org.openasr.idiolect.asr.ListeningState.Status.*
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

    val isTerminated get() = getStatus() == TERMINATED

    val isInit get() = getStatus() == INIT

    val isActive get() = getStatus() == ACTIVE

    fun waitIfStandby() =
        lock.withLock { if (status.get() == STANDBY) condition.await() }

    fun standBy() = setStatus(STANDBY)

    fun activate() = setStatus(ACTIVE)

    fun terminate() = setStatus(TERMINATED)
}
