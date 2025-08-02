package de.hglabor.plugins.hungergames.game.mechanics.implementation

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.SecondaryColor
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.mechanics.Mechanic
import de.hglabor.plugins.hungergames.game.phase.phases.LobbyPhase
import de.hglabor.plugins.hungergames.player.PlayerList
import de.hglabor.plugins.hungergames.player.hgPlayer
import de.hglabor.plugins.kitapi.implementation.None
import de.hglabor.plugins.kitapi.kit.KitManager
import de.hglabor.plugins.kitapi.player.PlayerKits.chooseKit
import de.hglabor.plugins.hungergames.HungerGames
import de.hglabor.plugins.hungergames.Manager
import net.axay.kspigot.extensions.onlinePlayers
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.event.player.PlayerJoinEvent

val RandomKits by Mechanic("Random Kits", isEvent = true) {
    displayMaterial = Material.CHEST
    onEnable {
        if (GameManager.phase != LobbyPhase) return@onEnable
        PlayerList.allPlayers.forEach { hgPlayer ->
            hgPlayer.kit = None
            hgPlayer.changedKitBefore = false
            hgPlayer.bukkitPlayer?.let { player ->
                player.inventory.remove(KitSelector.kitSelectorItem)
                player.sendMessage(Prefix.append(Component.text("Your kit was removed.")))
            }
        }
        Manager.audience.sendMessage(Component.text("Random Kits has been enabled for this round.", NamedTextColor.GREEN, TextDecoration.BOLD))
    }

    onGameStart {
        onlinePlayers.forEach { player ->
            if (player.hgPlayer.kit == None && !player.hgPlayer.changedKitBefore) {
                val kit = KitManager.kits.filter { it != None && it.properties.isEnabled }.random()
                player.chooseKit(kit, false)
                player.sendMessage(Prefix.append(Component.text("You have been given the kit ")).append(Component.text(kit.properties.kitname, SecondaryColor)).append(Component.text(".", NamedTextColor.GRAY)))
            }
        }
    }

    onDisable {
        if (GameManager.phase != LobbyPhase) return@onDisable
        onlinePlayers.forEach {
            it.inventory.addItem(KitSelector.kitSelectorItem)
        }
        Manager.audience.sendMessage(Component.text("Random Kits has been disabled for this round.", NamedTextColor.RED, TextDecoration.BOLD))
    }

    mechanicEvent<PlayerJoinEvent> {
        if (GameManager.phase != LobbyPhase) return@mechanicEvent
        val player = it.player
        if (player.hgPlayer.kit != None) return@mechanicEvent
        val kit = KitManager.kits.filter { it != None && it.properties.isEnabled }.random()
        player.chooseKit(kit, false)
        player.sendMessage(Prefix.append(Component.text("You have been given the kit ")).append(Component.text(kit.properties.kitname, SecondaryColor)).append(Component.text(".", NamedTextColor.GRAY)))
    }
}