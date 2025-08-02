package de.hglabor.plugins.kitapi.implementation

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.player.hgPlayer
import de.hglabor.plugins.kitapi.cooldown.CooldownProperties
import de.hglabor.plugins.kitapi.cooldown.applyCooldown
import de.hglabor.plugins.kitapi.kit.Kit
import kotlinx.coroutines.*
import net.axay.kspigot.extensions.events.isRightClick
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class RogueProperties : CooldownProperties(16) {
    val radius by double(25.0)
    val duration by int(10)
}

val Rogue by Kit("Rogue", ::RogueProperties) {
    displayMaterial = Material.STICK
    description = "${Color.GRAY}Disable your enemies' kits"

    val coroutineScope = CoroutineScope(Dispatchers.Default)
    clickableItem(ItemStack(Material.STICK)) {
        if (!it.action.isRightClick) return@clickableItem
        val roguePlayer = it.player
        val radius = kit.properties.radius
        val players = roguePlayer.getNearbyEntities(radius, radius, radius)
            .filterIsInstance<Player>()
            .ifEmpty { return@clickableItem }

        applyCooldown(it) {
            for (player in players) {
                val hgPlayer = player.hgPlayer
                if (!hgPlayer.isAlive || player.location.distanceSquared(roguePlayer.location) > radius * radius)
                    continue

                coroutineScope.launch {
                    hgPlayer.disableKit(isByRogue = true)
                    player.sendMessage("${Prefix}Your kit has been ${Color.RED}disabled${Color.GRAY}.")

                    delay(kit.properties.duration * 1000L)

                    hgPlayer.enableKit()
                    player.sendMessage("${Prefix}Your kit has been ${Color.GREEN}enabled${Color.GRAY}.")
                }
            }
        }
    }
}