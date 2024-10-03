package net.copokbl.uhc.crafting;

import net.mangolise.gamesdk.util.ChatUtil;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.Food;
import net.minestom.server.item.component.HeadProfile;

import java.util.List;

public class CustomItems {
    private static final String GOLDEN_HEAD_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTcyNzk0ODAyNzU1NiwKICAicHJvZmlsZUlkIiA6ICI2MTE5Y2RhNGI2MGQ0ODc4ODdkMTY0NmM0NGVjMzM3NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVNYW5Sb2JvdCIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mODA2NDFmODgyM2Q4Mzg0NzRlZDI3MmJlYThiYjcwOWMxNTE0OWFlOWFhY2IyMzhkODZhNTE1NWNjMjQ1MGFmIgogICAgfQogIH0KfQ";

    public static final ItemStack GOLDEN_HEAD;

    static {
        GOLDEN_HEAD = ItemStack.of(Material.PLAYER_HEAD)
                .with(
                    ItemComponent.FOOD,
                    new Food(1, 1, true, 0.5f, ItemStack.AIR, List.of()))
                .withCustomName(ChatUtil.toComponent("&e&lGolden Head"))
                .with(ItemComponent.PROFILE, new HeadProfile(new PlayerSkin(GOLDEN_HEAD_SKIN, null)));
    }
}
