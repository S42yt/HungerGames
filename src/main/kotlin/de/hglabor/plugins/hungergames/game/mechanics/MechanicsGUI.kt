package de.hglabor.plugins.hungergames.game.mechanics

import de.hglabor.plugins.hungergames.PrimaryColor
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.phase.phases.PvPPhase
import de.hglabor.plugins.hungergames.kitsettings.gui.KitSettingsGUI
import net.axay.kspigot.event.listen
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.items.toLoreList
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object MechanicsGUI {
    private val gui = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = Component.text("${PrimaryColor}Mechanics")

        page(1) {
            placeholder(Slots.RowOneSlotOne linTo Slots.RowFiveSlotOne, itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE) {
                meta { name = Component.text("") }
            })
            placeholder(Slots.RowOneSlotNine linTo Slots.RowFiveSlotNine, itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE) {
                meta { name = Component.text("") }
            })
            placeholder(Slots.RowFive, itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE) {
                meta { name = Component.text("") }
            })

            val compound = createRectCompound<Mechanic>(Slots.RowThreeSlotTwo, Slots.RowFourSlotEight,
                iconGenerator = { mechanic ->
                    mechanic.internal.displayItem.clone().apply {
                        meta {
                            name = Component.text("${if (mechanic.internal.isEnabled) Color.GREEN else Color.RED}${mechanic.name}")
                            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                            addItemFlags(ItemFlag.HIDE_ENCHANTS)
                            if (mechanic.internal.isEnabled) {
                                addUnsafeEnchantment(Enchantment.UNBREAKING, 1)
                            }
                            mechanic.description?.let { description ->
                                lore(description.toLoreList())
                            }
                        }
                    }
                },
                onClick = { clickEvent, mechanic ->
                    clickEvent.bukkitEvent.isCancelled = true
                    mechanic.internal.isEnabled = !mechanic.internal.isEnabled
                    clickEvent.guiInstance.reloadCurrentPage()
                })
            compound.sortContentBy { mechanic -> mechanic.name.lowercase() }
            /*compoundScroll(
                Slots.RowThreeSlotNine,
A                ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1).apply {
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
            )*/
            compound.setContent(MechanicsManager.mechanics.filter { !it.isEvent })

            placeholder(Slots.RowOneSlotOne, itemStack(Material.COMMAND_BLOCK) {
                meta {
                    name = Component.text("${PrimaryColor}Event Mechanics")
                }
            })
            val specialCompound = createRectCompound<Mechanic>(Slots.RowOneSlotTwo, Slots.RowOneSlotEight,
                iconGenerator = { mechanic ->
                    mechanic.internal.displayItem.clone().apply {
                        meta {
                            name = Component.text("${if (mechanic.internal.isEnabled) Color.GREEN else Color.RED}${mechanic.name}")
                            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                            addItemFlags(ItemFlag.HIDE_ENCHANTS)
                            if (mechanic.internal.isEnabled) {
                                addUnsafeEnchantment(Enchantment.UNBREAKING, 1)
                            }
                            mechanic.description?.let { description ->
                                lore(description.toLoreList())
                            }
                        }
                    }
                },
                onClick = { clickEvent, mechanic ->
                    clickEvent.bukkitEvent.isCancelled = true
                    mechanic.internal.isEnabled = !mechanic.internal.isEnabled
                    clickEvent.guiInstance.reloadCurrentPage()
                })
            specialCompound.sortContentBy { mechanic -> mechanic.name.lowercase() }
            specialCompound.setContent(MechanicsManager.mechanics.filter { it.isEvent })
            button(Slots.RowOneSlotNine, itemStack(Material.BARRIER) {
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