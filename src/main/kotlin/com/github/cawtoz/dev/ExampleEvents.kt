package com.github.cawtoz.dev

import com.github.cawtoz.stomium.event.EventHandler
import com.github.cawtoz.stomium.event.EventManager
import net.minestom.server.entity.Player
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.player.PlayerDeathEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.utils.time.TimeUnit

/**
 * Registers global events using EventManager.
 * If you want to register an event in a specific node instead of globally,
 * provide a `nodeName` as the first argument.
 */
fun exampleEvents() {

    // Cancels messages containing "badword".
    EventManager.onEvent<PlayerChatEvent>/*("nodeName")*/ { event ->
        if (event.rawMessage.contains("badword")) {
            event.player.sendMessage("Don't use bad words!")
            event.isCancelled = true
        }
    }

    // Player death handling with conditions and expiration.
    EventManager.onBuilderEvent<PlayerDeathEvent>/*("nodeName")*/ {
        filter { event -> event.player.username != "cawtoz" } // Ignore if player is "cawtoz".
        ignoreCancelled(false) // Execute even if canceled.
        expireCount(10) // Expires after 10 executions.
        expireWhen { event -> event.player.health > 10.0 } // Expires if HP > 10.

        handler { event ->
            event.player.sendMessage("You died! Respawning in 5 seconds...")
            event.player.scheduler().buildTask { event.player.respawn() }
                .delay(5, TimeUnit.SECOND).schedule()
        }
    }

}

/**
 * Groups events using EventHandler.
 * If `nodeName` is provided, the events will only be triggered inside that node.
 */
class ExampleEventHandler : EventHandler({

    // Notifies a player when hit by another player.
    onEvent<EntityAttackEvent> { event ->

        val target = event.target
        val entity = event.entity

        if (target is Player && entity is Player) {
            target.sendMessage("You were hit by ${entity.username}!")
        }

    }

    // Prevents players from falling out of the world.
    onBuilderEvent<PlayerMoveEvent> {

        filter { event -> event.newPosition.y < 0 } // Trigger only if below Y=0.
        expireCount(5) // Expires after 5 executions.

        handler { event ->
            event.player.teleport(event.player.respawnPoint)
            event.player.sendMessage("You can't fall out of the world!")
        }

    }

}/*, "nodeName"*/)
