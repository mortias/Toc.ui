package com.mitc.config;

import java.io.IOException;
import java.net.Inet4Address;

public class RestSettings {

    private String host;
    private int port;

    public RestSettings() {
        try {
            port = 9876;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
