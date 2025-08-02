package de.hglabor.plugins.hungergames.game.mechanics.implementation

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.Arena
import de.hglabor.plugins.hungergames.player.HGPlayer
import de.hglabor.plugins.hungergames.player.PlayerList
import de.hglabor.plugins.hungergames.player.hgPlayer
import de.hglabor.plugins.hungergames.Manager
import net.axay.kspigot.runnables.taskRunLater
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.entity.PlayerDeathEvent

object DeathMessages {
    fun announceArenaDeath(winner: HGPlayer, loser: HGPlayer) {
        val winnerText = Component.text(winner.name, NamedTextColor.GREEN)
        val loserText = Component.text(loser.name, NamedTextColor.RED)
        Manager.audience.sendMessage(
            Arena.Prefix.append(winnerText)
                .append(Component.text(" won the fight against ", NamedTextColor.GRAY))
                .append(loserText)
                .append(Component.text(".", NamedTextColor.GRAY))
        )
        Manager.audience.sendMessage(
            Arena.Prefix.append(winnerText)
                .append(Component.text(" has been revived.", NamedTextColor.GRAY))
        )
    }

    fun announce(event: PlayerDeathEvent, enteredArena: Boolean) {
        event.deathMessage(null)
        val hgPlayer = event.entity.hgPlayer
        val killer = event.entity.killer?.hgPlayer
        if (killer != null) {
            announce(hgPlayer, killer)
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
            .append(Component.text("(", NamedTextColor.GRAY))
            .append(Component.text(dead.kit.properties.kitname, NamedTextColor.RED))
            .append(Component.text(")", NamedTextColor.GRAY))
        val killerText = Component.text(killer?.name ?: "", NamedTextColor.GREEN)
            .append(Component.text("(", NamedTextColor.GRAY))
            .append(Component.text(killer?.kit?.properties?.kitname ?: "", NamedTextColor.GREEN))
            .append(Component.text(")", NamedTextColor.GRAY))
        val slainText = Component.text(" was eliminated by ", NamedTextColor.GRAY)
        Manager.audience.sendMessage(Prefix.append(deadText).append(slainText).append(killerText))
    }

    fun announce(dead: HGPlayer) {
        val deadText = Component.text(dead.name, NamedTextColor.RED)
        Manager.audience.sendMessage(Prefix.append(deadText).append(Component.text(" was eliminated", NamedTextColor.GRAY)))
    }

    fun announce(dead: HGPlayer, deathMessage: String) {
        val deadText = Component.text(dead.name, NamedTextColor.RED)
        var message = deathMessage
        if (message.contains("was slain by")) {
            message = message.replace("was slain by", "was eliminated by")
        }
        Manager.audience.sendMessage(Prefix.append(Component.text(message.replace(dead.name, deadText.content()), NamedTextColor.GRAY)))
    }

    private fun announcePlayerCount(enteredArena: Boolean) {
        if (enteredArena) {
            Manager.audience.sendMessage(
                Arena.Prefix
                    .append(Component.text("They have entered the "))
                    .append(Component.text("Arena", NamedTextColor.RED))
                    .append(Component.text(".", NamedTextColor.GRAY))
            )
        } else {
            Manager.audience.sendMessage(
                Prefix.append(Component.text("There are "))
                    .append(Component.text(PlayerList.alivePlayers.size, NamedTextColor.WHITE))
                    .append(Component.text(" players left.", NamedTextColor.GRAY))
            )
        }
    }
}