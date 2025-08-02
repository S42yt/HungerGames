package de.hglabor.plugins.hungergames.staff.module.implementation

import de.hglabor.plugins.hungergames.player.hgPlayer
import de.hglabor.plugins.hungergames.staff.StaffMode
import de.hglabor.plugins.hungergames.staff.module.InteractWithPlayerModule
import de.hglabor.plugins.hungergames.staff.module.command.IStaffCommand
import de.hglabor.plugins.hungergames.staff.module.command.staffCommand
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
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
        
        staff.sendMessage(Component.text("             ", NamedTextColor.GRAY, TextDecoration.STRIKETHROUGH)
            .append(Component.text(" | ", NamedTextColor.GRAY))
            .append(Component.text("Staff ", NamedTextColor.LIGHT_PURPLE))
            .append(Component.text(" | ", NamedTextColor.GRAY))
            .append(Component.text("             ", NamedTextColor.LIGHT_PURPLE)))
            
        staff.sendMessage(Component.text(" | ", NamedTextColor.GRAY)
            .append(Component.text("Information about ", NamedTextColor.GRAY))
            .append(Component.text(target.name, NamedTextColor.RED)))
            
        staff.sendMessage(Component.text("   • ", NamedTextColor.GRAY)
            .append(Component.text("Kills ", NamedTextColor.GRAY))
            .append(Component.text("» ", NamedTextColor.GRAY))
            .append(Component.text(hgPlayer.kills.toString(), NamedTextColor.RED)))
            
        staff.sendMessage(Component.text("   • ", NamedTextColor.GRAY)
            .append(Component.text("Remaining offline time ", NamedTextColor.GRAY))
            .append(Component.text("» ", NamedTextColor.GRAY))
            .append(Component.text("${hgPlayer.offlineTime.get()} seconds", NamedTextColor.RED)))
            
        staff.sendMessage(Component.text("   • ", NamedTextColor.GRAY)
            .append(Component.text("Was in Arena ", NamedTextColor.GRAY))
            .append(Component.text("» ", NamedTextColor.GRAY))
            .append(Component.text(hgPlayer.wasInArena.toString(), NamedTextColor.RED)))
            
        staff.sendMessage(Component.text("                                    ", NamedTextColor.GRAY, TextDecoration.STRIKETHROUGH))
    }

    override val command = staffCommand("info") { sender, args ->
        val targetName = args[0]
        val target = Bukkit.getPlayer(targetName)

        if (target == null) {
            sender.sendMessage(StaffMode.prefix.append(Component.text("This player is not online.", NamedTextColor.RED)))
            return@staffCommand
        }

        sendInformation(sender, target)
    }

    override val commandUsage = "/staff info <player>"

    override val description = "Display information about a player"
}