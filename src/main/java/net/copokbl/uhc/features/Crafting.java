package net.copokbl.uhc.features;

import net.mangolise.gamesdk.Game;
import net.copokbl.uhc.Uhc;
import net.copokbl.uhc.UhcUtils;
import net.copokbl.uhc.crafting.CraftingRecipe;
import net.copokbl.uhc.crafting.CraftingRecipes;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryClickEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// TODO: This code is very broken
public class Crafting implements Game.Feature<Uhc> {
    public static final List<Integer> PINV_CRAFTING_SLOTS = List.of(37, 38, 39, 40);
    public static final int PINV_RESULT_SLOT = 36;
    public static final int CINV_RESULT_SLOT = 0;

    @Override
    public void setup(Context<Uhc> context) {
        MinecraftServer.getGlobalEventHandler().addListener(InventoryPreClickEvent.class, this::invPreClick);
        MinecraftServer.getGlobalEventHandler().addListener(InventoryClickEvent.class, this::invClick);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockInteractEvent.class, this::blockInteract);
    }

    private void blockInteract(@NotNull PlayerBlockInteractEvent event) {
        if (event.getBlock().compare(Block.CRAFTING_TABLE)) {
            event.getPlayer().openInventory(new Inventory(InventoryType.CRAFTING, "Crafting"));
        }
    }

    private void invClick(@NotNull InventoryClickEvent event) {
        if (event.getInventory() == null) {  // Player inventory
            playerInvClick(event);
            return;
        }

        if (event.getInventory().getInventoryType() == InventoryType.CRAFTING) {
            craftingInvClick(event);
            return;
        }
    }

    private List<Material> getPInvSlots(PlayerInventory inventory) {
        return List.of(
                inventory.getItemStack(PINV_CRAFTING_SLOTS.getFirst()).material(), inventory.getItemStack(PINV_CRAFTING_SLOTS.get(1)).material(), Material.AIR,
                inventory.getItemStack(PINV_CRAFTING_SLOTS.get(2)).material(), inventory.getItemStack(PINV_CRAFTING_SLOTS.get(3)).material(), Material.AIR,
                Material.AIR, Material.AIR, Material.AIR
        );
    }

    private List<Material> getCInvSlots(Inventory inventory) {
        List<Material> items = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            items.add(inventory.getItemStack(i).material());
        }
        return List.copyOf(items);
    }

    private List<ItemStack> getCInvSlotItems(Inventory inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            items.add(inventory.getItemStack(i));
        }
        return List.copyOf(items);
    }

    private void setInvItem(Object inv, int slot, ItemStack stack) {
        if (inv instanceof PlayerInventory pInv) {
            pInv.setItemStack(slot, stack);
        } else if (inv instanceof Inventory rInv) {
            rInv.setItemStack(slot, stack);
        }
    }

    private CraftingRecipe updateTargetRecipe(List<Material> slots, Object inventory, int resultSlot) {
        CraftingRecipe targetRecipe = null;
        for (CraftingRecipe recipe : CraftingRecipes.RECIPES) {
            if (!recipe.canCraft(slots)) continue;
            targetRecipe = recipe;
            setInvItem(inventory, resultSlot, recipe.getCraftingResult());
            break;
        }

        if (targetRecipe == null) {
            setInvItem(inventory, resultSlot, ItemStack.AIR);
            return null;
        }

        return targetRecipe;
    }

    private void playerInvClick(InventoryClickEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

//        player.sendMessage("Click slot type(" + event.getSlot() + "): " + inventory.getItemStack(event.getSlot()).material().name());
        List<Material> slots = getPInvSlots(inventory);

        CraftingRecipe targetRecipe = updateTargetRecipe(slots, inventory, PINV_RESULT_SLOT);
        if (targetRecipe == null) {
            return;
        }

        if (inventory.getItemStack(event.getSlot()).isAir())  {
            return;
        }


        if (!event.getClickedItem().isAir() && event.getClickedItem().isSimilar(inventory.getItemStack(PINV_RESULT_SLOT))) {
//        if (!event.getClickedItem().isAir() && event.getSlot() == PINV_RESULT_SLOT) {
            List<ItemStack> existingSlots = new ArrayList<>(List.of(
                    inventory.getItemStack(PINV_CRAFTING_SLOTS.getFirst()), inventory.getItemStack(PINV_CRAFTING_SLOTS.get(1)), ItemStack.AIR,
                    inventory.getItemStack(PINV_CRAFTING_SLOTS.get(2)), inventory.getItemStack(PINV_CRAFTING_SLOTS.get(3)), ItemStack.AIR,
                    ItemStack.AIR, ItemStack.AIR, ItemStack.AIR
            ));

            if (event.getClickType() == ClickType.SHIFT_CLICK) {
                int craftedCount = 0;
                while (targetRecipe.canCraft(slots)) {
                    removeAndUpdateItemPInv(inventory, targetRecipe, existingSlots);

                    slots = getPInvSlots(inventory);
                    craftedCount++;
                }

                for (int i = 1; i < craftedCount; i++) {
                    if (inventory.addItemStack(targetRecipe.getCraftingResult())) {
                        continue;
                    }

                    // Throw on ground
                    UhcUtils.drop(player.getInstance(), player.getPosition(), targetRecipe.getCraftingResult());
                }
            } else {
                removeAndUpdateItemPInv(inventory, targetRecipe, existingSlots);
            }
        }

        slots = getPInvSlots(inventory);
        updateTargetRecipe(slots, inventory, PINV_RESULT_SLOT);
    }

    private void removeAndUpdateItemPInv(PlayerInventory inventory, CraftingRecipe targetRecipe, List<ItemStack> existingSlots) {
        targetRecipe.removeItems(existingSlots);

        inventory.setItemStack(PINV_CRAFTING_SLOTS.getFirst(), existingSlots.getFirst());
        inventory.setItemStack(PINV_CRAFTING_SLOTS.get(1), existingSlots.get(1));
        inventory.setItemStack(PINV_CRAFTING_SLOTS.get(2), existingSlots.get(3));
        inventory.setItemStack(PINV_CRAFTING_SLOTS.get(3), existingSlots.get(4));
    }

    private void craftingInvClick(@NotNull InventoryClickEvent event) {
        Player player = event.getPlayer();
        Inventory inventory = event.getInventory();
        if (inventory == null) {
            return;
        }

        List<Material> slots = getCInvSlots(inventory);

        CraftingRecipe targetRecipe = updateTargetRecipe(slots, inventory, CINV_RESULT_SLOT);
        if (targetRecipe == null) {
            return;
        }

        if (inventory.getItemStack(event.getSlot()).isAir())  {
            return;
        }

        if (inventory.getItemStack(event.getSlot()).isAir() && !event.getClickedItem().isAir() && event.getClickedItem().isSimilar(inventory.getItemStack(CINV_RESULT_SLOT))) {
            List<ItemStack> existingSlots = new ArrayList<>(getCInvSlotItems(inventory));

            if (event.getClickType() == ClickType.SHIFT_CLICK) {
                int craftedCount = 0;
                while (targetRecipe.canCraft(slots)) {
                    targetRecipe.removeItems(existingSlots);
                    for (int i = 0; i < 9; i++) {
                        inventory.setItemStack(i + 1, existingSlots.get(i));
                    }
                    slots = getCInvSlots(inventory);
                    craftedCount++;
                }

                for (int i = 1; i < craftedCount; i++) {
                    if (inventory.addItemStack(targetRecipe.getCraftingResult())) {
                        continue;
                    }

                    // Throw on ground
                    UhcUtils.drop(player.getInstance(), player.getPosition(), targetRecipe.getCraftingResult());
                }
            } else {
                targetRecipe.removeItems(existingSlots);

                for (int i = 0; i < 9; i++) {
                    inventory.setItemStack(i + 1, existingSlots.get(i));
                }
            }
        }

        slots = getCInvSlots(inventory);
        updateTargetRecipe(slots, inventory, CINV_RESULT_SLOT);
    }

    private void invPreClick(@NotNull InventoryPreClickEvent event) {
        int resultSlot = event.getInventory() == null ? PINV_RESULT_SLOT : CINV_RESULT_SLOT;
        if (event.getClickType() != ClickType.SHIFT_CLICK &&
                event.getClickType() != ClickType.START_SHIFT_CLICK &&
                (!event.getCursorItem().isAir() && event.getSlot() == resultSlot && !event.getCursorItem().material().equals(event.getClickedItem().material()))) {
            event.setCancelled(true);
        }
    }
}
