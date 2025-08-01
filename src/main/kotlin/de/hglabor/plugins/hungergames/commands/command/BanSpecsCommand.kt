package de.hglabor.plugins.hungergames.commands.command

import de.hglabor.plugins.hungergames.commands.executer.BanSpecsCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class BanSpecsCommand : Command("banspecs") {
    init {
        description = "Toggle spectator ban"
        usageMessage = "/banspecs"
    }
    private val executor = BanSpecsCommandExecutor()
    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        return executor.onCommand(sender, this, label, args)
    }
}