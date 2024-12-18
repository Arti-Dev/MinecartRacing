package com.articreep.minecartRacing;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class GameMinecart {
    private final RideableMinecart minecart;
    private final boolean doSustainSpeed;
    private double sustainedSpeed;
    private final BukkitTask task;

    public GameMinecart(RideableMinecart minecart, double maxSpeed, boolean doSustainSpeed) {
        this.minecart = minecart;
        this.minecart.setMaxSpeed(maxSpeed);
        this.minecart.setInvulnerable(true);
        this.sustainedSpeed = 0.0;
        this.doSustainSpeed = doSustainSpeed;
        task = createTask();
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
                if (doSustainSpeed) setMinecartSpeed(sustainedSpeed);
                sendActionBarMessage();

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

    public void sendActionBarMessage() {
        if (minecart.getPassengers().isEmpty()) return;
        if (minecart.getPassengers().getFirst() instanceof Player player) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(ChatColor.GREEN + "Speed: " + String.format("%.2f", getSpeed())));
        }
    }
}
