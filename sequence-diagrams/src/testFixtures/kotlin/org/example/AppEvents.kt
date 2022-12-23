package org.example

import org.http4k.events.EventFilter
import org.http4k.events.EventFilters.AddZipkinTraces
import org.http4k.events.plus
import org.http4k.events.then

fun AppEvents(name: String) = AddZipkinTraces().then(AddAppName(name))

private fun AddAppName(appName: String) = EventFilter { next ->
    {
        next(it + ("app" to appName))
    }
}
