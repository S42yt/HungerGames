package de.hglabor.plugins.hungergames.staff.module.implementation

import de.hglabor.plugins.hungergames.player.hgPlayer
import de.hglabor.plugins.hungergames.staff.StaffMode
import de.hglabor.plugins.hungergames.staff.module.InteractWithPlayerModule
import de.hglabor.plugins.hungergames.staff.module.command.IStaffCommand
import de.hglabor.plugins.hungergames.staff.module.command.staffCommand
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack

object PlayerInformation : InteractWithPlayerModule(), IStaffCommand {
    override val item: ItemStack = staffItem(Material.BOOK) {
        meta {
            name = Component.text("Player Information")
        }
    }

    override val onRightClickItem: PlayerInteractAtEntityEvent.() -> Unit = {
        sendInformation(player, rightClicked as? Player)
    }

    private fun sendInformation(staff: Player, target: Player?) {
        if (target == null) return
        val hgPlayer = target.hgPlayer
        staff.sendMessage("${Color.GRAY}${TextDecoration.STRIKETHROUGH}             ${Color.GRAY}| ${Color.PURPLE}Staff ${Color.GRAY}|${Color.PURPLE}             ")
        staff.sendMessage(" ${Color.GRAY}| ${Color.SILVER}Information about ${Color.MAROON}${target.name}")
        staff.sendMessage("   ${Color.GRAY}• ${Color.SILVER}Kills ${Color.GRAY}» ${Color.MAROON}${hgPlayer.kills}")
        staff.sendMessage("   ${Color.GRAY}• ${Color.SILVER}Remaining offline time ${Color.GRAY}» ${Color.MAROON}${hgPlayer.offlineTime.get()} seconds")
        staff.sendMessage("   ${Color.GRAY}• ${Color.SILVER}Was in Arena ${Color.GRAY}» ${Color.MAROON}${hgPlayer.wasInArena}")
        staff.sendMessage("${Color.GRAY}${TextDecoration.STRIKETHROUGH}                                    ")
    }

    override val command = staffCommand("info") { sender, args ->
        val targetName = args[0]
        val target = Bukkit.getPlayer(targetName)

        if (target == null) {
            sender.sendMessage("${StaffMode.prefix}${Color.RED}This player is not online.")
            return@staffCommand
        }

        sendInformation(sender, target)
    }

    override val commandUsage = "/staff info <player>"

    override val description = "Display information about a player"
}