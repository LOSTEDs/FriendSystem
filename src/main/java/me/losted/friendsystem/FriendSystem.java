package me.losted.friendsystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;
import java.util.HashSet;
import java.util.UUID;

public final class FriendSystem extends Plugin {

    public static FriendSystem instance;
    public static HashSet<UUID> existedUUIDs = new HashSet<>();
//    public static String webhookURL = "";

    @Override
    public void onLoad() {
        new Thread(() -> {
            initializeDataFolder();
            if (Utilities.getExistingUUIDs() != null) existedUUIDs.addAll(Utilities.getExistingUUIDs());
        }).start();
    }

    @Override
    public void onEnable() {
        instance = this;

        instance.getProxy().getPluginManager().registerCommand(this, new FriendCommands());
        instance.getProxy().getPluginManager().registerListener(this, new Listeners());
        getLogger().warning("This plugin is made by \"https://github.com/LOSTEDs\"! <3");
//        webhookURL = getWebhook();
//        if (!webhookURL.equals("")) {
//            Gson gson = new Gson();
//            gson.toJson("content");
//        } else {
//            getLogger().warning("No Discord Webhook found! Therefore, this feature has been disabled!");
//        }
    }

    @Override
    public void onDisable() {
        existedUUIDs.forEach(uuid -> {
            if (getProxy().getPlayer(uuid) == null) return;
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try {
                PlayerInfo playerInfo = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, uuid + ".json")), PlayerInfo.class);
                try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, uuid + ".json"))) {
                    playerInfo.setOnline(false);
                    gson.toJson(playerInfo, fileWriter);
                }
            } catch (IOException e) {

            }
        });
    }

    void initializeDataFolder() {
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        File dataFolder = new File(getDataFolder(), "data");
        if (!dataFolder.exists()) dataFolder.mkdir();
//            File webhookConf = new File(getDataFolder(), "webhook.losted");
//            if (!webhookConf.exists()) webhookConf.createNewFile();
    }

//    String getWebhook() {
//        try {
//            return new BufferedReader(new FileReader(new File(getDataFolder(), "webhook.losted"))).readLine();
//        } catch (IOException exception) {
//            return "";
//        }
//    }

}
