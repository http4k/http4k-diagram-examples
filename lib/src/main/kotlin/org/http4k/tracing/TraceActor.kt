package org.http4k.tracing

import org.http4k.events.MetadataEvent

data class TraceActor(val name: String, val type: ActorType)

fun interface TraceActorResolver : (MetadataEvent) -> String

enum class ActorType {
    Person, System, Database, Queue
}
