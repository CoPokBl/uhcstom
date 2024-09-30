package net.copokbl.uhc.crafting;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;
import java.util.Set;

public class ShapedRecipe implements CraftingRecipe {
    public final List<Set<Material>> shape;
    public final int width;
    public final int height;
    public final ItemStack result;

    public ShapedRecipe(List<Set<Material>> shape, int width, int height, ItemStack result) {
        if (shape.size() != width * height) {
            throw new IllegalArgumentException("Shape size must be width * height");
        }

        this.shape = shape;
        this.width = width;
        this.height = height;
        this.result = result;
    }

    @Override
    public boolean canCraft(List<Material> slots) {
        attempt: for (int x = 0; x < 4-width; x++) {
            for (int y = 0; y < 4-height; y++) {
                for (int i = 0; i < shape.size(); i++) {
                    Set<Material> check = shape.get(i);
                    int slot = (i % width + x) + ((i / width + y) * 3);

                    if (!check.contains(slots.get(slot))) {
                        continue attempt;
                    }
                }

                return true;
            }
        }

        return false;
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
