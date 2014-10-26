package tools;

import com.esotericsoftware.yamlbeans.YamlReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import settings.Config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public Config config;

    private Charset charset = StandardCharsets.UTF_8;

    public void loadConfig(String configPath) throws IOException, URISyntaxException {
        YamlReader reader = new YamlReader(
                new FileReader(new File(ClassLoader.getSystemResource(configPath).toURI())));
        Object object = reader.read();
        Map map = (Map) object;
        ArrayList<Config> configs = (ArrayList<Config>) map.get("config");
        config = configs.get(0);
    }

    public void prepareContent(String indexPath, String templatePath) throws IOException, URISyntaxException {

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("theme", config.getTheme());
        valuesMap.put("width", "" + config.getWidth());
        valuesMap.put("height", "" + config.getHeight());

        String raw = IOUtils.toString(ClassLoader.getSystemResource(templatePath).toURI(), charset);
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        FileUtils.writeStringToFile(new File(ClassLoader.getSystemResource(indexPath).toURI()), sub.replace(raw));

    }

}
