package dev.astatic.form.gui;

import cn.nukkit.Player;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.item.*;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import dev.astatic.database.DatabaseManager;
import dev.astatic.manager.BagManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class PlayerBagInterface {

    public static void openBag(Player player, int bagNumber) {
        int playerID = DatabaseManager.getPlayerID(player.getName());
        if (playerID < 1) {
            return;
        }

        List<Integer> bags = DatabaseManager.getBags(playerID);
        if (bagNumber > bags.size()) {
            player.sendMessage(TextFormat.RED + "You need to have VIP membership to open this bag.");
            return;
        }

        int bagID = bags.get(bagNumber - 1);
        String content = DatabaseManager.getBagContent(playerID, bagID);

        FakeInventory bagInv = new FakeInventory(FakeInventoryType.DOUBLE_CHEST, "Bag #" + bagNumber + " - " + player.getName());
        BagManager.loadBagContents(bagInv, playerID, bagID);
        player.addWindow(bagInv);
    }



}