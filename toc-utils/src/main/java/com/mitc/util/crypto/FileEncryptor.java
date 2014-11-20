package com.mitc.util.crypto;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.util.binary.BasicBinaryEncryptor;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

public class FileEncryptor {

    private String key, path;
    private BasicBinaryEncryptor binaryEncryptor = new BasicBinaryEncryptor();

    private Logger logger = LogManager.getLogger(FileEncryptor.class);
    private static FileEncryptor instance = null;

    public static FileEncryptor getInstance() {
        if (instance == null) {
            instance = new FileEncryptor();
        }
        return instance;
    }

    private void cryptFile(int cipherMode, File inputFile, File outputFile) {

        try {
            if (inputFile.exists() && !inputFile.isDirectory()) {

                logger.trace(inputFile.getName() + (cipherMode == 1 ? " >> " : " << ") + outputFile.getName());
                binaryEncryptor.setPassword(getKey());

                byte[] outputBytes;
                if (cipherMode == Cipher.ENCRYPT_MODE)
                    outputBytes = binaryEncryptor.encrypt(IOUtils.toByteArray(new FileInputStream(inputFile)));
                else
                    outputBytes = binaryEncryptor.decrypt(IOUtils.toByteArray(new FileInputStream(inputFile)));

                IOUtils.write(outputBytes, new FileOutputStream(outputFile));

            }
        } catch (IOException ex) {
            throw new RuntimeException(MessageFormat.format("Error cryptFile: {0}", ex.getMessage()));
        }

    }

    public void scanFiles(int cipherMode, File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                logger.trace(MessageFormat.format("Scanning directory: {0}", file.getAbsolutePath()));
                scanFiles(cipherMode, file.listFiles());
            } else {
                try {
                    if (Cipher.ENCRYPT_MODE == cipherMode) {
                        encryptFile(file);
                    } else if (Cipher.DECRYPT_MODE == cipherMode) {
                        decryptFile(file);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String encryptFile(File file) {
        try {
            String result = file.getAbsolutePath() + ".crypt";
            if (!FilenameUtils.getExtension(file.getAbsolutePath()).equals("crypt") &&
                    !file.getName().toLowerCase().contains("readme")) {
                cryptFile(Cipher.ENCRYPT_MODE, file, new File(result));
                if (!file.delete())
                    logger.error(MessageFormat.format("Could not delete file: {0}", file.getAbsolutePath()));
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return "";
    }

    public String decryptFile(File file) {
        try {
            String result = FilenameUtils.removeExtension(file.getAbsolutePath());
            if (FilenameUtils.getExtension(file.getAbsolutePath()).equals("crypt")) {
                cryptFile(Cipher.DECRYPT_MODE, file, new File(result));
                if (!file.delete())
                    logger.error(MessageFormat.format("Could not delete file: {0}", file.getAbsolutePath()));
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return "";
    }

    // scan the site root to encrypt / decrypt the bin folder
    public void init(boolean isEncrypted) {
        if (getKey() != null && getKey().trim().length() > 0) {
            File[] root = new File(getPath()).listFiles();
            scanFiles(isEncrypted ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, root);
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

