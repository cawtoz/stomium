package com.github.cawtoz.stomium

import com.github.cawtoz.stomium.event.EventManager
import com.github.cawtoz.stomium.extension.ExtensionManager
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.instance.block.Block

fun main() {

    val minecraftServer = MinecraftServer.init()
    val instanceManager = MinecraftServer.getInstanceManager()

    val instanceContainer = instanceManager.createInstanceContainer()

    instanceContainer.setGenerator { unit ->
        val start = unit.absoluteStart()

        val x = start.x().toInt()
        val z = start.z().toInt()

        if (x == 0 && z == 0) {
            unit.modifier().setBlock(x, 49, z, Block.BEDROCK)
        }
    }

    EventManager.onEvent<AsyncPlayerConfigurationEvent> { event ->
        event.spawningInstance = instanceContainer
        val player = event.player
        player.respawnPoint = Pos(0.5, 50.0, 0.5)
    }

    ExtensionManager.init()

    minecraftServer.start("0.0.0.0", 25565)

}
