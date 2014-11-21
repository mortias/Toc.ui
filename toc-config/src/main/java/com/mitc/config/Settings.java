package com.mitc.config;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.MessageFormat;

@Component("Settings")
public class Settings {

    private Logger logger = LogManager.getLogger(Settings.class);

    private String settingsPath = "settings.yml";
    private Parameters parameters;

    public Settings() {
        if (parameters == null) {
            try {
                logger.info(MessageFormat.format("Loading config from: {0}", settingsPath));
                YamlReader yamlReader = new YamlReader(
                        new InputStreamReader(new FileInputStream(new File(settingsPath).getCanonicalPath())));
                yamlReader.getConfig().setClassTag("settings", Parameters.class);
                parameters = yamlReader.read(Parameters.class);
                yamlReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        if (parameters != null) {
            try {
                logger.info(MessageFormat.format("Saving configuration to: {0}", settingsPath));
                YamlWriter yamlWriter = new YamlWriter(new FileWriter(new File(new File(settingsPath).getCanonicalPath())));
                yamlWriter.getConfig().setClassTag("settings", Parameters.class);
                yamlWriter.write(parameters);
                yamlWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getHost() {
        return parameters.getHost();
    }

    public void setHost(String host) {
        parameters.setHost(host);
    }

    public int getWidth() {
        return parameters.getWidth();
    }

    public void setWidth(int width) {
        parameters.setWidth(width);
    }

    public int getHeight() {
        return parameters.getHeight();
    }

    public void setHeight(int height) {
        parameters.setHeight(height);
    }

    public String getTheme() {
        return parameters.getTheme();
    }

    public void setTheme(String theme) {
        parameters.setTheme(theme);
    }

    public String getPathSep() {
        return parameters.getPathSep();
    }

    public void setPathSep(String pathSep) {
        parameters.setPathSep(pathSep);
    }

    public String getRoot() {
        return parameters.getRoot();
    }

    public void setRoot(String root) {
        parameters.setRoot(root);
    }

    public String getKey() {
        return parameters.getKey();
    }

    public void setKey(String key) {
        parameters.setKey(key);
    }

    public boolean isEncrypted() {
        return parameters.isEncrypted();
    }

    public void setEncrypted(boolean encrypted) {
        parameters.setEncrypted(encrypted);
    }

    public boolean getUndecorated() {
        return parameters.getUndecorated();
    }

    public void setUndecorated(boolean undecorated) {
        parameters.setUndecorated(undecorated);
    }

    public int getVertxPort() {
        return parameters.getVertxPort();
    }

    public void setVertxPort(int vertxPort) {
        parameters.setVertxPort(vertxPort);
    }

    public int getRestPort() {
        return parameters.getRestPort();
    }

    public void setRestPort(int restPort) {
        parameters.setRestPort(restPort);
    }

    public int getHawtioPort() {
        return parameters.getHawtioPort();
    }

    public void setHawtioPort(int hawtioPort) {
        parameters.setHawtioPort(hawtioPort);
    }

    public boolean getHawtio() {
        return parameters.getHawtio();
    }

    public void setHawtio(boolean hawtio) {
        parameters.setHawtio(hawtio);
    }

    public boolean getMonitoring() {
        return parameters.getMonitoring();
    }

    public void setMonitoring(boolean monitoring) {
        parameters.setMonitoring(monitoring);
    }

    public String getProxyUser() {
        return parameters.getProxyUser();
    }

    public void setProxyUser(String proxyUser) {
        parameters.setProxyUser(proxyUser);
    }

    public String getProxyHost() {
        return parameters.getProxyHost();
    }

    public void setProxyHost(String proxyHost) {
        parameters.setProxyHost(proxyHost);
    }

    public String getProxyPass() {
        return parameters.getProxyPass();
    }

    public void setProxyPass(String proxyPass) {
        parameters.setProxyPass(proxyPass);
    }

    public Integer getProxyPort() {
        return parameters.getProxyPort();
    }

    public void setProxyPort(Integer proxyPort) {
        parameters.setProxyPort(proxyPort);
    }
}

