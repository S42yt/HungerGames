package de.hglabor.plugins.hungergames.game.mechanics.implementation

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.Arena
import de.hglabor.plugins.hungergames.player.HGPlayer
import de.hglabor.plugins.hungergames.player.PlayerList
import de.hglabor.plugins.hungergames.player.hgPlayer
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.runnables.taskRunLater
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.entity.PlayerDeathEvent

object DeathMessages {
    fun announceArenaDeath(winner: HGPlayer, loser: HGPlayer) {
        broadcast(Component.text(Arena.Prefix)
            .append(Component.text(winner.name, NamedTextColor.GREEN))
            .append(Component.text(" won the fight against ", NamedTextColor.GRAY))
            .append(Component.text(loser.name, NamedTextColor.RED))
            .append(Component.text(".", NamedTextColor.GRAY)))
        broadcast(Component.text(Arena.Prefix)
            .append(Component.text("${winner.name} has been revived.", NamedTextColor.GREEN)))
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
        val deadText = Component.text(dead.name, NamedTextColor.RED)
            .append(Component.text(" (", NamedTextColor.GRAY))
            .append(Component.text(dead.kit.properties.kitname, NamedTextColor.RED))
            .append(Component.text(")", NamedTextColor.GRAY))
        val killerText = Component.text(killer?.name ?: "Unknown", NamedTextColor.GREEN)
            .append(Component.text(" (", NamedTextColor.GRAY))
            .append(Component.text(killer?.kit?.properties?.kitname ?: "Unknown", NamedTextColor.GREEN))
            .append(Component.text(")", NamedTextColor.GRAY))
        val slainText = Component.text(" was eliminated by ", NamedTextColor.GRAY)
        broadcast(Component.text(Prefix).append(deadText).append(slainText).append(killerText))
    }

    fun announce(dead: HGPlayer) {
        val deadText = Component.text(dead.name, NamedTextColor.RED)
        broadcast(Component.text(Prefix).append(deadText).append(Component.text(" was eliminated", NamedTextColor.GRAY)))
    }

    fun announce(dead: HGPlayer, deathMessage: String) {
        val deadText = Component.text(dead.name, NamedTextColor.RED)
        var message = deathMessage
        if (message.contains("was slain by")) {
            message = message.replace("was slain by", "was eliminated by")
        }
        val componentMessage = Component.text(message.replace(dead.name.toRegex(), ""), NamedTextColor.GRAY)
        broadcast(Component.text(Prefix).append(deadText).append(componentMessage))
    }

    private fun announcePlayerCount(enteredArena: Boolean) {
        if (enteredArena) {
            broadcast(Component.text(Arena.Prefix)
                .append(Component.text("They have entered the ", NamedTextColor.GRAY))
                .append(Component.text("Arena", NamedTextColor.RED))
                .append(Component.text(".", NamedTextColor.GRAY)))
        } else {
            broadcast(Component.text(Prefix)
                .append(Component.text("There are ", NamedTextColor.GRAY))
                .append(Component.text(PlayerList.alivePlayers.size.toString(), NamedTextColor.WHITE))
                .append(Component.text(" players left.", NamedTextColor.GRAY)))
        }
    }
}