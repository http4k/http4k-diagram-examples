package org.http4k.tracing

interface TracePersistence {
    fun store(trace: NamedTrace)
    fun load(): Iterable<NamedTrace>

    companion object
}

