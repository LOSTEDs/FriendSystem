package me.losted.friendsystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Listeners implements Listener {

    final BaseComponent[] beforeLine = new ComponentBuilder("-----------------------------------------------------\n").strikethrough(true).color(ChatColor.BLUE).create();
    final BaseComponent[] afterLine = new ComponentBuilder("-----------------------------------------------------").strikethrough(true).color(ChatColor.BLUE).create();

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (!FriendSystem.existedUUIDs.contains(uuid)) {
            Utilities.createData(player);
            FriendSystem.existedUUIDs.add(uuid);
            return;
        }

        HoverEvent hoverRequestsList = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + "Click to see " + ChatColor.AQUA + "requests" + ChatColor.YELLOW + "!"));
        ClickEvent clickRequestsList = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend requests");

        try {
            PlayerInfo p = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, uuid + ".json")), PlayerInfo.class);
            p.setOnline(true);

            if (p.getFriends() != null) {
                p.getFriends().forEach(friendsUUID -> {
                    if (FriendSystem.instance.getProxy().getPlayer(friendsUUID) != null) {
                        FriendSystem.instance.getProxy().getPlayer(friendsUUID).sendMessage(new ComponentBuilder("[FRIEND] > ").color(ChatColor.BLUE)
                                .append(player.getDisplayName())
                                .append(" has joined.").color(ChatColor.YELLOW)
                                .create());
                    }
                });
            }

            if (p.getPendingRequests() != null) {
                if (!p.getPendingRequests().isEmpty()) {

                    player.sendMessage(new ComponentBuilder()
                            .append(beforeLine)
                            .append(new ComponentBuilder("You have ").event(hoverRequestsList).event(clickRequestsList).strikethrough(false).color(ChatColor.GREEN).create())
                            .append(new ComponentBuilder(String.valueOf(p.getPendingRequests().size())).event(hoverRequestsList).event(clickRequestsList).strikethrough(false).color(ChatColor.GREEN).create())
                            .append(new ComponentBuilder(" pending friend requests.\n").event(hoverRequestsList).event(clickRequestsList).strikethrough(false).color(ChatColor.GREEN).create())
                            .append(new ComponentBuilder("Use ").event(hoverRequestsList).event(clickRequestsList).strikethrough(false).color(ChatColor.YELLOW).create())
                            .append(new ComponentBuilder("/f requests ").event(hoverRequestsList).event(clickRequestsList).strikethrough(false).color(ChatColor.AQUA).create())
                            .append(new ComponentBuilder("to see them!").event(hoverRequestsList).event(clickRequestsList).strikethrough(false).color(ChatColor.YELLOW).create())
                            .append(afterLine).event((HoverEvent) null).event((ClickEvent) null)
                            .create());

                    FriendSystem.instance.getProxy().getScheduler().schedule(FriendSystem.instance, () -> {
                        try {
                            PlayerInfo currentData = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, uuid + ".json")), PlayerInfo.class);

                            if (currentData.getFriends() != null) currentData.getFriends().forEach(p.getPendingRequests()::remove);
                            if (currentData.getPendingRequests() != null) currentData.getPendingRequests().removeIf(p.getPendingRequests()::contains);

                            try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, uuid + ".json"))) {
                                gson.toJson(currentData, fileWriter);
                            }

                            if (FriendSystem.instance.getProxy().getPlayer(uuid) == null || currentData.getPendingRequests() == null) return;
                            currentData.getPendingRequests().forEach(requestUUID -> {
                                if (!currentData.getFriends().contains(requestUUID)) {
                                    player.sendMessage(new ComponentBuilder()
                                            .append(beforeLine)
                                            .append(new ComponentBuilder("The friend request from ").strikethrough(false).color(ChatColor.YELLOW).create())
                                            .append(new ComponentBuilder(FriendSystem.instance.getProxy().getPlayer(requestUUID) != null ? FriendSystem.instance.getProxy().getPlayer(requestUUID).getDisplayName() : Utilities.getOfflinePlayerName(requestUUID)).strikethrough(false).create())
                                            .append(new ComponentBuilder(" has expired.").strikethrough(false).color(ChatColor.YELLOW).create())
                                            .append(afterLine)
                                            .create());
                                }
                            });
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }, 5, TimeUnit.MINUTES);
                }
            }

            try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, uuid + ".json"))) {
                gson.toJson(p, fileWriter);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            PlayerInfo p = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, uuid + ".json")), PlayerInfo.class);
            p.setOnline(false);
            if (p.getFriends() != null) {
                p.getFriends().forEach(friendsUUID -> {
                    if (FriendSystem.instance.getProxy().getPlayer(friendsUUID) != null) {
                        FriendSystem.instance.getProxy().getPlayer(friendsUUID).sendMessage(new ComponentBuilder("[FRIEND] > ").color(ChatColor.BLUE)
                                .append(player.getDisplayName())
                                .append(" has left.").color(ChatColor.RED)
                                .create());
                    }
                });
            }
            try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, uuid + ".json"))) {
                gson.toJson(p, fileWriter);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
