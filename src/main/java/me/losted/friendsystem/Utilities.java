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
import com.google.gson.annotations.SerializedName;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Utilities {
    // TODO: Add a method to report the error

    public static final String dataFolderPath = new File(new File(ProxyServer.getInstance().getPluginsFolder(), "FriendSystem").getAbsolutePath(), "data").getAbsolutePath();

    public static Collection<UUID> getExistingUUIDs() {
        Set<UUID> allFiles = new HashSet<>();

        File dataFolder = new File(Utilities.dataFolderPath);
        File[] uuids = dataFolder.listFiles();

        try {
            Arrays.stream(uuids).forEach(uuid -> { // It might cause NPE...
                if (uuid.getName().endsWith(".json")) allFiles.add(UUID.fromString(uuid.getName().replace(".json", "")));
            });
        } catch (NullPointerException exception) {
            return null;
        }
        return allFiles;
    }

    public static void createData(ProxiedPlayer player) {
        File data = new File(dataFolderPath, player.getUniqueId() + ".json");
        try {
            PlayerInfo p = new PlayerInfo(player.getUniqueId(), true, null, null, null);
            data.createNewFile();
            try (FileWriter fileWriter = new FileWriter(data.getAbsolutePath())) {
                new GsonBuilder().setPrettyPrinting().create().toJson(p, fileWriter);
            }
        } catch (IOException exception) {
            player.disconnect(new ComponentBuilder("An error has occurred!\n").color(ChatColor.RED)
                    .append(new ComponentBuilder("Please rejoin the server!").color(ChatColor.RED).create())
                    .create());
        }
    }
    public static UUID getOfflinePlayerUUID(String playerName) {
        Gson gson = new Gson();
        try {
            String result = getRequest(new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName));
            if (result.isEmpty()) return null;
            return UUID.fromString(gson.fromJson(result, MojangPlayerProfileInfo.class).getUUID().length() == 32 ? gson.fromJson(result, MojangPlayerProfileInfo.class).getUUID().substring(0, 8) + '-' + gson.fromJson(result, MojangPlayerProfileInfo.class).getUUID().substring(8, 12) + '-' + gson.fromJson(result, MojangPlayerProfileInfo.class).getUUID().substring(12, 16) + '-' + gson.fromJson(result, MojangPlayerProfileInfo.class).getUUID().substring(16, 20) + '-' + gson.fromJson(result, MojangPlayerProfileInfo.class).getUUID().substring(20) : gson.fromJson(result, MojangPlayerProfileInfo.class).getUUID());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static String getOfflinePlayerName(String playerName) {
        try {
            String result = getRequest(new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName));
            return new Gson().fromJson(result, MojangPlayerProfileInfo.class).getName();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static String getOfflinePlayerName(UUID uuid) {
        ArrayList<String> names = new ArrayList<>();
//        Type nameType = new TypeToken<ArrayList<UUIDDatabase>>() {}.getType();
        try {
            String result = getRequest(new URL("https://api.mojang.com/user/profiles/" + uuid + "/names"));
            Arrays.stream(new Gson().fromJson(result, MojangPlayerNameInfo[].class)).forEach(playerNameInfo -> names.add(playerNameInfo.getNames()));
            return names.get(names.size() - 1);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static String getRequest(URL url) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("user-agent", "LOSTED-Loves-Coffee");
        String line;
        while ((line = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine()) != null) stringBuilder.append(line);
        return stringBuilder.toString();
    }
}

class MojangPlayerProfileInfo {
    @SerializedName("id")
    String uuid;

    @SerializedName("name")
    String name;

    public String getUUID() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

}

class MojangPlayerNameInfo {

    @SerializedName("name")
    String names;

    @SerializedName("changedToAt")
    Long times;

    public String getNames() {
        return names;
    }

    public Long getTimes() {
        return times;
    }
}
