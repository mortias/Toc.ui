package com.mitc.util;

import com.mitc.Toc;
import com.mitc.dto.Settings;
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

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("theme", settings.getTheme());
        valuesMap.put("width", String.valueOf(settings.getWidth()));
        valuesMap.put("height", String.valueOf(settings.getHeight() - 60));
        valuesMap.put("theme", settings.getTheme());
        valuesMap.put("bin", settings.getSite() + "bin");
        valuesMap.put("server", settings.getServer());
        valuesMap.put("port", String.valueOf(settings.getPort()));

        // read
        String read = settings.getSite() + "html/" + templatePath;
        logger.info(MessageFormat.format(Toc.config.translate("read.template.from"), read));
        String raw = IOUtils.toString(new FileInputStream(new File(read)), charset.toString());

        // parse
        String res = new StrSubstitutor(valuesMap).replace(raw).replace("a href=\"", "a href=\"file:\\\\\\");

        // write
        String write = settings.getSite() + "html" + settings.getPathSep() + indexPath;
        logger.info(MessageFormat.format(Toc.config.translate("write.results.to"), write));
        FileUtils.writeStringToFile(new File(write), res);

    }


}
