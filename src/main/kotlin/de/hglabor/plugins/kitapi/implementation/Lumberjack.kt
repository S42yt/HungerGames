package de.hglabor.plugins.kitapi.implementation

import de.hglabor.plugins.kitapi.kit.Kit
import de.hglabor.plugins.kitapi.kit.KitProperties
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.atomic.AtomicInteger


class LumberjackProperties : KitProperties() {
    val maxBlocks by int(300)
}

val Lumberjack by Kit("Lumberjack", ::LumberjackProperties) {
    displayMaterial = Material.OAK_LOG
    description = "${Color.WHITE}Break an entire tree ${Color.GRAY}by breaking just one log"

    simpleItem(ItemStack(Material.WOODEN_AXE))

    val woodMaterials = setOf(
        Material.OAK_LOG,
        Material.SPRUCE_LOG,
        Material.BIRCH_LOG,
        Material.JUNGLE_LOG,
        Material.ACACIA_LOG,
        Material.DARK_OAK_LOG,
        Material.CRIMSON_STEM,
        Material.WARPED_STEM,
        Material.MUSHROOM_STEM,
        Material.BROWN_MUSHROOM_BLOCK,
        Material.RED_MUSHROOM_BLOCK,
    )

    fun isWood(block: Block) = block.type in woodMaterials


    fun breakSurroundingWood(block: Block, atomicInteger: AtomicInteger) {
        if (isWood(block)) {
            block.breakNaturally()
            if (atomicInteger.getAndIncrement() > kit.properties.maxBlocks) return
            val faces = arrayOf(BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)
            for (face in faces) {
                breakSurroundingWood(block.getRelative(face), atomicInteger)
            }
        }
    }

    kitPlayerEvent<BlockBreakEvent>({ it.player }) { it, _ ->
        if (isWood(it.block)) {
            breakSurroundingWood(it.block, AtomicInteger(0))
        }
    }
}
