package dev.astatic.manager;

import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.request.NetworkMapping;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.process.processor.ItemStackRequestPacketProcessor;
import dev.astatic.database.DatabaseManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class BagManager {

    public static void loadBagContents(FakeInventory inventory, int playerID, int bagID) {

        String content = DatabaseManager.getBagContent(playerID, bagID);
        if (content == null || content.isEmpty()) return;

        String[] encodedItems = content.split(";");
        for (int i = 0; i < encodedItems.length && i < inventory.getSize(); i++) {
            try {
                if (encodedItems[i].equals("empty")) {
                    continue;
                }

                byte[] byteData = Base64.getDecoder().decode(encodedItems[i]);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(byteData);
                CompoundTag tag = NBTIO.read(inputStream);
                Item item = NBTIO.getItemHelper(tag);

                if (item != null && !item.isNull()) {
                    inventory.setItem(i, item);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveBagContents(int playerID, int bagID, FakeInventory inventory) {
        StringBuilder nbtData = new StringBuilder();
        for (int i = 0; i < inventory.getSize(); i++) {
            Item item = inventory.getItem(i);
            if (!item.isNull()) {
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    CompoundTag tag = NBTIO.putItemHelper(item);
                    NBTIO.write(tag, outputStream);
                    String encodedNBT = Base64.getEncoder().encodeToString(outputStream.toByteArray());
                    nbtData.append(encodedNBT).append(";");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                nbtData.append("empty;");
            }
        }
        DatabaseManager.saveBagContent(playerID, bagID, nbtData.toString());
    }
}
