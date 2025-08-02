package de.hglabor.plugins.hungergames.staff.module

import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.setLore
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

interface StaffModule {
    val item: ItemStack

    fun staffItem(material: Material, builder: ItemStack.() -> Unit): ItemStack {
        return itemStack(material, builder).apply {
            meta {
                setLore {
                    +Component.text("Staff Item", NamedTextColor.LIGHT_PURPLE)
                }
            }
        }
    }
}