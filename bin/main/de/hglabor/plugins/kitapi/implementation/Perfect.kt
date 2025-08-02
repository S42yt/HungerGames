package de.hglabor.plugins.kitapi.implementation


import de.hglabor.plugins.hungergames.event.PlayerSoupEvent
import de.hglabor.plugins.kitapi.kit.Kit
import de.hglabor.plugins.kitapi.kit.KitProperties
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

class PerfectProperties : KitProperties() {
    val soupsForReward by int(8)
    val soupsAsReward by int(3)
}

val Perfect by Kit("Perfect", ::PerfectProperties)  {
    displayMaterial = Material.RABBIT_STEW
    description {
        +"${Color.GRAY}After eating ${kit.properties.soupsForReward} soups, without presouping,"
        +"${Color.GRAY}You will receive ${Color.WHITE}${kit.properties.soupsAsReward} soups."
    }

    val perfectSoupHolder = HashMap<UUID, Int>()
    kitPlayerEvent<PlayerSoupEvent>({ it.player }) { it, player ->
        if (!it.overhealed) {
            var perfectSoups = perfectSoupHolder.getOrPut(player.uniqueId) { 0 } + 1

            if (perfectSoups == this.kit.properties.soupsForReward) {
                repeat(kit.properties.soupsAsReward) {
                    player.inventory.addItem(ItemStack(Material.MUSHROOM_STEW))
                }
                perfectSoups = 0
            }
            perfectSoupHolder[player.uniqueId] = perfectSoups
        } else {
            perfectSoupHolder[player.uniqueId] = 0
        }
    }
}