package net.copokbl.uhc;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.item.ItemStack;

public record PlayerData(Pos pos, ItemStack[] inventory, float health, int food, float saturation) {
}
