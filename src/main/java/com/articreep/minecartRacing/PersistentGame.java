package com.articreep.minecartRacing;

import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityMountEvent;

public class PersistentGame extends Game {

    public PersistentGame() {
        super(2.0, false, 0.1, 20);
    }

    @Override
    @EventHandler
    public void onMinecartMount(EntityMountEvent event) {
        // todo make this only work around a start line
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getMount() instanceof RideableMinecart minecart)) return;
        if (playerToMinecart.containsKey(player)) return;

        addPlayer(player);
    }

    @Override
    public void startGame() {
        super.startGame();
        woolGenerationTask = woolGenerationLoop();
    }
}
