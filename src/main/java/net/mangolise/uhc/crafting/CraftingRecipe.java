package net.mangolise.uhc.crafting;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

public interface CraftingRecipe {
    boolean canCraft(List<Material> slots);
    void removeItems(List<ItemStack> slots);
    ItemStack getCraftingResult();
}
