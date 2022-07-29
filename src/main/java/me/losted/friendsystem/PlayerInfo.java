package me.losted.friendsystem;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;
import java.util.UUID;

public class PlayerInfo {
    @SerializedName("uuid")
    private UUID uuid;
    @SerializedName("online")
    private boolean online;
    @SerializedName("friends")
    private Collection<UUID> friends;
    @SerializedName("ignoredPlayers")
    private Collection<UUID> ignoredPlayers;
    @SerializedName("pendingRequests")
    private Collection<UUID> pendingRequests;

    public PlayerInfo(UUID uuid, boolean online, Collection<UUID> friends, Collection<UUID> ignoredPlayers, Collection<UUID> pendingRequests) {
        this.uuid = uuid;
        this.online = online;
        this.friends = friends;
        this.ignoredPlayers = ignoredPlayers;
        this.pendingRequests = pendingRequests;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean isOnline() {
        return online;
    }

    public Collection<UUID> getFriends() {
        return friends;
    }

    public Collection<UUID> getIgnoredPlayers() {
        return ignoredPlayers;
    }

    public Collection<UUID> getPendingRequests() {
        return pendingRequests;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void addFriend(UUID playersUUID) {
        friends.add(playersUUID);
    }

    public void removeFriend(UUID playersUUID) {
        friends.remove(playersUUID);
    }

    public void addIgnoredPlayer(UUID playersUUID) {
        ignoredPlayers.add(playersUUID);
    }

    public void removeIgnoredPlayer(UUID playersUUID) {
        ignoredPlayers.remove(playersUUID);
    }

    public void addPendingRequest(UUID playersUUID) {
        pendingRequests.add(playersUUID);
    }

    public void removePendingRequest(UUID playersUUID) {
        pendingRequests.remove(playersUUID);
    }

}
