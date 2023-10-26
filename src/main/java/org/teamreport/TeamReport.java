package org.teamreport;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.teamreport.api.OnlinePlayer;
import org.teamreport.util.Files;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class TeamReport extends JavaPlugin {
    public File pluginDir = getDataFolder();
    public LangResource langRes;
    public Map<String, Object> config;
    public Instant today;
    public int day;
    public BukkitTask scheduler;

    public void checkPluginFile() throws IOException {
        if (!pluginDir.exists()){
            pluginDir.mkdirs();
        }

        if (!pluginDir.isDirectory()){
            pluginDir.delete();
            pluginDir.mkdirs();
        }

        File configFileReal = pluginDir.toPath().resolve("config.yml").toFile();

        if (!configFileReal.exists()){
            URL configFileTemplate = getClass().getResource("/config.yml");
            assert configFileTemplate != null;
            FileUtils.copyURLToFile(configFileTemplate,configFileReal);
        }

        if (!configFileReal.isFile()){
            boolean ignored = configFileReal.delete();
            URL configFileTemplate = getClass().getResource("/config.yml");
            assert configFileTemplate != null;
            FileUtils.copyURLToFile(configFileTemplate,configFileReal);
        }
    }

    private void loadConfig(){
        File configFile = pluginDir.toPath().resolve("config.yml").toFile();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFile);
        } catch (Exception e) {}
        this.config = (new Yaml()).load(inputStream);
    }

    private void loadLang(){
        try {
            InputStream textSource = getClass().getClassLoader().getResourceAsStream("lang/"+config.get("lang")+".json");
            String fileData = new String(textSource.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject langJson = new Gson().fromJson(fileData , JsonObject.class);
            this.langRes = new LangResource(langJson);
        } catch (IOException e) {}
    }

    private void loadCommands(){
        Reflections reflections = new Reflections("org.teamreport.commands" ,new SubTypesScanner(false));
        Set<Class<?>> commands = reflections.getSubTypesOf(Object.class);
        for (Class<?> c : commands){
            try {
                c.getDeclaredConstructor().newInstance();
                Bukkit.getLogger().info("[TeamReport] Loading command - "+c.getName());
            } catch (Exception e) {
                Bukkit.getLogger().warning("[TeamReport] Can not load command - "+c.getName());
                Bukkit.getLogger().warning(e.toString());
                e.printStackTrace();
            }
        }
    }

    private void initDailyCounter(){
        Bukkit.getServer().getPluginManager().registerEvents(new Events(),this);
        File timeData = Files.pluginFileConstruct("timeData");
        if (!timeData.exists()){
            String timeStamp = Timestamp.from(Instant.now()).toString();
            try {
                Files.writeFile(timeData, timeStamp);
            }catch (Exception ignored){}
        }

        try {
            day = Timestamp.valueOf(Files.readFile(timeData))
                    .toInstant().atZone(ZoneOffset.systemDefault()).getDayOfMonth();
        }catch (Exception ignored){}

        this.scheduler = Bukkit.getScheduler().runTaskTimer(this,()->{
            int today = Instant.now().atZone(ZoneOffset.systemDefault()).getDayOfMonth();
            if (today != day){
                String timeStamp = Timestamp.from(Instant.now()).toString();
                try {
                    Files.writeFile(timeData, timeStamp);
                }catch (Exception ignored){}
                day = today;
                org.teamreport.api.Embed.sendEmbed(org.teamreport.PlayerData.getToday());
            }
        },300,20);
    }

    public static TeamReport getThis(){
        return (TeamReport) (Bukkit.getPluginManager().getPlugin("TeamReport"));
    }

    @Override
    public void onEnable(){
        //initialize plugin folder and files
        try{
            checkPluginFile();
        }
        catch (IOException e){
            Bukkit.getLogger().throwing(this.getName(),"IOException",e);
            return;
        }

        org.teamreport.api.OnlinePlayer.joinAll();
        loadConfig();
        loadLang();
        loadCommands();
        initDailyCounter();

    }

    @Override
    public void onDisable() {
        scheduler.cancel();
        // Plugin shutdown logic

        OnlinePlayer.quitAll();
    }
}
