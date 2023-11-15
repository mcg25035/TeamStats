package org.teamstats;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class LangResource {

    public class dict {
        public String player;
        public String reportEmbedFooter;
        public String reportEmbedTitle;
        public String yesterdayOnlinePlayersData;
        public String todayOnlinePlayersData;
        public String team;
        public String onlineTime;
        public String reportTimeSentSuccessfully;
        public String reportTimeClearSuccessfully;
        public String reportTimeSentFailed;
    }

    public dict lang = new dict();

    public LangResource(JsonObject fromLangFile){

        for (Map.Entry<String, JsonElement> entry : fromLangFile.entrySet()){

            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (!value.isJsonPrimitive()){
                continue;
            }
            if (!value.getAsJsonPrimitive().isString()){
                continue;
            }

            try {
                LangResource.dict.class.getField(key).set(lang, value.getAsString());
            } catch (Exception ignored) {}
        }
    }
}
