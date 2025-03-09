package com.github.cawtoz.minestom.core.event

import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventListener
import net.minestom.server.event.EventNode

/**
 * EventBus is a global event manager that simplifies event registration.
 * It provides utility functions for registering event listeners and event handlers.
 */
object EventBus {

    val eventNode = EventNode.all("global")

    /**
     * Registers a simple event listener.
     * @param T The event type.
     * @param handler The function that handles the event.
     */
    inline fun <reified T : Event> registerEvent(
        noinline handler: (T) -> Unit
    ) {
        println("Registering event: ${T::class.simpleName}")
        eventNode.addListener(T::class.java, handler)
        MinecraftServer.getGlobalEventHandler().addChild(eventNode)
    }

    /**
     * Registers an event listener using a builder pattern.
     * @param T The event type.
     * @param builder The builder function to configure the event listener.
     */
    inline fun <reified T : Event> register(
        builder: EventListener.Builder<T>.() -> Unit
    ) {
        val eventListener = EventListener.builder(T::class.java).apply(builder).build()
        eventNode.addListener(eventListener)
    }

    /**
     * Registers an event handler that contains multiple event listeners.
     * @param handler The event handler to register.
     */
    fun registerHandler(handler: EventHandler) {
        handler.register(eventNode)
    }

    /**
     * Unregisters an event handler, removing all its listeners.
     * @param handler The event handler to unregister.
     */
    fun unregisterHandler(handler: EventHandler) {
        handler.unregister(eventNode)
    }

}
