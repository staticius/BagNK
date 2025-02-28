package dev.astatic.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;
import dev.astatic.database.DatabaseManager;

import java.util.List;

public class AdminBagControlForm {

    public static final int ADMIN_BAG_FORM_ID = 988887755;
    public static final int SELECT_BAG_FORM_ID = 984555552;

    public static void openBaseForm(Player player) {
        FormWindowSimple form = new FormWindowSimple("Admin Interface", "Please select a player:");

        List<String> players = DatabaseManager.getPlayersWithBags();
        if (players.isEmpty()) {
            player.sendMessage(TextFormat.RED + "The player with a bag was not found.");
            return;
        }

        for (String playerName : players) {
            form.addButton(new ElementButton(playerName, new ElementButtonImageData("path", "textures/blocks/chest_front.png")));
        }

        player.showFormWindow(form, ADMIN_BAG_FORM_ID);
    }

    public static void openSelectBagForm(Player player, String targetPlayer) {
        FormWindowSimple form = new FormWindowSimple(targetPlayer + " Bags", "Select a bag:");

        int playerID = DatabaseManager.getPlayerID(targetPlayer);
        List<Integer> bags = DatabaseManager.getBags(playerID);

        if (bags.isEmpty()) {
            player.sendMessage(TextFormat.RED + "This player has no bag.");
            return;
        }

        for (int i = 0; i < bags.size(); i++) {
            int bagNumber = i + 1;
            form.addButton(new ElementButton(
                    "Bag " + bagNumber,
                    new ElementButtonImageData("path", "textures/blocks/chest_front.png")
            ));
        }

        player.showFormWindow(form, SELECT_BAG_FORM_ID);
    }

}
