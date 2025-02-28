package dev.astatic.form.gui;

import cn.nukkit.Player;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.utils.TextFormat;
import dev.astatic.database.DatabaseManager;
import dev.astatic.manager.BagManager;

import java.util.List;

public class AdminBagInterface {

    public static void openGUI(Player admin, String targetPlayer, int bagNumber) {
        int playerID = DatabaseManager.getPlayerID(targetPlayer);
        if (playerID == -1) {
            admin.sendMessage(TextFormat.RED + "Error: Cannot find player with name " + targetPlayer);
            return;
        }

        List<Integer> bags = DatabaseManager.getBags(playerID);
        if (bagNumber < 1 || bagNumber > bags.size()) {
            admin.sendMessage(TextFormat.RED + "Error: Invalid bag number");
            return;
        }

        int bagID = bags.get(bagNumber - 1);
        FakeInventory adminBagInv = new FakeInventory(
                FakeInventoryType.DOUBLE_CHEST,
                "Bag #" + bagNumber + " - " + targetPlayer
        );

        BagManager.loadBagContents(adminBagInv, playerID, bagID);
        admin.addWindow(adminBagInv);
    }
}