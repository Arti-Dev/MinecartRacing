package com.articreep.minecartRacing;

import org.bukkit.plugin.java.JavaPlugin;

public final class MinecartRacing extends JavaPlugin {

    private static MinecartRacing instance;
    private static Game persistentGame;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        persistentGame = new PersistentGame();
        persistentGame.startGame();
    }

    @Override
    public void onDisable() {
        if (persistentGame != null) {
            persistentGame.stopGame();
        }
    }

    public static MinecartRacing getInstance() {
        return instance;
    }
}
