package net.copokbl.uhc.drops;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class OnOffDrop implements ItemDrop {
    private final Material material;
    private final int amount;
    private final float chance;

    public OnOffDrop(Material material, int amount, float chance) {
        this.material = material;
        this.amount = amount;
        this.chance = chance;
    }

    @Override
    public List<ItemStack> get() {
        if (ThreadLocalRandom.current().nextFloat() >= chance) {
            return List.of();
        }

        return List.of(ItemStack.of(material, amount));
    }
}
