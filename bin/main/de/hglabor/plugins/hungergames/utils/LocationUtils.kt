package de.hglabor.plugins.hungergames.utils

import net.axay.kspigot.runnables.task
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.entity.LivingEntity
import kotlin.random.Random

object LocationUtils {
    fun getHighestBlock(world: World, spread: Int, tryCounter: Int): Location {
        val seaLevel = world.seaLevel
        val randomX: Int = Random.nextInt(spread + spread) - spread
        val randomZ: Int = Random.nextInt(spread + spread) - spread
        if (tryCounter > 8) {
            val highestY = world.getHighestBlockYAt(randomX, randomZ)
            return Location(world, randomX.toDouble(), highestY.toDouble(), randomZ.toDouble())
        }
        for (i in seaLevel + 20 downTo seaLevel - 10 + 1) {
            val block = world.getBlockAt(randomX, i, randomZ)
            val blockLoc = block.location
            val type = block.type
            if (type.isSolid && block.getRelative(BlockFace.DOWN).type.isSolid) {
                if (!blockLoc.clone().add(0.0, 1.0, 0.0).block.type.isSolid && !blockLoc.clone()
                        .add(0.0, 2.0, 0.0).block.type.isSolid
                ) {
                    return block.location
                }
            }
        }
        return getHighestBlock(world, spread, tryCounter + 1)
    }

    fun setDirection(livingEntity: LivingEntity, loc: Location) {
        task(true, 1) {
            val dir = loc.clone().subtract(livingEntity.eyeLocation).toVector()
            val finalLoc = livingEntity.location.setDirection(dir)
            finalLoc.pitch = 0f
            livingEntity.teleport(finalLoc)
        }
    }

    fun Location.setDirectionTo(loc: Location): Location {
        val dir = loc.clone().subtract(this).toVector()
        val finalLoc = this.setDirection(dir)
        finalLoc.pitch = 0f
        return finalLoc
    }
}