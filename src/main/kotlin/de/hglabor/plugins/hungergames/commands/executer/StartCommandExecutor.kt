package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.phase.phases.PvPPhase
import net.axay.kspigot.extensions.broadcast
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class StartCommandExecutor : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (!sender.hasPermission("hglabor.staff")) {
            sender.sendMessage("${Prefix}${Color.RED}You are not permitted to execute this command.")
            return false
        }
        if (GameManager.phase == PvPPhase) {
            sender.sendMessage("${Prefix}? x D")
            return false
        }
        broadcast("${Prefix}${Color.WHITE}${TextDecoration.BOLD}The next game phase has been started!")
        GameManager.startNextPhase()
        return true
    }
}