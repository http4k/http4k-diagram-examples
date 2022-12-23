package org.example

import org.http4k.events.EventFilter
import org.http4k.events.EventFilters.AddZipkinTraces
import org.http4k.events.plus
import org.http4k.events.then
import org.http4k.tracing.SystemDescriptor

fun AppEvents(name: SystemDescriptor) = AddZipkinTraces().then(AddAppName(name))

private fun AddAppName(systemDescriptor: SystemDescriptor) = EventFilter { next ->
    {
        next(it + ("app" to systemDescriptor.name))
    }
}
