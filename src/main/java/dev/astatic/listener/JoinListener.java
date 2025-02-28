package dev.astatic.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.utils.TextFormat;
import dev.astatic.database.DatabaseManager;

import java.util.List;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int playerID = DatabaseManager.getPlayerID(player.getName());

        if (playerID < 1) {
            return;
        }

        boolean hasTwoBags = player.hasPermission("bags.two");
        int bagCount = DatabaseManager.getBagCount(playerID);

        if (hasTwoBags && bagCount < 2) {
            DatabaseManager.addBag(playerID, "bos;");
            player.sendMessage(TextFormat.GREEN + "2." + TextFormat.DARK_GREEN + " Your bag has been added good games.");
        }
    }
}
