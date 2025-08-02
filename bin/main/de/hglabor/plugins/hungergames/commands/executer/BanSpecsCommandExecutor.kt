package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.Prefix
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class BanSpecsCommandExecutor : CommandExecutor {
    companion object {
        var allowSpecs = true
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (!sender.hasPermission("hglabor.hg.start")) {
            sender.sendMessage(Component.text(Prefix).append(Component.text("You are not permitted to execute this command.", NamedTextColor.RED)))
            return false
        }
        allowSpecs = !allowSpecs
        sender.sendMessage(Component.text(Prefix).append(Component.text("Allow specs: $allowSpecs.", NamedTextColor.RED)))
        return true
    }
}