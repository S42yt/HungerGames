package de.hglabor.plugins.kitapi.implementation

import de.hglabor.plugins.kitapi.kit.Kit
import de.hglabor.plugins.kitapi.kit.KitProperties
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.min

class AutomaticProperties : KitProperties() {
    val soupHealAmount by double(6.0)
}

val Automatic by Kit("Automatic", ::AutomaticProperties) {
    displayMaterial = Material.MUSHROOM_STEW
    description = "${Color.GRAY}The ${Color.WHITE}soups ${Color.GRAY}in your hotbar will ${Color.WHITE}automatically ${Color.GRAY}be ${Color.WHITE}consumed ${Color.GRAY}when needed"

    kitPlayerEvent<EntityDamageEvent>({ it.entity as? Player }) { _, player ->
        val maxHealth = player.maxHealth
        if (player.health >= maxHealth - this.kit.properties.soupHealAmount) return@kitPlayerEvent

        for (i in 0 until 9) {
            val item = player.inventory.getItem(i) ?: continue
            if (item.type != Material.MUSHROOM_STEW) continue
            player.health = min(player.health + this.kit.properties.soupHealAmount, maxHealth)
            player.inventory.setItem(i, ItemStack(Material.AIR))
            item.type = Material.BOWL

            val droppedItem = player.world.dropItem(player.location, item)
            droppedItem.pickupDelay = 40
            break
        }
    }
}
