package com.mitc.tools;

import com.esotericsoftware.yamlbeans.YamlReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;import java.util.*;

public class Utils {

    public Config config;
    public ResourceBundle lang;

    private Charset charset = StandardCharsets.UTF_8;
    private final static Logger logger = Logger.getLogger(Utils.class);

    public void loadConfig(String configPath) throws IOException, URISyntaxException {

        String absolutePath =  new File(configPath).getCanonicalPath();
        YamlReader reader = new YamlReader(
                new InputStreamReader(new FileInputStream(new File(absolutePath))));

        Map map = (Map) reader.read();
        config = ((ArrayList<Config>) map.get("config")).get(0);

        if (config.getLocale().contains("_"))
            setLanguage(config.getLocale().split("_")[0], config.getLocale().split("_")[1]);

        logger.info(MessageFormat.format(lang.getString("load.config.from"), absolutePath));

    }

    public void prepareContent(String indexPath, String templatePath) throws IOException, URISyntaxException {

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("theme", config.getTheme());
        valuesMap.put("width", String.valueOf(config.getWidth()));
        valuesMap.put("height", String.valueOf(config.getHeight() - 60));
        valuesMap.put("bin", config.getSite() + "bin");

        // read
        String read = config.getSite() + "html/" + templatePath;
        logger.info(MessageFormat.format(lang.getString("read.template.from"), read));
        String raw = IOUtils.toString(new File(read).toURI(), charset);

        // parse
        String res = new StrSubstitutor(valuesMap).replace(raw).replace("a href=\"", "a href=\"file:\\\\\\");

        // write
        String write = config.getSite() + "html" + config.getPathSep() + indexPath;
        logger.info(MessageFormat.format(lang.getString("write.results.to"), write));
        FileUtils.writeStringToFile(new File(write), res);

    }

    public void setLanguage(String language, String country) {
        Locale locale = new Locale(language.toLowerCase(), country.toUpperCase());
        lang = ResourceBundle.getBundle("i18n/bundle", locale);
    }
}
