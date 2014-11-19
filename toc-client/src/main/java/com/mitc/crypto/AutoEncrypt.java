package com.mitc.crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.MessageFormat;
import java.util.concurrent.Callable;

public class AutoEncrypt implements Callable<String> {

    private String target;
    private long waitTime;
    private boolean isEncrypted;

    public static FileEncryptor crypt = FileEncryptor.getInstance();
    private static final Logger logger = LogManager.getLogger(AutoEncrypt.class);

    public AutoEncrypt(int timeInMillis, String target, boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
        this.waitTime = timeInMillis;
        this.target = target;
    }

    @Override
    public String call() throws Exception {
        Thread.sleep(waitTime);
        logger.info(MessageFormat.format("Stopping action: {0}", target));
        if (isEncrypted)
            target = crypt.encryptFile(new File(target));
        return Thread.currentThread().getName();
    }

}