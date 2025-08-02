package de.hglabor.plugins.hungergames.kitsettings.gui.properties.property

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.PrimaryColor
import de.hglabor.plugins.hungergames.kitsettings.gui.properties.KitPropertiesGUI
import de.hglabor.plugins.kitapi.kit.Kit
import de.hglabor.plugins.kitapi.kit.KitProperties
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
import org.bukkit.inventory.ItemStack

object EnumPropertyGUI {
    fun openEnumPropertyGui(
        player: Player,
        property: KitProperties.KitProperty<Enum<*>>,
        kit: Kit<*>
    ) {
        player.openGUI(kSpigotGUI(GUIType.SIX_BY_NINE) {
            title = Component.text("${Prefix}${property.settings.propertyName}")
            page(1) {
                placeholder(Slots.All, ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1).apply { meta { name = null } })
                placeholder(Slots.Border, ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1).apply { meta { name = null } })
                placeholder(Slots.RowFiveSlotFive, property.settings.display.displayItem)
                val compound = createRectCompound<Enum<*>>(Slots.RowTwoSlotTwo, Slots.RowFiveSlotEight,
                    iconGenerator = { enum ->
                        val isActive = property.get() == enum
                        val color = if (isActive) Color.GREEN else Color.RED
                        ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1).apply {
                            meta {
                                name = Component.text("${color}${enum.name}")
                            }
                        }
                    }, onClick = { clickEvent, enum ->
                        clickEvent.bukkitEvent.isCancelled = true
                        property.set(enum, kit)
                        clickEvent.guiInstance.reloadCurrentPage()
                    })
                compound.sortContentBy { it.name }
                compound.setContent((property.get()::class.java.enumConstants).toList())
                compoundScroll(
                    Slots.RowSixSlotFive,
                    ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1).apply {
                        meta {
                            name = Component.text("${PrimaryColor}Next")
                        }
                    }, compound, 7 * 4, reverse = true
                )
                compoundScroll(
                    Slots.RowSixSlotOne,
                    ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1).apply {
                        meta {
                            name = Component.text("${PrimaryColor}Previous")
                        }
                    }, compound, 7 * 4
                )
                button(Slots.RowOneSlotNine, ItemStack(Material.BARRIER, 1).apply {
                    meta {
                        name = Component.text("${Color.RED}Back")
                    }
                }) {
                    it.bukkitEvent.isCancelled = true
                    KitPropertiesGUI.openKitProperties(player, kit)
                }
            }
        })
    }
}