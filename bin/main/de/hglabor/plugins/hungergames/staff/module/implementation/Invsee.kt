package de.hglabor.plugins.hungergames.staff.module.implementation

import de.hglabor.plugins.hungergames.staff.StaffMode
import de.hglabor.plugins.hungergames.staff.module.InteractWithPlayerModule
import de.hglabor.plugins.hungergames.staff.module.command.IStaffCommand
import de.hglabor.plugins.hungergames.staff.module.command.staffCommand
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack

object Invsee: InteractWithPlayerModule(), IStaffCommand {
    override val item: ItemStack = staffItem(Material.CHEST) {
        meta {
            name = Component.text("Player Inventory")
        }
    }

    override val onRightClickItem: PlayerInteractAtEntityEvent.() -> Unit = {
        player.openInventory((rightClicked as Player).inventory)
    }

    override val command = staffCommand("invsee") { sender, args ->
        val targetName = args[0]
        val target = Bukkit.getPlayer(targetName)

        if (target == null) {
            sender.sendMessage(StaffMode.prefix.append(Component.text("This player is not online.", NamedTextColor.RED)))
            return@staffCommand
        }

        sender.openInventory(target.inventory)
    }

    override val commandUsage = "/staff invsee <player>"

    override val description = "See a player's inventory"
}