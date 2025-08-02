package de.hglabor.plugins.hungergames.game.mechanics

import de.hglabor.plugins.hungergames.PrimaryColor
import de.hglabor.plugins.hungergames.game.mechanics.implementation.KitSelector
import de.hglabor.plugins.hungergames.kitsettings.gui.KitSettingsGUI
import net.axay.kspigot.event.listen
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent

object SettingsGUI {
    val item = itemStack(Material.COMMAND_BLOCK) {
        meta {
            name = Component.text("${PrimaryColor}Settings")
        }
    }

    private val gui = kSpigotGUI(GUIType.THREE_BY_NINE) {
        title = Component.text("${PrimaryColor}Settings")
        page(1) {
            placeholder(Slots.All, itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE) {
                meta { name = null }
            })

            button(Slots.RowTwoSlotThree, itemStack(Material.COMMAND_BLOCK) {
                meta {
                    name = Component.text("${PrimaryColor}Mechanics")
                }
            }) {
                it.bukkitEvent.isCancelled = true
                MechanicsGUI.open(it.player)
            }

            button(Slots.RowTwoSlotSeven, itemStack(Material.CHEST) {
                meta {
                    name = Component.text("${PrimaryColor}Kit Settings")
                }
            }) {
                it.bukkitEvent.isCancelled = true
                KitSettingsGUI.open(it.player)
            }
        }
    }

    fun register() {
        listen<PlayerInteractEvent> {
            if (!it.player.isOp) {
                it.player.inventory.remove(item)
                return@listen
            }
            if (it.player.inventory.itemInMainHand.isSimilar(item)) {
                open(it.player)
            }
        }

        listen<BlockPlaceEvent> {
            if (!it.player.isOp) {
                it.player.inventory.remove(item)
                return@listen
            }
            if (it.player.inventory.itemInMainHand.isSimilar(KitSelector.kitSelectorItem)) {
                it.isCancelled = true
            }
        }
    }

    fun open(player: Player) {
        player.openGUI(gui)
    }
}