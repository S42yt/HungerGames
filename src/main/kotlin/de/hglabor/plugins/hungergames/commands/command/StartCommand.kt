package de.hglabor.plugins.hungergames.commands.command

import de.hglabor.plugins.hungergames.commands.executer.StartCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class StartCommand : Command("start") {
    init {
        description = "Start the next gamephase"
        usageMessage = "/start"
    }
    private val executor = StartCommandExecutor()
    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        return executor.onCommand(sender, this, label, args)
    }
}