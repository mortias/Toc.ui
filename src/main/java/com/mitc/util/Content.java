package com.mitc.util;

import com.mitc.Toc;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class Content {

    private final static Logger logger = Logger.getLogger(Content.class);
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
        logger.setLevel(Level.toLevel(settings.getLevel()));

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("theme", settings.getTheme());
        valuesMap.put("width", String.valueOf(settings.getWidth()));
        valuesMap.put("height", String.valueOf(settings.getHeight() - 60));
        valuesMap.put("theme", settings.getTheme());
        valuesMap.put("bin", settings.getSite() + "bin");

        // read
        String read = settings.getSite() + "html/" + templatePath;
        logger.info(MessageFormat.format(Toc.config.translate("read.template.from"), read));
        String raw = IOUtils.toString(new File(read).toURI(), charset);

        // parse
        String res = new StrSubstitutor(valuesMap).replace(raw).replace("a href=\"", "a href=\"file:\\\\\\");

        // write
        String write = settings.getSite() + "html" + settings.getPathSep() + indexPath;
        logger.info(MessageFormat.format(Toc.config.translate("write.results.to"), write));
        FileUtils.writeStringToFile(new File(write), res);

    }


}
