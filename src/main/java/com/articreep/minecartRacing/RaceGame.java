package com.articreep.minecartRacing;

import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.entity.EntityMountEvent;

public class RaceGame extends Game {

    public RaceGame() {
        super(2.0, true, 0.1, -1);
    }

    @Override
    public void onMinecartMount(EntityMountEvent event) {
        // todo make this only work around a start line
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getMount() instanceof RideableMinecart minecart)) return;
        if (playerToMinecart.containsKey(player)) return;

        addPlayer(player);
        playerToMinecart.put(player, new GameMinecart(minecart, MAX_SPEED, SUSTAIN_SPEED));
    }
}
