package com.articreep.minecartRacing;

import org.bukkit.entity.Player;

public interface Game {

    void addPlayer(Player player);

    void removePlayer(Player player);

    /**
     * Activates listeners and starts the game.
     */
    void startGame();

    void stopGame();
}
