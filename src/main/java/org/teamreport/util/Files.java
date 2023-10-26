package org.teamreport.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.teamreport.TeamReport;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Files {
    static TeamReport plugin = (TeamReport) Bukkit.getPluginManager().getPlugin("TeamReport");

    public static void writeFile(File file, CharSequence data) throws IOException{
        FileUtils.write(file, data, StandardCharsets.UTF_8);
    }

    public static String readFile(File file) throws IOException{
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    public static void writeJsonToFile(File file, JsonObject json) throws IOException {
        if (file.exists()){
            file.delete();
        }
        file.createNewFile();
        Files.writeFile(file, json.toString());
    }

    public static JsonObject readFileToJson(File file) throws IOException{
        String rawData = Files.readFile(file);
        return new Gson().fromJson(rawData, JsonObject.class);
    }

    public static File fileResolve(File file, String toResolve){
        return file.toPath().resolve(toResolve).toFile();
    }
    public static File pluginFileConstruct(String toResolve){
        return fileResolve(plugin.pluginDir,toResolve);
    }
}
