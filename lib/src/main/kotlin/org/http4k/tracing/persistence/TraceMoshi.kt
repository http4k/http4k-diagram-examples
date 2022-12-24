package org.http4k.tracing.persistence

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings
import org.http4k.tracing.BiDirectional
import org.http4k.tracing.Event
import org.http4k.tracing.RequestResponse
import org.http4k.tracing.Trace
import org.http4k.tracing.TraceActor
import org.http4k.tracing.TraceActor.Database
import org.http4k.tracing.TraceActor.Events
import org.http4k.tracing.TraceActor.External
import org.http4k.tracing.TraceActor.Internal
import org.http4k.tracing.TraceActor.Person

object TraceMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .addLast(ListAdapter)
        .add(
            PolymorphicJsonAdapterFactory
                .of(Trace::class.java, "type")
                .withSubtype(RequestResponse::class.java, "RequestResponse")
                .withSubtype(BiDirectional::class.java, "BiDirectional")
                .withSubtype(Event::class.java, "Event")
        )
        .add(
            PolymorphicJsonAdapterFactory
                .of(TraceActor::class.java, "type")
                .withSubtype(Internal::class.java, "Internal")
                .withSubtype(External::class.java, "External")
                .withSubtype(Database::class.java, "Database")
                .withSubtype(Events::class.java, "Events")
                .withSubtype(Person::class.java, "Person")
        )
        .asConfigurable()
        .withStandardMappings()
        .done()
)