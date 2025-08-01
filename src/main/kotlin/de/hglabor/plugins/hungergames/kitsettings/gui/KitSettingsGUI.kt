package de.hglabor.plugins.hungergames.kitsettings.gui

import de.hglabor.plugins.hungergames.PrimaryColor
import de.hglabor.plugins.hungergames.SecondaryColor
import de.hglabor.plugins.hungergames.game.mechanics.SettingsGUI
import de.hglabor.plugins.hungergames.game.mechanics.implementation.RandomKits
import de.hglabor.plugins.hungergames.kitsettings.gui.properties.KitPropertiesGUI
import de.hglabor.plugins.kitapi.kit.Kit
import de.hglabor.plugins.kitapi.kit.KitManager
import net.axay.kspigot.event.listen
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object KitSettingsGUI {
    private val gui = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = Component.text("${SecondaryColor}Kit Settings")

        page(1) {
            val compound = createRectCompound<Kit<*>>(Slots.RowOneSlotTwo, Slots.RowFiveSlotEight,
                iconGenerator = { kit ->
                    kit.internal.displayItem.clone().apply {
                        meta {
                            name = Component.text("${SecondaryColor}${kit.properties.kitname}")
                            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                            if (kit.internal.description != null) {
                                lore(kit.internal.description as List<Component>)
                            }
                        }
                    }
                },
                onClick = { clickEvent, kit ->
                    clickEvent.bukkitEvent.isCancelled = true
                    KitPropertiesGUI.openKitProperties(clickEvent.player, kit)
                })
            compound.sortContentBy { kit -> kit.properties.kitname.lowercase() }
            compoundScroll(
                Slots.RowThreeSlotNine,
                ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1).apply {
                    meta {
                        name = Component.text("${PrimaryColor}Next")
                    }
                }, compound, 7 * 4, reverse = true
            )
            compoundScroll(
                Slots.RowThreeSlotOne,
                ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1).apply {
                    meta {
                        name = Component.text("${PrimaryColor}Previous")
                    }
                }, compound, 7 * 4
            )
            compound.setContent(KitManager.kits)

            button(Slots.RowOneSlotNine, ItemStack(Material.BARRIER, 1).apply {
                meta {
                    name = Component.text("${Color.RED}Back")
                }
            }) {
                it.bukkitEvent.isCancelled = true
                SettingsGUI.open(it.player)
            }
        }
    }

    fun open(player: Player) {
        player.openGUI(gui)
    }
}