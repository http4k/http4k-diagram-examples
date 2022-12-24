package org.http4k.tracing.persistence

import org.http4k.tracing.TraceRenderPersistence

val TraceRenderPersistence.Companion.NoOp get() = TraceRenderPersistence { }