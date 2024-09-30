package net.copokbl.uhc.features;

import net.kyori.adventure.sound.Sound;
import net.mangolise.gamesdk.Game;
import net.copokbl.uhc.UhcUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ChestsFeature implements Game.Feature<Game> {
    private static final Map<BlockVec, Inventory> CHEST_CONTENTS = new HashMap<>();
    private static final List<WeightedLoot> LOOT_TABLE = List.of(
            new WeightedLoot(20, 1, 1, Material.AIR),
            new WeightedLoot(1, 1, 3, Material.DIAMOND),
            new WeightedLoot(4, 1, 3, Material.IRON_INGOT),
            new WeightedLoot(1, 1, 3, Material.GOLD_INGOT),
            new WeightedLoot(1, 1, 1, Material.CHAINMAIL_CHESTPLATE),
            new WeightedLoot(1, 1, 1, Material.MUSIC_DISC_PIGSTEP),
            new WeightedLoot(5, 1, 1, Material.STICK)
    );
    private static final int LOOT_TABLE_WEIGHT_TOTAL = LOOT_TABLE.stream().mapToInt(loot -> loot.weight).sum();
    private static final int ThreeXThreeSlots = 3 * 9;

    @Override
    public void setup(Context<Game> context) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockInteractEvent.class, this::blockInteract);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent.class, this::blockBreak);
    }

    private Inventory getChestInv(BlockVec pos) {
        return CHEST_CONTENTS.computeIfAbsent(pos, k -> generateLoot());
    }

    private void blockBreak(@NotNull PlayerBlockBreakEvent event) {
        if (!event.getBlock().compare(Block.CHEST)) return;

        Inventory inv = getChestInv(event.getBlockPosition());
        for (ItemStack item : inv.getItemStacks()) {
            UhcUtils.drop(event.getInstance(), event.getBlockPosition(), item);
        }

        CHEST_CONTENTS.remove(event.getBlockPosition());
    }

    private void blockInteract(@NotNull PlayerBlockInteractEvent event) {
        if (!event.getBlock().compare(Block.CHEST)) return;

        Inventory inv = getChestInv(event.getBlockPosition());
        event.getPlayer().openInventory(inv);
        event.getInstance().playSound(Sound.sound(SoundEvent.BLOCK_CHEST_OPEN, Sound.Source.BLOCK, 0.5f, 1f), event.getBlockPosition());
        event.setBlockingItemUse(true);
    }

    private static Inventory generateLoot() {
        Inventory inv = new Inventory(InventoryType.CHEST_3_ROW, "Chest");

        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < ThreeXThreeSlots; i++) {
            int currentItem = random.nextInt(LOOT_TABLE_WEIGHT_TOTAL + 1);
            for (WeightedLoot loot : LOOT_TABLE) {
                currentItem -= loot.weight;

                if (currentItem > 0) continue;

                // We selected this
                int amount = loot.min == loot.max ? loot.min : random.nextInt(loot.min, loot.max);
                ItemStack item = ItemStack.of(loot.item, amount);

                inv.setItemStack(i, item);
                break;
            }
        }

        return inv;
    }

    record WeightedLoot(int weight, int min, int max, Material item) { }
}
