/*
Copyright (c) 2022 LOSTED

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package me.losted.friendsystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;
import java.util.HashSet;
import java.util.UUID;

public final class FriendSystem extends Plugin {

    public static FriendSystem instance;
    public static HashSet<UUID> existedUUIDs = new HashSet<>();

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
    }
}
