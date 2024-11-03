package com.articreep.minecartRacing;

import org.bukkit.plugin.java.JavaPlugin;

public final class MinecartRacing extends JavaPlugin {

    private static MinecartRacing instance;
    private static PersistentGame persistentGame;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        persistentGame = new PersistentGame();
        persistentGame.startGame();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MinecartRacing getInstance() {
        return instance;
    }
}
