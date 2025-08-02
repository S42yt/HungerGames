package de.hglabor.plugins.hungergames.player

import de.hglabor.plugins.hungergames.staff.StaffMode
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import java.util.*

class StaffPlayer(uuid: UUID, name: String) : HGPlayer(uuid, name) {
    var isStaffMode: Boolean = false
    var isBuildMode: Boolean = false
    var isVisible: Boolean = false
    var canCollectItems: Boolean = false

    fun toggleStaffMode() {
        if (isStaffMode) {
            bukkitPlayer?.sendMessage(StaffMode.prefix.append(Component.text("Staffmode ", NamedTextColor.DARK_RED)).append(Component.text("» ", NamedTextColor.GRAY)).append(Component.text("Restored staff inventory", NamedTextColor.GREEN)))
            StaffMode.setStaffInventory(this)
            return
        }
        isStaffMode = !isStaffMode
        bukkitPlayer?.sendMessage(StaffMode.prefix.append(Component.text("Staffmode ", NamedTextColor.DARK_RED)).append(Component.text("» ", NamedTextColor.GRAY)).append(isStaffMode.text))
        status = PlayerStatus.SPECTATOR
        bukkitPlayer?.gameMode = GameMode.CREATIVE
        StaffMode.setStaffInventory(this)
        StaffMode.addScoreboardLines(this)
        StaffMode.hide(this)
    }

    fun toggleBuildMode() {
        isBuildMode = !isBuildMode
        bukkitPlayer?.sendMessage(StaffMode.prefix.append(Component.text("Buildmode ", NamedTextColor.DARK_RED)).append(Component.text("» ", NamedTextColor.GRAY)).append(isBuildMode.text))
    }

    fun toggleCollectingItems() {
        canCollectItems = !canCollectItems
        bukkitPlayer?.sendMessage(StaffMode.prefix.append(Component.text("Collecting Items ", NamedTextColor.DARK_RED)).append(Component.text("» ", NamedTextColor.GRAY)).append(canCollectItems.text))
    }

    fun toggleVisibility() {
        isVisible = !isVisible
        bukkitPlayer?.sendMessage(StaffMode.prefix.append(Component.text("Visibility ", NamedTextColor.DARK_RED)).append(Component.text("» ", NamedTextColor.GRAY)).append(isVisible.text))

        if (isVisible) StaffMode.show(this)
        else StaffMode.hide(this)

    }


    val Boolean.text get() = if (this) Component.text("Enabled", NamedTextColor.GREEN) else Component.text("Disabled", NamedTextColor.RED)
}