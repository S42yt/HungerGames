package de.hglabor.plugins.hungergames.commands.completer

import de.hglabor.plugins.kitapi.kit.KitManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class KitCommandCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        return KitManager.kits.filter { it.properties.isEnabled }.map { it.properties.kitname }.toMutableList()
    }
}