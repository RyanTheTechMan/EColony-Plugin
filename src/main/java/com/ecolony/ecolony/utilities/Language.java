package com.ecolony.ecolony.utilities;

import com.ecolony.ecolony.Main;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Level;

public class Language {
    public HashMap<String, JsonObject> languages = new HashMap<>();
    public String currentLanguage = "en_us";

    public String get(String key) {
        return languages.get(currentLanguage).get("").getAsString();
    }

    public Language() {
        Main.instance.getLogger().log(Level.WARNING, "Loading Languages...");
        String content = null;
        try {
            content = Files.readString(Path.of(getClass().getResource("Lang/en_us.json").toURI()));

            Main.instance.getLogger().log(Level.WARNING, "Content: " + content);;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        JsonObject json = new Gson().fromJson(content, JsonObject.class);

//        this.languages.put("en_us", new Gson().fromJson(new FileReader(nextFile), JsonObject.class));
        Main.instance.getLogger().log(Level.WARNING, "Made: " + json.getAsString());
//        for (File nextFile : new File(Main.instance.getResource("L") + File.separator + "Lang").listFiles()) {
//            Main.instance.getLogger().log(Level.WARNING, "EEEEEEE: " + nextFile.toURI());
//            if (nextFile.getName().endsWith(".json")) {
//                try {
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }
}
