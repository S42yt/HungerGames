package de.hglabor.plugins.hungergames.event

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack

class PlayerSoupEvent(player: Player, val soup: ItemStack) : PlayerEvent(player) {

    companion object {
        @JvmStatic
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    val overhealed: Boolean = player.health + 7 > player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH)!!.value
}