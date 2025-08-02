package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.SecondaryColor
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.player.hgPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.TextComponent
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
            player.sendMessage(
                Prefix.append(
                    Component.text("You can't use this command while spectating.", NamedTextColor.RED)
                )
            )
            return false
        }

        val feast = GameManager.feast
        if (feast == null) {
            player.sendMessage(
                Prefix.append(
                    Component.text("The feast hasn't been announced yet.", NamedTextColor.RED)
                )
            )
            return false
        }

        if (feast.isFinished || feast.inPreparation) {
            player.compassTarget = feast.feastCenter!!
            player.sendMessage(
                Prefix.append(
                    Component.text("Your compass is now pointing towards the ")
                ).append(
                    Component.text("feast", NamedTextColor.GRAY)
                ).append(
                    Component.text(".")
                )
            )
        }
        return true
    }
}