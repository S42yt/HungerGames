package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.Arena
import de.hglabor.plugins.hungergames.game.phase.IngamePhase
import de.hglabor.plugins.hungergames.player.HGPlayer
import de.hglabor.plugins.hungergames.player.PlayerList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ListCommandExecutor : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (GameManager.phase !is IngamePhase) {
            sender.sendMessage(Prefix.append(Component.text("The game hasn't begun yet.")))
            return true
        }
        val alivePlayers = PlayerList.alivePlayers
        val arenaPlayers = Arena.currentMatch?.players?.toMutableList()?.apply {
            addAll(Arena.queuedPlayers)
        } ?: Arena.queuedPlayers

        sender.sendMessage(
            Component.text("Alive ", NamedTextColor.GREEN)
                .append(Component.text("(", NamedTextColor.GRAY))
                .append(Component.text(alivePlayers.size))
                .append(Component.text("): ", NamedTextColor.GRAY))
                .append(alivePlayers.stringify())
        )
        sender.sendMessage(
            Component.text("Arena ", NamedTextColor.RED)
                .append(Component.text("(", NamedTextColor.GRAY))
                .append(Component.text(arenaPlayers.size))
                .append(Component.text("): ", NamedTextColor.GRAY))
                .append(arenaPlayers.stringify())
        )
        return true
    }

    private fun Collection<HGPlayer>.stringify(): Component {
        if (isEmpty()) return Component.empty()
        val iterator = iterator()
        var result = Component.text(iterator.next().name, NamedTextColor.WHITE)
        while (iterator.hasNext()) {
            result = result.append(Component.text(", ", NamedTextColor.GRAY))
                .append(Component.text(iterator.next().name, NamedTextColor.WHITE))
        }
        return result
    }
}