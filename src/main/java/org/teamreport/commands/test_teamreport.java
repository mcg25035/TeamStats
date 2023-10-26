package org.teamreport.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;

public class test_teamreport {
    public test_teamreport(){
        new CommandAPICommand("test_teamreport")
                .withPermission(CommandPermission.OP)
                .executes((sender, args) -> {
                    org.teamreport.api.Embed.sendEmbed(org.teamreport.PlayerData.getToday());
                    sender.sendMessage("\u00a7aSucceed!");
                })
                .register();
    }
}
