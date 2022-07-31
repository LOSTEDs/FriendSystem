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
