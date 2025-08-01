package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.Arena
import de.hglabor.plugins.hungergames.game.phase.IngamePhase
import de.hglabor.plugins.hungergames.player.HGPlayer
import de.hglabor.plugins.hungergames.player.PlayerList
import org.bukkit.Color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ListCommandExecutor : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (GameManager.phase !is IngamePhase) {
            sender.sendMessage("${Prefix}The game hasn't begun yet.")
        }
        val alivePlayers = PlayerList.alivePlayers
        val arenaPlayers = Arena.currentMatch?.players?.toMutableList()?.apply {
            addAll(Arena.queuedPlayers)
        } ?: Arena.queuedPlayers

        sender.sendMessage("${Color.GREEN}Alive ${Color.GRAY}(${alivePlayers.size}): ${alivePlayers.stringify()}")
        sender.sendMessage("${Color.RED}Arena ${Color.GRAY}(${arenaPlayers.size}): ${arenaPlayers.stringify()}")
        return true
    }

    private fun Collection<HGPlayer>.stringify() = joinToString(separator = "${Color.GRAY}, ") { "${Color.WHITE}${it.name}" }
}