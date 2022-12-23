package org.example

import org.http4k.events.Event

data class DatabaseCall(val name: String) : Event

data class DomainEvent(val name: String) : Event