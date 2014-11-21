package com.mitc.util.crypto;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.util.binary.BasicBinaryEncryptor;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

public class FileEncryptor {

    private String key, path;
    private static FileEncryptor instance = null;

    private BasicBinaryEncryptor binaryEncryptor;
    private Logger logger = LogManager.getLogger(FileEncryptor.class);

    public static FileEncryptor getInstance() {
        if (instance == null) {
            instance = new FileEncryptor();
        }
        return instance;
    }

    private boolean cryptFile(int cipherMode, File inputFile, File outputFile) {

        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            if (inputFile.exists() && !inputFile.isDirectory()) {

                logger.trace(inputFile.getName() + (cipherMode == 1 ? " >> " : " << ") + outputFile.getName());

                inputStream = new FileInputStream(inputFile);
                byte[] inputBytes = new byte[(int) inputFile.length()];
                inputStream.read(inputBytes);
                inputStream.close();

                byte[] outputBytes;
                if (cipherMode == Cipher.ENCRYPT_MODE)
                    outputBytes = binaryEncryptor.encrypt(inputBytes);
                else
                    outputBytes = binaryEncryptor.decrypt(inputBytes);

                outputStream = new FileOutputStream(outputFile);
                outputStream.write(outputBytes);
                outputStream.close();

                if (!inputFile.delete())
                    logger.error(MessageFormat.format("Could not delete file: {0}", inputFile.getAbsolutePath()));

                return true;
            }
        } catch (IOException ex) {
            throw new RuntimeException(
                    MessageFormat.format("Error cryptFile: {0}", ex.getMessage()));
        } finally {

            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException ex) {
                // ignore;
            }
            try {
                assert outputStream != null;
                outputStream.close();
            } catch (IOException ex) {
                // ignore;
            }
        }
        return false;
    }

    public String handleFile(Path path, boolean encrypt) {
        try {
            File file = path.toFile();
            if (file.exists()) {
                String result;
                if (encrypt) {
                    result = file.getAbsolutePath() + ".crypt";
                    if (!FilenameUtils.getExtension(file.getAbsolutePath()).equals("crypt") &&
                            !file.getName().toLowerCase().contains("readme"))
                        return cryptFile(Cipher.ENCRYPT_MODE, file, new File(result)) ? result : "";
                } else {
                    result = FilenameUtils.removeExtension(file.getAbsolutePath());
                    if (FilenameUtils.getExtension(file.getAbsolutePath()).equals("crypt"))
                        return cryptFile(Cipher.DECRYPT_MODE, file, new File(result)) ? result : "";
                }
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return "";
    }

    // scan the site root to encrypt / decrypt the bin folder
    public void scanFiles(boolean isEncrypted) {
        if (getKey() != null && getKey().trim().length() > 0) {
            try {
                Files.walk(Paths.get(getPath()))
                        .filter((p) -> !p.toFile().isDirectory())
                        .forEach(p -> handleFile(p, isEncrypted));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if (key != null && key.length() > 0) {
            binaryEncryptor = new BasicBinaryEncryptor();
            binaryEncryptor.setPassword(key);
        }
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}