package de.hglabor.plugins.hungergames.commands.command

import de.hglabor.plugins.hungergames.commands.completer.KitCommandCompleter
import de.hglabor.plugins.hungergames.commands.executer.KitCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class KitCommand : Command("kit") {
    init {
        description = "Choose a kit"
        usageMessage = "/kit"
    }

    private val executor = KitCommandExecutor()
    private val completer = KitCommandCompleter()

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        return executor.onCommand(sender, this, label, args)
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return completer.onTabComplete(sender, this, alias, args) ?: mutableListOf()
    }
}