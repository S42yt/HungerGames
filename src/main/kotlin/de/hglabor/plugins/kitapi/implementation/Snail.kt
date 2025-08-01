package de.hglabor.plugins.kitapi.implementation

import de.hglabor.plugins.hungergames.utils.ChanceUtils
import de.hglabor.plugins.kitapi.kit.Kit
import de.hglabor.plugins.kitapi.kit.KitProperties
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SnailProperties : KitProperties() {
    val slownessDuration by int(3)
    val slownessAmplifier by int(0)
    val likelihood by int(30)
}

val Snail by Kit("Snail", ::SnailProperties) {
    displayMaterial = Material.SLIME_BALL
    description {
        +"${Color.WHITE}Hit ${Color.GRAY}an enemy to give them ${Color.WHITE}slowness"
        +"${Color.WHITE}While sneaking ${Color.GRAY}you deal and take half a heart of damage"
    }

    kitPlayerEvent<EntityDamageByEntityEvent>({ it.damager as? Player }, priority = EventPriority.HIGH) { it, damager ->
        if (damager.isSneaking) {
            it.damage = it.finalDamage.coerceAtMost(1.0)
        }

        if (!ChanceUtils.roll(kit.properties.likelihood)) return@kitPlayerEvent
        val target = (it.entity as? LivingEntity) ?: return@kitPlayerEvent

        target.addPotionEffect(
            PotionEffect(
                PotionEffectType.SLOWNESS,
                this.kit.properties.slownessDuration * 20,
                this.kit.properties.slownessAmplifier
            )
        )
    }

    kitPlayerEvent<EntityDamageByEntityEvent>({ it.entity as? Player }, priority = EventPriority.HIGH) { it, player ->
        if (player.isSneaking) {
            it.damage = it.finalDamage.coerceAtMost(1.0)
        }
    }
}
