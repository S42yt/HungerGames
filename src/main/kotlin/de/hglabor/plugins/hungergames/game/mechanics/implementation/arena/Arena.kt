package de.hglabor.plugins.hungergames.game.mechanics.implementation.arena

import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.player.HGPlayer
import de.hglabor.plugins.hungergames.player.PlayerList
import de.hglabor.plugins.hungergames.player.PlayerStatus
import de.hglabor.plugins.hungergames.player.hgPlayer
import de.hglabor.plugins.hungergames.scoreboard.setScoreboard
import de.hglabor.plugins.hungergames.utils.TimeConverter
import net.axay.kspigot.extensions.broadcast
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Color
import org.bukkit.entity.Player

object Arena {
    val Prefix = " &7| &cArena &7» &f"
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
            this.title = LegacyComponentSerializer.legacySection().deserialize("&b&lHG&f&lLabor.de")
            period = 20
            content {
                fun fightDuration(): String {
                    if (currentMatch == null) return " "
                    val timer = currentMatch?.timer?.get() ?: return " "
                    if (timer >= 0) return TimeConverter.stringify(ArenaMatch.MAX_DURATION - timer)
                    return TimeConverter.stringify(ArenaMatch.MAX_DURATION)
                }
                +" "
                +{ "&a&lPlayers: &f${PlayerList.getShownPlayerCount()}" }
                +{ "&e&l${GameManager.phase.timeName}: &f${GameManager.phase.getTimeString()}" }
                +"&7&m          &7&m          "
                +{ "&b&lWaiting: &f${queuedPlayers.size}" }
                +{ "&c&lFighting: &f${fightDuration()}" }
                +{ "  &7-&f${(currentMatch?.players?.firstOrNull()?.name ?: "None").take(15)}" }
                +{ "  &7-&f${(currentMatch?.players?.lastOrNull()?.name ?: "None").take(15)}" }
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
        broadcast("$Prefix${Color.RED}${TextDecoration.BOLD}The Arena has been closed!")
    }
}
