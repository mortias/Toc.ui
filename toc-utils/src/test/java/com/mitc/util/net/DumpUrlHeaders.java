package com.mitc.util.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DumpUrlHeaders {

    public static void main(String[] args) {

        try {

            String url = "http://www.java2s.com/Tutorial/Java/0320__Network/Displayheaderinformation.htm";

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(1000);

            Map<String, List<String>> headers = conn.getHeaderFields();
            Set<String> hdrKeys = headers.keySet();

            for (String k : hdrKeys)
                System.out.println("Key: " + k + "  Value: " + headers.get(k));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
