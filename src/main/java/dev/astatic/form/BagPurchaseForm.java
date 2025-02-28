package dev.astatic.form;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowModal;

public class BagPurchaseForm {
    public static final int PURCHASE_ID = 9998;

    public static void openForm(Player player) {

        FormWindowModal modal = new FormWindowModal("Purchase a bag", "Do you want to buy bags for 50000 money ?", "Yes", "No");

        player.showFormWindow(modal, PURCHASE_ID);

    }

}
