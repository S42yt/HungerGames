package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.ArenaWorld
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ArenaTpCommandExecutor : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val player = sender as? Player ?: return false
        if (!player.hasPermission("hglabor.admin")) {
            player.sendMessage(
                Prefix.append(
                    Component.text("You are not permitted to execute this command.", NamedTextColor.RED)
                )
            )
            return false
        }
        player.teleport(Location(ArenaWorld.world, 0.0, 10.0, 0.0))
        return true
    }
}