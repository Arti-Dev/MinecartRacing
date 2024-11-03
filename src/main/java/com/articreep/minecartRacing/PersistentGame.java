package com.articreep.minecartRacing;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class PersistentGame implements Game, Listener {
    public static final double MAX_SPEED = 2.0;
    public static final boolean SUSTAIN_SPEED = false;
    public static final double SPEED_INCREMENT = 0.1;
    private final Map<Player, TeamColor> playerToColor = new HashMap<>();
    private final Map<Player, GameMinecart> playerToMinecart = new HashMap<>();
    private final WoolGenerator woolGenerator = new WoolGenerator();
    private BukkitTask woolGenerationTask;
    public static final int WOOL_RESET_INTERVAL = 20;


    @Override
    public void addPlayer(Player player) {
        TeamColor color = chooseColor();
        playerToColor.put(player, color);
        player.sendMessage("You are " + color.chatColor + color);
    }

    @Override
    public void removePlayer(Player player) {
        if (playerToMinecart.containsKey(player)) {
            GameMinecart toRemove = playerToMinecart.get(player);
            // Kill next tick in the case that the player dismounted from the minecart voluntarily
            Bukkit.getScheduler().runTask(MinecartRacing.getInstance(), toRemove::kill);
        }
        playerToMinecart.remove(player);
        playerToColor.remove(player);
    }

    @Override
    public void startGame() {
        Bukkit.getPluginManager().registerEvents(this, MinecartRacing.getInstance());
        woolGenerationTask = woolGenerationLoop();
    }

    private BukkitTask woolGenerationLoop() {
        return new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                woolGenerator.generateWool(playerToColor);
                if (i == WOOL_RESET_INTERVAL) {
                    woolGenerator.resetBlocksOutOfRange(playerToColor.keySet());
                    i = 0;
                }
                i++;
            }
        }.runTaskTimer(MinecartRacing.getInstance(), 0, 20);
    }

    @Override
    public void stopGame() {
        HandlerList.unregisterAll(this);
        if (woolGenerationTask != null) {
            woolGenerationTask.cancel();
        }
    }

    public TeamColor chooseColor() {
        for (TeamColor color : TeamColor.values()) {
            if (!playerToColor.containsValue(color)) {
                return color;
            }
        }
        // otherwise choose a random color
        return TeamColor.values()[(int) (Math.random() * TeamColor.values().length)];
    }

    @EventHandler
    public void onMinecartMount(EntityMountEvent event) {
        // todo make this only work around a start line
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getMount() instanceof RideableMinecart minecart)) return;
        if (playerToMinecart.containsKey(player)) return;

        addPlayer(player);
        playerToMinecart.put(player, new GameMinecart(minecart, MAX_SPEED, SUSTAIN_SPEED));
    }

    @EventHandler
    public void onMinecartDismount(EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        removePlayer(player);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        Block block = event.getHitBlock();
        if (block == null) return;
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;
        if (!playerToMinecart.containsKey(player)) return;

        // Get minecart that player is in
        playerToMinecart.get(player).increaseSpeed(SPEED_INCREMENT);

        block.breakNaturally();
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
        event.getEntity().remove();
    }
}
