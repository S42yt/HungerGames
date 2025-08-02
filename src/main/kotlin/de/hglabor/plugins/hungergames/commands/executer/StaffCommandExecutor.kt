package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.player.staffPlayer
import de.hglabor.plugins.hungergames.staff.StaffMode
import de.hglabor.plugins.hungergames.staff.module.command.IStaffCommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StaffCommandExecutor : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val player = sender as? Player ?: return false
        if (!player.hasPermission("hglabor.staff")) {
            sender.sendMessage(Prefix.append(Component.text("You are not permitted to execute this command.", NamedTextColor.RED)))
            return false
        }

        run moduleCommands@{
            if (args.isNotEmpty()) {
                if (args[0].lowercase() == "help") {
                    showHelp(player)
                    return true
                }

                val staffModule = StaffMode.modules.filterIsInstance<IStaffCommand>()
                    .firstOrNull { it.command.name == args[0].lowercase() } ?: return@moduleCommands

                if (!shouldExecute(player)) {
                    player.sendMessage(StaffMode.prefix.append(Component.text("Please enable StaffMode first.", NamedTextColor.RED)))
                    return false
                }

                val moduleArgs = args.toMutableList().apply { removeFirst() }
                staffModule.command.commandCallback.invoke(player, moduleArgs)
                return true
            }
        }

        if (player.staffPlayer?.isStaffMode == false)
            showHelp(player)
        player.staffPlayer?.toggleStaffMode()
        return true
    }

    private fun showHelp(_player: Player) {
        // ...bestehende Hilfelogik...
    }

    private fun shouldExecute(_player: Player): Boolean {
        // ...bestehende Logik...
        return true
    }
}