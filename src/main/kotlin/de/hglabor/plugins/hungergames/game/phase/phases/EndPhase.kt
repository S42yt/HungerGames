package de.hglabor.plugins.hungergames.game.phase.phases

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.SecondaryColor
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.Arena
import de.hglabor.plugins.hungergames.game.phase.GamePhase
import de.hglabor.plugins.hungergames.player.HGPlayer
import de.hglabor.plugins.hungergames.player.PlayerList
import de.hglabor.plugins.hungergames.player.hgPlayer
import de.hglabor.plugins.hungergames.HungerGames
import de.hglabor.plugins.hungergames.Manager
import net.axay.kspigot.extensions.onlinePlayers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack


object EndPhase : GamePhase(25, null) {
    override fun getTimeString() = "Ended"
    override val timeName = "Game"
    var winner: HGPlayer? = null

    private fun getWinner() {
        winner = PlayerList.alivePlayers.singleOrNull() ?: Arena.queuedPlayers.singleOrNull() ?: PlayerList.alivePlayers.maxByOrNull { it.kills.get() }
    }

    override fun onStart() {
        getWinner()
        val platformLoc: Location? = createWinningPlatform()

        onlinePlayers.forEach {
            it.hgPlayer.setGameScoreboard(true)
        }

        onlinePlayers.filter { it != winner?.bukkitPlayer }.forEach {
            it.gameMode = GameMode.SPECTATOR
            platformLoc?.let { loc -> it.teleport(loc) }
        }

        winner?.bukkitPlayer?.apply {
            allowFlight = true
            isFlying = false
            platformLoc?.let { teleport(it) }
            inventory.clear()
            inventory.addItem(ItemStack(Material.WATER_BUCKET))
        }
    }

    override fun tick(tickCount: Int) {
        if (tickCount < 5) {
            Manager.audience.sendMessage(
                if (winner != null) Prefix.append(Component.text(winner?.name ?: "", SecondaryColor)).append(Component.text(" won", NamedTextColor.GRAY))
                else Prefix.append(Component.text("Nobody ", NamedTextColor.RED)).append(Component.text("won.", NamedTextColor.GRAY))
            )
        }

        if (GameManager.elapsedTime.get() == 25L) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "Stop")
        }
    }

    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        event.isCancelled = true
    }

    private fun createWinningPlatform(): Location? {
        val loc = GameManager.world?.getHighestBlockAt(0, 0)?.location?.add(0.0, 50.0, 0.0)
        for (x in -2..2) {
            for (y in -2..-1) {
                for (z in -2..2) {
                    val material = if (y == -2) Material.GLASS else Material.CAKE
                    loc?.clone()?.add(x.toDouble(), y.toDouble(), z.toDouble())?.block?.type = material
                }
            }
        }
        return loc?.clone()?.add(0.0, 2.0, 0.0)
    }
}