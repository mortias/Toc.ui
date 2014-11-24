package com.mitc.util.net;

import com.mitc.config.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@Component
public class VerifyUrl implements Callable<Map<String, Object>> {

    private Settings settings;

    private String url = "";
    private Logger logger = LogManager.getLogger(VerifyUrl.class);

    public VerifyUrl() {
    }

    public VerifyUrl(String url, Settings settings) {
        this.settings = settings;
        this.url = url.replace("file:///", "");
    }

    @Override
    public Map<String, Object> call() throws Exception {
        try {
            // first try without a proxy
            HttpURLConnection conn = (HttpURLConnection) new URL(this.url).openConnection();
            conn.setConnectTimeout(500);
            return sendResult("direct", this.url, conn.getResponseCode(), conn.getResponseMessage());
        } catch (Exception e) {
            try {
                if (isProxyEnabled()) {
                    Authenticator authenticator = new Authenticator() {
                        public PasswordAuthentication getPasswordAuthentication() {
                            return (new PasswordAuthentication(settings.getProxyUser(), settings.getProxyPass().toCharArray()));
                        }
                    };
                    Authenticator.setDefault(authenticator);
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(settings.getProxyHost(), settings.getProxyPort()));
                    HttpURLConnection conn = (HttpURLConnection) new URL(this.url).openConnection(proxy);
                    conn.setConnectTimeout(500);
                    return sendResult("proxy", this.url, conn.getResponseCode(), conn.getResponseMessage());
                } else {
                    return sendResult("none", this.url, -1, "Forbidden");
                }
            } catch (Exception ex) {
                return sendResult("none", this.url, -1, "Forbidden");
            }
        }
    }

    private boolean isProxyEnabled() {
        return (settings.getProxyUser().length() > 0 &&
                settings.getProxyPass().length() > 0 &&
                settings.getProxyHost().length() > 0 &&
                settings.getProxyPort().toString().length() > 0);
    }

    private Map<String, Object> sendResult(String location, String url, int responseCode, String responseMessage) {
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("action", "handleVerifyUrl");
        resMap.put("reference", url);
        resMap.put("proxy", location);
        resMap.put("text", responseMessage);
        resMap.put("code", responseCode);
        return resMap;
    }

}
