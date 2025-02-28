package dev.astatic.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.astatic.form.AdminBagControlForm;

public class AdminCommand extends Command {

    public AdminCommand() {
        super("adminbg");
        this.setPermission("admin");
        this.setDescription("Edit the players' bags.");
        this.setUsage("/adminbg");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        if (!sender.isPlayer()){
            sender.sendMessage(TextFormat.RED + "This command can only be executed by a player!");
            return false;
        }

        if (!sender.hasPermission("admin")) return false;

        Player player = (Player) sender;

        AdminBagControlForm.openBaseForm(player);

        return true;
    }

}
