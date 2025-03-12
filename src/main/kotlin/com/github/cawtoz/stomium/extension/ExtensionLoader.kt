package com.github.cawtoz.stomium.extension

import com.github.cawtoz.stomium.console.ConsoleUtils
import java.io.File
import java.net.URLClassLoader
import java.util.*

/**
 * Handles the dynamic loading and updating of extensions from a specified folder.
 *
 * @param pathName The directory where extensions are stored.
 */
class ExtensionLoader(private val pathName: String) {

    private val extensionsFolder = File(pathName)
    private val extensions = mutableMapOf<UUID, Extension>()
    private val extensionTimestamps = mutableMapOf<File, Long>()

    /**
     * Loads all extensions from the specified folder.
     * Ensures that only valid extensions are loaded.
     */
    fun loadExtensions() {
        if (!extensionsFolder.exists()) extensionsFolder.mkdirs()

        getNewExtensions().forEach { loadJarExtension(it) }
        ConsoleUtils.sendInfo("Total extensions loaded: ${extensions.size}")
        enableAll()
    }

    /**
     * Attempts to load a JAR file as an extension.
     *
     * @param jarFile The JAR file containing the extension.
     */
    private fun loadJarExtension(jarFile: File) {
        val extension = isValidExtension(jarFile) ?: return
        extensions[extension.id] = extension
        extensionTimestamps[jarFile] = jarFile.lastModified()
        extension.onLoad()
        ConsoleUtils.sendInfo("Loaded extension: ${extension.id}")
    }

    /**
     * Validates if a JAR file is a proper extension and returns its instance.
     *
     * @param jarFile The JAR file to verify.
     * @return The `Extension` instance if valid, or `null` if invalid.
     */
    private fun isValidExtension(jarFile: File): Extension? {
        return try {
            val classLoader = URLClassLoader(arrayOf(jarFile.toURI().toURL()), Extension::class.java.classLoader)

            val properties = classLoader.getResourceAsStream("extension.properties")?.use { stream ->
                Properties().apply { load(stream) }
            } ?: return null.also {
                ConsoleUtils.sendWarning("No extension.properties found in ${jarFile.name}")
            }

            val mainClassName = properties.getProperty("main-class") ?: return null.also {
                ConsoleUtils.sendWarning("Missing 'main-class' in ${jarFile.name}")
            }

            val clazz = Class.forName(mainClassName, true, classLoader)
            val instance = clazz.getDeclaredConstructor().newInstance()

            if (instance is Extension) instance
            else null.also { ConsoleUtils.sendError("Class $mainClassName does not implement Extension") }

        } catch (ex: Exception) {
            ConsoleUtils.sendError("Invalid extension JAR: ${jarFile.name}: ${ex.message}")
            null
        }
    }

    /**
     * Detects and applies updates to extensions by checking for new, modified, or removed JAR files.
     */
    fun updateExtensions() {
        ConsoleUtils.sendInfo("Checking for extension updates...")

        getRemovedExtensions().forEach { removeExtension(it) }
        getModifiedExtensions().forEach { reloadExtension(it) }
        getNewExtensions().forEach { loadJarExtension(it) }

        ConsoleUtils.sendInfo("Extension update check completed.")
    }

    /**
     * Removes an extension that no longer exists.
     *
     * @param jarFile The JAR file of the extension that was removed.
     */
    private fun removeExtension(jarFile: File) {
        val id = getExtensionIdByFile(jarFile) ?: return
        disableById(id)
        unload(id)
        ConsoleUtils.sendInfo("Removed extension: ${jarFile.name}")
    }

    /**
     * Reloads an extension that was modified.
     *
     * @param jarFile The JAR file of the modified extension.
     */
    private fun reloadExtension(jarFile: File) {
        val id = getExtensionIdByFile(jarFile)
        ConsoleUtils.sendInfo("Modified extension detected: ${jarFile.name}. Reloading...")

        if (id != null) {
            disableById(id)
            unload(id)
        }
        loadJarExtension(jarFile)
    }

    /**
     * Retrieves the UUID of an extension based on its JAR file name.
     *
     * @param jarFile The JAR file of the extension.
     * @return The UUID of the extension if found, or `null` if not found.
     */
    private fun getExtensionIdByFile(jarFile: File): UUID? {
        return extensions.entries.find { it.value::class.java.simpleName in jarFile.name }?.key
    }

    /**
     * Gets new JAR files that have not been loaded yet.
     *
     * @return A set of JAR files that are new.
     */
    private fun getNewExtensions(): Set<File> {
        return getJarFiles() - extensionTimestamps.keys
    }

    /**
     * Gets JAR files that have been removed from the extensions folder.
     *
     * @return A set of JAR files that are no longer present.
     */
    private fun getRemovedExtensions(): Set<File> {
        return extensionTimestamps.keys - getJarFiles()
    }

    /**
     * Gets JAR files that have been modified by comparing timestamps.
     *
     * @return A set of modified JAR files.
     */
    private fun getModifiedExtensions(): Set<File> {
        return getJarFiles().filter { file ->
            val lastModified = file.lastModified()
            val storedTimestamp = extensionTimestamps[file]
            storedTimestamp != null && lastModified > storedTimestamp
        }.toSet()
    }

    /**
     * Retrieves all JAR files present in the extensions folder.
     *
     * @return A set of all JAR files in the directory.
     */
    private fun getJarFiles(): Set<File> {
        return extensionsFolder.listFiles { file -> file.extension == "jar" }?.toSet() ?: emptySet()
    }

    /**
     * Enables an extension by UUID.
     *
     * @param id The UUID of the extension to enable.
     */
    fun enableById(id: UUID) {
        extensions[id]?.let {
            it.onEnable()
            it.updateStatus(Extension.Status.ENABLED)
        }
    }

    /**
     * Disables an extension by UUID.
     *
     * @param id The UUID of the extension to disable.
     */
    fun disableById(id: UUID) {
        extensions[id]?.let {
            it.onDisable()
            it.updateStatus(Extension.Status.DISABLED)
        }
    }

    /** Enables all loaded extensions. */
    fun enableAll() = extensions.values.forEach { it.onEnable() }

    /** Disables all loaded extensions. */
    fun disableAll() = extensions.values.forEach { it.onDisable() }

    /**
     * Unloads and removes an extension by UUID.
     *
     * @param id The UUID of the extension to unload.
     */
    fun unload(id: UUID) {
        extensions.remove(id)?.onDisable()
        extensionTimestamps.entries.removeIf { it.key.name.contains(id.toString()) }
    }

    /**
     * Updates a specific extension by UUID.
     *
     * @param id The UUID of the extension to update.
     */
    fun updateExtensionById(id: UUID) {
        val file = getModifiedExtensions().find { it.name.contains(id.toString()) } ?: return
        reloadExtension(file)
    }

    /**
     * Returns the total number of loaded extensions.
     *
     * @return The number of currently loaded extensions.
     */
    fun size(): Int = extensions.size

}
