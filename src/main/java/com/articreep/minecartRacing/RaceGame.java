package com.articreep.minecartRacing;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class RaceGame extends Game {
    private Location startLocation;
    private Location endLocation;
    private Vector lineDirection;
    private int lineLength;
    private BukkitTask finishLineTask = null;
    private final int gameHeight = 10;

    public RaceGame(Location startLocation, Location endLocation, Vector lineDirection, int lineLength) {
        super(2.0, true, 0.1, 20);

        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.lineDirection = lineDirection;
        this.lineLength = lineLength;
    }

    @Override
    public void startGame() {
        super.startGame();
        Location otherCorner = endLocation.clone().add(lineDirection.clone().multiply(lineLength));
        otherCorner.add(0, gameHeight, 0);
        BoundingBox box = new BoundingBox(startLocation.getX(), startLocation.getY(), startLocation.getZ(),
                otherCorner.getX(), otherCorner.getY(), otherCorner.getZ());
        woolGenerator.generateWoolBetweenLocations(playerToColor, box, startLocation.getWorld());
        finishLineTask = finishLineLoop();
    }

    @Override
    public void stopGame() {
        super.stopGame();
        if (finishLineTask != null) finishLineTask.cancel();
    }

    private BukkitTask finishLineLoop() {
        return new BukkitRunnable() {

            @Override
            public void run() {
                // todo this is kinda dumb. probably want to just test individual coordinates instead of using an angle
                Vector vector = lineDirection.clone();
                for (Player player : playerToMinecart.keySet()) {
                    Minecart minecart = playerToMinecart.get(player).getMinecart();
                    if (endLocation.distanceSquared(minecart.getLocation()) > lineLength * lineLength) continue;
                    Vector endToMinecart = minecart.getLocation().toVector().subtract(endLocation.toVector());
                    endToMinecart.setY(0);
                    if (endToMinecart.angle(vector) < 0.1) {
                        player.sendMessage("You crossed the finish line!");
                    }
                }
            }
        }.runTaskTimer(MinecartRacing.getInstance(), 0, 1);
    }

    public void addNearbyPlayers() {
        World world = startLocation.getWorld();
        if (world == null) return;

        for (Entity entity : world.getNearbyEntities(startLocation, lineLength, lineLength, lineLength)) {
            if (entity instanceof Player player && player.getVehicle() instanceof RideableMinecart) {
                addPlayer(player);
            }
        }
    }

    @Override
    @EventHandler
    public void onMinecartMount(EntityMountEvent event) {
        // do nothing
    }
}
