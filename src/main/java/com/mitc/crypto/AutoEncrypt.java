package com.mitc.crypto;

import com.mitc.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.MessageFormat;
import java.util.concurrent.Callable;

public class AutoEncrypt implements Callable<String> {

    public static Config config = Config.getInstance();
    public static FileEncryptor crypt = FileEncryptor.getInstance();

    private static final Logger logger = LogManager.getLogger(AutoEncrypt.class);

    private long waitTime;
    private String target;

    public AutoEncrypt(int timeInMillis, String target) {
        this.waitTime = timeInMillis;
        this.target = target;
    }

    @Override
    public String call() throws Exception {

        Thread.sleep(waitTime);

        logger.info(MessageFormat.format(
                config.translate("stopping.action"), target));

        if (config.getTocSettings().isEncrypted())
            target = crypt.encryptFile(new File(target));

        return Thread.currentThread().getName();

    }

}