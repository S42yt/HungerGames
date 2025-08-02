package de.hglabor.plugins.hungergames.kitsettings.gui.properties

import de.hglabor.plugins.hungergames.PrimaryColor
import de.hglabor.plugins.hungergames.kitsettings.gui.KitSettingsGUI
import de.hglabor.plugins.hungergames.kitsettings.gui.properties.property.EnumPropertyGUI
import de.hglabor.plugins.hungergames.kitsettings.gui.properties.property.IntPropertyGUI
import de.hglabor.plugins.kitapi.kit.Kit
import de.hglabor.plugins.kitapi.kit.KitProperties
import de.hglabor.plugins.kitapi.kit.NumberPropertySettings
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.items.*
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player

object KitPropertiesGUI {
    fun openKitProperties(player: Player, kit: Kit<*>) {
        player.openGUI(kSpigotGUI(GUIType.THREE_BY_NINE) {
            title = Component.text("${PrimaryColor}${kit.properties.kitname}")

            page(1) {
                placeholder(Slots.Border, itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE) { meta { name = null } })
                val compound = createRectCompound<KitProperties.KitProperty<*>>(
                    Slots.RowTwoSlotTwo,
                    Slots.RowTwoSlotEight,
                    iconGenerator = { property ->
                        val display = property.settings.display
                        display.displayItem.clone().apply {
                            meta {
                                val displayName = try {
                                    val nameField = display::class.members.find { it.name == "name" }
                                    nameField?.call(display)?.toString()
                                } catch (e: Exception) { null }
                                name = Component.text(displayName ?: display.toString())
                            }
                        }
                    },
                    onClick = { clickEvent, property ->
                        clickEvent.bukkitEvent.isCancelled = true
                        val value = property.get() ?: return@onClick
                        when (value::class) {
                            Int::class -> {
                                if(property.settings is NumberPropertySettings) {
                                    IntPropertyGUI.openIntPropertyGui(
                                        player,
                                        property as KitProperties.KitProperty<Int>,
                                        kit
                                    )
                                }
                            }
                            Boolean::class -> property.set(!(property.get() as Boolean), kit)
                            //String::class -> StringPropertyGUI.openStringPropertyGui(player, property as KitProperties.KitProperty<String>, kit)
                            else -> {
                                if (value::class.java.isEnum) {
                                    EnumPropertyGUI.openEnumPropertyGui(
                                        player,
                                        property as KitProperties.KitProperty<Enum<*>>,
                                        kit
                                    )
                                }
                            }
                        }
                        clickEvent.guiInstance.reloadCurrentPage()
                    })
                compound.setContent(kit.properties.properties)
                button(Slots.RowOneSlotNine, itemStack(Material.BARRIER) { meta { name = Component.text("${Color.RED}Close") } }) {
                    it.bukkitEvent.isCancelled = true
                    KitSettingsGUI.open(player)
                }
            }
        })
    }
}