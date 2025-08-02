package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.SecondaryColor
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.Arena
import de.hglabor.plugins.hungergames.player.hgPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ReviveCommandExecutor : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val player = sender as? Player ?: return false
        if (!player.hasPermission("hglabor.staff")) {
            sender.sendMessage(Component.text(Prefix).append(Component.text("You are not permitted to execute this command.", NamedTextColor.RED)))
            return false
        }

        if (args.size != 1) {
            sender.sendMessage(Component.text(Prefix)
                .append(Component.text("Please use ", NamedTextColor.GRAY))
                .append(Component.text("/revive ", NamedTextColor.WHITE))
                .append(Component.text("<", NamedTextColor.GRAY))
                .append(Component.text("Player", SecondaryColor))
                .append(Component.text(">.", NamedTextColor.GRAY)))
            return false
        }

        val target = Bukkit.getPlayer(args[0])

        if (target == null || !target.isOnline) {
            sender.sendMessage(Component.text(Prefix)
                .append(Component.text("The player ", NamedTextColor.RED))
                .append(Component.text(args[0], NamedTextColor.RED))
                .append(Component.text(" is not online.", NamedTextColor.RED)))
            return false
        }
        if (target.hgPlayer.isAlive) {
            sender.sendMessage(Component.text(Prefix)
                .append(Component.text(target.name, NamedTextColor.GRAY))
                .append(Component.text(" is still ", NamedTextColor.GRAY))
                .append(Component.text("alive", NamedTextColor.RED))
                .append(Component.text(".", NamedTextColor.GRAY)))
            return false
        }

        if (Arena.currentMatch?.players?.contains(target.hgPlayer) == true) {
            sender.sendMessage(Component.text(Prefix)
                .append(Component.text(target.name, NamedTextColor.GRAY))
                .append(Component.text(" is currently fighting in arena.", NamedTextColor.GRAY)))
            return false
        }
        sender.sendMessage(Component.text(Prefix)
            .append(Component.text(target.name, SecondaryColor))
            .append(Component.text(" has been ", NamedTextColor.GRAY))
            .append(Component.text("revived", NamedTextColor.GREEN))
            .append(Component.text(".", NamedTextColor.GRAY)))
        target.hgPlayer.makeGameReady()
        target.hgPlayer.wasInArena = false
        target.hgPlayer.setGameScoreboard(true)
        return true
    }
}