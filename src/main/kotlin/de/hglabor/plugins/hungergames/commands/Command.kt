package de.hglabor.plugins.hungergames.commands

import de.hglabor.plugins.hungergames.commands.command.ArenaTpCommand
import de.hglabor.plugins.hungergames.commands.command.BanSpecsCommand
import de.hglabor.plugins.hungergames.commands.command.FeastCommand
import de.hglabor.plugins.hungergames.commands.command.InfoCommand
import de.hglabor.plugins.hungergames.commands.command.KitCommand
import de.hglabor.plugins.hungergames.commands.command.ListCommand
import de.hglabor.plugins.hungergames.commands.command.ReviveCommand
import de.hglabor.plugins.hungergames.commands.command.SettingsCommand
import de.hglabor.plugins.hungergames.commands.command.StaffCommand
import de.hglabor.plugins.hungergames.commands.command.StartCommand
import org.bukkit.Bukkit

object Command {
    fun registerAll() {
        val commandMap = Bukkit.getServer().commandMap
        commandMap.register("start", StartCommand())
        commandMap.register("feast", FeastCommand())
        commandMap.register("revive", ReviveCommand())
        commandMap.register("arenatp", ArenaTpCommand())
        commandMap.register("info", InfoCommand())
        commandMap.register("list", ListCommand())
        commandMap.register("kit", KitCommand())
        commandMap.register("staffmode", StaffCommand())
        commandMap.register("banspecs", BanSpecsCommand())
        commandMap.register("settings", SettingsCommand())
    }
}

