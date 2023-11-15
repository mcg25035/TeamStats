package org.teamstats.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.teamstats.TeamStats;

public class ManualReportCommand {
    public ManualReportCommand(){
        new CommandAPICommand("manualreport")
                .withPermission(CommandPermission.OP)
                .executes((sender, args) -> {
                    try{
                        org.teamstats.api.Embed.sendEmbed(org.teamstats.PlayerData.getToday(),true);
                        sender.sendMessage("\u00a7a"+ TeamStats.getThis().langRes.lang.reportTimeSentSuccessfully);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        sender.sendMessage("\u00a7c"+ TeamStats.getThis().langRes.lang.reportTimeSentFailed);
                    }

                })
                .register();
    }
}
