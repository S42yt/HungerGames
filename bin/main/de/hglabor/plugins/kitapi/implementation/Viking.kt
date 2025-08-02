package de.hglabor.plugins.kitapi.implementation

import de.hglabor.plugins.kitapi.kit.Kit
import de.hglabor.plugins.kitapi.kit.KitProperties
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

class VikingProperties : KitProperties()

val Viking by Kit("Viking", ::VikingProperties) {
    displayMaterial = Material.IRON_AXE
    description = "${Color.GRAY}Deal increased damage using axes"

    kitPlayerEvent<EntityDamageByEntityEvent>({ it.damager as? Player }) { it, player ->
        when (player.itemInHand.type) {
            Material.DIAMOND_AXE, Material.WOODEN_AXE, Material.IRON_AXE, Material.STONE_AXE ->
                it.damage += 1.5
            else -> {}
        }
    }
}