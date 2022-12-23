package org.example

import org.http4k.tracing.SystemDescriptor

data class AppName(val value: String) : SystemDescriptor {
    override val name: String = value
}