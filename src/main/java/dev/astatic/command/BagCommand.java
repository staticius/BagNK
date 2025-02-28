package dev.astatic.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.astatic.database.DatabaseManager;
import dev.astatic.form.BagPurchaseForm;
import dev.astatic.form.gui.PlayerBagInterface;

import java.util.List;

public class BagCommand extends Command {

    String alias[] = {"bg"};

    public BagCommand() {
        super("bag");
        this.setDescription("Can't you find a place to put your items? A bag is a bag immediately!");
        this.setUsage("/bag <1|2>");
        this.setAliases(alias);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextFormat.RED + "This command can only be executed by a player!");
            return false;
        }

        if (args.length != 1) {
            player.sendMessage("§eUsage: §6/bag <1|2>");
            return false;
        }

        try {
            int bagNumber = Integer.parseInt(args[0]);

            if (bagNumber < 1 || bagNumber > 2) {
                player.sendMessage("§cInvalid bag number! You can only enter 1 or 2.");
                return false;
            }

            int playerID = DatabaseManager.getPlayerID(player.getName());
            if (playerID < 1) {
                BagPurchaseForm.openForm(player);
                return true;
            }

            if (bagNumber == 2 && !player.hasPermission("bags.two")) {
                player.sendMessage("§cYou must have VIP membership to open this bag!");
                return false;
            }

            PlayerBagInterface.openBag(player, bagNumber);
        } catch (NumberFormatException e) {
            player.sendMessage("§cPlease enter a valid bag number!");
        }

        return true;
    }
}