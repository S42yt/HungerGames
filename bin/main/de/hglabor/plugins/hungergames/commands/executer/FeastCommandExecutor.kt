package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.SecondaryColor
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.player.hgPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class FeastCommandExecutor : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val player = sender as? Player ?: return false
        if (!player.hgPlayer.isAlive) {
            player.sendMessage(Component.text(Prefix).append(Component.text("You can't use this command while spectating.", NamedTextColor.RED)))
            return false
        }

        val feast = GameManager.feast
        if (feast == null) {
            player.sendMessage(Component.text(Prefix).append(Component.text("The feast hasn't been announced yet.", NamedTextColor.RED)))
            return false
        }

        if (feast.isFinished || feast.inPreparation) {
            feast.feastCenter?.let { center ->
                player.compassTarget = center
                player.sendMessage(Component.text(Prefix)
                    .append(Component.text("Your compass is now pointing towards the ", NamedTextColor.GRAY))
                    .append(Component.text("feast", SecondaryColor))
                    .append(Component.text(".", NamedTextColor.GRAY)))
            } ?: run {
                player.sendMessage(Component.text(Prefix).append(Component.text("Feast center is not available.", NamedTextColor.RED)))
            }
        }
        return true
    }
}