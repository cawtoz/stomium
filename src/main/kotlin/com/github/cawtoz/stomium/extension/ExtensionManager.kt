package com.github.cawtoz.stomium.extension

import java.util.UUID

/**
 * Provides a global access point for managing extensions.
 * Uses an internal `ExtensionLoader` to handle loading, updating, and unloading extensions.
 */
object ExtensionManager {

    /** The global instance of `ExtensionLoader` handling extensions. */
    private val extensionLoader = ExtensionLoader("extensions")

    /** Loads all extensions from the "extensions" folder. */
    fun init() = extensionLoader.loadExtensions()

    /** Updates all extensions by detecting modified, new, or removed JARs. */
    fun updateExtensions() = extensionLoader.updateExtensions()

    /**
     * Updates a specific extension by its UUID.
     *
     * @param id The UUID of the extension to update.
     */
    fun updateExtensionById(id: UUID) = extensionLoader.updateExtensionById(id)

    /**
     * Enables an extension by UUID.
     *
     * @param id The UUID of the extension to enable.
     */
    fun enableById(id: UUID) = extensionLoader.enableById(id)

    /**
     * Disables an extension by UUID.
     *
     * @param id The UUID of the extension to disable.
     */
    fun disableById(id: UUID) = extensionLoader.disableById(id)

    /** Enables all loaded extensions. */
    fun enableAll() = extensionLoader.enableAll()

    /** Disables all loaded extensions. */
    fun disableAll() = extensionLoader.disableAll()

    /**
     * Unloads an extension by UUID.
     *
     * @param id The UUID of the extension to unload.
     */
    fun unload(id: UUID) = extensionLoader.unload(id)

    /**
     * Returns the total number of loaded extensions.
     *
     * @return The total count of currently loaded extensions.
     */
    fun size(): Int = extensionLoader.size()

}
