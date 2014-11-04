package com.mitc.util;

import com.mitc.Toc;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class Content {

    private static final Logger logger = LogManager.getLogger(Content.class);
    private Charset charset = StandardCharsets.UTF_8;

    private static Content instance = null;

    public static Content getInstance() {
        if (instance == null) {
            instance = new Content();
        }
        return instance;
    }

    protected Content() {
        // Exists only to defeat instantiation.
    }

    public void load(String indexPath, String templatePath) throws IOException, URISyntaxException {

        Settings settings = Toc.config.getSettings();

        String site = settings.getRoot() + "site" + settings.getPathSep();

        Map<String, String> siteMap = new HashMap<>();
        siteMap.put("theme", settings.getTheme());
        siteMap.put("width", String.valueOf(settings.getWidth()));
        siteMap.put("height", String.valueOf(settings.getHeight()));
        siteMap.put("theme", settings.getTheme());
        siteMap.put("bin", site + "bin");
        siteMap.put("host", settings.getHost());
        siteMap.put("port", String.valueOf(settings.getPort()));
        handleFile(
                site + "html" + settings.getPathSep() + templatePath,
                site + "html" + settings.getPathSep() + indexPath, siteMap, true);

        String swagger = settings.getRoot() + "swagger" + settings.getPathSep();
        Map<String, String> swaggerMap = new HashMap<>();
        swaggerMap.put("host", settings.getHost());
        swaggerMap.put("port", String.valueOf(settings.getPort()));
        handleFile(swagger + "index.html", swagger + "index.html", swaggerMap, false);

    }

    private void handleFile(String in, String out, Map<String, String> props, boolean handleFiles) throws IOException, URISyntaxException {

        // read
        logger.info(MessageFormat.format(Toc.config.translate("read.template.from"), in));

        String res = IOUtils.toString(new FileInputStream(new File(in)), charset.toString());
        res = new StrSubstitutor(props).replace(res);

        // parse a hrefs
        if (handleFiles)
            res = res.replace("a href=\"", "a href=\"file:\\\\\\");

        // write
        logger.info(MessageFormat.format(Toc.config.translate("write.results.to"), out));
        FileUtils.writeStringToFile(new File(out), res);

    }


}
