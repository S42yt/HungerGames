package de.hglabor.plugins.hungergames.game.mechanics.feast

import de.hglabor.plugins.hungergames.Manager
import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.SecondaryColor
import de.hglabor.plugins.hungergames.event.FeastBeginEvent
import de.hglabor.plugins.hungergames.utils.BlockQueue
import de.hglabor.plugins.hungergames.utils.RandomCollection
import de.hglabor.plugins.hungergames.utils.TimeConverter
import de.hglabor.plugins.hungergames.utils.WorldUtils
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.runnables.sync
import net.axay.kspigot.runnables.task
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random


class Feast(val world: World) : Listener {
    private val feastBlocks: MutableSet<Block> = HashSet()
    var feastCenter: Location? = null
    var platformMaterial: Material = Material.GRASS_BLOCK
    var radius = 20
    var timer: AtomicInteger = AtomicInteger(300)
    var totalTime = 0
    var airHeight = 8
    var maxItemsInChest = 6
    var inPreparation = false
    var isFinished = false
    private var shouldDamageItems = false
    val queue: BlockQueue = BlockQueue()

    fun spawn() {
        announceFeast()
        inPreparation = true
        feastCenter?.clone()?.let {
            createCylinder()
            startCountDown()
        }
    }

    private fun createCylinder() {
        val radiusSquared = (radius * radius).toDouble()
        for (x in -radius until radius) {
            for (z in -radius until radius) {
                if (x * x + z * z <= radiusSquared) {
                    for (y in 0..airHeight) {
                        val loc = feastCenter?.block?.getRelative(x, y, z)?.location ?: continue
                        val material = if (y == 0) platformMaterial else Material.AIR
                        WorldUtils.setBlock(loc, material, 0, queue)
                    }
                }
            }
        }
    }

