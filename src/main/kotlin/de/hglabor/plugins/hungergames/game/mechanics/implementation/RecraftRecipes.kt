package de.hglabor.plugins.hungergames.game.mechanics.implementation

import net.axay.kspigot.extensions.server
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe

object RecraftRecipes {
    fun register() {
        server.addRecipe(recipe("cactus", Material.CACTUS))
        server.addRecipe(recipe("ink_sac", Material.INK_SAC))
    }

    fun recipe(key: String, material: Material): ShapelessRecipe =
        ShapelessRecipe(NamespacedKey("hungergames", key), ItemStack(Material.MUSHROOM_STEW)).apply {
            addIngredient(Material.BOWL)
            addIngredient(material)
        }
}