package de.hglabor.plugins.hungergames.game.mechanics.implementation

import de.hglabor.plugins.hungergames.game.mechanics.Mechanic
import de.hglabor.plugins.hungergames.player.hgPlayer
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent

val BlocksToInv by Mechanic("Blocks to Inv") {
    description = "Blocks broken by a player will go directly into their inventory"
    displayMaterial = Material.DIRT

    mechanicEvent<BlockBreakEvent> {
        if (it.isCancelled) return@mechanicEvent
        if (it.player.hgPlayer.isInCombat) return@mechanicEvent
        when (it.block.type) {
            Material.OAK_LOG,
            Material.SPRUCE_LOG,
            Material.BIRCH_LOG,
            Material.JUNGLE_LOG,
            Material.ACACIA_LOG,
            Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG,
            Material.CHERRY_LOG,
            Material.BAMBOO_BLOCK,
            Material.CRIMSON_STEM,
            Material.WARPED_STEM,
            Material.COBBLESTONE,
            Material.RED_MUSHROOM,
            Material.BROWN_MUSHROOM,
            Material.STONE -> {
                val player = it.player
                if (player.inventory.contents.any { block -> block == null || block.type == Material.AIR }) {
                    player.inventory.addItem(*it.block.drops.toTypedArray())
                    it.isCancelled = true
                    it.block.type = Material.AIR
                }
            }
            else -> {}
        }
    }
}