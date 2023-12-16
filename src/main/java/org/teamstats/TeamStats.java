package org.teamstats;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.teamstats.api.Embed;
import org.teamstats.api.OnlinePlayer;
import org.teamstats.util.Files;
import org.teamstats.util.NoExcept;
import org.teamstats.util.Time;
import org.teamstats.util.WithExcept;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

public final class TeamStats extends JavaPlugin {
    public File pluginDir = getDataFolder();
    public File playerTimeDataDir;
    public LangResource langRes;
    public Map<String, Object> config;
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
        InputStream inputStream = NoExcept.getOrNull(() -> new FileInputStream(configFile));
        this.config = (new Yaml()).load(inputStream);
    }

    private void loadLang(){
        NoExcept.run(() -> {
            InputStream textSource = getClass().getClassLoader().getResourceAsStream("lang/"+config.get("lang")+".json");
            String fileData = new String(textSource.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject langJson = new Gson().fromJson(fileData , JsonObject.class);
            this.langRes = new LangResource(langJson);
        });
    }

    private void loadCommands(){
        Reflections reflections = new Reflections("org.teamstats.commands", new SubTypesScanner(false));
        Set<Class<?>> commands = reflections.getSubTypesOf(Object.class);
        for (Class<?> c : commands){
            try {
                c.getDeclaredConstructor().newInstance();
                Bukkit.getLogger().info("[TeamStats] Loading command - " + c.getName());
            } catch (Exception e) {
                Bukkit.getLogger().warning("[TeamStats] Can not load command - " + c.getName());
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
            NoExcept.run(() -> Files.writeFile(timeData, timeStamp));
        }

        NoExcept.run(() -> day = Time.dayOfMonth(Timestamp.valueOf(Files.readFile(timeData)).toInstant()));

        this.scheduler = Bukkit.getScheduler().runTaskTimer(this, () -> {
            int today = Time.dayOfMonth(Instant.now());
            if (today == this.day){
                return;
            }
            String timeStamp = Timestamp.from(Instant.now()).toString();
            NoExcept.run(() -> Files.writeFile(timeData, timeStamp));
            day = today;
            WithExcept.run(() -> Embed.sendEmbed(PlayerData.getToday(), false));
            clearTimeData();

        }, 300, 20);

        this.scheduler = Bukkit.getScheduler().runTaskTimer(this, () -> {
            OnlinePlayer.quitAll();
            OnlinePlayer.joinAll();
        }, 300, 20*60*10);
    }

    public static void clearTimeData(){
        for (File i : getThis().playerTimeDataDir.listFiles()){
            if (!i.isFile()){
                continue;
            }
            i.delete();
        }
    }

    public static TeamStats getThis(){
        return (TeamStats) (Bukkit.getPluginManager().getPlugin("TeamStats"));
    }



    @Override
    public void onEnable(){
        //initialize plugin folder and files
        playerTimeDataDir = Files.pluginFileConstruct("playerTimeDatas");

        try{
            checkPluginFile();
        }
        catch (IOException e){
            Bukkit.getLogger().throwing(this.getName(),"IOException",e);
            return;
        }

        OnlinePlayer.joinAll();
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
