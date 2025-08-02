package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.phase.phases.PvPPhase
import de.hglabor.plugins.hungergames.HungerGames
import de.hglabor.plugins.hungergames.Manager
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
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
            sender.sendMessage(Prefix.append(Component.text("You are not permitted to execute this command.", NamedTextColor.RED)))
            return false
        }
        if (GameManager.phase == PvPPhase) {
            sender.sendMessage(Prefix.append(Component.text("? x D")))
            return false
        }
        Manager.audience.sendMessage(
            Prefix.append(
                Component.text("The next game phase has been started!", NamedTextColor.WHITE, TextDecoration.BOLD)
            )
        )
        GameManager.startNextPhase()
        return true
    }
}