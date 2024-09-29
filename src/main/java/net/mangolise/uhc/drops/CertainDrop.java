package net.mangolise.uhc.drops;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

public class CertainDrop implements ItemDrop {
    private final Material material;
    private final int amount;

    public CertainDrop(Material material) {
        this.material = material;
        this.amount = 1;
    }

    public CertainDrop(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    @Override
    public List<ItemStack> get() {
        return List.of(ItemStack.of(material, amount));
    }
}
