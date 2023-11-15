package org.teamstats.api;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.teamstats.TeamStats;
import org.teamstats.util.Files;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OnlinePlayer {
    private TeamStats main = (TeamStats) Bukkit.getPluginManager().getPlugin("TeamReport");
    public static File playerTimeDatas = Files.pluginFileConstruct("playerTimeDatas");
    private Instant joinTime;
    private Instant quitTime;
    public UUID player;
    public static HashMap<UUID, OnlinePlayer> players = new HashMap<>();

    public static void quitAll(){
        for (Map.Entry<UUID, OnlinePlayer> entry : players.entrySet()){
            entry.getValue().playerQuit();
        }
    }

    public static void joinAll(){
        for (Player i : Bukkit.getServer().getOnlinePlayers()){
            UUID player = i.getUniqueId();
            if (!org.teamstats.api.OnlinePlayer.players.containsKey(player)){
                org.teamstats.api.OnlinePlayer.playerJoin(player);
            }
        }
    }

    public static void playerJoin(UUID player){
        if (!playerTimeDatas.exists()){
            playerTimeDatas.mkdirs();
        }

        OnlinePlayer opd = new OnlinePlayer();
        opd.joinTime = Instant.now();
        opd.player = player;
        OnlinePlayer.players.put(player, opd);
    }

    public void playerQuit(){
        quitTime = Instant.now();
        File playerTimeData = Files.fileResolve(playerTimeDatas, player.toString()+".json");
        Long playerOnlineTime = Duration.between(joinTime, quitTime).toSeconds();

        if (!playerTimeData.exists()){
            JsonObject newJson = new JsonObject();
            newJson.addProperty("time", playerOnlineTime);
            try {
                Files.writeJsonToFile(playerTimeData, newJson);
            }catch (Exception ignored){}
        }

        try {
            JsonObject json = Files.readFileToJson(playerTimeData);
            Long playerLastOnlineTime = json.get("time").getAsLong();
            json.addProperty("time", (playerOnlineTime + playerLastOnlineTime));
            Files.writeJsonToFile(playerTimeData, json);
        }catch (Exception ignored){
            return;
        }

        OnlinePlayer.players.remove(this.player);
    }

}
