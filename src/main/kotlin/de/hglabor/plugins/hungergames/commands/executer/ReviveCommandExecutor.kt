package de.hglabor.plugins.hungergames.commands.executer

import de.hglabor.plugins.hungergames.Prefix
import de.hglabor.plugins.hungergames.SecondaryColor
import de.hglabor.plugins.hungergames.game.mechanics.implementation.arena.Arena
import de.hglabor.plugins.hungergames.player.hgPlayer
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ReviveCommandExecutor : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val player = sender as? Player ?: return false
        if (!player.hasPermission("hglabor.staff")) {
            sender.sendMessage("${Prefix}${Color.RED}You are not permitted to execute this command.")
            return false
        }

        if (args.size != 1) {
            sender.sendMessage("${Prefix}Please use ${Color.WHITE}/revive ${Color.GRAY}<${SecondaryColor}Player${Color.GRAY}>.")
            return false
        }

        val target = Bukkit.getPlayer(args[0])

        if (target == null || !target.isOnline) {
            sender.sendMessage("${Prefix}${Color.RED}The player ${Color.RED}${args[0]} ${Color.RED}is not online.")
            return false
        }
        if (target.hgPlayer.isAlive) {
            sender.sendMessage("${Prefix}${Color.GRAY}${target.name} is still ${Color.RED}alive${Color.GRAY}.")
            return false
        }

        if (Arena.currentMatch?.players?.contains(target.hgPlayer) == true) {
            sender.sendMessage("${Prefix}${Color.GRAY}${target.name} is currently fighting in arena.")
            return false
        }
        sender.sendMessage("${Prefix}${SecondaryColor}${target.name}${Color.GRAY} has been ${Color.GREEN}revived${Color.GRAY}.")
        target.hgPlayer.makeGameReady()
        target.hgPlayer.wasInArena = false
        target.hgPlayer.setGameScoreboard(true)
        return true
    }
}