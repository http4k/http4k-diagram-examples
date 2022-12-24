package org.example

import org.http4k.events.EventFilter
import org.http4k.events.MetadataEvent
import org.http4k.events.plus
import org.http4k.tracing.Actor
import org.http4k.tracing.ActorResolver
import org.http4k.tracing.ActorType.System

object AppName : ActorResolver {
    override fun invoke(p1: MetadataEvent) = Actor(p1.metadata["app"].toString(), System)
}

fun AddAppName(appName: String) = EventFilter { next ->
    {
        next(it + ("app" to appName))
    }
}
