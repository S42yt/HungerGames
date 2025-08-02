package de.hglabor.plugins.hungergames

import de.hglabor.plugins.hungergames.commands.*
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.mechanics.MechanicsGUI
import de.hglabor.plugins.hungergames.game.mechanics.MechanicsManager
import de.hglabor.plugins.hungergames.game.mechanics.SettingsGUI
import de.hglabor.plugins.hungergames.game.mechanics.implementation.KitSelector
import de.hglabor.plugins.hungergames.game.mechanics.implementation.PlayerTracker
import de.hglabor.plugins.hungergames.game.mechanics.implementation.RecraftRecipes
import de.hglabor.plugins.hungergames.game.mechanics.implementation.SoupHealing
import de.hglabor.plugins.hungergames.commands.command.StaffCommand
import de.hglabor.plugins.hungergames.player.OffhandBlocker
import net.axay.kspigot.extensions.bukkit.register
import net.axay.kspigot.main.KSpigot
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.WorldCreator
import java.io.File

class HungerGames : KSpigot() {
    companion object {
        lateinit var INSTANCE: HungerGames; private set
    }

    override fun load() {
        File("world/").let { file ->
            if (file.exists() && file.isDirectory) file.deleteRecursively()
        }
        INSTANCE = this
    }

    override fun startup() {
        registerListeners()
        this.server.createWorld(WorldCreator("arena"))
        registerMechanics()
        Command.registerAll()
    }

    override fun shutdown() {

    }

    private fun registerListeners() {
        OffhandBlocker.register()
    }

    private fun registerMechanics() {
        GameManager.enable()
        SoupHealing.register()
        PlayerTracker.register()
        KitSelector.register()
        RecraftRecipes.register()
        SettingsGUI.register()
    }
}

val Manager by lazy { HungerGames.INSTANCE }
val PrimaryColor = NamedTextColor.LIGHT_PURPLE
val SecondaryColor = NamedTextColor.RED
val Prefix = " ${NamedTextColor.GRAY}| ${PrimaryColor}HGLabor ${NamedTextColor.GRAY}» ${NamedTextColor.GRAY}"
