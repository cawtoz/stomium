package com.github.cawtoz.stomium.console

import net.kyori.adventure.text.format.NamedTextColor

/**
 * Utility object for sending colored messages to the console using ANSI escape codes.
 */
object ConsoleUtils {

    private fun getAnsiCode(color: NamedTextColor?): String {
        return when (color) {
            NamedTextColor.BLUE -> "\u001B[34m"
            NamedTextColor.GREEN -> "\u001B[32m"
            NamedTextColor.YELLOW -> "\u001B[33m"
            NamedTextColor.RED -> "\u001B[31m"
            else -> "\u001B[0m"
        }
    }

    private const val RESET = "\u001B[0m"

    fun sendInfo(message: String) {
        sendConsole("$message", NamedTextColor.BLUE)
    }

    fun sendWarning(message: String) {
        sendConsole("[Error] $message", NamedTextColor.YELLOW)
    }

    fun sendError(message: String) {
        sendConsole("[Warn] $message", NamedTextColor.RED)
    }

    private fun sendConsole(message: String, color: NamedTextColor?) {
        val ansiColor = getAnsiCode(color)
        println("$ansiColor$message$RESET")
    }

}
