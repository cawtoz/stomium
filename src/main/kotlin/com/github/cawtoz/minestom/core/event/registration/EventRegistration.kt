package com.github.cawtoz.minestom.core.event.registration

import net.minestom.server.event.Event

/**
 * Represents a generic event registration.
 * This sealed class is used to store event-related data before creating an actual EventListener.
 *
 * @param T The event type.
 * @property eventClass The class of the event being registered.
 * @property eventNode The name of the event node where the event should be registered.
 */
sealed class EventRegistration<T : Event>(
    val eventClass: Class<T>,
    val eventNode: String?
)