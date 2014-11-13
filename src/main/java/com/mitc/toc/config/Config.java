package com.mitc.toc.config;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Config {

    private ResourceBundle resourceBundle;
    private static final Logger logger = LogManager.getLogger(Config.class);

    private static Config instance = null;

    private Settings settings;
    private String settingsPath = "settings.yml";

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void loadSettings() {
        try {

            YamlReader yamlReader = new YamlReader(
                    new InputStreamReader(new FileInputStream(new File(settingsPath).getCanonicalPath())));
            yamlReader.getConfig().setClassTag("settings", Settings.class);
            this.settings = yamlReader.read(Settings.class);
            yamlReader.close();

            // set the language
            if (settings.getLocale().contains("_"))
                setLanguage(settings.getLocale().split("_")[0], settings.getLocale().split("_")[1]);

            logger.info(MessageFormat.format(resourceBundle.getString("load.config.from"), settingsPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSettings() throws IOException, URISyntaxException {

        YamlWriter yamlWriter = new YamlWriter(new FileWriter(new File(new File(settingsPath).getCanonicalPath())));
        yamlWriter.getConfig().setClassTag("settings", Settings.class);
        yamlWriter.write(settings);
        yamlWriter.close();

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
