package de.hglabor.plugins.hungergames.commands.command

import de.hglabor.plugins.hungergames.commands.executer.InfoCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class InfoCommand : Command("info") {
    init {
        description = "Show game info"
        usageMessage = "/info"
    }
    private val executor = InfoCommandExecutor()
    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        return executor.onCommand(sender, this, label, args)
    }
}