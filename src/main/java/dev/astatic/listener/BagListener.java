package dev.astatic.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.utils.TextFormat;
import dev.astatic.database.DatabaseManager;
import dev.astatic.form.AdminBagControlForm;
import dev.astatic.form.BagPurchaseForm;
import dev.astatic.form.gui.AdminBagInterface;
import dev.astatic.manager.BagManager;
import java.util.List;

public class BagListener implements Listener {

    private String selectedPlayer;

    @EventHandler
    public void onFormResponded(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        Object response = event.getResponse();

        if (event.getFormID() == BagPurchaseForm.PURCHASE_ID) {
            if (response instanceof FormResponseModal formResponseModal) {
                int clickedButtonID = formResponseModal.getClickedButtonId();

                switch (clickedButtonID) {
                    case 0 -> {

                    //YOU CAN ADD HERE YOUR ECONOMY SYSTEM

                        DatabaseManager.addPlayer(player.getName());
                        player.sendMessage(TextFormat.DARK_GREEN + "You successfully bought bag, " + TextFormat.GREEN + "/bag <1/2> ");
                    }
                    case 1 -> {
                        return;
                    }
                }
            }
        }


        if (event.getFormID() == AdminBagControlForm.ADMIN_BAG_FORM_ID) {
            if (response instanceof FormResponseSimple formResponseSimple) {
                List<String> players = DatabaseManager.getPlayersWithBags();
                if (players.isEmpty()) {
                    player.sendMessage(TextFormat.RED + "Cannot find.");
                    return;
                }

                selectedPlayer = formResponseSimple.getClickedButton().getText();

                AdminBagControlForm.openSelectBagForm(player, selectedPlayer);
            }
        }

        if (event.getFormID() == AdminBagControlForm.SELECT_BAG_FORM_ID) {
            if (response instanceof FormResponseSimple formResponseSimple) {
                String buttonText = formResponseSimple.getClickedButton().getText();

                int bagNumber;
                try {
                    bagNumber = Integer.parseInt(buttonText.replace("Bag ", ""));
                } catch (NumberFormatException e) {
                    player.sendMessage(TextFormat.RED + "Invalid bag number!");
                    return;
                }
                int selectedPlayerID = DatabaseManager.getPlayerID(selectedPlayer);
                if (selectedPlayerID == -1) {
                    player.sendMessage(TextFormat.RED + "Cannot find player with ID!");
                    return;
                }
                List<Integer> playerBags = DatabaseManager.getBags(selectedPlayerID);
                if (bagNumber < 1 || bagNumber > playerBags.size()) {
                    player.sendMessage(TextFormat.RED + "Invalid bag number!");
                    return;
                }
                int bagID = playerBags.get(bagNumber - 1);
                AdminBagInterface.openGUI(player, selectedPlayer, bagNumber);
            }
        }

    }


    @EventHandler
    public void onBagClose(InventoryCloseEvent event) {
        if (!(event.getInventory() instanceof FakeInventory inventory)) return;
        if (!inventory.getTitle().startsWith("Bag #")) return;

        String title = inventory.getTitle();
        String[] split = title.replace("Bag #", "").split(" - ");
        if (split.length < 2) return;

        try {
            int bagNumber = Integer.parseInt(split[0].trim());
            String playerName = split[1].trim();

            int playerID = DatabaseManager.getPlayerID(playerName);
            if (playerID == -1) return;

            List<Integer> bags = DatabaseManager.getBags(playerID);
            if (bagNumber > bags.size() || bagNumber < 1) return;

            int bagID = bags.get(bagNumber - 1);
            BagManager.saveBagContents(playerID, bagID, inventory);
        } catch (NumberFormatException e) {
            System.err.println("Integer parsing error: " + title);
        }

    }
}
