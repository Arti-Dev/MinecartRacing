package com.articreep.minecartRacing;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class WoolGenerator {
    Set<Block> blocksAffected = new HashSet<>();
    public static final int DETECTION_RADIUS = 30;
    public static final Material DEFAULT_MATERIAL = Material.WHITE_WOOL;
    public static final int MINIMUM_COLORS = 8;

    /**
     * Generates wool colors for blocks around players.
     * @param playerMap A map with all players and their corresponding team colors
     */
    public void generateWool(Map<Player, TeamColor> playerMap) {
        for (Player player : playerMap.keySet()) {
            Set<Block> defaultMaterialBlocks = getDefaultMaterialBlocksAroundPlayer(player);
            for (Block block : defaultMaterialBlocks) {
                chooseColor(new HashSet<>(playerMap.values()));
                block.setType(chooseColor(new HashSet<>(playerMap.values())));
                blocksAffected.add(block);
            }
        }
    }

    public void generateWoolBetweenLocations(Map<Player, TeamColor> playerMap, BoundingBox box, World world) {
        for (double x = box.getMinX(); x < box.getMaxX(); x++) {
            for (double y = box.getMinY(); y < box.getMaxY(); y++) {
                for (double z = box.getMinZ(); z < box.getMaxZ(); z++) {
                    Block block = new Location(world, x, y, z).getBlock();
                    if (block.getType() == DEFAULT_MATERIAL) {
                        block.setType(chooseColor(new HashSet<>(playerMap.values())));
                        blocksAffected.add(block);
                    }
                }
            }
        }
    }

    /**
     * Resets blocks that aren't in range of any players.
     * @param players The players to check for blocks around.
     */
    public void resetBlocksOutOfRange(Set<Player> players) {
        Set<Block> blocksToReset = new HashSet<>(blocksAffected);
        for (Player player : players) {
            blocksToReset.removeAll(getActiveBlocksAroundPlayer(player));
        }
        blocksToReset.forEach(block -> block.setType(DEFAULT_MATERIAL));
        blocksAffected.removeAll(blocksToReset);
    }

    public void resetAllBlocks() {
        blocksAffected.forEach(block -> block.setType(DEFAULT_MATERIAL));
        blocksAffected.clear();
    }

    private Set<Block> getDefaultMaterialBlocksAroundPlayer(Player player) {
        Set<Block> defaultMaterialBlocks = new HashSet<>();
        for (int x = -DETECTION_RADIUS; x <= DETECTION_RADIUS; x++) {
            for (int y = -DETECTION_RADIUS; y <= DETECTION_RADIUS; y++) {
                for (int z = -DETECTION_RADIUS; z <= DETECTION_RADIUS; z++) {
                    Block block = player.getLocation().add(x, y, z).getBlock();
                    if (block.getType() == DEFAULT_MATERIAL) {
                        defaultMaterialBlocks.add(block);
                    }
                }
            }
        }
        return defaultMaterialBlocks;
    }

    private Set<Block> getActiveBlocksAroundPlayer(Player player) {
        Set<Block> blocks = new HashSet<>();
        for (int x = -DETECTION_RADIUS; x <= DETECTION_RADIUS; x++) {
            for (int y = -DETECTION_RADIUS; y <= DETECTION_RADIUS; y++) {
                for (int z = -DETECTION_RADIUS; z <= DETECTION_RADIUS; z++) {
                    Block block = player.getLocation().add(x, y, z).getBlock();
                    if (blocksAffected.contains(block)) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    /**
     * Chooses a random color out of the possible colors provided.
     * @param possibleColors The colors to choose from.
     * @return The chosen color.
     */
    private Material chooseColor(Set<TeamColor> possibleColors) {
        if (possibleColors.size() < MINIMUM_COLORS) {
            addRandomColors(possibleColors);
        }

        Optional<TeamColor> color = chooseRandomElement(possibleColors);
        if (color.isEmpty()) {
            throw new IllegalStateException("WoolGenerator: No colors to choose from");
        } else {
            return color.get().getMaterial();
        }
    }

    private void addRandomColors(Set<TeamColor> teamColors) {
        Set<TeamColor> unselectedColors = new HashSet<>(List.of(TeamColor.values()));
        unselectedColors.removeAll(teamColors);

        while (teamColors.size() < WoolGenerator.MINIMUM_COLORS) {
            Optional<TeamColor> randomColor = chooseRandomElement(unselectedColors);
            if (randomColor.isEmpty()) {
                Bukkit.getLogger().warning("WoolGenerator: Not enough colors to choose from");
                break;
            } else {
                teamColors.add(randomColor.get());
                unselectedColors.remove(randomColor.get());
            }
        }
    }

    private <T> Optional<T> chooseRandomElement(Set<T> set) {
        if (set.isEmpty()) return Optional.empty();
        int index = (int) (Math.random() * set.size());
        return Optional.of(new ArrayList<>(set).get(index));
    }

}
