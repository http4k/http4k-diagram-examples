package org.http4k.tracing.persistence

import org.http4k.tracing.NamedTrace
import org.http4k.tracing.TracePersistence

fun TracePersistence.Companion.InMemory() = object : TracePersistence {
    private val list = mutableListOf<NamedTrace>()
    override fun store(trace: NamedTrace) {
        list += trace
    }

    override fun load() = list
}