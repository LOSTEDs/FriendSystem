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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FriendCommands extends Command {

    final BaseComponent[] beforeLine = new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶\n").color(ChatColor.BLUE).create();
    final BaseComponent[] afterLine = new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create();

    public FriendCommands() {
        super("friend", "", "f");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("You must be a player in order to execute Friend Commands!").color(ChatColor.RED).create());
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 0) {
            sendHelpMessage(player);
            return;
        }

        if (args.length <= 1) {
            switch (args[0]) {
                case "help": {
                    sendHelpMessage(player);
                    break;
                }
                case "list": {
                    sendFriendsList(player);
                    break;
                }
//                case "notifications" : {
//
//                    break;
//                }
//                case "removeall": {
//
//                    break;
//                }
                case "requests": {
                    sendFriendRequestsList(player);
                    break;
                }
//                case "toggle": {
//
//                    break;
//                }
                default: {
                    if (FriendSystem.instance.getProxy().getPlayer(args[0]) != null) sendFriendRequestToOnlinePlayer(player, FriendSystem.instance.getProxy().getPlayer(args[0])); else sendFriendRequestToOfflinePlayer(player, args[0]);
                    return;
                }
            }
        }

        if (args.length <= 2) {
            switch (args[0]) {
                case "accept": {
                    acceptFriendRequestFrom(player, args[1]);
                    break;
                }
                case "deny": {
                    denyFriendRequestFrom(player, args[1]);
                    break;
                }
                case "add": {
                    ProxiedPlayer receiver = FriendSystem.instance.getProxy().getPlayer(args[1]);
                    if (receiver != null) {
                        sendFriendRequestToOnlinePlayer(player, receiver);
                    } else {
                        sendFriendRequestToOfflinePlayer(player, args[1]);
                    }
                    break;
                }
                case "remove": {
                    ProxiedPlayer badFriend = FriendSystem.instance.getProxy().getPlayer(args[1]);
                    if (badFriend != null) {
                        removeFriendFromOnlinePlayer(player, badFriend);
                    } else {
                        removeFriendFromOfflinePlayer(player, args[1]);
                    }
                    break;
                }
                case "requests": {
                    if (args.length <= 1) return;
                    try {
                        int pageNumber = Integer.parseInt(args[1]);
                        sendFriendRequestsList(player, pageNumber);
                    } catch (NumberFormatException exception) {
                        player.sendMessage(new ComponentBuilder()
                                .append(beforeLine)
                                .append(new ComponentBuilder("Invalid Page Number!").strikethrough(false).color(ChatColor.RED).create())
                                .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create()) // I know I can use afterLine... However, it would bugged out sometimes...
                                .create());
                    }
                    break;
                }
                case "list": {
                    if (args.length <= 1) return;
                    try {
                        int pageNumber = Integer.parseInt(args[1]);
                        sendFriendsList(player, pageNumber);
                    } catch (NumberFormatException exception) {
                        player.sendMessage(new ComponentBuilder()
                                .append(beforeLine)
                                .append(new ComponentBuilder("Invalid Page Number!").strikethrough(false).color(ChatColor.RED).create())
                                .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                                .create());
                    }
                    break;
                }
                default: {
                    sendHelpMessage(player);
                    break;
                }
            }
        }
    }

    protected void sendHelpMessage(ProxiedPlayer player) {
        player.sendMessage(new ComponentBuilder()
                .append(beforeLine)
                .append(new ComponentBuilder("Friend Commands:\n").strikethrough(false).color(ChatColor.GREEN).create())
                .append(new ComponentBuilder("/friend accept <player>").strikethrough(false).color(ChatColor.YELLOW).create())
                .append(new ComponentBuilder(" - ").color(ChatColor.DARK_GRAY).strikethrough(false).create())
                .append(new ComponentBuilder("Accept a friend request\n").strikethrough(false).color(ChatColor.AQUA).create())
                .append(new ComponentBuilder("/friend add <player>").strikethrough(false).color(ChatColor.YELLOW).create())
                .append(new ComponentBuilder(" - ").strikethrough(false).color(ChatColor.DARK_GRAY).create())
                .append(new ComponentBuilder("Add a player as a friend\n").strikethrough(false).color(ChatColor.AQUA).create())
                .append(new ComponentBuilder("/friend deny <player>").strikethrough(false).color(ChatColor.YELLOW).create())
                .append(new ComponentBuilder(" - ").strikethrough(false).color(ChatColor.DARK_GRAY).create())
                .append(new ComponentBuilder("Decline a friend request\n").strikethrough(false).color(ChatColor.AQUA).create())
                .append(new ComponentBuilder("/friend help").strikethrough(false).color(ChatColor.YELLOW).create())
                .append(new ComponentBuilder(" - ").strikethrough(false).color(ChatColor.DARK_GRAY).create())
                .append(new ComponentBuilder("Prints all available friend commands!\n").strikethrough(false).color(ChatColor.AQUA).create())
                .append(new ComponentBuilder("/friend list").strikethrough(false).color(ChatColor.YELLOW).create())
                .append(new ComponentBuilder(" - ").strikethrough(false).color(ChatColor.DARK_GRAY).create())
                .append(new ComponentBuilder("List your friends\n").strikethrough(false).color(ChatColor.AQUA).create())
//                .append(new ComponentBuilder("/friend notifications").strikethrough(false).color(ChatColor.YELLOW).create())
//                .append(new ComponentBuilder(" - ").strikethrough(false).color(ChatColor.DARK_GRAY).create())
//                .append(new ComponentBuilder("Toggle friend join/leave notifications\n").strikethrough(false).color(ChatColor.AQUA).create())
//                .append(new ComponentBuilder("/friend removeall").strikethrough(false).color(ChatColor.YELLOW).create())
//                .append(new ComponentBuilder(" - ").strikethrough(false).color(ChatColor.DARK_GRAY).create())
//                .append(new ComponentBuilder("Remove all your friends\n").strikethrough(false).color(ChatColor.AQUA).create())
                .append(new ComponentBuilder("/friend remove <player>").strikethrough(false).color(ChatColor.YELLOW).create())
                .append(new ComponentBuilder(" - ").strikethrough(false).color(ChatColor.DARK_GRAY).create())
                .append(new ComponentBuilder("Remove a player from your friends\n").strikethrough(false).color(ChatColor.AQUA).create())
                .append(new ComponentBuilder("/friend requests <pages>").strikethrough(false).color(ChatColor.YELLOW).create())
                .append(new ComponentBuilder(" - ").strikethrough(false).color(ChatColor.DARK_GRAY).create())
                .append(new ComponentBuilder("View friend requests\n").strikethrough(false).color(ChatColor.AQUA).create())
//                .append(new ComponentBuilder("/friend toggle").strikethrough(false).color(ChatColor.YELLOW).create())
//                .append(new ComponentBuilder(" - ").strikethrough(false).color(ChatColor.DARK_AQUA).create())
//                .append(new ComponentBuilder("Toggle friend requests\n").strikethrough(false).color(ChatColor.AQUA).create())
                .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                .create());
    }

    protected void sendFriendRequestToOfflinePlayer(ProxiedPlayer sender, String receiver) {
        UUID receiversUUID = Utilities.getOfflinePlayerUUID(receiver);

        if (receiversUUID == null || !FriendSystem.existedUUIDs.contains(receiversUUID)) {
            sender.sendMessage(new ComponentBuilder("No player found with name ").color(ChatColor.RED)
                    .append(receiver).color(ChatColor.RED)
                    .append("!").color(ChatColor.RED)
                    .create());
            return;
        }

        UUID sendersUUID = sender.getUniqueId();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            PlayerInfo p = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, receiversUUID + ".json")), PlayerInfo.class);

            if (p.getFriends() != null) {
                if (p.getFriends().contains(sendersUUID)) {
                    sender.sendMessage(new ComponentBuilder()
                            .append(beforeLine)
                            .append(new ComponentBuilder("You're already friends with this person!\n").strikethrough(false).color(ChatColor.RED).create())
                            .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                            .create());
                    return;
                }
            }

            if (p.getPendingRequests() != null) {
                if (!p.getPendingRequests().isEmpty() && p.getPendingRequests().contains(sender.getUniqueId())) {
                    sender.sendMessage(new ComponentBuilder()
                            .append(beforeLine)
                            .append(new ComponentBuilder("You've already sent a friend request to this person!\n").strikethrough(false).color(ChatColor.RED).create())
                            .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                            .create());
                    return;
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        FriendSystem.instance.getProxy().getScheduler().schedule(FriendSystem.instance, () -> {
            UUID offlineReceiversUUID = Utilities.getOfflinePlayerUUID(receiver);
            try {
                PlayerInfo p = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, receiversUUID + ".json")), PlayerInfo.class);
                if (p.getFriends() != null) if (p.getFriends().contains(sendersUUID)) return;

                if (FriendSystem.instance.getProxy().getPlayer(sendersUUID) != null) {
                    sender.sendMessage(new ComponentBuilder()
                            .append(beforeLine)
                            .append(new ComponentBuilder("Your friend request to ").strikethrough(false).color(ChatColor.YELLOW).create())
                            .append(new ComponentBuilder(FriendSystem.instance.getProxy().getPlayer(receiversUUID) != null ? FriendSystem.instance.getProxy().getPlayer(receiversUUID).getDisplayName() : offlineReceiversUUID + "").strikethrough(false).create())
                            .append(new ComponentBuilder(" has expired.").strikethrough(false).color(ChatColor.YELLOW).create())
                            .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                            .create());
                }

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }, 1, TimeUnit.MINUTES);

        Set<UUID> pendingRequests = new HashSet<>();
        if (FriendSystem.instance.getProxy().getPlayer(sendersUUID) != null) {
            String offlinePlayerName = Utilities.getOfflinePlayerName(receiver);
            sender.sendMessage(new ComponentBuilder()
                    .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡≡〗").color(ChatColor.BLUE).create())
                    .append(new ComponentBuilder("[FRIEND REQUEST!]").color(ChatColor.DARK_AQUA).bold(true).create())
                    .append(new ComponentBuilder("〖≡≡≡≡≡≡≡≡≡≡≡▶\n").color(ChatColor.BLUE).bold(false).create())
                    .append(new ComponentBuilder("You ").color(ChatColor.YELLOW).bold(false).create())
                    .append(new ComponentBuilder("have requested to be on ").bold(false).color(ChatColor.GREEN).create())
                    .append(new ComponentBuilder(offlinePlayerName).bold(false).create())
                    .append(new ComponentBuilder(offlinePlayerName.endsWith("s") ? "' friend list" : "'s friend list").color(ChatColor.GREEN).create())
                    .append(new ComponentBuilder(". They have").bold(false).color(ChatColor.GREEN).create())
                    .append(new ComponentBuilder(" 60 ").bold(false).color(ChatColor.YELLOW).create())
                    .append(new ComponentBuilder("seconds to accept.\n").bold(false).color(ChatColor.GREEN).create())
                    .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡≡《═════⋘✖⋙═════》≡≡≡≡≡≡≡≡≡≡≡▶").bold(false).color(ChatColor.BLUE).create())
                    .create());
        }

        try {
            PlayerInfo p = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, receiversUUID + ".json")), PlayerInfo.class);
            if (p.getPendingRequests() == null) pendingRequests.add(sendersUUID); else p.addPendingRequest(sendersUUID);
            PlayerInfo playerInfo = new PlayerInfo(receiversUUID, p.isOnline(), p.getFriends() == null ? null : p.getFriends(), p.getIgnoredPlayers() == null ? null : p.getIgnoredPlayers(), p.getPendingRequests() == null ? pendingRequests : p.getPendingRequests());

            try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, receiversUUID + ".json"))) {
                gson.toJson(playerInfo, fileWriter);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    protected void sendFriendRequestToOnlinePlayer(ProxiedPlayer sender, ProxiedPlayer receiver) {
        if (sender == receiver) {
            sender.sendMessage(new ComponentBuilder()
                    .append(beforeLine)
                    .append(new ComponentBuilder("You can't add yourself as a friend!\n").strikethrough(false).color(ChatColor.RED).create())
                    .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                    .create());
            return;
        }

        UUID uuid = receiver.getUniqueId();
        UUID sendersUUID = sender.getUniqueId();
        String receiverDisplayName = receiver.getDisplayName();
        String senderDisplayName = sender.getDisplayName();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            PlayerInfo p = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, uuid + ".json")), PlayerInfo.class);

            if (p.getFriends() != null) {
                if (p.getFriends().contains(sendersUUID)) {
                    sender.sendMessage(new ComponentBuilder()
                            .append(beforeLine)
                            .append(new ComponentBuilder("You're already friends with this person!\n").strikethrough(false).color(ChatColor.RED).create())
                            .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                            .create());
                    return;
                }
            }

            if (p.getPendingRequests() != null) {
                if (!p.getPendingRequests().isEmpty() && p.getPendingRequests().contains(sender.getUniqueId())) {
                    sender.sendMessage(new ComponentBuilder()
                            .append(beforeLine)
                            .append(new ComponentBuilder("Please wait before sending this person another request!\n").strikethrough(false).color(ChatColor.RED).create())
                            .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                            .create());
                    return;
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        FriendSystem.instance.getProxy().getScheduler().schedule(FriendSystem.instance, () -> {
            try {
                PlayerInfo p = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, uuid + ".json")), PlayerInfo.class);
                if (p.getFriends() != null) if (!p.getFriends().isEmpty()) if (p.getFriends().contains(sendersUUID)) return;
                p.getPendingRequests().remove(sendersUUID);
                try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, uuid + ".json"))) {
                    gson.toJson(p, fileWriter);
                }

                if (FriendSystem.instance.getProxy().getPlayer(uuid) != null) {
                    receiver.sendMessage(new ComponentBuilder()
                            .append(beforeLine)
                            .append(new ComponentBuilder("The friend request from ").strikethrough(false).color(ChatColor.YELLOW).create())
                            .append(new ComponentBuilder(FriendSystem.instance.getProxy().getPlayer(sendersUUID) != null ? sender.getDisplayName() : senderDisplayName).strikethrough(false).create())
                            .append(new ComponentBuilder(" has expired.\n").strikethrough(false).color(ChatColor.YELLOW).create())
                            .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                            .create());
                }

                if (FriendSystem.instance.getProxy().getPlayer(sendersUUID) != null) {
                    sender.sendMessage(new ComponentBuilder()
                            .append(beforeLine)
                            .append(new ComponentBuilder("Your friend request to ").strikethrough(false).color(ChatColor.YELLOW).create())
                            .append(new ComponentBuilder(FriendSystem.instance.getProxy().getPlayer(uuid) != null ? receiver.getDisplayName() : receiverDisplayName).strikethrough(false).create())
                            .append(new ComponentBuilder(" has expired.\n").strikethrough(false).color(ChatColor.YELLOW).create())
                            .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                            .create());
                }

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }, 1, TimeUnit.MINUTES);

        Set<UUID> pendingRequests = new HashSet<>();
        if (FriendSystem.instance.getProxy().getPlayer(sendersUUID) != null) {
            sender.sendMessage(new ComponentBuilder()
                    .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡≡〗").color(ChatColor.BLUE).create())
                    .append(new ComponentBuilder("[FRIEND REQUEST!]").bold(true).color(ChatColor.DARK_AQUA).create())
                    .append(new ComponentBuilder("〖≡≡≡≡≡≡≡≡≡≡≡▶\n").bold(false).color(ChatColor.BLUE).create())
                    .append(new ComponentBuilder("You ").bold(false).strikethrough(false).color(ChatColor.YELLOW).create())
                    .append(new ComponentBuilder("have requested to be on ").bold(false).strikethrough(false).color(ChatColor.GREEN).create())
                    .append(new ComponentBuilder(receiver.getDisplayName()).bold(false).create())
                    .append(new ComponentBuilder(receiver.getName().endsWith("s") ? "' friend list" : "'s friend list").color(ChatColor.GREEN).create())
                    .append(new ComponentBuilder(". They have").bold(false).color(ChatColor.GREEN).create())
                    .append(new ComponentBuilder(" 60 ").bold(false).color(ChatColor.YELLOW).create())
                    .append(new ComponentBuilder("seconds to accept.\n").bold(false).color(ChatColor.GREEN).create())
                    .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡≡《═════⋘✖⋙═════》≡≡≡≡≡≡≡≡≡≡≡▶").bold(false).color(ChatColor.BLUE).create())
                    .create());
        }

        try {
            PlayerInfo p = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, uuid + ".json")), PlayerInfo.class);
            if (p.getPendingRequests() == null) pendingRequests.add(sender.getUniqueId()); else p.addPendingRequest(sender.getUniqueId());
            PlayerInfo playerInfo = new PlayerInfo(uuid, p.isOnline(), p.getFriends() == null ? null : p.getFriends(), p.getIgnoredPlayers() == null ? null : p.getIgnoredPlayers(), p.getPendingRequests() == null ? pendingRequests : p.getPendingRequests());

            try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, uuid + ".json"))) {
                gson.toJson(playerInfo, fileWriter);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        HoverEvent hoverAccept = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.AQUA + "Click to accept the friend request"));
        HoverEvent hoverDeny = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.AQUA + "Click to deny the friend request"));
        HoverEvent hoverIgnore = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.AQUA + "Click to ignore player"));

        ClickEvent clickAccept = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + sender.getName());
        ClickEvent clickDeny = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + sender.getName());
        // TODO: Add Ignore System
        //        ClickEvent clickIgnore = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignore add")

        receiver.sendMessage(new ComponentBuilder()
                .append(beforeLine)
                .append(new ComponentBuilder("Friend request from ").strikethrough(false).color(ChatColor.YELLOW).create())
                .append(new ComponentBuilder(sender.getDisplayName()).strikethrough(false).color(ChatColor.RESET).create())
                .append(new ComponentBuilder("\n[ACCEPT]").event(hoverAccept).event(clickAccept).strikethrough(false).bold(true).color(ChatColor.GREEN).create())
                .append(new ComponentBuilder(" - ").event((HoverEvent) null).event((ClickEvent) null).strikethrough(false).bold(false).color(ChatColor.DARK_GRAY).create())
                .append(new ComponentBuilder("[DENY]").event(hoverDeny).event(clickDeny).strikethrough(false).bold(true).color(ChatColor.RED).create())
//                .append(new ComponentBuilder(" - ").event((HoverEvent) null).event((ClickEvent) null).strikethrough(false).bold(false).color(ChatColor.DARK_GRAY).create())
//                .append(new ComponentBuilder("[IGNORE]\n").event(hoverIgnore).strikethrough(false).bold(true).color(ChatColor.GRAY).create())
                .create());

        receiver.sendMessage(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create());

    }

    protected void acceptFriendRequestFrom(ProxiedPlayer accepter, String asker) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        UUID askersUUID = Utilities.getOfflinePlayerUUID(asker);
        Set<UUID> friends = new HashSet<>();
        friends.add(askersUUID);
        Set<UUID> askersFriends = new HashSet<>();
        askersFriends.add(accepter.getUniqueId());

        try {
            PlayerInfo p = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, accepter.getUniqueId() + ".json")), PlayerInfo.class);
            if (p.getPendingRequests() == null || p.getPendingRequests().isEmpty()) {
                accepter.sendMessage(new ComponentBuilder()
                        .append(beforeLine)
                        .append(new ComponentBuilder("That person hasn't invited you to be friends! Try").color(ChatColor.RED).create())
                        .append(new ComponentBuilder(" /friend " + asker).color(ChatColor.YELLOW).create())
                        .append(new ComponentBuilder("\n").create())
                        .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                        .create());
                return;
            }

            if (p.getPendingRequests().contains(FriendSystem.instance.getProxy().getPlayer(asker) == null ? askersUUID : FriendSystem.instance.getProxy().getPlayer(asker).getUniqueId())) {
                accepter.sendMessage(new ComponentBuilder()
                        .append(beforeLine)
                        .append(new ComponentBuilder("You are now friends with ").color(ChatColor.GREEN).create())
                        .append(new ComponentBuilder(Utilities.getOfflinePlayerName(asker)).create())
                        .append(new ComponentBuilder("\n").create())
                        .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                        .create());

                Set<UUID> pendingRequests = new HashSet<>();
                if (p.getPendingRequests() != null) {
                    pendingRequests.addAll(p.getPendingRequests());
                    pendingRequests.remove(askersUUID);
                }
                if (p.getFriends() != null) friends.addAll(p.getFriends());
                PlayerInfo newData = new PlayerInfo(accepter.getUniqueId(), p.isOnline(), friends, p.getIgnoredPlayers() == null || p.getIgnoredPlayers().isEmpty() ? null : p.getIgnoredPlayers(), pendingRequests == null ? null : pendingRequests.isEmpty() ? null : pendingRequests);
                try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, accepter.getUniqueId() + ".json"))) {
                    gson.toJson(newData, fileWriter);
                }

                if (FriendSystem.instance.getProxy().getPlayer(asker) != null) {
                    FriendSystem.instance.getProxy().getPlayer(asker).sendMessage(new ComponentBuilder()
                            .append(beforeLine)
                            .append(new ComponentBuilder("You are now friends with ").strikethrough(false).color(ChatColor.GREEN).create())
                            .append(new ComponentBuilder(accepter.getDisplayName()).strikethrough(false).create())
                            .append(new ComponentBuilder("\n").create())
                            .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                            .create());

                    PlayerInfo askerData = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, FriendSystem.instance.getProxy().getPlayer(asker).getUniqueId() + ".json")), PlayerInfo.class);

                    if (askerData.getFriends() != null) askersFriends.addAll(askerData.getFriends());
                    PlayerInfo askerNewData = new PlayerInfo(FriendSystem.instance.getProxy().getPlayer(asker).getUniqueId(), askerData.isOnline(), askersFriends, askerData.getIgnoredPlayers() == null ? null : askerData.getIgnoredPlayers(), askerData.getPendingRequests() == null ? null : askerData.getPendingRequests());

                    try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, FriendSystem.instance.getProxy().getPlayer(asker).getUniqueId() + ".json"))) {
                        gson.toJson(askerNewData, fileWriter);
                    }
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    protected void denyFriendRequestFrom(ProxiedPlayer denier, String asker) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        UUID askersUUID = Utilities.getOfflinePlayerUUID(asker);
        String askersName = Utilities.getOfflinePlayerName(askersUUID);

        try {
            PlayerInfo playerInfo = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, denier.getUniqueId() + ".json")), PlayerInfo.class);
            if (playerInfo.getPendingRequests() == null || playerInfo.getPendingRequests().isEmpty()) {
                denier.sendMessage(new ComponentBuilder()
                        .append(beforeLine)
                        .append(new ComponentBuilder("That person hasn't invited you to be friends! Try").strikethrough(false).color(ChatColor.RED).create())
                        .append(new ComponentBuilder(" /friend " + asker).strikethrough(false).color(ChatColor.YELLOW).create())
                        .append(new ComponentBuilder("\n◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                        .create());
                return;
            }

            if (playerInfo.getPendingRequests().contains(FriendSystem.instance.getProxy().getPlayer(asker) == null ? askersUUID : FriendSystem.instance.getProxy().getPlayer(asker).getUniqueId())) {
                denier.sendMessage(new ComponentBuilder()
                        .append(beforeLine)
                        .append(new ComponentBuilder("Declined ").color(ChatColor.RED).create())
                        .append(new ComponentBuilder(askersName).create())
                        .append(new ComponentBuilder("'s friend request!\n").color(ChatColor.RED).create())
                        .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                        .create());

                playerInfo.getPendingRequests().remove(FriendSystem.instance.getProxy().getPlayer(asker) == null ? askersUUID : FriendSystem.instance.getProxy().getPlayer(asker).getUniqueId());
                PlayerInfo newData = new PlayerInfo(denier.getUniqueId(), playerInfo.isOnline(), playerInfo.getFriends() == null || playerInfo.getFriends().isEmpty() ? null : playerInfo.getFriends(), playerInfo.getIgnoredPlayers() == null || playerInfo.getIgnoredPlayers().isEmpty() ? null : playerInfo.getIgnoredPlayers(), playerInfo.getPendingRequests());
                try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, denier.getUniqueId() + ".json"))) {
                    gson.toJson(newData, fileWriter);
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    protected void removeFriendFromOfflinePlayer(ProxiedPlayer remover, String badFriend) {
        UUID badFriendUUID = Utilities.getOfflinePlayerUUID(badFriend);

        if (badFriendUUID == null || !FriendSystem.existedUUIDs.contains(badFriendUUID)) {
            remover.sendMessage(new ComponentBuilder("No player found with name ").color(ChatColor.RED)
                    .append(badFriend).color(ChatColor.RED)
                    .append("!").color(ChatColor.RED)
                    .create());
            return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String offlineBadFriendName = Utilities.getOfflinePlayerName(badFriendUUID);

        try {
            PlayerInfo playerInfo = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, remover.getUniqueId() + ".json")), PlayerInfo.class);
            // TODO: Optimise the code here
            if (playerInfo.getFriends() != null) if (!playerInfo.getFriends().contains(badFriendUUID)) {
                remover.sendMessage(new ComponentBuilder()
                        .append(beforeLine)
                        // Even though the bad friend is online, we still gonna to check it (just in case, because I don't like NPE)
                        .append(new ComponentBuilder(FriendSystem.instance.getProxy().getPlayer(badFriend) != null ? FriendSystem.instance.getProxy().getPlayer(badFriend).getDisplayName() : offlineBadFriendName).strikethrough(false).create())
                        .append(new ComponentBuilder(" isn't on your friend list!\n").strikethrough(false).color(ChatColor.RED).create())
                        .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                        .create());
                return;
            }

            if (playerInfo.getFriends().contains(badFriendUUID)) {

                try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, remover.getUniqueId() + ".json"))) {
                    playerInfo.removeFriend(badFriendUUID);
                    gson.toJson(playerInfo, fileWriter);
                    if (remover.isConnected()) {
                        remover.sendMessage(new ComponentBuilder()
                                .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡≡〗").color(ChatColor.BLUE).create())
                                .append(new ComponentBuilder("[FRIEND REMOVED]").color(ChatColor.AQUA).bold(true).create())
                                .append(new ComponentBuilder("〖≡≡≡≡≡≡≡≡≡≡≡▶\n").color(ChatColor.BLUE).bold(false).create())
                                .append(new ComponentBuilder("You").bold(false).strikethrough(false).color(ChatColor.YELLOW).create())
                                .append(new ComponentBuilder(" have removed ").bold(false).color(ChatColor.RED).create())
                                .append(FriendSystem.instance.getProxy().getPlayer(badFriendUUID) != null ? FriendSystem.instance.getProxy().getPlayer(badFriendUUID).getDisplayName() : offlineBadFriendName).bold(false).strikethrough(false)
                                .append(new ComponentBuilder(" from your friend list!\n").bold(false).strikethrough(false).color(ChatColor.RED).create())
                                .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡≡《═════⋘✖⋙═════》≡≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                                .create());
                    }
                }

                Gson badFriendGson = new GsonBuilder().setPrettyPrinting().create();
                PlayerInfo badFriendInfo = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, badFriendUUID + ".json")), PlayerInfo.class);
                if (badFriendInfo.getFriends() != null || !badFriendInfo.getFriends().isEmpty()) badFriendInfo.removeFriend(remover.getUniqueId());

                try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, badFriendUUID + ".json"))) {
                    badFriendGson.toJson(badFriendInfo, fileWriter);
                }
            }
        } catch (IOException exception) {

        }
    }

    protected void removeFriendFromOnlinePlayer(ProxiedPlayer remover, ProxiedPlayer badFriend) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        UUID badFriendUUID = badFriend.getUniqueId();
        String offlineBadFriendName = Utilities.getOfflinePlayerName(badFriendUUID);

        try {
            PlayerInfo playerInfo = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, remover.getUniqueId() + ".json")), PlayerInfo.class);
            // TODO: Optimise the code here
            if (playerInfo.getFriends() == null || !playerInfo.getFriends().contains(badFriendUUID)) {
                remover.sendMessage(new ComponentBuilder()
                        .append(beforeLine)
                        // Even though the bad friend is online, we still gonna to check it (just in case, because I don't like NPE)
                        .append(new ComponentBuilder(FriendSystem.instance.getProxy().getPlayer(badFriend.getUniqueId()) != null ? badFriend.getDisplayName() : offlineBadFriendName).strikethrough(false).color(ChatColor.RESET).create())
                        .append(new ComponentBuilder(" isn't on your friend list!\n").strikethrough(false).color(ChatColor.RED).create())
                        .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                        .create());
                return;
            }

            try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, remover.getUniqueId() + ".json"))) {
                playerInfo.removeFriend(badFriendUUID);
                gson.toJson(playerInfo, fileWriter);
                if (remover.isConnected()) {
                    remover.sendMessage(new ComponentBuilder()
                            .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡≡〗").color(ChatColor.BLUE).create())
                            .append(new ComponentBuilder("[FRIEND REMOVED]").bold(true).color(ChatColor.DARK_AQUA).create())
                            .append(new ComponentBuilder("〖≡≡≡≡≡≡≡≡≡≡≡▶\n").bold(false).color(ChatColor.BLUE).create())
                            .append(new ComponentBuilder("You").bold(false).color(ChatColor.YELLOW).create())
                            .append(new ComponentBuilder(" have removed ").bold(false).color(ChatColor.RED).create())
                            .append(FriendSystem.instance.getProxy().getPlayer(badFriendUUID) != null ? badFriend.getDisplayName() : offlineBadFriendName).bold(false)
                            .append(new ComponentBuilder(" from your friend list.\n").bold(false).color(ChatColor.RED).create())
                            .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡≡《═════⋘✖⋙═════》≡≡≡≡≡≡≡≡≡≡≡▶").bold(false).color(ChatColor.BLUE).create())
                            .create());
                }
            }

            Gson badFriendGson = new GsonBuilder().setPrettyPrinting().create();
            PlayerInfo badFriendInfo = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, badFriendUUID + ".json")), PlayerInfo.class);
            if (badFriendInfo.getFriends() != null || !badFriendInfo.getFriends().isEmpty()) badFriendInfo.removeFriend(remover.getUniqueId());

            try (FileWriter fileWriter = new FileWriter(new File(Utilities.dataFolderPath, badFriendUUID + ".json"))) {
                badFriendGson.toJson(badFriendInfo, fileWriter);

                if (badFriend.isConnected()) {
                    badFriend.sendMessage(new ComponentBuilder()
                            .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡≡〗").color(ChatColor.BLUE).create())
                            .append(new ComponentBuilder("[FRIEND REMOVED]").bold(true).color(ChatColor.DARK_AQUA).create())
                            .append(new ComponentBuilder("〖≡≡≡≡≡≡≡≡≡≡≡▶\n").bold(false).color(ChatColor.BLUE).create())
                            .append(remover.getDisplayName()).bold(false)
                            .append(new ComponentBuilder(" has removed ").bold(false).color(ChatColor.RED).create())
                            .append(new ComponentBuilder("you").bold(false).color(ChatColor.YELLOW).create())
                            .append(new ComponentBuilder(" from their friend list.\n").bold(false).color(ChatColor.RED).create())
                            .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡≡《═════⋘✖⋙═════》≡≡≡≡≡≡≡≡≡≡≡▶").bold(false).color(ChatColor.BLUE).create())
                            .create());
                }

            }
        } catch (IOException exception) {

        }
    }

    protected void sendFriendRequestsList(ProxiedPlayer player, int pageNumber) {
        Gson gson = new Gson();

        try {

            PlayerInfo playerInfo = gson.fromJson(new FileReader(new File(Utilities.dataFolderPath, player.getUniqueId() + ".json")), PlayerInfo.class);

            player.sendMessage(new ComponentBuilder("--- Friend Requests (Page ").color(ChatColor.YELLOW)
                    .append(String.valueOf(pageNumber)).color(ChatColor.YELLOW)
                    .append(" of ").color(ChatColor.YELLOW)
                    .append(String.valueOf(playerInfo.getPendingRequests() == null || playerInfo.getPendingRequests().isEmpty() ? 0 : playerInfo.getPendingRequests().size() <= 8 ? 1 : (int) Math.ceil((float) playerInfo.getPendingRequests().size() / 8))) // Feel like this cast is useless but whatever
                    .append(") ---").color(ChatColor.YELLOW).create());

            if (playerInfo.getPendingRequests() != null) if (!playerInfo.getPendingRequests().isEmpty()) playerInfo.getPendingRequests().stream().skip((pageNumber - 1) * 8L).limit(8).forEach(pendingUUID -> {
                String offlinePlayerName = Utilities.getOfflinePlayerName(pendingUUID);
                player.sendMessage(new ComponentBuilder("From ").color(ChatColor.YELLOW)
                        .append(FriendSystem.instance.getProxy().getPlayer(pendingUUID) == null ? offlinePlayerName : FriendSystem.instance.getProxy().getPlayer(pendingUUID).getDisplayName())
                        .append(new ComponentBuilder("  ").create())
                        .append(new ComponentBuilder("[ACCEPT]").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.AQUA + "Click to accept the friend request"))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + offlinePlayerName)).bold(true).color(ChatColor.GREEN).create())
                        .append(new ComponentBuilder(" ").event((HoverEvent) null).event((ClickEvent) null).create())
                        .append(new ComponentBuilder("[DENY]").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.AQUA + "Click to deny the friend request"))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + offlinePlayerName)).bold(true).color(ChatColor.RED).create())
