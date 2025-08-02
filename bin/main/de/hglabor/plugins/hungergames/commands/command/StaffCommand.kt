package de.hglabor.plugins.hungergames.commands.command

import de.hglabor.plugins.hungergames.commands.executer.StaffCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class StaffCommand : Command("staffmode") {
    init {
        description = "Toggle staff mode or use staff commands"
        usageMessage = "/staffmode"
    }
    private val executor = StaffCommandExecutor()
    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        return executor.onCommand(sender, this, label, args)
    }
}