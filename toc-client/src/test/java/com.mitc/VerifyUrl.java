package com.mitc;

import java.net.HttpURLConnection;
import java.net.URL;

public class VerifyUrl {

    public static void main(String[] args) {

        try {

            String url = "http://cf8rtdd1.cc.cec.eu.int:6090/ercbackoffice/index.cfm";
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                // Not OK.
                System.out.println("not avalable");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
