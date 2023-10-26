package org.teamreport.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Team {
    public static List<com.booksaw.betterTeams.Team> getAllTeams(){
        Map<UUID, com.booksaw.betterTeams.Team> team = com.booksaw.betterTeams.Team.getTeamManager().getLoadedTeamListClone();
        List<com.booksaw.betterTeams.Team> teamList = new ArrayList<>();

        String[] teams = com.booksaw.betterTeams.Team.getTeamManager().sortTeamsByMembers();
        for (String i : teams){
            teamList.add(com.booksaw.betterTeams.Team.getTeamByName(i));
        }

        return teamList;
    }
}