package net.copokbl.uhc;

import net.copokbl.uhc.crafting.CraftingRecipe;
import net.copokbl.uhc.crafting.CraftingRecipes;
import net.minestom.server.MinecraftServer;
import net.minestom.server.item.Material;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CraftingTest {

    @BeforeAll
    public static void init() {
        MinecraftServer.init();
    }

    private void doCraft(Material expectedResult, List<Material> grid) {
        for (CraftingRecipe recipe : CraftingRecipes.RECIPES) {
            if (recipe.canCraft(grid)) {
                Assertions.assertEquals(expectedResult, recipe.getCraftingResult().material());
            }
        }
    }

    @Test
    public void craftSticks() {
        doCraft(Material.STICK, List.of(
                Material.AIR, Material.AIR, Material.OAK_PLANKS,
                Material.AIR, Material.AIR, Material.JUNGLE_PLANKS,
                Material.AIR, Material.AIR, Material.AIR
        ));
        doCraft(Material.STICK, List.of(
                Material.OAK_PLANKS, Material.AIR, Material.AIR,
                Material.JUNGLE_PLANKS, Material.AIR, Material.AIR,
                Material.AIR, Material.AIR, Material.AIR
        ));
        doCraft(Material.STICK, List.of(
                Material.AIR, Material.OAK_PLANKS, Material.AIR,
                Material.AIR, Material.OAK_PLANKS, Material.AIR,
                Material.AIR, Material.AIR, Material.AIR
        ));
    }

    @Test
    public void craftPlanks() {
        doCraft(Material.OAK_PLANKS, List.of(
                Material.AIR, Material.AIR, Material.AIR,
                Material.AIR, Material.OAK_LOG, Material.AIR,
                Material.AIR, Material.AIR, Material.AIR
        ));
        doCraft(Material.OAK_PLANKS, List.of(
                Material.OAK_LOG, Material.AIR, Material.AIR,
                Material.AIR, Material.AIR, Material.AIR,
                Material.AIR, Material.AIR, Material.AIR
        ));
        doCraft(Material.OAK_PLANKS, List.of(
                Material.AIR, Material.AIR, Material.AIR,
                Material.AIR, Material.AIR, Material.AIR,
                Material.AIR, Material.AIR, Material.OAK_LOG
        ));
    }

    @Test
    public void craftPick() {
        doCraft(Material.STONE_PICKAXE, List.of(
                Material.COBBLESTONE, Material.COBBLESTONE, Material.COBBLESTONE,
                Material.AIR, Material.STICK, Material.AIR,
                Material.AIR, Material.STICK, Material.AIR
        ));
    }

    @Test
    public void craftAxe() {
        doCraft(Material.STONE_AXE, List.of(
                Material.COBBLESTONE, Material.COBBLESTONE, Material.AIR,
                Material.COBBLESTONE, Material.STICK, Material.AIR,
                Material.AIR, Material.STICK, Material.AIR
        ));
        doCraft(Material.STONE_AXE, List.of(
                Material.AIR, Material.COBBLESTONE, Material.COBBLESTONE,
                Material.AIR, Material.COBBLESTONE, Material.STICK,
                Material.AIR, Material.AIR, Material.STICK
        ));
    }

    @Test
    public void craftHoe() {
        doCraft(Material.STONE_HOE, List.of(
                Material.COBBLESTONE, Material.COBBLESTONE, Material.AIR,
                Material.AIR, Material.STICK, Material.AIR,
                Material.AIR, Material.STICK, Material.AIR
        ));
        doCraft(Material.STONE_HOE, List.of(
                Material.AIR, Material.COBBLESTONE, Material.COBBLESTONE,
                Material.AIR, Material.AIR, Material.STICK,
                Material.AIR, Material.AIR, Material.STICK
        ));
    }

    @Test
    public void craftSword() {
        doCraft(Material.STONE_SWORD, List.of(
                Material.AIR, Material.COBBLESTONE, Material.AIR,
                Material.AIR, Material.COBBLESTONE, Material.AIR,
                Material.AIR, Material.STICK, Material.AIR
        ));
        doCraft(Material.STONE_SWORD, List.of(
                Material.AIR, Material.AIR, Material.COBBLESTONE,
                Material.AIR, Material.AIR, Material.COBBLESTONE,
                Material.AIR, Material.AIR, Material.STICK
        ));
        doCraft(Material.STONE_SWORD, List.of(
                Material.COBBLESTONE, Material.AIR, Material.AIR,
                Material.COBBLESTONE, Material.AIR, Material.AIR,
                Material.STICK, Material.AIR, Material.AIR
        ));
    }
}
