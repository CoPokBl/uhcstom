package net.mangolise.uhc.features;

import net.mangolise.gamesdk.Game;
import net.mangolise.uhc.Uhc;
import net.mangolise.uhc.UhcUtils;
import net.mangolise.uhc.drops.CertainDrop;
import net.mangolise.uhc.drops.ItemDrop;
import net.mangolise.uhc.drops.OnOffDrop;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockLoot implements Game.Feature<Uhc> {
    private Uhc uhc;

    private static final Map<Integer, List<ItemDrop>> drops = new HashMap<>() {{
        put(Block.SHORT_GRASS.id(), List.of(new OnOffDrop(Material.WHEAT_SEEDS, 1, 0.125f)));
        put(Block.TALL_GRASS.id(), List.of(new OnOffDrop(Material.WHEAT_SEEDS, 1, 0.125f)));
        put(Block.GRASS_BLOCK.id(), i(Material.DIRT));
        put(Block.OAK_LEAVES.id(), List.of(new OnOffDrop(Material.APPLE, 1, 0.5f)));
        put(Block.ACACIA_LEAVES.id(), List.of(new OnOffDrop(Material.APPLE, 1, 0.5f)));
        put(Block.AZALEA_LEAVES.id(), List.of(new OnOffDrop(Material.APPLE, 1, 0.5f)));
        put(Block.BIRCH_LEAVES.id(), List.of(new OnOffDrop(Material.APPLE, 1, 0.5f)));
        put(Block.DARK_OAK_LEAVES.id(), List.of(new OnOffDrop(Material.APPLE, 1, 0.5f)));
        put(Block.CHERRY_LEAVES.id(), List.of(new OnOffDrop(Material.APPLE, 1, 0.5f)));
        put(Block.JUNGLE_LEAVES.id(), List.of(new OnOffDrop(Material.APPLE, 1, 0.5f)));
        put(Block.SPRUCE_LEAVES.id(), List.of(new OnOffDrop(Material.APPLE, 1, 0.5f)));
        put(Block.DIAMOND_ORE.id(), i(Material.DIAMOND));
        put(Block.DEEPSLATE_DIAMOND_ORE.id(), i(Material.DIAMOND));
        put(Block.IRON_ORE.id(), i(Material.IRON_INGOT));
        put(Block.DEEPSLATE_IRON_ORE.id(), i(Material.IRON_INGOT));
        put(Block.GOLD_ORE.id(), i(Material.GOLD_INGOT));
        put(Block.DEEPSLATE_GOLD_ORE.id(), i(Material.GOLD_INGOT));
        put(Block.LAPIS_ORE.id(), i(Material.LAPIS_LAZULI));
        put(Block.DEEPSLATE_LAPIS_ORE.id(), i(Material.LAPIS_LAZULI));
        put(Block.COAL_ORE.id(), i(Material.COAL));
        put(Block.DEEPSLATE_COAL_ORE.id(), i(Material.COAL));
        put(Block.COPPER_ORE.id(), i(Material.COPPER_INGOT));
        put(Block.DEEPSLATE_COPPER_ORE.id(), i(Material.COPPER_INGOT));
        put(Block.EMERALD_ORE.id(), i(Material.EMERALD));
        put(Block.STONE.id(), i(Material.COBBLESTONE));
        put(Block.DEEPSLATE_EMERALD_ORE.id(), i(Material.EMERALD));
        put(Block.DEAD_BUSH.id(), List.of(new OnOffDrop(Material.STICK, 1, 0.4f)));
    }};

    private static List<ItemDrop> i(Material material) {
        return List.of(new CertainDrop(material));
    }

    @Override
    public void setup(Context<Uhc> context) {
        uhc = context.game();
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent.class, this::blockBreak);
    }

    private BlockVec getBedOffset(String part, String facing) {
        return switch (facing) {
            case "north" -> switch (part) {
                case "head" -> new BlockVec(0, 0, 1);
                case "foot" -> new BlockVec(0, 0, -1);
                default -> throw new IllegalStateException("Unexpected value: " + part);
            };
            case "south" -> switch (part) {
                case "head" -> new BlockVec(0, 0, -1);
                case "foot" -> new BlockVec(0, 0, 1);
                default -> throw new IllegalStateException("Unexpected value: " + part);
            };
            case "east" -> switch (part) {
                case "head" -> new BlockVec(-1, 0, 0);
                case "foot" -> new BlockVec(1, 0, 0);
                default -> throw new IllegalStateException("Unexpected value: " + part);
            };
            case "west" -> switch (part) {
                case "head" -> new BlockVec(1, 0, 0);
                case "foot" -> new BlockVec(-1, 0, 0);
                default -> throw new IllegalStateException("Unexpected value: " + part);
            };
            default -> throw new IllegalStateException("Unexpected value: " + facing);
        };
    }

    private void blockBreak(PlayerBlockBreakEvent event) {
        MinecraftServer.getSchedulerManager().scheduleEndOfTick(() -> {
            Block block = event.getBlock();
            String half = block.getProperty("half");
            if (half != null) switch (half) {
                case "upper" -> event.getInstance().setBlock(event.getBlockPosition().sub(0, 1, 0), Block.AIR);
                case "lower" -> event.getInstance().setBlock(event.getBlockPosition().add(0, 1, 0), Block.AIR);
            }

            String part = block.getProperty("part");
            String facing = block.getProperty("facing");
            if (part != null && facing != null) {
                event.getInstance().setBlock(event.getBlockPosition().add(getBedOffset(part, facing)), Block.AIR);
            }

            if (event.isCancelled()) {
                return;
            }

            List<ItemDrop> items = drops.get(block.id());
            if (items == null) {
                Material mat = block.registry().material();
                if (mat != null) {
                    UhcUtils.drop(uhc.world(), event.getBlockPosition().add(0.5, 0, 0.5), ItemStack.of(mat));
                }
                return;
            }

            for (ItemDrop drop : items) {
                for (ItemStack stack : drop.get()) {
                    UhcUtils.drop(uhc.world(), event.getBlockPosition().add(0.5, 0, 0.5), stack);
                }
            }
        });
    }
}
