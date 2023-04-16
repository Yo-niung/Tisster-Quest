package org.tbtc.test.quest;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

public class command implements CommandExecutor {
    private final Quest quest;

    public command(Quest quest) {
        this.quest = quest;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (label.equalsIgnoreCase("quest")) {
                if (!player.isOp()) {
                    player.sendMessage(ChatColor.RED + "이 명령어를 사용할 권한이 없습니다.");
                    return true;
                }
                if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                    File questDataFile = new File(quest.getDataFolder(), "questData.csv");
                    if (questDataFile.exists()) {
                        quest.questStatus.clear();
                        try (CSVReader reader = new CSVReader(new FileReader(questDataFile))) {
                            String[] nextLine;
                            while ((nextLine = reader.readNext()) != null) {
                                UUID uuid = UUID.fromString(nextLine[0]);
                                int value = Integer.parseInt(nextLine[1]);
                                quest.questStatus.put(uuid, value);
                            }
                            player.sendMessage(ChatColor.GREEN + "questData.csv 파일을 리로드했습니다.");
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (IOException | CsvValidationException e) {
                            e.printStackTrace();
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "questData.csv 파일이 존재하지 않습니다.");
                    }
                    return true;
                } else {
                    int numLogs = quest.getNumBlocksOfType(player, Material.OAK_LOG);
                    int numCoalOre = quest.getNumBlocksOfType(player, Material.COAL_ORE) + quest.getNumBlocksOfType(player, Material.DEEPSLATE_COAL_ORE);
                    int numIronOre = quest.getNumBlocksOfType(player, Material.IRON_ORE) + quest.getNumBlocksOfType(player, Material.DEEPSLATE_IRON_ORE);
                    int numGoldOre = quest.getNumBlocksOfType(player, Material.GOLD_ORE) + quest.getNumBlocksOfType(player, Material.DEEPSLATE_GOLD_ORE);
                    int numDiamondOre = quest.getNumBlocksOfType(player, Material.DIAMOND_ORE) + quest.getNumBlocksOfType(player, Material.DEEPSLATE_DIAMOND_ORE);
                    Integer currentQuest = quest.questStatus.get(player.getUniqueId());

                    player.sendMessage("현재 " + ChatColor.GREEN + player.getName() + ChatColor.RESET + "님의 통계:");
                    player.sendMessage("나무 원목 수: " + numLogs);
                    player.sendMessage("석탄 원석 수: " + numCoalOre);
                    player.sendMessage("철 원석 수: " + numIronOre);
                    player.sendMessage("금 원석 수: " + numGoldOre);
                    player.sendMessage("다이아몬드 원석 수: " + numDiamondOre);
                    player.sendMessage("현재 퀘스트 진행 상태: " + (currentQuest == null ? "없음" : currentQuest));

                    if (quest.isPlayerInTown(player)) {
                        player.sendMessage("마을에 속해 있습니다.");
                    } else {
                        player.sendMessage("마을에 속해 있지 않습니다.");
                    }

                    return true;
                }

            }
        }
        return false;
    }
}