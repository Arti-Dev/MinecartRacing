package com.articreep.minecartRacing;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class GameMinecart {
    private RideableMinecart minecart;
    private final boolean doSustainSpeed;
    private double sustainedSpeed;
    private BukkitTask task;

    public GameMinecart(RideableMinecart minecart, double maxSpeed, boolean doSustainSpeed) {
        this.minecart = minecart;
        this.minecart.setMaxSpeed(maxSpeed);
        this.sustainedSpeed = 0.0;
        this.doSustainSpeed = doSustainSpeed;
        if (doSustainSpeed) task = createTask();
    }

    public GameMinecart(RideableMinecart minecart, boolean doSustainSpeed) {
        this(minecart, 0.4, doSustainSpeed);
    }

    private BukkitTask createTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (minecart.isDead()) {
                    cancel();
                    return;
                }
                setMinecartSpeed(sustainedSpeed);
            }
        }.runTaskTimer(MinecartRacing.getInstance(), 0, 1);
    }

    public Minecart getMinecart() {
        return minecart;
    }

    public double getSpeed() {
        if (doSustainSpeed) {
            return sustainedSpeed;
        } else {
            return minecart.getVelocity().length();
        }
    }

    public void setSpeed(double speed) {
        if (doSustainSpeed) {
            this.sustainedSpeed = speed;
        } else {
            setMinecartSpeed(speed);
        }
    }

    public void increaseSpeed(double amount) {
        if (doSustainSpeed) {
            sustainedSpeed += amount;
        } else {
            setMinecartSpeed(minecart.getVelocity().length() + amount);
        }
    }

    private void setMinecartSpeed(double speed) {
        Vector velocity = minecart.getVelocity();

        if (velocity.isZero()) {
            if (!minecart.getPassengers().isEmpty()) {
                Entity entity = minecart.getPassengers().getFirst();
                minecart.setVelocity(entity.getLocation().getDirection().multiply(speed));
            }
        } else {
            minecart.setVelocity(minecart.getVelocity().normalize().multiply(speed));
        }
    }

    public void kill() {
        if (task != null) task.cancel();
        minecart.remove();
    }
}
