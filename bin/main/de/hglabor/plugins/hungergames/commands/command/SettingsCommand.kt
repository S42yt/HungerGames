package de.hglabor.plugins.hungergames.commands.command

import de.hglabor.plugins.hungergames.commands.executer.SettingsCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class SettingsCommand : Command("settings") {
    init {
        description = "Open the settings gui"
        usageMessage = "/settings"
    }
    private val executor = SettingsCommandExecutor()
    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        return executor.onCommand(sender, this, label, args)
    }
}