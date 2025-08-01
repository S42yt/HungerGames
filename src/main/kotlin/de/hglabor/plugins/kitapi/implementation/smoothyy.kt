package de.hglabor.plugins.kitapi.implementation

import de.hglabor.plugins.kitapi.kit.Kit
import de.hglabor.plugins.kitapi.kit.KitProperties
import net.axay.kspigot.extensions.broadcast
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

class SmoothyyProperties : KitProperties()


val Smoothyy by Kit("smoothyy", ::SmoothyyProperties) {
    displayMaterial = Material.COOKED_CHICKEN
    description = "Taitos erstes kit! Es geht aber nichts haha"

    simpleItem(ItemStack(Material.RED_TERRACOTTA, 1))
    simpleItem(ItemStack(Material.YELLOW_TERRACOTTA, 9))
    simpleItem(ItemStack(Material.GRAY_TERRACOTTA, 1))

    kitPlayerEvent<BlockPlaceEvent>({ it.player }) { it, player ->
        {
            broadcast("Jonas: Freitag letzte Arbeit meiner Schullaufbahn, sehr geil!")
        }

    }
}
