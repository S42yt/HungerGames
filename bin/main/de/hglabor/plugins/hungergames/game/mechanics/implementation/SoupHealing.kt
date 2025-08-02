package de.hglabor.plugins.hungergames.game.mechanics.implementation

import de.hglabor.plugins.hungergames.event.PlayerSoupEvent
import net.axay.kspigot.event.listen
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.math.min

object SoupHealing {
    fun register() {
        listen<PlayerInteractEvent> {
            it.player.apply {
                if (it.action == Action.LEFT_CLICK_AIR) return@listen
                if (inventory.itemInMainHand.type != Material.MUSHROOM_STEW) return@listen
                val maxHealth = getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
                if (health >= maxHealth - 0.4 && foodLevel >= 20) return@listen
                Bukkit.getPluginManager().callEvent(PlayerSoupEvent(this, inventory.itemInMainHand))
                health = min(maxHealth, health + 7)
                foodLevel = min(20, foodLevel + 6)
                saturation = min(20f, saturation + 6)
                inventory.itemInMainHand.type = Material.BOWL
                updateInventory()
            }
        }
    }
}