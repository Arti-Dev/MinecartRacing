package com.articreep.minecartRacing;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

public class MinecartRacingCommand implements CommandExecutor {
    protected static RaceGame raceGame = null;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (raceGame != null && !raceGame.hasEnded()) {
            raceGame.cancelCountdown();
            raceGame.stopGame();
            raceGame = null;
            sender.sendMessage("Ended game");
        } else {
            FileConfiguration config = MinecartRacing.getInstance().getConfig();
            Vector incomingDirection = BlockFace.valueOf(config.getString("end-line-direction")).getDirection();
            int lineLength = config.getInt("end-line-length");
            Location startLineLocation = config.getLocation("start-line-location");
            Location endLineLocation = config.getLocation("end-line-location");

            raceGame = new RaceGame(startLineLocation, endLineLocation, incomingDirection, lineLength);
            raceGame.addNearbyPlayers();
            raceGame.startGame();
            sender.sendMessage("Created and started game");
        }
        return true;
    }
}
