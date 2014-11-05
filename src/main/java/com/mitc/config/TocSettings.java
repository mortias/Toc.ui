package com.mitc.config;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class TocSettings {

    private String key;
    private String root;
    private String theme;
    private String locale;
    private String pathSep;

    private int width;
    private int height;
    private int timeout;
    private boolean encrypted;
    private boolean undecorated;

    public TocSettings() {
        try {
            timeout = 2;
            theme = "cupertino";
            undecorated = true;
            locale = "en_US";
            pathSep = System.getProperty("file.separator");
            root = FilenameUtils.getFullPath(new File("test.txt").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean getUndecorated() {
        return undecorated;
    }

    public void setUndecorated(boolean undecorated) {
        this.undecorated = undecorated;
    }
}
