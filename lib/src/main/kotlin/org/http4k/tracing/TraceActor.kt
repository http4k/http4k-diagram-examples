package org.http4k.tracing

data class TraceActor(val name: String, val type: ActorType)

enum class ActorType {
    Person, System, Database, Queue
}