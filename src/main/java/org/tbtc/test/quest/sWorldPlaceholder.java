package org.tbtc.test.quest;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import org.bukkit.ChatColor;
import java.util.Map;
import java.util.UUID;

public class sWorldPlaceholder extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "sworld";
    }

    @Override
    public String getAuthor() {
        return "Glen_Yhy";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        if (identifier.equals("name")) {
            String worldName = player.getWorld().getName();
            switch (worldName) {
                case "world":
                    return "";
                case "world_nether":
                    return "";
                case "world_the_end":
                    return "";
                case "town":
                    return "    ┗ " + PlaceholderAPI.setPlaceholders(player, "%townyadvanced_player_location_town_or_wildname%");
                case "wild":
                    return "";
                case "jump":
                    return ChatColor.YELLOW + "   /spawn 명령어로 스폰으로 이동";
                default:
                    return worldName;
            }
        }
        return null;
    }
}
