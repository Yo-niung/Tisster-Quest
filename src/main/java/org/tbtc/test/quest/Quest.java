package org.tbtc.test.quest;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.object.Town;
import com.opencsv.CSVWriter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Quest extends JavaPlugin implements Listener {
    private Economy econ;
    public Map<UUID, Integer> questStatus = new HashMap<>();
    private File questDataFile;
    private command cmdExecutor;
    private QuestPlaceholder questPlaceholder;

    @EventHandler
    public void onNewTown(NewTownEvent event) {
        // 마을이 생성되었을 때 실행되는 코드
        Town town = event.getTown();
        Resident mayor = town.getMayor();
        Player player = mayor.getPlayer();
        UUID playerId = player.getUniqueId();
        Integer currentQuest = questStatus.get(playerId);

        if (player != null && currentQuest != null && currentQuest == 3) {
            givePlayerMoney(player, 10000);
            sendTitle(player, ChatColor.WHITE + "퀘스트 완료!", ChatColor.GREEN + "정착 지원금 10000₮ 지급!", 20, 40, 20);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            questStatus.put(playerId, 4);
        }
    }

    @EventHandler
    public void onTownAddResident(TownAddResidentEvent event) {
        // 마을에 플레이어가 가입하였을 때 실행되는 코드
        Town town = event.getTown();
        Resident resident = event.getResident();
        Player player = resident.getPlayer();
        UUID playerId = player.getUniqueId();
        Integer currentQuest = questStatus.get(playerId);

        if (player != null && currentQuest != null && currentQuest == 3) {
            givePlayerMoney(player, 10000);
            sendTitle(player, ChatColor.WHITE + "퀘스트 완료!", ChatColor.GREEN + "정착 지원금 10000₮ 지급!", 20, 40, 20);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            questStatus.put(player.getUniqueId(), 4);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        Integer currentQuest = questStatus.get(playerId);

        // 플레이어 아이디가 맵에 없는 경우 초기값으로 0을 설정하고 파일에 저장합니다.
        if (!questStatus.containsKey(playerId)) {
            questStatus.put(playerId, 0);
            saveQuestStatusToFile(playerId, 0); // 파일에 저장
        }
        // 플레이어 아이디가 맵에 있는 경우, 현재 퀘스트가 null 인 경우 초기값으로 0을 설정하고 파일에 저장합니다.
        else if (currentQuest == null) {
            questStatus.put(playerId, 0);
            saveQuestStatusToFile(playerId, 0); // 파일에 저장
        }
    }

    public void saveQuestStatusToFile(UUID playerId, int status) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(questDataFile))) {
            String[] record = {playerId.toString(), String.valueOf(status)};
            writer.writeNext(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onEnable() {
        new WorldPlaceholder().register();
        new sWorldPlaceholder().register();
        // QuestPlaceholder 인스턴스 생성
        questStatus = new HashMap<>();
        questPlaceholder = new QuestPlaceholder(this, questStatus);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            questPlaceholder.register();
        }

        // 플러그인 활성화 시 실행되는 코드
        if (!setupEconomy()) {
            getLogger().severe("Vault 플러그인이 없습니다! 플러그인을 비활성화합니다.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        cmdExecutor = new command(this);
        if (getCommand("quest") != null) {
            getCommand("quest").setExecutor(cmdExecutor);
        } else {
            getLogger().warning("명령어 'quest'를 등록하지 못했습니다.");
        }

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("플러그인 활성화!");

        // 데이터 파일 생성 또는 로드
        questDataFile = new File(getDataFolder(), "questData.csv");
        if (!questDataFile.exists()) {
            saveResource("questData.csv", false);
        }
        try (CSVReader reader = new CSVReader(new FileReader(questDataFile))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(nextLine[0]);
                } catch (IllegalArgumentException e) {
                    getLogger().warning("잘못된 UUID 문자열입니다: " + nextLine[0]);
                    continue;
                }
                int value = Integer.parseInt(nextLine[1]);
                questStatus.put(uuid, value);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDisable() {
        // 플러그인 비활성화 시 실행되는 코드
        if (this.questPlaceholder != null) {
            this.questPlaceholder.unregister();
        }

        // 퀘스트 데이터를 파일에 저장
        try (CSVWriter writer = new CSVWriter(new FileWriter(questDataFile))) {
            for (Map.Entry<UUID, Integer> entry : questStatus.entrySet()) {
                String[] record = {entry.getKey().toString(), entry.getValue().toString()};
                writer.writeNext(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        getLogger().info("플러그인 비활성화!");
    }



    public void reloadQuestStatus() {
        try (CSVReader reader = new CSVReader(new FileReader(questDataFile))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                UUID uuid = UUID.fromString(nextLine[0]);
                int value = Integer.parseInt(nextLine[1]);
                questStatus.put(uuid, value);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayerInTown(Player player) {
        Resident resident = null;
        try {
            resident = TownyAPI.getInstance().getResident(player.getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resident != null && resident.hasTown();
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        Material blockType = event.getBlock().getType();
        Integer currentQuest = questStatus.get(playerId);
        if (currentQuest == null) {
            return;
        }
        int numLogs = getNumBlocksOfType(player, Material.OAK_LOG);
        int numCoalOre = getNumBlocksOfType(player, Material.COAL_ORE) + getNumBlocksOfType(player, Material.DEEPSLATE_COAL_ORE);
        int numIronOre = getNumBlocksOfType(player, Material.IRON_ORE) + getNumBlocksOfType(player, Material.DEEPSLATE_IRON_ORE);
        int numGoldOre = getNumBlocksOfType(player, Material.GOLD_ORE) + getNumBlocksOfType(player, Material.DEEPSLATE_GOLD_ORE);
        int numDiamondOre = getNumBlocksOfType(player, Material.DIAMOND_ORE) + getNumBlocksOfType(player, Material.DEEPSLATE_DIAMOND_ORE);

        if (currentQuest == 0 && blockType.name().endsWith("_LOG") && numLogs >= 5) {
            givePlayerMoney(player, 1000);
            sendTitle(player, ChatColor.WHITE + "퀘스트 완료!", ChatColor.GREEN + "1000₮ 지급!", 20, 40, 20);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            questStatus.put(playerId, 1);
        } else if (currentQuest == 1 && (blockType == Material.COAL_ORE || blockType == Material.DEEPSLATE_COAL_ORE) && numCoalOre >= 9) {
            givePlayerMoney(player, 1000);
            sendTitle(player, ChatColor.WHITE + "퀘스트 완료!", ChatColor.GREEN + "1000₮ 지급!", 20, 40, 20);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            questStatus.put(playerId, 2);
        } else if (currentQuest == 2 && (blockType == Material.DIAMOND_ORE || blockType == Material.DEEPSLATE_DIAMOND_ORE) && numDiamondOre >= 2) {
            givePlayerMoney(player, 5000);
            sendTitle(player, ChatColor.WHITE + "퀘스트 완료!", ChatColor.GREEN + "5000₮ 지급!", 20, 40, 20);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            questStatus.put(playerId, 3);
            if (isPlayerInTown(player)) {
                givePlayerMoney(player, 10000);
                sendTitle(player, ChatColor.WHITE + "퀘스트 완료!", ChatColor.GREEN + "정착 지원금 10000₮ 지급!", 20, 40, 20);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                questStatus.put(playerId, 4);
            }
        }
    }

    public int getNumBlocksOfType(Player player, Material blockType) {
        int numBlocks = 0;
        if (blockType.name().endsWith("_LOG")) {
            for (Material material : Material.values()) {
                if (material.isBlock() && material.name().endsWith("_LOG")) {
                    numBlocks += player.getStatistic(Statistic.MINE_BLOCK, material);
                }
            }
        } else {
            numBlocks = player.getStatistic(Statistic.MINE_BLOCK, blockType);
        }
        return numBlocks;
    }


    // 플레이어에게 돈을 지급하는 메소드
    private void givePlayerMoney(Player player, double amount) {
        if (econ != null) {
            econ.depositPlayer(player, amount);
        } else {
            getLogger().warning("Vault 플러그인이 없습니다. 플레이어에게 돈을 지급할 수 없습니다.");
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        econ = rsp.getProvider(); // econ 변수에 직접 할당합니다.
        return econ != null;
    }

    private void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        // 플레이어에게 타이틀과 서브타이틀을 보여주는 함수
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}
