package com.github.cawtoz.stomium.extension

import java.util.UUID

/**
 * Represents a modular extension that can be loaded, enabled, and disabled dynamically.
 * Each extension has a unique identifier, a name, and a status.
 *
 * @param name The display name of the extension.
 */
abstract class Extension(val name: String) {

    /** The unique identifier of the extension, generated automatically. */
    val id: UUID = UUID.randomUUID()

    /** The current status of the extension. */
    var status: Status = Status.DISABLED
        private set

    /**
     * Called when the extension is loaded.
     * This can be overridden to perform initialization logic.
     */
    open fun onLoad() {}

    /**
     * Called when the extension is enabled.
     * Must be implemented by subclasses.
     * Automatically updates the extension status.
     */
    abstract fun onEnable()

    /**
     * Called when the extension is disabled.
     * Must be implemented by subclasses.
     * Automatically updates the extension status.
     */
    abstract fun onDisable()

    /** Enum representing the possible statuses of an extension. */
    enum class Status {
        ENABLED, DISABLED, OUTDATED
    }

    /**
     * Updates the extension status.
     *
     * @param newStatus The new status to set.
     */
    fun updateStatus(newStatus: Status) {
        status = newStatus
    }

}
