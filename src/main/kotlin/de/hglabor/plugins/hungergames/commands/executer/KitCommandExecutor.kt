package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.SecondaryColor
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.mechanics.implementation.KitSelector
import de.hglabor.plugins.hungergames.game.mechanics.implementation.RandomKits
import de.hglabor.plugins.hungergames.game.phase.phases.EndPhase
import de.hglabor.plugins.hungergames.game.phase.phases.InvincibilityPhase
import de.hglabor.plugins.hungergames.game.phase.phases.PvPPhase
import de.hglabor.plugins.hungergames.player.hgPlayer
import de.hglabor.plugins.kitapi.implementation.None
import de.hglabor.plugins.kitapi.kit.KitManager
import de.hglabor.plugins.kitapi.player.PlayerKits.chooseKit
import net.axay.kspigot.gui.openGUI
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class KitCommandExecutor : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val player = sender as? Player ?: return false
        if (RandomKits.internal.isEnabled) {
            sender.sendMessage("${Prefix}${Color.RED}You can't choose a kit whilst ${TextDecoration.UNDERLINED}Random Kit${Color.RED} is enabled")
            return false
        }
        when (GameManager.phase) {
            PvPPhase, EndPhase -> {
                sender.sendMessage("${Prefix}You can't choose a kit anymore.")
                return false
            }
            InvincibilityPhase -> {
                if (player.hgPlayer.kit != None) {
                    sender.sendMessage("${Prefix}You already have a kit.")
                    return false
                }
            }
        }

        if (args.size != 1) {
            player.openGUI(KitSelector.gui)
            sender.sendMessage("${Prefix}Please use ${Color.WHITE}/kit ${Color.GRAY}<${SecondaryColor}Kit${Color.GRAY}>.")
            return false
        }

        val kit = KitManager.kits.firstOrNull { it.properties.kitname.lowercase() == args[0].lowercase() }
        if (kit == null) {
            sender.sendMessage("${Prefix}Please specify a kit.")
            return false
        }

        if (kit.properties.isEnabled)
            player.chooseKit(kit)
        else
            player.sendMessage("${Prefix}This Kit is disabled.")
        return true
    }
}