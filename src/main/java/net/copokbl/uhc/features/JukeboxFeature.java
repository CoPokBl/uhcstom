package net.copokbl.uhc.features;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.mangolise.gamesdk.Game;
import net.copokbl.uhc.UhcUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

public class JukeboxFeature implements Game.Feature<Game> {
    private static final Tag<ItemStack> PLAYING_DISC_TAG = Tag.ItemStack("jukebox_disc");

    @Override
    public void setup(Context<Game> context) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockInteractEvent.class, this::blockInteract);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent.class, this::blockBreak);
    }

    private void blockBreak(@NotNull PlayerBlockBreakEvent event) {
        popIfPlaying(event.getInstance(), event.getBlockPosition(), event.getBlock());
    }

    /**
     * Replace the last '_' with '.'.
     * @param material The material name.
     * @return The sound name.
     */
    private static SoundEvent convertMaterialToSound(String material) {
        StringBuilder newMat = new StringBuilder(material);
        for (int i = material.length() - 1; i >= 0; i--) {
            if (newMat.charAt(i) == '_') {
                newMat.setCharAt(i, '.');
                return SoundEvent.fromNamespaceId(newMat.toString());
            }
        }

        return SoundEvent.fromNamespaceId(material);
    }

    private void popIfPlaying(Instance world, BlockVec pos, Block block) {
        ItemStack playing = block.getTag(PLAYING_DISC_TAG);
        if (playing == null) {
            return;
        }

        world.setBlock(pos, block.withTag(PLAYING_DISC_TAG, null));
        UhcUtils.drop(world, pos.add(0, 1, 0), playing);

        world.stopSound(SoundStop.named(convertMaterialToSound(playing.material().name())));
    }

    private void blockInteract(@NotNull PlayerBlockInteractEvent event) {
        if (!event.getBlock().compare(Block.JUKEBOX)) return;

        ItemStack inHand = event.getPlayer().getItemInHand(event.getHand());

        popIfPlaying(event.getInstance(), event.getBlockPosition(), event.getBlock());
        event.setBlockingItemUse(true);

        if (!inHand.material().name().contains("music_disc")) return;

        // Set now playing
        SoundEvent sound = convertMaterialToSound(inHand.material().name());
        if (sound == null) {
            event.getPlayer().sendMessage(convertMaterialToSound(inHand.material().name()).namespace() + " is not a sound, " + SoundEvent.MUSIC_DISC_FAR.namespace());
            return;
        }

        event.getInstance().setBlock(event.getBlockPosition(), event.getBlock().withTag(PLAYING_DISC_TAG, inHand));
        event.getPlayer().setItemInHand(event.getHand(), inHand.consume(1));

        event.getInstance().playSound(Sound.sound(sound, Sound.Source.RECORD, 0.7f, 1f), event.getBlockPosition());
    }
}
