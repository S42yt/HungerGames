package de.hglabor.plugins.hungergames.player

import de.hglabor.plugins.hungergames.HungerGames
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent

object OffhandBlocker : Listener {
    @EventHandler
    fun onSwapHand(event: PlayerSwapHandItemsEvent) {
        event.isCancelled = true
    }

    fun register() {
        Bukkit.getPluginManager().registerEvents(this, HungerGames.INSTANCE)
    }
}