//                        .append(new ComponentBuilder(" ").event((HoverEvent) null).event((ClickEvent) null).create())
//                        .append(new ComponentBuilder("[IGNORE]").event((HoverEvent) null).event((ClickEvent) null).bold(true).color(ChatColor.GRAY).create()) // TODO: Add Ignore System
                        .create());
            });

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    protected void sendFriendRequestsList(ProxiedPlayer player) {
        sendFriendRequestsList(player, 1);
    }

    protected void sendFriendsList(ProxiedPlayer player, int pageNumber) {
        try {
            PlayerInfo playerInfo = new Gson().fromJson(new FileReader(new File(Utilities.dataFolderPath, player.getUniqueId() + ".json")), PlayerInfo.class);

            // ===== Combine these two
            if (playerInfo.getFriends() == null) {
                player.sendMessage(new ComponentBuilder()
                        .append(beforeLine)
                        .append(new ComponentBuilder("You don't have any friends yet! Add some with /friend add\n").color(ChatColor.YELLOW).create())
                        .append(new ComponentBuilder("player\n").color(ChatColor.YELLOW).create())
                        .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                        .create());
                return;
            }

            if (playerInfo.getFriends().isEmpty()) {
                player.sendMessage(new ComponentBuilder()
                        .append(beforeLine)
                        .append(new ComponentBuilder("You don't have any friends yet! Add some with /friend add\n").color(ChatColor.YELLOW).create())
                        .append(new ComponentBuilder("player\n").color(ChatColor.YELLOW).create())
                        .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                        .create());
                return;
            }
            // =====
            if (pageNumber <= 0) {
                player.sendMessage(new ComponentBuilder()
                        .append(beforeLine)
                        .append(new ComponentBuilder("Page number must be positive!\n").color(ChatColor.RED).create())
                        .append(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡╠════════════════╣≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create())
                        .create());
                return;
            }


            if (playerInfo.getFriends().size() >= 9) {
                if (pageNumber > playerInfo.getFriends().size() / 8) {
                    sendFriendsList(player, playerInfo.getFriends().size() / 8);
                    return;
                }
            }

            if (playerInfo.getFriends().size() <= 8 && pageNumber >= 2) {
                sendFriendsList(player, 1);
                return;
            }
            player.sendMessage(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡〗").color(ChatColor.BLUE)
                    .append(new ComponentBuilder("≪ ").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + "Click to view page " + (pageNumber - 1 == 0 ? pageNumber : pageNumber - 1)))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend list " + (pageNumber - 1 == 0 ? pageNumber : pageNumber - 1))).color(ChatColor.YELLOW).create())
                    .append(new ComponentBuilder("FRIENDS LIST ").bold(true).color(ChatColor.DARK_AQUA).create())
                    .append(new ComponentBuilder("(").bold(false).color(ChatColor.BLUE).create())
                    .append(new ComponentBuilder(pageNumber + "").bold(false).color(ChatColor.DARK_AQUA).create())
                    .append(new ComponentBuilder("/").bold(false).color(ChatColor.DARK_AQUA).create())
                    .append(new ComponentBuilder((int) Math.ceil(playerInfo.getFriends().size() / (float) 8) + "").bold(false).color(ChatColor.DARK_AQUA).create())
                    .append(new ComponentBuilder(") ").bold(false).color(ChatColor.BLUE).create())
                    .append(new ComponentBuilder("≫").bold(false).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + "Click to view page " + (pageNumber + 1)))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend list " + (pageNumber + 1))).color(ChatColor.YELLOW).create())
                    .append(new ComponentBuilder("〖≡≡≡≡≡≡≡≡≡≡▶").bold(false).color(ChatColor.BLUE).create())
                    .create());

            ArrayList<UUID> orderedFriends = new ArrayList<>();

            // ===== We should optimise this
            playerInfo.getFriends().forEach(friend -> {
                try {
                    PlayerInfo friendInfo = new Gson().fromJson(new FileReader(new File(Utilities.dataFolderPath, friend + ".json")), PlayerInfo.class);
                    if (!friendInfo.isOnline()) return;
                    orderedFriends.add(friend);
                } catch (IOException exception) {

                }
            });

            playerInfo.getFriends().forEach(friend -> {
                try {
                    PlayerInfo friendInfo = new Gson().fromJson(new FileReader(new File(Utilities.dataFolderPath, friend + ".json")), PlayerInfo.class);
                    if (friendInfo.isOnline()) return;
                    orderedFriends.add(friend);
                } catch (IOException exception) {

                }
            });

            orderedFriends.stream().skip((pageNumber - 1) * 8L).limit(8).forEach(friend -> {
                if (FriendSystem.instance.getProxy().getPlayer(friend) == null) {
                    player.sendMessage(new ComponentBuilder(Utilities.getOfflinePlayerName(friend))
                            .append(" is offline").color(ChatColor.RED).create());
                } else {
                    player.sendMessage(new ComponentBuilder(FriendSystem.instance.getProxy().getPlayer(friend).getDisplayName())
                            .append(" is online").color(ChatColor.GREEN).create());
                }
            });
            // =====

            player.sendMessage(new ComponentBuilder("◀≡≡≡≡≡≡≡≡≡≡≡《═════⋘✖⋙═════》≡≡≡≡≡≡≡≡≡≡≡▶").color(ChatColor.BLUE).create());

        } catch (IOException exception) {
            // TODO: report this to discord webhook
        }
    }

    protected void sendFriendsList(ProxiedPlayer player) {
        sendFriendsList(player, 1);
    }

//    protected void toggleNotification(ProxiedPlayer player) {
//
//    }
//
//    protected void removeAllPlayer(ProxiedPlayer player) {
//
//    }

}
