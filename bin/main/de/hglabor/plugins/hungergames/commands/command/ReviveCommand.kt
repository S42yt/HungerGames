package de.hglabor.plugins.hungergames.commands.command

import de.hglabor.plugins.hungergames.commands.executer.ReviveCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class ReviveCommand : Command("revive") {
    init {
        description = "Revive a player"
        usageMessage = "/revive"
    }
    private val executor = ReviveCommandExecutor()
    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        return executor.onCommand(sender, this, label, args)
    }
}