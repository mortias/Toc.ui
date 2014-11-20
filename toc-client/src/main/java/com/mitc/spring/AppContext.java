package com.mitc.spring;

import com.mitc.util.crypto.FileEncryptor;
import com.mitc.services.hawtio.HawtioService;
import com.mitc.services.restfull.RestService;
import com.mitc.services.system.SystemService;
import com.mitc.services.vertx.VertxService;
import com.mitc.javafx.Content;
import com.mitc.config.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("AppContext")
public class AppContext {

    @Autowired
    private RestService restService;

    @Autowired
    private VertxService vertxService;

    @Autowired
    private SystemService systemStatusService;

    @Autowired
    private HawtioService hawtioService;

    @Autowired
    private Content content;

    @Autowired
    private Settings settings;

    public AppContext() {
    }

    public void launch() {

        // encrypt / decrypt
        FileEncryptor crypt = FileEncryptor.getInstance();
        crypt.setKey(settings.getKey());
        crypt.setPath(settings.getRoot() + "site" + settings.getPathSep() + "bin");
        crypt.scanFiles(settings.isEncrypted());

        restService.init();
        vertxService.init();

        if (settings.getMonitoring())
            systemStatusService.init();

        if (settings.getHawtio())
            hawtioService.init();

        // load the site
        content.load(settings);

    }

    public Settings getSettings() {
        return settings;
    }

    public VertxService getVertxService() {
        return vertxService;
    }

}
