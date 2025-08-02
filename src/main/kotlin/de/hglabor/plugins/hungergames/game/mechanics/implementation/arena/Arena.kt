package de.hglabor.plugins.hungergames.game.mechanics.implementation.arena

import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.player.HGPlayer
import de.hglabor.plugins.hungergames.player.PlayerList
import de.hglabor.plugins.hungergames.player.PlayerStatus
import de.hglabor.plugins.hungergames.player.hgPlayer
import de.hglabor.plugins.hungergames.scoreboard.setScoreboard
import de.hglabor.plugins.hungergames.utils.TimeConverter
import de.hglabor.plugins.hungergames.HungerGames
import de.hglabor.plugins.hungergames.Manager
import net.kyori.adventure.text.format.TextDecoration

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player

object Arena {
    val Prefix: Component = Component.text("|", NamedTextColor.GRAY).append(Component.text(" Arena ", NamedTextColor.RED)).append(Component.text("» ", NamedTextColor.GRAY)).append(Component.text("", NamedTextColor.WHITE))
    var isOpen = true
    val queuedPlayers = mutableListOf<HGPlayer>()
    var currentMatch: ArenaMatch? = null

    fun queuePlayer(player: Player) {
        player.spigot().respawn()
        player.teleport(ArenaWorld.queueLocation)
        player.hgPlayer.status = PlayerStatus.GULAG
        queuedPlayers += player.hgPlayer
        player.hgPlayer.wasInArena = true
        player.inventory.clear()

        player.setScoreboard {
            this.title = Component.text("HG", NamedTextColor.AQUA, TextDecoration.BOLD).append(Component.text("Labor.de", NamedTextColor.WHITE, TextDecoration.BOLD))
            period = 20
            content {
                fun fightDuration(): String {
                    if (currentMatch == null) return " "
                    val timer = currentMatch?.timer?.get() ?: return " "
                    if (timer >= 0) return TimeConverter.stringify(ArenaMatch.MAX_DURATION - timer)
                    return TimeConverter.stringify(ArenaMatch.MAX_DURATION)
                }
                " "
                { PlainTextComponentSerializer.plainText().serialize(Component.text("Players: ", NamedTextColor.GREEN, TextDecoration.BOLD).append(Component.text(PlayerList.getShownPlayerCount(), NamedTextColor.WHITE))) }
                { PlainTextComponentSerializer.plainText().serialize(Component.text(GameManager.phase.timeName, NamedTextColor.YELLOW, TextDecoration.BOLD).append(Component.text(": ", NamedTextColor.YELLOW)).append(Component.text(GameManager.phase.getTimeString(), NamedTextColor.WHITE))) }
                "&7&m          &7&m          "
                { PlainTextComponentSerializer.plainText().serialize(Component.text("Waiting: ", NamedTextColor.AQUA, TextDecoration.BOLD).append(Component.text(queuedPlayers.size, NamedTextColor.WHITE))) }
                { PlainTextComponentSerializer.plainText().serialize(Component.text("Fighting: ", NamedTextColor.RED, TextDecoration.BOLD).append(Component.text(fightDuration(), NamedTextColor.WHITE))) }
                { PlainTextComponentSerializer.plainText().serialize(Component.text("  -", NamedTextColor.GRAY).append(Component.text((currentMatch?.players?.firstOrNull()?.name ?: "None").take(15), NamedTextColor.WHITE))) }
                { PlainTextComponentSerializer.plainText().serialize(Component.text("  -", NamedTextColor.GRAY).append(Component.text((currentMatch?.players?.lastOrNull()?.name ?: "None").take(15), NamedTextColor.WHITE))) }
                +" "
            }
        }
    }

    fun startNewMatch() {
        if (currentMatch?.isEnded == false) return
        if (queuedPlayers.size >= 2) {
            val players = queuedPlayers.take(2).toTypedArray()
            queuedPlayers.removeAll(players)
            currentMatch = ArenaMatch(*players).also {
                it.start()
            }
        }
    }

    fun close() {
        isOpen = false
        Manager.audience.sendMessage(Prefix.append(Component.text("The Arena has been closed!", NamedTextColor.RED, TextDecoration.BOLD)))
    }
}
