package com.github.cawtoz.stomium.event.registration

import net.minestom.server.event.Event

/**
 * Represents a simple event registration using a direct event handler.
 * This is used when registering an event with a basic lambda function as the handler.
 *
 * @param T The event type.
 * @property handler The function that will handle the event when triggered.
 */
class SimpleEventRegistration<T : Event>(
    eventClass: Class<T>,
    eventNode: String?,
    val handler: (T) -> Unit
) : EventRegistration<T>(eventClass, eventNode)