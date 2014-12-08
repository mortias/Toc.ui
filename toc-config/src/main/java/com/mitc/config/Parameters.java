package com.mitc.config;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;

public class Parameters {

    private int width;
    private int height;

    private int restPort;
    private int vertxPort;

    private int hawtioPort;
    private boolean hawtio;

    private boolean monitoring;

    private String key;
    private String root;
    private String theme;
    private String mode;
    private String pathSep;
    private String host;

    private String proxyUser;
    private String proxyHost;
    private String proxyPass;
    private Integer proxyPort;

    private boolean encrypted;

    public Parameters() {

        theme = "cupertino";
        mode = "jquery-ui";

        restPort = 9999;
        vertxPort = 8888;

        hawtio = false;
        hawtioPort = 7777;

        try {
            pathSep = System.getProperty("file.separator");
            root = FilenameUtils.getFullPath(new File("test.txt").getCanonicalPath());
            host = Inet4Address.getLocalHost().getHostAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getPathSep() {
        return pathSep;
    }

    public void setPathSep(String pathSep) {
        this.pathSep = pathSep;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public int getVertxPort() {
        return vertxPort;
    }

    public void setVertxPort(int vertxPort) {
        this.vertxPort = vertxPort;
    }

    public int getRestPort() {
        return restPort;
    }

    public void setRestPort(int restPort) {
        this.restPort = restPort;
    }

    public int getHawtioPort() {
        return hawtioPort;
    }

    public void setHawtioPort(int hawtioPort) {
        this.hawtioPort = hawtioPort;
    }

    public boolean getHawtio() {
        return hawtio;
    }

    public void setHawtio(boolean hawtio) {
        this.hawtio = hawtio;
    }

    public boolean getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(boolean monitoring) {
        this.monitoring = monitoring;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPass() {
        return proxyPass;
    }

    public void setProxyPass(String proxyPass) {
        this.proxyPass = proxyPass;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}