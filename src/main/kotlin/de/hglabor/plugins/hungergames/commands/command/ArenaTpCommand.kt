package de.hglabor.plugins.hungergames.commands.command

import de.hglabor.plugins.hungergames.commands.executer.ArenaTpCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class ArenaTpCommand : Command("arenatp") {
    init {
        description = "Teleport into arena world"
        usageMessage = "/arenatp"
    }
    private val executor = ArenaTpCommandExecutor()
    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        return executor.onCommand(sender, this, label, args)
    }
}