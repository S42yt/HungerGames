package de.hglabor.plugins.hungergames.game.mechanics.implementation.arena

import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.mechanics.implementation.DeathMessages
import de.hglabor.plugins.hungergames.player.HGPlayer
import de.hglabor.plugins.hungergames.player.PlayerStatus
import de.hglabor.plugins.hungergames.player.hgPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.unregister
import de.hglabor.plugins.hungergames.HungerGames
import de.hglabor.plugins.hungergames.Manager
import net.axay.kspigot.extensions.bukkit.*
import net.axay.kspigot.extensions.geometry.add
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.atomic.AtomicInteger

class ArenaMatch(vararg val players: HGPlayer) {
    companion object {
        const val MAX_DURATION = 60
    }

    val timer = AtomicInteger(-4)
    var isEnded = false
    private val listeners = mutableListOf<SingleListener<out Event>>()

    fun start() {
        registerListeners()
        Manager.audience.sendMessage(Arena.Prefix.append(Component.text("Starting a fight between ")).append(players.map { Component.text(it.name, NamedTextColor.WHITE) }.reduce { acc, component -> acc.append(Component.text(" and ", NamedTextColor.GRAY)).append(component) }).append(Component.text(".", NamedTextColor.GRAY)))
        players.forEachIndexed { index, hgPlayer ->
            hgPlayer.bukkitPlayer?.let { player ->
                val loc = if (index == 1) ArenaWorld.spawn1Location else ArenaWorld.spawn2Location
                player.gameMode = GameMode.SURVIVAL
                player.heal()
                player.feedSaturate()
                player.teleport(loc)
                player.give(ItemStack(Material.STONE_SWORD))
                repeat(8) {
                    player.give(ItemStack(Material.MUSHROOM_STEW))
                }
            }
        }
    }

    fun tick() {
        val currentTimer = timer.getAndIncrement()
        sendCountdown(currentTimer)
        checkIfPlayerIsInWater()?.let { player -> end(player.bukkitPlayer) }
        if (currentTimer >= MAX_DURATION) end(null)
    }

    private fun sendCountdown(time: Int) {
        if (time > 0) return
        players.forEach { fighting ->
            fighting.bukkitPlayer?.title(
                when (timer.get()) {
                    -3 -> Component.text("3", NamedTextColor.RED)
                    -2 -> Component.text("2", NamedTextColor.YELLOW)
                    -1 -> Component.text("1", NamedTextColor.GREEN)
                    0 -> Component.text("Go!", NamedTextColor.GREEN)
                    else -> Component.text(" ")
                }
            )
        }
    }

    private fun checkIfPlayerIsInWater(): HGPlayer? {
        players.forEach { hgPlayer ->
            if (hgPlayer.bukkitPlayer?.location?.block?.type == Material.WATER ||
                hgPlayer.bukkitPlayer?.eyeLocation?.block?.type == Material.WATER) {
                return hgPlayer
            }
        }
        return null
    }

    private fun end(loser: Player?) {
        isEnded = true
        Arena.currentMatch = null
        players.forEach { it.setGameScoreboard(true) }

        if (loser != null) {
            val winner = players.first { op -> op != loser.hgPlayer }
            DeathMessages.announceArenaDeath(winner, loser.hgPlayer)
            winner.makeGameReady()
            winner.bukkitPlayer?.inventory?.apply {
                addItem(ItemStack(Material.STONE_SWORD))
                for (i in 0..35) {
                    addItem(ItemStack(Material.MUSHROOM_STEW))
                }
            }

            loser.inventory.clear()
            loser.gameMode = GameMode.SPECTATOR
            // loser.teleport(GameManager.world.spawnLocation.clone().add(0, 10, 0))
            GameManager.world?.spawnLocation?.clone()?.add(0, 10, 0)?.let { loser.teleport(it) }
        } else {
            Manager.audience.sendMessage(Arena.Prefix.append(Component.text("Current fight ", NamedTextColor.RED)).append(Component.text("timed out", NamedTextColor.RED)).append(Component.text(". Eliminating both, ", NamedTextColor.GRAY)).append(players.map { Component.text(it.name, NamedTextColor.WHITE) }.reduce { acc, component -> acc.append(Component.text(" and ", NamedTextColor.GRAY)).append(component) }).append(Component.text(".", NamedTextColor.GRAY)))
            players.forEach { fighting ->
                fighting.bukkitPlayer?.inventory?.clear()
                fighting.bukkitPlayer?.gameMode = GameMode.SPECTATOR
                GameManager.world?.spawnLocation?.clone()?.add(0, 10, 0)?.let { loc ->
                    fighting.bukkitPlayer?.teleport(loc)
                }
            }
        }
        listeners.onEach { it.unregister() }
    }

    fun registerListeners() {
        listeners.addAll(
            listOf(
                listen<PlayerDeathEvent> {
                    if (it.entity.world != ArenaWorld.world) return@listen
                    if (it.entity.hgPlayer !in players) return@listen
                    it.deathMessage(null)
                    end(it.entity)
                },

                listen<EntityDamageByEntityEvent> {
                    if (it.entity.world != ArenaWorld.world) return@listen
                    val entity = it.entity
                    val damager = it.damager
                    if (entity !is Player || damager !is Player || timer.get() <= 0) {
                        it.isCancelled = true
                        return@listen
                    }

                    if (entity.hgPlayer !in players || damager.hgPlayer !in players || timer.get() < 0) {
                        it.isCancelled = true
                        return@listen
                    }
                },

                listen<PlayerQuitEvent> {
                    val player = it.player
                    if (it.player.world != ArenaWorld.world) return@listen
                    if (player.hgPlayer in Arena.queuedPlayers) {
                        player.hgPlayer.status = PlayerStatus.ELIMINATED
                        Arena.queuedPlayers.remove(player.hgPlayer)
                        GameManager.world?.spawnLocation?.let { player.teleport(it) }
                        player.gameMode = GameMode.SPECTATOR
                    }

                    if (player.hgPlayer in players) {
                        player.hgPlayer.status = PlayerStatus.ELIMINATED
                        Arena.queuedPlayers.remove(player.hgPlayer)
                        GameManager.world?.spawnLocation?.let { player.teleport(it) }
                        player.gameMode = GameMode.SPECTATOR
                        end(player)
                    }
                }
            )
        )
    }
}