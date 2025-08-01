package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.Arena
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.ArenaMechanic
import de.hglabor.plugins.hungergames.player.PlayerList
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class InfoCommandExecutor : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        sender.sendMessage("${Color.RED}${TextDecoration.BOLD}${GameManager.phase.timeName}: ${Color.WHITE}${GameManager.phase.getTimeString()}")
        sender.sendMessage("${Color.GREEN}${TextDecoration.BOLD}Players: ${Color.WHITE}${PlayerList.getShownPlayerCount()}")
        if (ArenaMechanic.internal.isEnabled) {
            sender.sendMessage("")
            sender.sendMessage("${Color.RED}${TextDecoration.BOLD}Arena: ${Color.WHITE}${if (Arena.isOpen) "Open" else "Closed"}")
            sender.sendMessage("${Color.RED}${TextDecoration.BOLD}Waiting: ${Color.WHITE}${Arena.queuedPlayers.size}")
            sender.sendMessage("${Color.RED}${TextDecoration.BOLD}Fighting: ${Color.WHITE}${Arena.currentMatch?.players?.size ?: 0}")
        }
        return true
    }
}