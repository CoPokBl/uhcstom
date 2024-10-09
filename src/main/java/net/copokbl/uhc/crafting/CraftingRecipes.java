package net.copokbl.uhc.crafting;

import net.mangolise.gamesdk.features.crafting.CraftingRecipe;
import net.mangolise.gamesdk.features.crafting.ShapedRecipe;
import net.mangolise.gamesdk.features.crafting.UnShapedRecipe;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;
import java.util.Set;

public class CraftingRecipes {
    private static final Set<Material> LOGS = Set.of(
            Material.OAK_LOG,
            Material.BIRCH_LOG,
            Material.ACACIA_LOG,
            Material.CHERRY_LOG,
            Material.JUNGLE_LOG,
            Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG,
            Material.SPRUCE_LOG
    );

    private static final Set<Material> PLANKS = Set.of(
            Material.OAK_PLANKS,
            Material.BIRCH_PLANKS,
            Material.ACACIA_PLANKS,
            Material.CHERRY_PLANKS,
            Material.JUNGLE_PLANKS,
            Material.DARK_OAK_PLANKS,
            Material.MANGROVE_PLANKS,
            Material.SPRUCE_PLANKS
    );

    private static final Set<Material> AIR = Set.of(Material.AIR);
    private static final Set<Material> STICK = Set.of(Material.STICK);
    private static final Set<Material> DIAMOND = Set.of(Material.DIAMOND);
    private static final Set<Material> IRON = Set.of(Material.IRON_INGOT);
    private static final Set<Material> GOLD = Set.of(Material.GOLD_INGOT);
    private static final Set<Material> COBBLE = Set.of(Material.COBBLESTONE);
    private static final Set<Material> NETHERITE = Set.of(Material.NETHERITE_INGOT);

    private static ItemStack i(Material mat, int amount) {
        return ItemStack.of(mat, amount);
    }

    private static ItemStack i(Material mat) {
        return ItemStack.of(mat);
    }

    private static List<Set<Material>> s(Material item) {
        return List.of(Set.of(item));
    }

    private static Set<Material> o(Material item) {
        return Set.of(item);
    }

    public static final List<CraftingRecipe> RECIPES;

