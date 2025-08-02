package de.hglabor.plugins.kitapi.implementation

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.event.PlayerSoupEvent
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.utils.hasMark
import de.hglabor.plugins.hungergames.utils.mark
import de.hglabor.plugins.hungergames.utils.unmark
import de.hglabor.plugins.kitapi.cooldown.CooldownProperties
import de.hglabor.plugins.kitapi.cooldown.applyCooldown
import de.hglabor.plugins.kitapi.kit.Kit
import net.axay.kspigot.event.listen
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.runnables.taskRunLater
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class NightshadeProperties  : CooldownProperties(20) {
    val duration by int(8)
}

val Nightshade by Kit("Nightshade", ::NightshadeProperties) {
    displayMaterial = Material.NETHER_BRICK
    description {
        +Component.text("Right-click ", NamedTextColor.WHITE).append(Component.text("a player to:", NamedTextColor.GRAY))
        +Component.text(" - ", NamedTextColor.GRAY).append(Component.text("Reduce their health ", NamedTextColor.WHITE)).append(Component.text("for ${kit.properties.duration} seconds", NamedTextColor.GRAY))
        +Component.text(" - ", NamedTextColor.GRAY).append(Component.text("Infect up to 2 soups", NamedTextColor.GRAY))
        +Component.text("When ", NamedTextColor.GRAY).append(Component.text("presouping ", NamedTextColor.WHITE)).append(Component.text("or eating an ", NamedTextColor.GRAY)).append(Component.text("infected soup", NamedTextColor.WHITE))
        +Component.text("They will receive ", NamedTextColor.GRAY).append(Component.text("wither effect", NamedTextColor.WHITE))
    }

    clickOnEntityItem(ItemStack(Material.NETHER_BRICK)) {
        val rightClicked = it.rightClicked as? Player ?: return@clickOnEntityItem
        applyCooldown(it) {
            if (rightClicked.hasMark("nightshadeHealth")) {
                it.player.sendMessage(Prefix.append(Component.text("This player is already affected by nightshade")))
                cancelCooldown()
                return@clickOnEntityItem
            }

            rightClicked.healthScale -= 2.0
            rightClicked.mark("nightshadeHealth")

            repeat(if (GameManager.feast?.isFinished == true) 2 else 1) {
                val slot = (0..9)
                    .filter { s ->
                        val item = rightClicked.inventory.getItem(s)
                        item != null && item.type == Material.MUSHROOM_STEW && item.itemMeta != null && item.itemMeta.displayName != "Nightshade"
                    }.randomOrNull()

                if (slot != null) {
                    var item = rightClicked.inventory.getItem(slot)
                    item?.meta {
                        displayName
                    }
                }
            }

            taskRunLater(kit.properties.duration * 20L) {
                rightClicked.healthScale += 2.0
                rightClicked.unmark("nightshadeHealth")
            }
        }
    }

    fun Player.giveNightshadeWither() {
        player?.mark("nightshadeEffect")
        player?.addPotionEffect(PotionEffect(PotionEffectType.WITHER, 80, 3))
        taskRunLater(80) {
            player?.unmark("nightshadeEffect")
        }
    }

    listen<PlayerSoupEvent> {
        val player = it.player

        if (player.itemInHand.itemMeta?.displayName == "Nightshade") {
            player.giveNightshadeWither()
            return@listen
        }

        if (!player.hasMark("nightshadeHealth")) return@listen
        if (it.overhealed) {
            if (!player.hasMark("nightshadeEffect")) {
                player.giveNightshadeWither()
            }
        }
    }
}
