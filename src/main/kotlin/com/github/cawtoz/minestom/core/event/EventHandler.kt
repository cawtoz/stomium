package com.github.cawtoz.minestom.core.event

import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.EventListener

/**
 * Base class for event handlers in Minestom.
 * This class allows grouping multiple event listeners and registering them easily.
 */
abstract class EventHandler(eventSetup: EventHandler.() -> Unit) {

    val eventListeners = mutableListOf<EventListener<out Event>>()

    init {
        eventSetup()
    }

    /**
     * Registers a simple event listener.
     * @param T The event type.
     * @param handler The function that handles the event.
     */
    inline fun <reified T : Event> onEvent(
        noinline handler: (T) -> Unit
    ) {
        val eventListener = EventListener.builder(T::class.java).handler(handler).build()
        eventListeners.add(eventListener)
    }

    /**
     * Registers an event listener using a builder pattern.
     * @param T The event type.
     * @param builder The builder function to configure the event listener.
     */
    inline fun <reified T : Event> on(
        builder: EventListener.Builder<T>.() -> Unit
    ) {
        val eventListener = EventListener.builder(T::class.java).apply(builder).build()
        eventListeners.add(eventListener)
    }

    /**
     * Registers all stored event listeners into an event node.
     * @param eventNode The event node where the listeners will be added.
     */
    fun register(eventNode: EventNode<Event>) {
        eventListeners.forEach { eventNode.addListener(it) }
    }

    /**
     * Unregisters all stored event listeners from an event node.
     * @param eventNode The event node from which the listeners will be removed.
     */
    fun unregister(eventNode: EventNode<Event>) {
        eventListeners.forEach { eventNode.removeListener(it) }
    }

}
