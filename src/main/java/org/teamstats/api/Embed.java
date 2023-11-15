package org.teamstats.api;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.objects.MessageFormat;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.teamstats.LangResource;
import org.teamstats.PlayerData;
import org.teamstats.TeamStats;

import java.time.Instant;
import java.util.*;

public class Embed {
    public static void sendEmbed(List <PlayerData> data,boolean isManual) throws Exception{
        if (Bukkit.getPluginManager().getPlugin("DiscordSRV") == null){
            throw new Exception("DiscordSRV is not installed , report data embed can not be sent!");
        }
        LangResource langRes = TeamStats.getThis().langRes;
        MessageFormat embed = new MessageFormat();

        embed.setAuthorName(langRes.lang.reportEmbedTitle);
        embed.setAuthorImageUrl("https://cdn.discordapp.com/attachments/763787703958372402/1172554499106287627/statistics-icon-512x512-gdqstvvk.png");
        embed.setFooterIconUrl("https://cdn.discordapp.com/avatars/492908862647697409/bf4ff10c052a338db04647dd23a70e62?size=1024");

        if (isManual){
            embed.setTitle(langRes.lang.todayOnlinePlayersData);
        }
        else{
            embed.setTitle(langRes.lang.yesterdayOnlinePlayersData);
        }

        String players = "";
        String allOnlineTime = "";
        String teams = "";

        for (PlayerData i : data){
            players += Bukkit.getServer().getOfflinePlayer(i.player).getName();
            players += "\n";
            allOnlineTime += i.onlineSeconds;
            allOnlineTime += "\n";
            if (i.team == null){
                continue;
            }
            teams += i.team.getName();
            teams += "\n";
        }

        MessageEmbed.Field player = new MessageEmbed.Field(langRes.lang.player,players,true,true);
        MessageEmbed.Field time = new MessageEmbed.Field(langRes.lang.onlineTime,allOnlineTime,true,true);
        MessageEmbed.Field team = new MessageEmbed.Field(langRes.lang.team,teams,true,true);

        embed.setFields(List.of(player, time, team));
        embed.setFooterText(langRes.lang.reportEmbedFooter);
        embed.setTimestamp(Instant.now());
        embed.setColorRaw(8767231);

        Message discordMessage = DiscordSRV.translateMessage(embed, (content, needsEscape) -> content);
        String channel = (String) TeamStats.getThis().config.get("channel");

        if (Long.valueOf(channel) == 0){
            throw new Exception("Invalid discord channel id!");
        }
        TextChannel target = DiscordUtil.getTextChannelById(channel);
        DiscordUtil.queueMessage(target,discordMessage);
    }

}

