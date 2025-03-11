package com.github.cawtoz.stomium.event

import com.github.cawtoz.stomium.event.registration.BuilderEventRegistration
import com.github.cawtoz.stomium.event.registration.EventRegistration
import com.github.cawtoz.stomium.event.registration.SimpleEventRegistration
import net.minestom.server.event.Event
import net.minestom.server.event.EventListener

/**
 * Base class for managing event handlers.
 * This class allows grouping multiple event listeners and registering them efficiently.
 *
 * @param eventSetup A lambda function where events should be registered.
 * @param nodeName Optional event node name where the handler should be registered. If null, it uses the global handler.
 */
abstract class EventHandler(eventSetup: EventHandler.() -> Unit, val nodeName: String? = null) {

    /**
     * List of event registrations to be processed when registering the handler.
     */
    @PublishedApi
    internal val eventRegistrations = mutableListOf<EventRegistration<out Event>>()

    init {
        eventSetup()
    }

    /**
     * Registers a simple event listener.
     *
     * @param T The type of event to listen for.
     * @param handler The function that handles the event when triggered.
     */
    inline fun <reified T : Event> onEvent(noinline handler: (T) -> Unit) {
        eventRegistrations.add(SimpleEventRegistration(T::class.java, nodeName, handler))
    }

    /**
     * Registers an event listener using a builder pattern.
     * This allows additional customization of the event listener.
     *
     * @param T The type of event to listen for.
     * @param builder The builder function to configure the event listener.
     */
    inline fun <reified T : Event> onBuilderEvent(noinline builder: EventListener.Builder<T>.() -> Unit) {
        eventRegistrations.add(BuilderEventRegistration(T::class.java, nodeName, builder))
    }

}
