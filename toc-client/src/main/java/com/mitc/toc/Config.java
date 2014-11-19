package com.mitc.toc;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.MessageFormat;

@Component("Config")
public class Config {

    private String settingsPath = "settings.yml";
    private static final Logger logger = LogManager.getLogger(Config.class);

    public Config() {}

    public Settings load() {
        try {

            YamlReader yamlReader = new YamlReader(
                    new InputStreamReader(new FileInputStream(new File(settingsPath).getCanonicalPath())));
            yamlReader.getConfig().setClassTag("settings", Settings.class);
            Settings settings = yamlReader.read(Settings.class);
            yamlReader.close();

            logger.info(MessageFormat.format("Load config from: {0}", settingsPath));
            return settings;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Settings settings) {
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

}
