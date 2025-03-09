package com.github.cawtoz.minestom.dev

import com.github.cawtoz.minestom.core.event.EventHandler
import net.minestom.server.event.player.PlayerSpawnEvent

class ExampleEventHandler : EventHandler({

    onEvent<PlayerSpawnEvent> { event ->
        val player = event.player
        player.sendMessage("Hello, ${player.username}!")
    }

})
