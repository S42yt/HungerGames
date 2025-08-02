package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.Arena
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.ArenaMechanic
import de.hglabor.plugins.hungergames.player.PlayerList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class InfoCommandExecutor : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        sender.sendMessage(Component.text("${GameManager.phase.timeName}: ", NamedTextColor.RED, TextDecoration.BOLD)
            .append(Component.text(GameManager.phase.getTimeString(), NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("Players: ", NamedTextColor.GREEN, TextDecoration.BOLD)
            .append(Component.text(PlayerList.getShownPlayerCount().toString(), NamedTextColor.WHITE)))
        if (ArenaMechanic.internal.isEnabled) {
            sender.sendMessage(Component.text(""))
            sender.sendMessage(Component.text("Arena: ", NamedTextColor.RED, TextDecoration.BOLD)
                .append(Component.text(if (Arena.isOpen) "Open" else "Closed", NamedTextColor.WHITE)))
            sender.sendMessage(Component.text("Waiting: ", NamedTextColor.RED, TextDecoration.BOLD)
                .append(Component.text(Arena.queuedPlayers.size.toString(), NamedTextColor.WHITE)))
            sender.sendMessage(Component.text("Fighting: ", NamedTextColor.RED, TextDecoration.BOLD)
                .append(Component.text((Arena.currentMatch?.players?.size ?: 0).toString(), NamedTextColor.WHITE)))
        }
        return true
    }
}