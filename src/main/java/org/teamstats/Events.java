package org.teamstats;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.teamstats.api.OnlinePlayer;

import java.util.UUID;

public class Events implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        UUID playerUUID = e.getPlayer().getUniqueId();
        OnlinePlayer.playerJoin(playerUUID);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        UUID player = e.getPlayer().getUniqueId();
        if (!OnlinePlayer.players.containsKey(player)){
            return;
        }

        OnlinePlayer.players.get(e.getPlayer().getUniqueId()).playerQuit();

    }
}
