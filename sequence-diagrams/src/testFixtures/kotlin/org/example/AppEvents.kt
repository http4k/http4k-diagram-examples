package org.example

import org.http4k.events.EventFilters.AddZipkinTraces
import org.http4k.events.then

fun AppEvents(name: String) = AddZipkinTraces().then(AddAppName(name))
