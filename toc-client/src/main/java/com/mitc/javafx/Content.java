package com.mitc.javafx;

import com.mitc.config.Settings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Component("Content")
public class Content {

    private final String templatePath = "site.html";
    private final String indexPath = "index.html";
    @Autowired
    Settings settings;
    private Logger logger = LogManager.getLogger(Content.class);
    private Charset charset = StandardCharsets.UTF_8;

    public Content() {
    }

    public void load(Settings settings) {
        try {

            String site = settings.getRoot() + "site" + settings.getPathSep();

            Map<String, String> siteMap = new HashMap<>();
            siteMap.put("theme", settings.getTheme());
            siteMap.put("width", String.valueOf(settings.getWidth()));
            siteMap.put("height", String.valueOf(settings.getHeight()));
            siteMap.put("theme", settings.getTheme());
            siteMap.put("bin", site + "bin");
            siteMap.put("host", settings.getHost());
            siteMap.put("restPort", String.valueOf(settings.getRestPort()));
            siteMap.put("vertxPort", String.valueOf(settings.getVertxPort()));
            siteMap.put("hawtioPort", String.valueOf(settings.getHawtioPort()));
            handleFile(site + "html-" + settings.getMode() + settings.getPathSep(), templatePath, indexPath, siteMap, true);

            String swagger = settings.getRoot() + "tools" + settings.getPathSep() + "swagger" + settings.getPathSep();
            Map<String, String> swaggerMap = new HashMap<>();
            swaggerMap.put("host", settings.getHost());
            swaggerMap.put("restPort", String.valueOf(settings.getRestPort()));
            handleFile(swagger, "index.html", "index.html", swaggerMap, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleFile(String path, String in, String out, Map<String, String> props, boolean merge) throws IOException, URISyntaxException {

        logger.info(MessageFormat.format("Read template from: {0}", path + in));
        String res = IOUtils.toString(new FileInputStream(new File(path + in)), charset.toString());

        if (merge) {
            Map<String, String> files = new HashMap<>();
            Files.walk(Paths.get(path))
                    .filter((filePath)
                            -> filePath.toFile().getName().startsWith("tab")
                            && Files.isRegularFile(filePath)
                            && filePath.toFile().getAbsolutePath().endsWith(".html"))
                    .forEach(filePath -> {
                        try {
                            files.put(filePath.getFileName().toString(),
                                    IOUtils.toString(new FileInputStream(filePath.toFile()), charset.toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            res = new StrSubstitutor(files).replace(res);
            res = res.replace("a href=\"", "a href=\"file:\\\\\\")
                     .replace("a href=\"file:\\\\\\#", "a href=\"#");
        }

        res = new StrSubstitutor(props).replace(res);

        // write
        logger.info(MessageFormat.format("Write results to: {0}", path + out));
        FileUtils.writeStringToFile(new File(path + out), res);

    }

}
