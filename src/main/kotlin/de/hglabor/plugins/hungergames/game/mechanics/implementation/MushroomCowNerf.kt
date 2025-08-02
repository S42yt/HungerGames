package de.hglabor.plugins.hungergames.game.mechanics.implementation

import de.hglabor.plugins.hungergames.SecondaryColor
import de.hglabor.plugins.hungergames.game.mechanics.Mechanic
import de.hglabor.plugins.hungergames.player.hgPlayer
import net.axay.kspigot.event.listen
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.MushroomCow
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import java.util.*

val MushroomCowNerf by Mechanic("Mushroom Cow Nerf") {
    description = "Mushroom Cows will turn into normal cows after milking it 16 times"
    displayMaterial = Material.RED_MUSHROOM

    val SOUPS_PER_COW = 16
    val ALLOW_IN_COMBAT = true
    val cows = mutableMapOf<UUID, Int>()

    mechanicEvent<EntitySpawnEvent> {
        if (it.entity.type != EntityType.MOOSHROOM) return@mechanicEvent
        it.entity.isCustomNameVisible = false
        it.entity.customName(Component.text(SOUPS_PER_COW.toString(), NamedTextColor.DARK_RED, TextDecoration.BOLD))
    }

    mechanicPlayerEvent<PlayerInteractEntityEvent> { it, player ->
        val rightClicked = it.rightClicked as? MushroomCow ?: return@mechanicPlayerEvent
        if (player.inventory.itemInMainHand.type != Material.BOWL) return@mechanicPlayerEvent
        if (!ALLOW_IN_COMBAT && player.hgPlayer.isInCombat) {
            it.isCancelled = true
            return@mechanicPlayerEvent
        }
        val amountMilked = cows.getOrDefault(rightClicked.uniqueId, 0) + 1

        if (amountMilked == SOUPS_PER_COW) {
            cows.remove(rightClicked.uniqueId)
            rightClicked.remove()
            val loc = rightClicked.location.clone()
            loc.world.spawn(loc, org.bukkit.entity.Cow::class.java)
            loc.world.spawnParticle(org.bukkit.Particle.EXPLOSION, loc.x, loc.y + 0.5, loc.z, 1)
            loc.world.playSound(loc, Sound.ENTITY_SHEEP_SHEAR, 3f, 1f)
            return@mechanicPlayerEvent
        }
        rightClicked.customName(Component.text((SOUPS_PER_COW - amountMilked).toString(), NamedTextColor.DARK_RED, TextDecoration.BOLD))
        cows[rightClicked.uniqueId] = amountMilked

    }
}