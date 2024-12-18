package com.articreep.minecartRacing;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public enum TeamColor {
    RED(ChatColor.RED, Material.RED_WOOL),
    BLUE(ChatColor.BLUE, Material.BLUE_WOOL),
    GREEN(ChatColor.GREEN, Material.GREEN_WOOL),
    YELLOW(ChatColor.YELLOW, Material.YELLOW_WOOL),
    PINK(ChatColor.LIGHT_PURPLE, Material.PINK_WOOL),
    ORANGE(ChatColor.GOLD, Material.ORANGE_WOOL),
    BROWN(ChatColor.GOLD, Material.BROWN_WOOL),
    AQUA(ChatColor.AQUA, Material.CYAN_WOOL);

    final ChatColor chatColor;
    final Material material;
    TeamColor(ChatColor chatColor, Material material) {
        this.chatColor = chatColor;
        this.material = material;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public Material getMaterial() {
        return material;
    }

    public static Set<Material> getMaterials() {
        Set<Material> materials = new HashSet<>();
        for (TeamColor color : TeamColor.values()) {
            materials.add(color.getMaterial());
        }
        return materials;
    }

    public static TeamColor getTeamColor(Material material) {
        for (TeamColor color : TeamColor.values()) {
            if (color.getMaterial().equals(material)) {
                return color;
            }
        }
        return null;
    }
}
