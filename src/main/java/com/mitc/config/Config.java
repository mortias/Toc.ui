package com.mitc.config;

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

    private TocSettings tocSettings;
    private static final String tocSettingsPath = "toc-settings.yml";

    private RestSettings restSettings;
    private static final String restSettingsPath = "rest-settings.yml";

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void loadSettings() throws IOException, URISyntaxException {

        YamlReader yamlReader = new YamlReader(
                new InputStreamReader(new FileInputStream(new File(tocSettingsPath).getCanonicalPath())));
        yamlReader.getConfig().setClassTag("toc", TocSettings.class);
        this.tocSettings = yamlReader.read(TocSettings.class);
        if (tocSettings.getLocale().contains("_"))
            setLanguage(tocSettings.getLocale().split("_")[0], tocSettings.getLocale().split("_")[1]);
        yamlReader.close();

        logger.info(MessageFormat.format(resourceBundle.getString("load.config.from"), tocSettingsPath));
        System.out.println(this.tocSettings.getKey());

        yamlReader = new YamlReader(
                new InputStreamReader(new FileInputStream(new File(restSettingsPath).getCanonicalPath())));
        yamlReader.getConfig().setClassTag("rest", RestSettings.class);
        RestSettings yamlRestSettings = yamlReader.read(RestSettings.class);
        this.restSettings = yamlRestSettings != null ? yamlRestSettings : new RestSettings();
        yamlReader.close();

        logger.info(MessageFormat.format(resourceBundle.getString("load.config.from"), restSettingsPath));

    }

    public void saveSettings() throws IOException, URISyntaxException {

        YamlWriter yamlWriter = new YamlWriter(new FileWriter(new File(new File("2" + tocSettingsPath).getCanonicalPath())));
        yamlWriter.getConfig().setClassTag("toc", TocSettings.class);
        yamlWriter.write(tocSettings);
        yamlWriter.close();

        yamlWriter = new YamlWriter(new FileWriter(new File(new File("2" + restSettingsPath).getCanonicalPath())));
        yamlWriter.getConfig().setClassTag("rest", RestSettings.class);
        yamlWriter.write(restSettings);
        yamlWriter.close();

    }

    public void setLanguage(String language, String country) {
        Locale locale = new Locale(language.toLowerCase(), country.toUpperCase());
        resourceBundle = ResourceBundle.getBundle("i18n/bundle", locale);
    }

    public String translate(String key) {
        return this.resourceBundle.getString(key);
    }

    public TocSettings getTocSettings() {
        return this.tocSettings;
    }

    public RestSettings getRestSettings() {
        return this.restSettings;
    }

}
