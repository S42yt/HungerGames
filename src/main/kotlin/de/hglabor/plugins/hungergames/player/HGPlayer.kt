package de.hglabor.plugins.hungergames.player

import de.hglabor.plugins.hungergames.event.KitDisableEvent
import de.hglabor.plugins.hungergames.event.KitEnableEvent
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.mechanics.implementation.KitSelector
import de.hglabor.plugins.hungergames.game.mechanics.implementation.OfflineTimer
import de.hglabor.plugins.hungergames.game.mechanics.implementation.RandomKits
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.Arena
import de.hglabor.plugins.hungergames.game.phase.phases.InvincibilityPhase
import de.hglabor.plugins.hungergames.scoreboard.Board
import de.hglabor.plugins.hungergames.scoreboard.setScoreboard
import de.hglabor.plugins.kitapi.implementation.None
import de.hglabor.plugins.kitapi.kit.Kit
import net.axay.kspigot.extensions.bukkit.feedSaturate
import net.axay.kspigot.extensions.geometry.add
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

open class HGPlayer(val uuid: UUID, val name: String) {
    val bukkitPlayer: Player?
        get() = Bukkit.getPlayer(uuid)
    val isAlive: Boolean
        get() = status == PlayerStatus.INGAME || status == PlayerStatus.OFFLINE
    var status: PlayerStatus = PlayerStatus.LOBBY

    //TODO var combatLogMob: UUID? = null
    var offlineTime: AtomicInteger = AtomicInteger(120)

    var kills: AtomicInteger = AtomicInteger(0)
    var combatTimer: AtomicInteger = AtomicInteger(0)
    val isInCombat: Boolean
        get() = combatTimer.get() > 0 && isAlive
    var board: Board? = null
    var kit: Kit<*> = None
    var changedKitBefore: Boolean = false
    var isKitEnabled = true
    var isKitByRogueDisabled: Boolean = false
    var wasInArena: Boolean = false
    val kitPrefix: String
        get() = if(!kit.properties.isEnabled || isKitByRogueDisabled) TextDecoration.STRIKETHROUGH.toString() else ""

    fun login() {
        OfflineTimer.stopTimer(this)
        setGameScoreboard()
    }

    fun setGameScoreboard(forceReset: Boolean = false) {
        val player = bukkitPlayer ?: return
        if (board != null && !forceReset) {
            board!!.setScoreboard(player)
            return
        }

        board = player.setScoreboard {
            this.title = Component.text().content("HG").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD)
                .append(Component.text("Labor.de").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)).build()
            period = 20
            content {
                " "
                { Component.text().content("Players: ").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                    .append(Component.text("${PlayerList.getShownPlayerCount()} ").color(NamedTextColor.WHITE))
                    .append(Component.text("(${Arena.queuedPlayers.size + (Arena.currentMatch?.players?.size ?: 0)})").color(NamedTextColor.GRAY)).build() }
                { Component.text().content("Kit: ").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD)
                    .append(Component.text("${kitPrefix}${kit.properties.kitname}").color(NamedTextColor.WHITE)).build() }
                { Component.text().content("Kills: ").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
                    .append(Component.text("${kills.get()}").color(NamedTextColor.WHITE)).build() }
                { Component.text().content("${GameManager.phase.timeName}: ").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)
                    .append(Component.text(GameManager.phase.getTimeString()).color(NamedTextColor.WHITE)).build() }
                { if (isInCombat) Component.text("IN COMBAT").color(NamedTextColor.RED).decorate(TextDecoration.BOLD) else Component.empty() }
            }
        }
    }

    fun makeGameReady() {
        status = PlayerStatus.INGAME
        bukkitPlayer?.apply {
            inventory.clear()
            inventory.addItem(ItemStack(Material.COMPASS))
            gameMode = GameMode.SURVIVAL
            closeInventory()
            val maxHealthAttribute = getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH)
            maxHealthAttribute?.baseValue = 20.0
            health = maxHealthAttribute?.value ?: 20.0
            feedSaturate()

            getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED)?.baseValue = 1024.0

            inventory.setItemInOffHand(null)

            if (kit == None && GameManager.phase == InvincibilityPhase) {
                if (!RandomKits.internal.isEnabled) {
                    inventory.addItem(KitSelector.kitSelectorItem)
                }
            } else {
                kit.internal.givePlayer(this)
            }
            hgPlayer.combatTimer.set(0)
            teleport(getSpawnLocation().add(0.0, 3.0 ,0.0))
        }
    }

    private fun getSpawnLocation(): Location {
        val spawnLoc = GameManager.world?.spawnLocation ?: throw IllegalStateException("World or spawn is null")
        val maxTries = 20
        repeat(maxTries) {
            val newLoc = spawnLoc.clone().add((-25..25).random().toDouble(), 0.0, (-25..25).random().toDouble())
            val world = newLoc.world ?: return@repeat
            val highestBlock = world.getHighestBlockAt(newLoc)
            if (highestBlock.y > 85) return@repeat
            val block = highestBlock
            val blockBelow = block.getRelative(BlockFace.DOWN)
            if (block.type.isSolid && blockBelow.type.isSolid) {
                return block.location
            }
        }
        return spawnLoc
    }

    fun enableKit() {
        isKitEnabled = true
        isKitByRogueDisabled = false
        Bukkit.getPluginManager().callEvent(KitEnableEvent(bukkitPlayer ?: return, kit))
    }

    fun disableKit(isByRogue: Boolean = false) {
        isKitEnabled = false
        isKitByRogueDisabled = isByRogue
        Bukkit.getPluginManager().callEvent(KitDisableEvent(bukkitPlayer ?: return, kit))
    }
}

val Player.hgPlayer get() = PlayerList.getPlayer(this)

val Player.staffPlayer
    get() = hgPlayer as? StaffPlayer
