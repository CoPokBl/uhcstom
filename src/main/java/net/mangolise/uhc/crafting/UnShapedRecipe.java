package net.mangolise.uhc.crafting;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.*;

public class UnShapedRecipe implements CraftingRecipe {
    public final List<Set<Material>> items;
    public final ItemStack result;

    public UnShapedRecipe(List<Set<Material>> items, ItemStack result) {
        this.items = items;
        this.result = result;
    }

    @Override
    public boolean canCraft(List<Material> slots) {
        List<Material> modSlots = new ArrayList<>(List.copyOf(slots));

        main: for (Set<Material> req : items) {
            for (Material item : req) {
                int index = modSlots.indexOf(item);
                if (index != -1) {
                    modSlots.remove(index);
                    continue main;
                }
            }

            return false;
        }

        return true;
    }

    @Override
    public void removeItems(List<ItemStack> slots) {
        slots.replaceAll(itemStack -> {
            if (itemStack.isAir() || itemStack.amount() == 1) {
                return ItemStack.AIR;
            }

            return itemStack.consume(1);
        });
    }

    @Override
    public ItemStack getCraftingResult() {
        return result;
    }
}
