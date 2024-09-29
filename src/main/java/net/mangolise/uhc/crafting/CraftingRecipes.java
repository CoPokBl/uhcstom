package net.mangolise.uhc.crafting;

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

    private static ItemStack i(Material mat, int amount) {
        return ItemStack.of(mat, amount);
    }

    private static ItemStack i(Material mat) {
        return ItemStack.of(mat);
    }

    private static List<Set<Material>> s(Material item) {
        return List.of(Set.of(item));
    }

    public static final List<CraftingRecipe> RECIPES = List.of(
            new ShapedRecipe(List.of(PLANKS, PLANKS, PLANKS, PLANKS), 2, 2, i(Material.CRAFTING_TABLE)),
            new ShapedRecipe(List.of(PLANKS, PLANKS), 1, 2, i(Material.STICK, 4)),
            new UnShapedRecipe(s(Material.OAK_LOG), i(Material.OAK_PLANKS, 4)),
            new UnShapedRecipe(s(Material.BIRCH_LOG), i(Material.BIRCH_PLANKS, 4)),
            new UnShapedRecipe(s(Material.ACACIA_LOG), i(Material.ACACIA_PLANKS, 4)),
            new UnShapedRecipe(s(Material.CHERRY_LOG), i(Material.CHERRY_PLANKS, 4)),
            new UnShapedRecipe(s(Material.JUNGLE_LOG), i(Material.JUNGLE_PLANKS, 4)),
            new UnShapedRecipe(s(Material.DARK_OAK_LOG), i(Material.DARK_OAK_PLANKS, 4)),
            new UnShapedRecipe(s(Material.MANGROVE_LOG), i(Material.MANGROVE_PLANKS, 4)),
            new UnShapedRecipe(s(Material.SPRUCE_LOG), i(Material.SPRUCE_PLANKS, 4)),
            new UnShapedRecipe(List.of(Set.of(Material.DIRT), Set.of(Material.WHEAT_SEEDS)), i(Material.GRASS_BLOCK, 1)),

            new ShapedRecipe(List.of(PLANKS, PLANKS, PLANKS,
                                     PLANKS, DIAMOND,PLANKS,
                                     PLANKS, PLANKS ,PLANKS), 3, 3, i(Material.JUKEBOX)),

            // Pickaxe
            new ShapedRecipe(List.of(PLANKS, PLANKS, PLANKS,
                                     AIR   , STICK , AIR   ,
                                     AIR   , STICK , AIR   ), 3, 3, i(Material.WOODEN_PICKAXE)),
            new ShapedRecipe(List.of(COBBLE, COBBLE, COBBLE,
                                     AIR   , STICK , AIR   ,
                                     AIR   , STICK , AIR   ), 3, 3, i(Material.STONE_PICKAXE)),
            new ShapedRecipe(List.of(IRON, IRON, IRON,
                                     AIR   , STICK , AIR   ,
                                     AIR   , STICK , AIR   ), 3, 3, i(Material.IRON_PICKAXE)),
            new ShapedRecipe(List.of(DIAMOND, DIAMOND, DIAMOND,
                                     AIR   , STICK , AIR   ,
                                     AIR   , STICK , AIR   ), 3, 3, i(Material.DIAMOND_PICKAXE)),
            new ShapedRecipe(List.of(GOLD, GOLD, GOLD,
                                     AIR   , STICK , AIR   ,
                                     AIR   , STICK , AIR   ), 3, 3, i(Material.GOLDEN_PICKAXE)),

            // Axe
            new ShapedRecipe(List.of(PLANKS, PLANKS, AIR,
                                     PLANKS   , STICK , AIR   ,
                                     AIR   , STICK , AIR   ), 3, 3, i(Material.WOODEN_AXE)),
            new ShapedRecipe(List.of(COBBLE, COBBLE, AIR,
                                     COBBLE   , STICK , AIR   ,
                                     AIR   , STICK , AIR   ), 3, 3, i(Material.STONE_AXE)),
            new ShapedRecipe(List.of(IRON, IRON, AIR,
                                     IRON   , STICK , AIR   ,
                                     AIR   , STICK , AIR   ), 3, 3, i(Material.IRON_AXE)),
            new ShapedRecipe(List.of(DIAMOND, DIAMOND, AIR,
                                     DIAMOND   , STICK , AIR   ,
                                     AIR   , STICK , AIR   ), 3, 3, i(Material.DIAMOND_AXE)),
            new ShapedRecipe(List.of(GOLD, GOLD, AIR,
                                     GOLD   , STICK , AIR   ,
                                     AIR   , STICK , AIR   ), 3, 3, i(Material.GOLDEN_AXE))
    );
}
