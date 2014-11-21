package com.mitc.util.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class VerifyUrl implements Callable<Map<String, Object>> {

    private final String url;
    private Logger logger = LogManager.getLogger(VerifyUrl.class);
    public VerifyUrl(String url) {
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
                Authenticator authenticator = new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return (new PasswordAuthentication("----", "----".toCharArray()));
                    }
                };
                Authenticator.setDefault(authenticator);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("147.67.117.13", 8012));
                HttpURLConnection conn = (HttpURLConnection) new URL(this.url).openConnection(proxy);
                conn.setConnectTimeout(500);
                return sendResult("proxy", this.url, conn.getResponseCode(), conn.getResponseMessage());
            } catch (Exception ex) {
                return sendResult("none", this.url, -1, "Forbidden");
            }
        }
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
