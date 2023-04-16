package org.tbtc.test.quest;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import org.bukkit.ChatColor;
import java.util.Map;
import java.util.UUID;

public class WorldPlaceholder extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {return "world";}

    @Override
    public String getAuthor() {return "Your Name";}

    @Override
    public String getVersion() {return "1.0";}

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        if (identifier.equals("name")) {
            String worldName = player.getWorld().getName();
            switch (worldName) {
                case "world":
                    return "  ⊙  스폰 월드";
                case "world_nether":
                    return "  ⊙  지옥  ";
                case "world_the_end":
                    return "  ⊙  엔더 월드";
                case "town":
                    return "  ⊙  거주 월드";
                case "wild":
                    return "  ⊙  야생  ";
                case "jump":
                    return "  ⊙  점프맵  ";
                default:
                    return worldName;
            }
        }
        return null;
    }
}
