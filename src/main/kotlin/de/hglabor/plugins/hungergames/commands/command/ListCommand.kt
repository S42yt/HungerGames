package de.hglabor.plugins.hungergames.commands.command

import de.hglabor.plugins.hungergames.commands.executer.ListCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class ListCommand : Command("list") {
    init {
        description = "List alive and arena players"
        usageMessage = "/list"
    }
    private val executor = ListCommandExecutor()
    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        return executor.onCommand(sender, this, label, args)
    }
}