    static {
        RECIPES = List.of(
                new ShapedRecipe(List.of(PLANKS, PLANKS, PLANKS, PLANKS), 2, 2, i(Material.CRAFTING_TABLE)), // Crafting
                new ShapedRecipe(List.of(PLANKS, PLANKS), 1, 2, i(Material.STICK, 4)),  // Sticks

                // Planks
                new UnShapedRecipe(s(Material.OAK_LOG), i(Material.OAK_PLANKS, 4)),
                new UnShapedRecipe(s(Material.BIRCH_LOG), i(Material.BIRCH_PLANKS, 4)),
                new UnShapedRecipe(s(Material.ACACIA_LOG), i(Material.ACACIA_PLANKS, 4)),
                new UnShapedRecipe(s(Material.CHERRY_LOG), i(Material.CHERRY_PLANKS, 4)),
                new UnShapedRecipe(s(Material.JUNGLE_LOG), i(Material.JUNGLE_PLANKS, 4)),
                new UnShapedRecipe(s(Material.DARK_OAK_LOG), i(Material.DARK_OAK_PLANKS, 4)),
                new UnShapedRecipe(s(Material.MANGROVE_LOG), i(Material.MANGROVE_PLANKS, 4)),
                new UnShapedRecipe(s(Material.SPRUCE_LOG), i(Material.SPRUCE_PLANKS, 4)),
                new UnShapedRecipe(List.of(o(Material.DIRT), o(Material.WHEAT_SEEDS)), i(Material.GRASS_BLOCK, 1)),

                // Jukebox
                new ShapedRecipe(List.of(PLANKS, PLANKS, PLANKS,
                                         PLANKS, DIAMOND, PLANKS,
                                         PLANKS, PLANKS, PLANKS), 3, 3, i(Material.JUKEBOX)),

                // Gap
                new ShapedRecipe(List.of(GOLD, GOLD, GOLD,
                                         GOLD, o(Material.APPLE), GOLD,
                                         GOLD, GOLD, GOLD), 3, 3, i(Material.GOLDEN_APPLE)),
                new ShapedRecipe(List.of(GOLD, GOLD, GOLD,
                                         GOLD, o(Material.PLAYER_HEAD), GOLD,
                                         GOLD, GOLD, GOLD), 3, 3, CustomItems.GOLDEN_HEAD),

                // Bucket
                new ShapedRecipe(List.of(IRON, AIR, IRON,
                                         AIR, IRON, AIR), 3, 2, i(Material.BUCKET)),

                // Pickaxe
                new ShapedRecipe(List.of(PLANKS, PLANKS, PLANKS,
                        AIR, STICK, AIR,
                        AIR, STICK, AIR), 3, 3, i(Material.WOODEN_PICKAXE)),
                new ShapedRecipe(List.of(COBBLE, COBBLE, COBBLE,
                        AIR, STICK, AIR,
                        AIR, STICK, AIR), 3, 3, i(Material.STONE_PICKAXE)),
                new ShapedRecipe(List.of(IRON, IRON, IRON,
                        AIR, STICK, AIR,
                        AIR, STICK, AIR), 3, 3, i(Material.IRON_PICKAXE)),
                new ShapedRecipe(List.of(DIAMOND, DIAMOND, DIAMOND,
                        AIR, STICK, AIR,
                        AIR, STICK, AIR), 3, 3, i(Material.DIAMOND_PICKAXE)),
                new ShapedRecipe(List.of(GOLD, GOLD, GOLD,
                        AIR, STICK, AIR,
                        AIR, STICK, AIR), 3, 3, i(Material.GOLDEN_PICKAXE)),
                new ShapedRecipe(List.of(NETHERITE, NETHERITE, NETHERITE,
                        AIR, STICK, AIR,
                        AIR, STICK, AIR), 3, 3, i(Material.NETHERITE_PICKAXE)),

                // Axe
                new ShapedRecipe(List.of(PLANKS, PLANKS,
                        PLANKS, STICK,
                        AIR, STICK), 2, 3, i(Material.WOODEN_AXE)),
                new ShapedRecipe(List.of(COBBLE, COBBLE,
                        COBBLE, STICK,
                        AIR, STICK), 2, 3, i(Material.STONE_AXE)),
                new ShapedRecipe(List.of(IRON, IRON,
                        IRON, STICK,
                        AIR, STICK), 2, 3, i(Material.IRON_AXE)),
                new ShapedRecipe(List.of(DIAMOND, DIAMOND,
                        DIAMOND, STICK,
                        AIR, STICK), 2, 3, i(Material.DIAMOND_AXE)),
                new ShapedRecipe(List.of(GOLD, GOLD,
                        GOLD, STICK,
                        AIR, STICK), 2, 3, i(Material.GOLDEN_AXE)),
                new ShapedRecipe(List.of(NETHERITE, NETHERITE,
                        NETHERITE, STICK,
                        AIR, STICK), 2, 3, i(Material.NETHERITE_AXE)),

                // Sword
                new ShapedRecipe(List.of(PLANKS,
                        PLANKS,
                        STICK), 1, 3, i(Material.WOODEN_SWORD)),
                new ShapedRecipe(List.of(COBBLE,
                        COBBLE,
                        STICK), 1, 3, i(Material.STONE_SWORD)),
                new ShapedRecipe(List.of(IRON,
                        IRON,
                        STICK), 1, 3, i(Material.IRON_SWORD)),
                new ShapedRecipe(List.of(DIAMOND,
                        DIAMOND,
                        STICK), 1, 3, i(Material.DIAMOND_SWORD)),
                new ShapedRecipe(List.of(GOLD,
                        GOLD,
                        STICK), 1, 3, i(Material.GOLDEN_SWORD)),
                new ShapedRecipe(List.of(NETHERITE,
                        NETHERITE,
                        STICK), 1, 3, i(Material.NETHERITE_SWORD)),

                // Shovel
                new ShapedRecipe(List.of(PLANKS,
                        STICK,
                        STICK), 1, 3, i(Material.WOODEN_SHOVEL)),
                new ShapedRecipe(List.of(COBBLE,
                        STICK,
                        STICK), 1, 3, i(Material.STONE_SHOVEL)),
                new ShapedRecipe(List.of(IRON,
                        STICK,
                        STICK), 1, 3, i(Material.IRON_SHOVEL)),
                new ShapedRecipe(List.of(DIAMOND,
                        STICK,
                        STICK), 1, 3, i(Material.DIAMOND_SHOVEL)),
                new ShapedRecipe(List.of(GOLD,
                        STICK,
                        STICK), 1, 3, i(Material.GOLDEN_SHOVEL)),
                new ShapedRecipe(List.of(NETHERITE,
                        STICK,
                        STICK), 1, 3, i(Material.NETHERITE_SHOVEL)),

                // Hoe
                new ShapedRecipe(List.of(PLANKS, PLANKS,
                        AIR, STICK,
                        AIR, STICK), 2, 3, i(Material.WOODEN_HOE)),
                new ShapedRecipe(List.of(COBBLE, COBBLE,
                        AIR, STICK,
                        AIR, STICK), 2, 3, i(Material.STONE_HOE)),
                new ShapedRecipe(List.of(IRON, IRON,
                        IRON, STICK,
                        AIR, STICK), 2, 3, i(Material.IRON_HOE)),
                new ShapedRecipe(List.of(DIAMOND, DIAMOND,
                        AIR, STICK,
                        AIR, STICK), 2, 3, i(Material.DIAMOND_HOE)),
                new ShapedRecipe(List.of(GOLD, GOLD,
                        AIR, STICK,
                        AIR, STICK), 2, 3, i(Material.GOLDEN_HOE)),
                new ShapedRecipe(List.of(NETHERITE, NETHERITE,
                        AIR, STICK,
                        AIR, STICK), 2, 3, i(Material.NETHERITE_HOE))
                );
    }
}
