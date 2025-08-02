package de.hglabor.plugins.hungergames.game.phase.phases

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.SecondaryColor
import de.hglabor.plugins.hungergames.commands.command.BanSpecsCommand
import de.hglabor.plugins.hungergames.commands.executer.BanSpecsCommandExecutor
import de.hglabor.plugins.hungergames.game.GameManager
import de.hglabor.plugins.hungergames.game.mechanics.feast.Feast
import de.hglabor.plugins.hungergames.game.mechanics.implementation.KitSelector
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.Arena
import de.hglabor.plugins.hungergames.game.phase.IngamePhase
import de.hglabor.plugins.hungergames.player.PlayerList
import de.hglabor.plugins.hungergames.player.PlayerStatus
import de.hglabor.plugins.hungergames.player.StaffPlayer
import de.hglabor.plugins.hungergames.utils.LocationUtils
import de.hglabor.plugins.hungergames.utils.TimeConverter
import de.hglabor.plugins.kitapi.implementation.None
import de.hglabor.plugins.kitapi.kit.KitManager
import de.hglabor.plugins.kitapi.player.PlayerKits.chooseKit
import de.hglabor.plugins.hungergames.HungerGames
import de.hglabor.plugins.hungergames.Manager
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.async
import net.axay.kspigot.runnables.sync
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import kotlin.collections.forEach

object PvPPhase : IngamePhase(3600, EndPhase) {
    override val timeName = "Time"
    override fun getTimeString() = TimeConverter.stringify((GameManager.elapsedTime.get()).toInt())

    override fun onStart() {
        PlayerList.alivePlayers.forEach { hgPlayer ->
            val player = hgPlayer.bukkitPlayer
            player?.inventory?.remove(KitSelector.kitSelectorItem)
            if (hgPlayer.kit == None && !hgPlayer.changedKitBefore) {
                val kit = KitManager.kits.filter { it.properties.isEnabled }.random()
                player?.chooseKit(kit, false)
                player?.sendMessage(Prefix.append(Component.text("You have been given the kit ")).append(Component.text(kit.properties.kitname, SecondaryColor)).append(Component.text(".", NamedTextColor.GRAY)))
            }
        }
    }

    override fun tick(tickCount: Int) {
        fun handleCombatTimer() {
            async {
                PlayerList.alivePlayers.filter { it.isInCombat }.forEach { alive ->
                    alive.combatTimer.decrementAndGet()
                }
            }
        }

        fun handleBorderShrink() {
            // Bordershrink - 20 min vor ende
            if (remainingTime.toInt() == 20 * 60) {
                Manager.audience.sendMessage(
                    Prefix.append(
                        Component.text("The border starts shrinking now.")
                            .color(NamedTextColor.WHITE)
                            .decorate(TextDecoration.BOLD)
                    )
                )
                GameManager.world?.worldBorder?.setSize(25.0 * 2, 10 * 60)
            }
        }

        fun handleFeast() {
            // Feast - nach 10 minuten announcen | 5 min später spawnt es
            if (tickCount == 600) {
                val world = GameManager.world
                if (world != null) {
                    GameManager.feast = Feast(world).apply {
                        feastCenter = LocationUtils.getHighestBlock(world, (world.worldBorder.size / 4).toInt(), 0)
                        spawn()
                    }
                }
            }
        }

        fun checkForWinner() {
            // Winner
            if (PlayerList.alivePlayers.size <= 1 && Arena.currentMatch == null && Arena.queuedPlayers.size < 2) {
                GameManager.startNextPhase()
            }
        }

        fun teleportAutisticSpectators() {
            async {
                if (tickCount % 2 != 0) return@async
                val worldBorder = GameManager.world?.worldBorder
                val borderRadius = worldBorder?.size?.div(2.0)

                onlinePlayers.filter { it.gameMode == GameMode.SPECTATOR }.forEach { player ->
                    val playerLoc = player.location
                    if (borderRadius != null &&
                        (playerLoc.x > borderRadius || playerLoc.x < -borderRadius ||
                                playerLoc.z > borderRadius || playerLoc.z < -borderRadius)
                    ) {
                        sync {
                            GameManager.world.spawnLocation.let { safeLoc -> player.teleport(safeLoc) }
                        }
                    }
                }
            }
        }

        fun kickSpectatorsIfBanned() {
            if (tickCount % 5 != 0) return
            if (BanSpecsCommandExecutor.allowSpecs) return
            PlayerList.spectatingPlayers.mapNotNull {
                if (it is StaffPlayer) null
                else it.bukkitPlayer
            }.forEach {
                it.kick(Component.text("Sorry, you can't spectate anymore."))
            }
        }

        handleCombatTimer()
        handleBorderShrink()
        handleFeast()
        checkForWinner()
        teleportAutisticSpectators()
        kickSpectatorsIfBanned()

        super.tick(tickCount)
    }

    @EventHandler
    fun onPlayerPreLogin(event: AsyncPlayerPreLoginEvent) {
        if (BanSpecsCommandExecutor.allowSpecs) return
        if (Bukkit.getOfflinePlayer(event.uniqueId).isOp) return
        val hgPlayer = PlayerList.getPlayer(event.uniqueId)
        if (hgPlayer is StaffPlayer) return
        if (hgPlayer == null || hgPlayer.status == PlayerStatus.ELIMINATED || hgPlayer.status == PlayerStatus.SPECTATOR) {
            event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                Component.text("Sorry, you can't spectate this game.")
            )
        }
    }
}
