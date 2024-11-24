package com.articreep.minecartRacing;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Minecart;
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
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Game implements Listener {
    public final double MAX_SPEED;
    public final boolean SUSTAIN_SPEED;
    public final double SPEED_INCREMENT;
    public final int WOOL_RESET_INTERVAL;
    protected final Map<Player, TeamColor> playerToColor = new HashMap<>();
    protected final Map<Player, GameMinecart> playerToMinecart = new HashMap<>();
    protected final WoolGenerator woolGenerator = new WoolGenerator();
    protected BukkitTask woolGenerationTask;
    protected boolean hasStarted = false;

    public Game(double maxSpeed, boolean sustainSpeed, double speedIncrement, int woolResetInterval) {
        MAX_SPEED = maxSpeed;
        SUSTAIN_SPEED = sustainSpeed;
        SPEED_INCREMENT = speedIncrement;
        WOOL_RESET_INTERVAL = woolResetInterval;
    }

    public void addPlayer(Player player) {
        TeamColor color = chooseColor();
        playerToColor.put(player, color);
        assignMinecart(player);
        player.sendMessage("You are " + color.chatColor + color);
    }

    public void assignMinecart(Player player) {
        if (!playerToColor.containsKey(player)) return;
        if (playerToMinecart.containsKey(player)) return;
        if (!(player.getVehicle() instanceof RideableMinecart minecart)) return;
        playerToMinecart.put(player, new GameMinecart(minecart, MAX_SPEED, SUSTAIN_SPEED));
    }

    public void removePlayer(Player player) {
        if (playerToMinecart.containsKey(player)) {
            GameMinecart toRemove = playerToMinecart.get(player);
            // Kill next tick in the case that the player dismounted from the minecart voluntarily
            Bukkit.getScheduler().runTask(MinecartRacing.getInstance(), toRemove::kill);
        }
        playerToMinecart.remove(player);
        playerToColor.remove(player);
    }

    public void removePlayerLaunch(Player player) {
        Vector vector = player.getLocation().getDirection().multiply(2).setY(1);
        if (playerToMinecart.containsKey(player)) {
            Minecart minecart = playerToMinecart.get(player).getMinecart();
            vector = minecart.getVelocity().setY(1);
        }
        removePlayer(player);
        Vector finalVector = vector;
        Bukkit.getScheduler().runTaskLater(MinecartRacing.getInstance(), () -> {
            player.setVelocity(finalVector);
            player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
        }, 2);
    }

    public void startGame() {
        // todo sometimes these events don't get registered. potentially because of lingering listeners from previous games
        Bukkit.getPluginManager().registerEvents(this, MinecartRacing.getInstance());
        hasStarted = true;
    }

    BukkitTask woolGenerationLoop() {
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

    public void stopGame() {
        HandlerList.unregisterAll(this);
        if (woolGenerationTask != null) {
            woolGenerationTask.cancel();
        }
        woolGenerator.resetAllBlocks();
        hasStarted = false;
    }

    private TeamColor chooseColor() {
        for (TeamColor color : TeamColor.values()) {
            if (!playerToColor.containsValue(color)) {
                return color;
            }
        }
        // otherwise choose a random color
        return TeamColor.values()[(int) (Math.random() * TeamColor.values().length)];
    }

    @EventHandler
    public abstract void onMinecartMount(EntityMountEvent event);

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

        TeamColor color = TeamColor.getTeamColor(block.getType());
        if (color == null) return;

        if (playerToColor.containsValue(color)) {
            Set<Player> affectedPlayers = new HashSet<>();
            for (Map.Entry<Player, TeamColor> entry : playerToColor.entrySet()) {
                if (entry.getValue().equals(color)) {
                    affectedPlayers.add(entry.getKey());
                }
            }

            for (Player affectedPlayer : affectedPlayers) {
                if (playerToMinecart.containsKey(affectedPlayer)) {
                    World world = affectedPlayer.getWorld();
                    playerToMinecart.get(affectedPlayer).increaseSpeed(SPEED_INCREMENT);
                    affectedPlayer.playSound(affectedPlayer, Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
                    world.spawnParticle(Particle.SMALL_GUST, affectedPlayer.getLocation().add(0, 1, 0), 30, 0.6, 0.6, 0.6, 0.05);
                    // todo play a separate sound if you were boosted by someone else
                }
            }
        }

        block.getWorld().spawnParticle(Particle.BLOCK, block.getLocation().add(0.5, 0.5, 0.5),
                50, 0.3, 0.3, 0.3, block.getBlockData());
        block.setType(Material.AIR);
        event.getEntity().remove();
    }

    public boolean hasStarted() {
        return hasStarted;
    }
}
