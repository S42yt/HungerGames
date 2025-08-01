package de.hglabor.plugins.kitapi.implementation

import de.hglabor.plugins.kitapi.kit.Kit
import de.hglabor.plugins.kitapi.kit.KitProperties
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class RedstonerProperties : KitProperties()

val Redstoner by Kit("Redstoner", ::RedstonerProperties) {
    displayMaterial = Material.REDSTONE
    description = "${Color.GRAY}You receive a lot of redstone items"

    simpleItem(ItemStack(Material.PISTON, 32))
    simpleItem(ItemStack(Material.STICKY_PISTON, 16))
    simpleItem(ItemStack(Material.DISPENSER, 2))
    simpleItem(ItemStack(Material.DROPPER, 2))
    simpleItem(ItemStack(Material.REPEATER, 4))
    simpleItem(ItemStack(Material.COMPARATOR, 4))
    simpleItem(ItemStack(Material.TRIPWIRE_HOOK, 4))
    simpleItem(ItemStack(Material.STRING, 8))
    simpleItem(ItemStack(Material.REDSTONE, 64))
    simpleItem(ItemStack(Material.REDSTONE_TORCH, 12))
    simpleItem(ItemStack(Material.TNT, 12))
    simpleItem(ItemStack(Material.SLIME_BLOCK, 4))
}
