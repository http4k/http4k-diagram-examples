package org.http4k.tracing

data class NamedTrace(val name: String, val traces: List<Trace>)