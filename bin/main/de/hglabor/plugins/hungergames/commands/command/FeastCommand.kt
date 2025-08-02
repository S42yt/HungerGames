package de.hglabor.plugins.hungergames.commands.command

import de.hglabor.plugins.hungergames.commands.executer.FeastCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class FeastCommand : Command("feast") {
    init {
        description = "Point your compass towards the feast"
        usageMessage = "/feast"
    }
    private val executor = FeastCommandExecutor()
    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        return executor.onCommand(sender, this, label, args)
    }
}