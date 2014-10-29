package com.mitc.common;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.mitc.Toc;
import com.mitc.settings.Config;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Utils {

    public Config config;
    public ResourceBundle lang;

    private Charset charset = StandardCharsets.UTF_8;

    public void loadConfig(String configPath) throws IOException, URISyntaxException {

        System.out.println("Load config from: " + configPath);

        YamlReader reader = new YamlReader(
                new InputStreamReader(Toc.class.getClassLoader().getResourceAsStream(configPath)));

        Map map = (Map) reader.read();
        config = ((ArrayList<Config>) map.get("config")).get(0);

        if (config.getLocale().contains("_"))
            setLanguage(config.getLocale().split("_")[0], config.getLocale().split("_")[1]);
    }

    public void prepareContent(String indexPath, String templatePath) throws IOException, URISyntaxException {

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("theme", config.getTheme());
        valuesMap.put("width", String.valueOf(config.getWidth()));
        valuesMap.put("height", String.valueOf(config.getHeight() - 60));
        valuesMap.put("bin", config.getSite() + "bin");

        // read
        String read = config.getSite() + "html/" + templatePath;
        System.out.println("Read template from: " + read);
        String raw = IOUtils.toString(new File(read).toURI(), charset);

        // parse
        String res = new StrSubstitutor(valuesMap).replace(raw).replace("a href=\"", "a href=\"file:\\\\\\");

        // write
        String write = config.getSite() + "html" + config.getPathSep() + indexPath;
        System.out.println("Write results to: " + write);
        FileUtils.writeStringToFile(new File(write), res);

    }

    public void setLanguage(String language, String country) {
        Locale locale = new Locale(language.toLowerCase(), country.toUpperCase());
        lang = ResourceBundle.getBundle("i18n/bundle", locale);
    }
}
