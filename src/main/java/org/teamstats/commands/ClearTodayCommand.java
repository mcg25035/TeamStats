package org.teamstats.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.teamstats.TeamStats;
import org.teamstats.api.OnlinePlayer;

public class ClearTodayCommand {
    public ClearTodayCommand(){
        new CommandAPICommand("cleartoday")
                .withPermission(CommandPermission.OP)
                .withSubcommand(clearTime())
                .register();
    }

    public CommandAPICommand clearTime(){
        return new CommandAPICommand("time")
                .withPermission(CommandPermission.OP)
                .executes((sender,args)->{
                    OnlinePlayer.quitAll();
                    TeamStats.clearTimeData();
                    OnlinePlayer.joinAll();
                    sender.sendMessage("\u00a7a"+ TeamStats.getThis().langRes.lang.reportTimeClearSuccessfully);
                });
    }
}
