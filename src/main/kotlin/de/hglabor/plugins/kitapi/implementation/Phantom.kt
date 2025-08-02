package de.hglabor.plugins.kitapi.implementation

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.SecondaryColor
import de.hglabor.plugins.hungergames.utils.cancelFalldamage
import de.hglabor.plugins.kitapi.cooldown.CooldownProperties
import de.hglabor.plugins.kitapi.cooldown.applyCooldown
import de.hglabor.plugins.kitapi.kit.Kit
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import de.hglabor.plugins.hungergames.Manager
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.concurrent.atomic.AtomicInteger

class PhantomProperties : CooldownProperties(30) {
    val flightTime by int(5)
}

val Phantom by Kit("Phantom", ::PhantomProperties) {
    displayMaterial = Material.FEATHER
    description = Component.text("Right-click ", NamedTextColor.WHITE)
        .append(Component.text("your kit-item to fly for ${kit.properties.flightTime} seconds", NamedTextColor.GRAY)).toString()

    clickableItem(ItemStack(Material.PHANTOM_MEMBRANE)) {
        it.item?.meta {
            name = Component.text("Phantom")
        }
        applyCooldown(it) {
            it.player.apply {
                allowFlight = true
                isFlying = true
                player?.sendMessage(Prefix.append(Component.text("You are now able to fly.")))
                player?.velocity = Vector(0.0, 0.3, 0.0)
                Manager.audience.sendMessage(
                    Component.text().append(
                        Component.text("A ").decorate(TextDecoration.BOLD)
                    ).append(
                        Component.text("Phantom ", SecondaryColor).decorate(TextDecoration.BOLD)
                    ).append(
                        Component.text("has risen!", NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
                    ).build()
                )

                val timer = AtomicInteger(kit.properties.flightTime)
                task(true, 20, 20) { task ->
                    val timeRemaining = timer.getAndDecrement()
                    if (timeRemaining == 0) {
                        task.cancel()
                        cancelFalldamage(100, true)
                        allowFlight = false
                        isFlying = false
                        player?.sendMessage(Prefix.append(Component.text("You are no longer able to fly.")))
                        return@task
                    }
                    player?.sendMessage(Prefix.append(Component.text("Your flight has ")).append(Component.text(timeRemaining.toString(), SecondaryColor)).append(Component.text(" seconds remaining.", NamedTextColor.GRAY)))
                }
            }
        }
    }
}
