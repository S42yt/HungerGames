package de.hglabor.plugins.hungergames.staff

import de.hglabor.plugins.hungergames.player.StaffPlayer
import de.hglabor.plugins.hungergames.staff.module.implementation.*
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Field
import java.lang.reflect.Method

object StaffMode {
    val modules = listOf(
        BuildMode,
        CollectItems,
        Invsee,
        PlayerInformation,
        RandomTP,
        Visibility
    )

    val prefix = " ${KColors.DARKGRAY}| ${KColors.DARKPURPLE}Staff ${KColors.DARKGRAY}» ${KColors.GRAY}"

    fun hide(staffPlayer: StaffPlayer) {
        val player = staffPlayer.bukkitPlayer ?: return
        staffPlayer.isVisible = false
        onlinePlayers.forEach { on ->
            on.hidePlayer(player)
        }
    }

    fun show(staffPlayer: StaffPlayer) {
        val player = staffPlayer.bukkitPlayer ?: return
        staffPlayer.isVisible = true
        onlinePlayers.forEach { on ->
            on.showPlayer(player)
        }
    }

    fun setStaffInventory(staffPlayer: StaffPlayer) {
        staffPlayer.bukkitPlayer?.inventory?.apply {
            clear()
            setItem(0, RandomTP.item)
            setItem(1, Invsee.item)
            setItem(2, PlayerInformation.item)
            setItem(6, CollectItems.item)
            setItem(7, Visibility.item)
            setItem(8, BuildMode.item)
        }
    }

    fun addScoreboardLines(staffPlayer: StaffPlayer) {
        /*fun tpsString(): String {
            val tps = getTps()
            val color = when {
                tps >= 17.0 -> Color.GREEN
                tps >= 13.0 -> Color.DARK_GREEN
                tps >= 10.0 -> Color.YELLOW
                tps >= 7.0 -> Color.GOLD
                else -> Color.RED
            }
            val roundedValue = "%.1f".format(tps)
            return "${color}${roundedValue}"
        }*/

        staffPlayer.board?.apply {
            addLineBelow("${Color.PURPLE}${TextDecoration.BOLD}Staff:")
            addLineBelow { "  ${Color.MAROON}${TextDecoration.BOLD}Visible:# ${if (staffPlayer.isVisible) "${Color.GREEN}Yes" else "${Color.RED}No"}" }
            addLineBelow { "  ${Color.MAROON}${TextDecoration.BOLD}Build:# ${if (staffPlayer.isBuildMode) "${Color.GREEN}Yes" else "${Color.RED}No"}" }
            addLineBelow { "  ${Color.MAROON}${TextDecoration.BOLD}Pick up:# ${if (staffPlayer.canCollectItems) "${Color.GREEN}Yes" else "${Color.RED}No"}" }
        }
    }

    init {
        listen<PlayerDropItemEvent> {
            if (it.itemDrop.itemStack.isStaffItem) it.isCancelled = true
        }
    }
}

val ItemStack?.isStaffItem: Boolean
    get() {
        if (this == null) return false
        if (type == Material.AIR) return false
        if (itemMeta == null) return false
        if (itemMeta.lore == null || itemMeta.lore?.isEmpty() == true) return false
        return itemMeta.lore?.first() == "${Color.PURPLE}Staff Item"
    }