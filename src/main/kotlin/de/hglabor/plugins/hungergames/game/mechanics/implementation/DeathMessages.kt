package de.hglabor.plugins.hungergames.game.mechanics.implementation

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.Arena
import de.hglabor.plugins.hungergames.player.HGPlayer
import de.hglabor.plugins.hungergames.player.PlayerList
import de.hglabor.plugins.hungergames.player.hgPlayer
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.runnables.taskRunLater
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Color
import org.bukkit.event.entity.PlayerDeathEvent

object DeathMessages {
    fun announceArenaDeath(winner: HGPlayer, loser: HGPlayer) {
        broadcast("${Arena.Prefix}${Color.GREEN}${winner.name} ${Color.GRAY}won the fight against ${Color.RED}${loser.name}${Color.GRAY}.")
        broadcast("${Arena.Prefix}${winner.name} has been revived.")
    }

    fun announce(event: PlayerDeathEvent, enteredArena: Boolean) {
        event.deathMessage(null)
        val hgPlayer = event.entity.hgPlayer
        if (event.entity.killer != null) {
            announce(hgPlayer, event.entity.killer?.hgPlayer)
        } else {
            val deathMsg = event.deathMessage()
            if (deathMsg != null) {
                announce(hgPlayer, PlainTextComponentSerializer.plainText().serialize(deathMsg))
            } else {
                announce(hgPlayer)
            }
        }
        taskRunLater(2) {
            announcePlayerCount(enteredArena)
        }
    }

    private fun announce(dead: HGPlayer, killer: HGPlayer?) {
        val deadText = "${Color.RED}${dead.name} ${Color.GRAY}(${Color.RED}${dead.kit.properties.kitname}${Color.GRAY})"
        val killerText = "${Color.GREEN}${killer?.name} ${Color.GRAY}(${Color.GREEN}${killer?.kit?.properties?.kitname}${Color.GRAY})"
        val slainText = " ${Color.GRAY}was eliminated by "
        broadcast(Prefix + deadText + slainText + killerText)
    }

    fun announce(dead: HGPlayer) {
        val deadText = "${Color.RED}${dead.name}"
        broadcast(Prefix + deadText + Color.GRAY + " was eliminated")
    }

    fun announce(dead: HGPlayer, deathMessage: String) {
        val deadText = "${Color.RED}${dead.name}${Color.GRAY}"
        var message = deathMessage
        if (message.contains("was slain by")) {
            message = message.replace("was slain by", "was eliminated by${Color.GREEN}")
        }
        broadcast(Prefix + message.replace(dead.name.toRegex(), deadText))
    }

    private fun announcePlayerCount(enteredArena: Boolean) {
        if (enteredArena) {
            broadcast("${Arena.Prefix}They have entered the ${Color.RED}Arena${Color.GRAY}.")
        } else {
            broadcast("${Prefix}There are ${Color.WHITE}${PlayerList.alivePlayers.size} ${Color.GRAY}players left.")
        }
    }
}