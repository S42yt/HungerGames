package de.hglabor.plugins.hungergames.staff.module

import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.setLore
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

interface StaffModule {
    val item: ItemStack

    fun staffItem(material: Material, builder: ItemStack.() -> Unit): ItemStack {
        return itemStack(material, builder).apply {
            meta {
                setLore {
                    +"${Color.PURPLE}Staff Item"
                }
            }
        }
    }
}