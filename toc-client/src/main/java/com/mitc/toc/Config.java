package com.mitc.toc;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.MessageFormat;

public class Config {

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

            logger.info(MessageFormat.format("Load config from: {0}", settingsPath));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSettings() {
        try {
            YamlWriter yamlWriter = new YamlWriter(new FileWriter(new File(new File(settingsPath).getCanonicalPath())));
            yamlWriter.getConfig().setClassTag("settings", Settings.class);
            yamlWriter.write(settings);
            yamlWriter.close();
            logger.info(MessageFormat.format("Saving configuration to: {0}", settingsPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Settings getSettings() {
        return this.settings;
    }

}
