package com.mitc.util;

import com.esotericsoftware.yamlbeans.YamlReader;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Config {

    private ResourceBundle resourceBundle;
    private final static Logger logger = Logger.getLogger(Config.class);

    private static Config instance = null;
    private Settings settings;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void load(String settingsPath) throws IOException, URISyntaxException {

        String absolutePath = new File(settingsPath).getCanonicalPath();
        YamlReader reader = new YamlReader(
                new InputStreamReader(new FileInputStream(new File(absolutePath))));

        Map map = (Map) reader.read();
        this.settings = ((ArrayList<Settings>) map.get("settings")).get(0);

        if (settings.getLocale().contains("_"))
            setLanguage(settings.getLocale().split("_")[0], settings.getLocale().split("_")[1]);

        logger.setLevel(Level.toLevel(getSettings().getLevel()));
        logger.info(MessageFormat.format(resourceBundle.getString("load.config.from"), absolutePath));

    }

    public void setLanguage(String language, String country) {
        Locale locale = new Locale(language.toLowerCase(), country.toUpperCase());
        resourceBundle = ResourceBundle.getBundle("i18n/bundle", locale);
    }

    public String translate(String key) {
        return this.resourceBundle.getString(key);
    }

    public Settings getSettings() {
        return this.settings;
    }

}
