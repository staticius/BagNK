package dev.astatic;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import dev.astatic.command.AdminCommand;
import dev.astatic.command.BagCommand;
import dev.astatic.database.DatabaseManager;
import dev.astatic.listener.BagListener;
import dev.astatic.listener.JoinListener;

import java.io.File;
import java.io.IOException;

public class Bag extends PluginBase {

    @Override
    public void onEnable() {
        getLogger().info(TextFormat.GREEN + "[+]");
        try {
            File dataFolder = getDataFolder();
            if (!dataFolder.exists() && !dataFolder.mkdir()) {
                getLogger().error(TextFormat.RED + "Plugin Data folder could not be created!");
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }

            File databaseFile = new File(dataFolder, "bags.sqlite");
            if (!databaseFile.exists() && !databaseFile.createNewFile()) {
                getLogger().error(TextFormat.RED + "Database File could not be created!");
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }

            String dbPath = databaseFile.getCanonicalPath();
            getLogger().info("Database Path: " + dbPath);
            DatabaseManager.connect(dbPath);

        } catch (IOException e) {
            getLogger().error("There was a problem with the database connection!", e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerEvents();
        registerCommands();
    }

    @Override
    public void onLoad() {
        getLogger().info(TextFormat.GREEN + "Loading plugin...");
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "[-]");
            DatabaseManager.close();
    }

    private void registerCommands() {
        getServer().getCommandMap().register("bag", new BagCommand());
        getServer().getCommandMap().register("adminbg", new AdminCommand());
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new BagListener(), this);
    }

}