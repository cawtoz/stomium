package com.github.cawtoz.stomium.event.registration

import net.minestom.server.event.Event
import net.minestom.server.event.EventListener

/**
 * Represents an event registration using an event builder.
 * This is used when registering an event with additional configurations via a builder pattern.
 *
 * @param T The event type.
 * @property builder The builder function that configures the EventListener before registration.
 */
class BuilderEventRegistration<T : Event>(
    eventClass: Class<T>,
    eventNode: String?,
    val builder: EventListener.Builder<T>.() -> Unit
) : EventRegistration<T>(eventClass, eventNode)