    private fun spawnFeastLoot() {
        feastCenter?.let { center ->
            center.clone().add(0.0, 1.0, 0.0).block.type = Material.ENCHANTING_TABLE
            val chestLocations = arrayOf(
                center.clone().add(1.0, 1.0, 1.0),
                center.clone().add(-1.0, 1.0, 1.0),
                center.clone().add(-1.0, 1.0, -1.0),
                center.clone().add(1.0, 1.0, -1.0),
                center.clone().add(2.0, 1.0, 2.0),
                center.clone().add(0.0, 1.0, 2.0),
                center.clone().add(-2.0, 1.0, 2.0),
                center.clone().add(2.0, 1.0, 0.0),
                center.clone().add(-2.0, 1.0, 0.0),
                center.clone().add(2.0, 1.0, -2.0),
                center.clone().add(0.0, 1.0, -2.0),
                center.clone().add(-2.0, 1.0, -2.0)
            )
            chestLocations.forEach { it.block.type = Material.CHEST }

            //FEAST ITEMS
            val ironItems: RandomCollection<ItemStack> = RandomCollection()
            ironItems.add(1.0, ItemStack(Material.IRON_HELMET))
            ironItems.add(1.0, ItemStack(Material.IRON_CHESTPLATE))
            ironItems.add(1.0, ItemStack(Material.IRON_LEGGINGS))
            ironItems.add(1.0, ItemStack(Material.IRON_BOOTS))
            ironItems.add(1.0, ItemStack(Material.IRON_SWORD))
            ironItems.add(1.07, ItemStack(Material.IRON_PICKAXE))

            val diamondItems: RandomCollection<ItemStack> = RandomCollection()
            diamondItems.add(1.0, ItemStack(Material.DIAMOND_HELMET))
            diamondItems.add(1.0, ItemStack(Material.DIAMOND_CHESTPLATE))
            diamondItems.add(1.0, ItemStack(Material.DIAMOND_LEGGINGS))
            diamondItems.add(1.0, ItemStack(Material.DIAMOND_BOOTS))
            diamondItems.add(1.07, ItemStack(Material.DIAMOND_SWORD))

            val sizeableItems: RandomCollection<ItemStack> = RandomCollection()
            sizeableItems.add(1.0, ItemStack(Material.COOKED_BEEF))
            sizeableItems.add(1.0, ItemStack(Material.COOKED_CHICKEN))
            sizeableItems.add(1.0, ItemStack(Material.MUSHROOM_STEW))

            val singleItems: RandomCollection<ItemStack> = RandomCollection()
            singleItems.add(1.0, ItemStack(Material.BOW))
            singleItems.add(1.0, ItemStack(Material.COBWEB))
            singleItems.add(1.0, ItemStack(Material.FLINT_AND_STEEL))
            singleItems.add(1.0, ItemStack(Material.TNT))
            singleItems.add(1.0, ItemStack(Material.ENDER_PEARL))
            singleItems.add(1.0, ItemStack(Material.LAVA_BUCKET))
            singleItems.add(1.0, ItemStack(Material.WATER_BUCKET))
            /*val strengthPotion = ItemStack(Material.POTION, 1, 16393.toShort())
            val meta: PotionMeta = strengthPotion.itemMeta as PotionMeta
            meta.addCustomEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 2500, 0), true)
            strengthPotion.setItemMeta(meta)*/
            singleItems.add(0.2, ItemStack(Material.POTION, 1))

            val lootPool: RandomCollection<RandomCollection<ItemStack>> = RandomCollection()
            lootPool.add(21.0, ironItems)
            lootPool.add(13.0, diamondItems)
            lootPool.add(33.0, sizeableItems)
            lootPool.add(33.0, singleItems)
            for (chestLocation in chestLocations) {
                val chest = chestLocation.block.state as Chest
                for (i in 0 until maxItemsInChest) {
                    val randomItemCollection: RandomCollection<ItemStack> = lootPool.getRandom()
                    val item: ItemStack = randomItemCollection.getRandom()
                    if (randomItemCollection == sizeableItems) {
                        item.amount = Random.nextInt(11) + 1
                    }
                    if (shouldDamageItems) {
                        if (randomItemCollection == diamondItems) {
                            val maxDurability: Int = item.type.maxDurability.toInt()
                            val damage = (maxDurability - Random.nextInt(maxDurability / 4))
                            val meta = item.itemMeta
                            if(meta is org.bukkit.inventory.meta.Damageable) {
                                meta.damage = damage
                                item.itemMeta = meta
                            }
                        }
                    }
                    chest.inventory.setItem(Random.nextInt(26 - 1) + 1, item)
                }
            }
        }
    }

    private fun startCountDown() {
        task(false, 0, 20) {
            if (timer.decrementAndGet() <= 0) {
                //CHEST SPAWNING
                inPreparation = false
                isFinished = true
                Bukkit.getPluginManager().callEvent(FeastBeginEvent())
                feastBlocks.forEach { feastBlock: Block ->
                    feastBlock.removeMetadata(BLOCK_KEY, Manager)
                }
                sync {
                    announceFeast()
                    spawnFeastLoot()
                }
                it.cancel()
            } else {
                if (timer.get() % 60 == 0 || timer.get() in listOf(30, 15, 10, 5, 3, 2, 1)) {
                    announceFeast()
                }
            }
        }
    }

    private fun announceFeast() {
        broadcast("${Prefix}Feast will spawn at ${getCenterString()} ${Color.SILVER}in ${getTimeString()}${Color.SILVER}.")
    }

    private fun getCenterString(): String? {
        val loc = feastCenter ?: return null
        return "${SecondaryColor}${loc.blockX}${Color.GRAY}, ${SecondaryColor}${loc.blockY}${Color.GRAY}, ${SecondaryColor}${loc.blockZ}${Color.GRAY}"
    }

    private fun getTimeString(): String {
        val time = timer.get()
        return "${Color.WHITE}${TimeConverter.stringify(time)}"
    }

    companion object {
        const val BLOCK_KEY = "FEAST_BLOCK"
    }
}