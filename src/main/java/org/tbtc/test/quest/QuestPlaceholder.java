package org.tbtc.test.quest;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import org.bukkit.ChatColor;
import java.util.Map;
import java.util.UUID;

public class QuestPlaceholder extends PlaceholderExpansion {
    private Map<UUID, Integer> questStatus;
    private Quest quest;

    public QuestPlaceholder(Quest quest, Map<UUID, Integer> questStatus) {
        this.quest = quest;
        this.questStatus = questStatus;
    }

    @Override
    public String getIdentifier() {
        return "quest";
    }

    @Override
    public String getAuthor() {
        return "Glen_Yhy";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equals("text")) {
            int currentQuest = 0;
            UUID uuid = player.getUniqueId();
            int numLogs = quest.getNumBlocksOfType(player, Material.OAK_LOG);
            int numCoalOre = quest.getNumBlocksOfType(player, Material.COAL_ORE) + quest.getNumBlocksOfType(player, Material.DEEPSLATE_COAL_ORE);
            int numIronOre = quest.getNumBlocksOfType(player, Material.IRON_ORE) + quest.getNumBlocksOfType(player, Material.DEEPSLATE_IRON_ORE);
            int numGoldOre = quest.getNumBlocksOfType(player, Material.GOLD_ORE) + quest.getNumBlocksOfType(player, Material.DEEPSLATE_GOLD_ORE);
            int numDiamondOre = quest.getNumBlocksOfType(player, Material.DIAMOND_ORE) + quest.getNumBlocksOfType(player, Material.DEEPSLATE_DIAMOND_ORE);
            if (questStatus.containsKey(uuid)) {
                currentQuest = questStatus.get(uuid);
            }
            String questText;
            switch (currentQuest) {
                case 0:
                    questText = "퀘스트:  나무 " + ChatColor.GREEN + numLogs + "/5 " + ChatColor.WHITE + "개 캐기";
                    break;
                case 1:
                    questText = "퀘스트:  석탄 " + ChatColor.GREEN + numCoalOre + "/10 " + ChatColor.WHITE + "개 캐기";
                    break;
                case 2:
                    questText = "퀘스트:  다이아몬드 " + ChatColor.GREEN + numDiamondOre + "/3 " + ChatColor.WHITE + "개 찾기";
                    break;
                case 3:
                    questText = "퀘스트:  마을 가입/설립 하기";
                    break;
                default:
                    questText = "";
                    break;
            }
            return questText;
        }
        // Placeholder not found
        return null;
    }
}
