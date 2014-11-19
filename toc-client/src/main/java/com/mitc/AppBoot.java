package com.mitc;

import com.mitc.crypto.FileEncryptor;
import com.mitc.services.hawtio.HawtioService;
import com.mitc.services.rest.RestService;
import com.mitc.services.system.SystemStatusService;
import com.mitc.services.vertx.VertxService;
import com.mitc.toc.Config;
import com.mitc.toc.Content;
import com.mitc.toc.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("AppBoot")
public class AppBoot {

    @Autowired
    private RestService restService;

    @Autowired
    private VertxService vertxService;

    @Autowired
    private SystemStatusService systemStatusService;

    @Autowired
    private HawtioService hawtioService;

    @Autowired
    private Config config;

    @Autowired
    private Content content;

    public AppBoot() {
    }

    private Settings settings;

    public void launch() {

        // load the yml file
        settings = config.load();

        // encrypt / decrypt
        FileEncryptor crypt = FileEncryptor.getInstance();
        crypt.setKey(settings.getKey());
        crypt.setPath(settings.getRoot() + "site" + settings.getPathSep() + "bin");
        crypt.init(settings.isEncrypted());

        restService.init(settings);
        vertxService.init(settings);

        if (settings.getMonitoring())
            systemStatusService.init(settings);

        if (settings.getHawtio())
            hawtioService.init(settings);

        // load the site
        content.load(settings);

    }

    public Settings getSettings() {
        return settings;
    }
}
