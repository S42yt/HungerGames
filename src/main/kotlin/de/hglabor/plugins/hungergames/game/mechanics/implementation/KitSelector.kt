package de.hglabor.plugins.hungergames.game.mechanics.implementation

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.PrimaryColor
import de.hglabor.plugins.hungergames.SecondaryColor
import de.hglabor.plugins.hungergames.event.KitPropertyChangeEvent
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.phase.IngamePhase
import de.hglabor.plugins.hungergames.game.phase.phases.LobbyPhase
import de.hglabor.plugins.hungergames.game.phase.phases.PvPPhase
import de.hglabor.plugins.hungergames.player.PlayerList
import de.hglabor.plugins.kitapi.implementation.None
import de.hglabor.plugins.kitapi.kit.Kit
import de.hglabor.plugins.kitapi.kit.KitManager
import de.hglabor.plugins.kitapi.player.PlayerKits.chooseKit
import net.axay.kspigot.event.listen
import de.hglabor.plugins.hungergames.HungerGames
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object KitSelector {
    val kitSelectorItem = itemStack(Material.CHEST) { meta { name = Component.text("Kit Selector", NamedTextColor.DARK_RED) } }
    val gui
        get() = kSpigotGUI(GUIType.FIVE_BY_NINE) {
            title = Component.text("Kit Selector", NamedTextColor.DARK_RED)
            page(1) {
                val compound = createRectCompound<Kit<*>>(Slots.RowOneSlotTwo, Slots.RowFiveSlotEight,
                    iconGenerator = { kit ->
                        kit.internal.displayItem.clone().apply {
                            meta {
                                name = Component.text(kit.properties.kitname, NamedTextColor.DARK_RED)
                                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                            }
                        }
                    },
                    onClick = { clickEvent, kit ->
                        clickEvent.bukkitEvent.isCancelled = true
                        if (RandomKits.internal.isEnabled) {
                            clickEvent.player.sendMessage(
                                Prefix.append(
                                    Component.text("You can't choose a kit whilst ", NamedTextColor.RED)
                                ).append(
                                    Component.text("Random Kit", NamedTextColor.RED, TextDecoration.UNDERLINED)
                                ).append(
                                    Component.text(" is enabled", NamedTextColor.RED)
                                )
                            )
                        } else {
                            clickEvent.player.chooseKit(kit)
                        }
                        clickEvent.player.closeInventory()
                    })
                compound.sortContentBy { kit -> kit.properties.kitname.lowercase() }
                compoundScroll(
                    Slots.RowThreeSlotNine,
                    ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1).apply {
                        meta {
                            name = Component.text("Next", NamedTextColor.LIGHT_PURPLE)
                        }
                    }, compound, 7 * 4, reverse = true
                )
                compoundScroll(
                    Slots.RowThreeSlotOne,
                    ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1).apply {
                        meta {
                            name = Component.text("Previous", NamedTextColor.LIGHT_PURPLE)
                        }
                    }, compound, 7 * 4
                )
                compound.setContent(KitManager.kits.filter { it.properties.isEnabled })
            }
        }

    fun register() {
        listen<PlayerInteractEvent> {
            if (RandomKits.internal.isEnabled) return@listen
            if (it.item == kitSelectorItem) {
                if (GameManager.phase == PvPPhase) {
                    it.player.inventory.remove(kitSelectorItem)
                } else {
                    it.player.openGUI(gui)
                }
            }
        }

        listen<BlockPlaceEvent> {
            if (RandomKits.internal.isEnabled) return@listen
            if (it.player.inventory.itemInMainHand.isSimilar(kitSelectorItem)) {
                it.isCancelled = true
            }
        }

        listen<KitPropertyChangeEvent> {
            val kit = it.kit
            if (it.property.kProperty.name != "isEnabled") return@listen
            val playersWithKit = PlayerList.allPlayers.filter { it.kit == kit }
            val newValue = kit.properties.isEnabled
            playersWithKit.forEach { hgPlayer ->
                if (!newValue && GameManager.phase == LobbyPhase) {
                    hgPlayer.kit = None
                    hgPlayer.changedKitBefore = false
                    hgPlayer.bukkitPlayer?.sendMessage(Prefix.append(Component.text("Your kit has been ", NamedTextColor.RED)).append(Component.text("disabled", NamedTextColor.RED)).append(Component.text(".", NamedTextColor.GRAY)))
                }
            }
        }
    }
}