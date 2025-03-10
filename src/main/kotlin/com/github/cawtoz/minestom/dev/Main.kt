package com.github.cawtoz.minestom.dev

import com.github.cawtoz.minestom.core.event.EventManager
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.instance.block.Block

fun main() {

    val minecraftServer = MinecraftServer.init()
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()

    instanceContainer.setGenerator { it.modifier().setBlock(0, 99, 0, Block.BEDROCK) }

    EventManager.onEvent<AsyncPlayerConfigurationEvent> { event ->
        event.spawningInstance = instanceContainer
        val player = event.player
        player.respawnPoint = Pos(0.5, 100.0, 0.5)
    }

    exampleEvents()
    EventManager.registerHandler(ExampleEventHandler())

    minecraftServer.start("0.0.0.0", 25565)

}