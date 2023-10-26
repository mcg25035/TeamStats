package org.teamreport;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.google.gson.JsonObject;
import org.teamreport.api.OnlinePlayer;
import org.teamreport.util.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {
    public com.booksaw.betterTeams.Team team;
    public UUID player;
    public long onlineSeconds;

    private PlayerData(com.booksaw.betterTeams.Team team, UUID player, long onlineSeconds){
        this.team = team;
        this.player = player;
        this.onlineSeconds = onlineSeconds;
    }

    public static PlayerData uuidToPlayerData(UUID playerUUID, Team team){
        File playerTimeDatas = OnlinePlayer.playerTimeDatas;

        File playerTimeData = Files.fileResolve(playerTimeDatas,
                playerUUID+".json");

        if (!playerTimeData.exists()){
            return null;
        }
        try {
            JsonObject playerData = Files.readFileToJson(playerTimeData);
            Long onlineTime = playerData.get("time").getAsLong();
            if (team != null){
                return new PlayerData(team, playerUUID, onlineTime);
            }
        }catch (Exception ignored){}
        return null;
    }

    public static List<PlayerData> getToday(){
        List<PlayerData> result = new ArrayList<>();

        File[] playerFiles = OnlinePlayer.playerTimeDatas.listFiles();
        List<UUID> players = new ArrayList<>();

        for (File i : playerFiles){
            if (!i.isFile()){continue;}
            players.add(UUID.fromString(i.getName().replaceAll("\\.json","")));
        }

        org.teamreport.api.OnlinePlayer.quitAll();

        for (Team i : org.teamreport.api.Team.getAllTeams()){
            List<TeamPlayer> teamPlayers = i.getMembers().get();
            for (TeamPlayer i2 : teamPlayers){
                UUID playerUUID = i2.getPlayer().getUniqueId();
                PlayerData data = uuidToPlayerData(playerUUID, i);
                players.remove(playerUUID);
                if (data.onlineSeconds > 0){
                    result.add(data);
                }
            }
        }

        org.teamreport.api.OnlinePlayer.joinAll();

        for (UUID i : players){
            PlayerData data = uuidToPlayerData(i, null);
            result.add(data);
        }

        return result;
    }
}
