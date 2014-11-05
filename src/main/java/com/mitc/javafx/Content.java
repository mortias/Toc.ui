package com.mitc.javafx;

import com.mitc.Toc;
import com.mitc.config.TocSettings;
import com.mitc.config.RestSettings;
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

        TocSettings tocSettings = Toc.config.getTocSettings();
        RestSettings restSettings = Toc.config.getRestSettings();

        String site = tocSettings.getRoot() + "site" + tocSettings.getPathSep();

        Map<String, String> siteMap = new HashMap<>();
        siteMap.put("theme", tocSettings.getTheme());
        siteMap.put("width", String.valueOf(tocSettings.getWidth()));
        siteMap.put("height", String.valueOf(tocSettings.getHeight()));
        siteMap.put("theme", tocSettings.getTheme());
        siteMap.put("bin", site + "bin");
        siteMap.put("host", restSettings.getHost());
        siteMap.put("port", String.valueOf(restSettings.getPort()));

        handleFile(
                site + "html" + tocSettings.getPathSep() + templatePath,
                site + "html" + tocSettings.getPathSep() + indexPath, siteMap, true);

        String swagger = tocSettings.getRoot() + "swagger" + tocSettings.getPathSep();
        Map<String, String> swaggerMap = new HashMap<>();
        swaggerMap.put("host", restSettings.getHost());
        swaggerMap.put("port", String.valueOf(restSettings.getPort()));

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
