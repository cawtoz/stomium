package com.github.cawtoz.minestom.core.event

import com.github.cawtoz.minestom.core.event.registration.BuilderEventRegistration
import com.github.cawtoz.minestom.core.event.registration.EventRegistration
import com.github.cawtoz.minestom.core.event.registration.SimpleEventRegistration
import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventListener
import net.minestom.server.event.EventNode

/**
 * Manages global event registration, providing utilities for handling event listeners and handlers.
 */
object EventManager {

    /**
     * The global event handler used to register events when no specific node is provided.
     */
    private val globalEventHandler = MinecraftServer.getGlobalEventHandler()

    /**
     * Registers a simple event listener.
     *
     * @param T The event type to listen for.
     * @param nodeName The event node where the listener should be registered. If null, it registers globally.
     * @param handler The function to handle the event.
     */
    inline fun <reified T : Event> onEvent(nodeName: String? = null, noinline handler: (T) -> Unit) {
        registerListener(SimpleEventRegistration(T::class.java, nodeName, handler))
    }

    /**
     * Registers an event listener using a builder pattern.
     *
     * @param T The event type to listen for.
     * @param nodeName The event node where the listener should be registered. If null, it registers globally.
     * @param builder The function to configure the event listener.
     */
    inline fun <reified T : Event> onBuilderEvent(nodeName: String? = null, noinline builder: EventListener.Builder<T>.() -> Unit) {
        registerListener(BuilderEventRegistration(T::class.java, nodeName, builder))
    }

    /**
     * Registers an event handler that contains multiple event listeners.
     *
     * @param eventHandler The event handler containing multiple event registrations.
     */
    fun registerHandler(eventHandler: EventHandler) {
        eventHandler.eventRegistrations.forEach { registerListener(it) }
    }

    /**
     * Registers an event based on its EventRegistration type.
     *
     * @param eventRegistration The event registration to be processed.
     */
    @PublishedApi
    internal fun registerListener(eventRegistration: EventRegistration<out Event>) {

        val eventListener: EventListener<Event> = when (eventRegistration) {
            is SimpleEventRegistration<*> -> {
                @Suppress("UNCHECKED_CAST")
                EventListener.builder(eventRegistration.eventClass as Class<Event>)
                    .handler(eventRegistration.handler as (Event) -> Unit)
                    .build()
            }
            is BuilderEventRegistration<*> -> {
                @Suppress("UNCHECKED_CAST")
                EventListener.builder(eventRegistration.eventClass as Class<Event>)
                    .apply(eventRegistration.builder as EventListener.Builder<Event>.() -> Unit)
                    .build()
            }
        }

        val eventNode = eventRegistration.eventNode
        if (eventNode == null) {
            globalEventHandler.addListener(eventListener)
        } else {
            EventNode.all(eventNode).addListener(eventListener)
        }

    }

}
