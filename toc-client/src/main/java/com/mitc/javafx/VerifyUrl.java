package com.mitc.javafx;

import com.mitc.services.vertx.VertxService;
import com.mitc.services.vertx.resources.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vertx.java.core.json.JsonObject;

import java.net.*;
import java.util.concurrent.Callable;

public class VerifyUrl implements Callable<Boolean> {

    private final String url;
    private VertxService vertxService;
    private Logger logger = LogManager.getLogger(VerifyUrl.class);

    public VerifyUrl(String url, VertxService vertxService) {
        this.vertxService = vertxService;
        this.url = url.replace("file:///", "");
    }

    @Override
    public Boolean call() throws Exception {

        try {
            // first try without a proxy
            HttpURLConnection conn = (HttpURLConnection) new URL(this.url).openConnection();
            conn.setConnectTimeout(2000);
            sendResult("local", this.url, conn.getResponseCode(), conn.getResponseMessage());
        } catch (Exception e) {
            try {
                Authenticator authenticator = new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return (new PasswordAuthentication("", "".toCharArray()));
                    }
                };
                Authenticator.setDefault(authenticator);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("147.67.117.13", 8012));
                HttpURLConnection conn = (HttpURLConnection) new URL(this.url).openConnection(proxy);
                conn.setConnectTimeout(2000);
                sendResult("proxy", this.url, conn.getResponseCode(), conn.getResponseMessage());
            } catch (Exception ex) {
                // ignore;
            }
        }
        return null;
    }

    private void sendResult(String location, String url, int responseCode, String responseMessage) {
        JsonObject msg = new JsonObject();
        msg.putString("action", "handleVerifyUrl");
        msg.putString("reference", url);
        msg.putString("proxy", location);
        msg.putString("text", responseMessage);
        msg.putNumber("code", responseCode);
        vertxService.sendMessage(Channel.BO_READ_CHANNEL.getName(), msg);
    }

}
