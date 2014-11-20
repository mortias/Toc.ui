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

    private BasicBinaryEncryptor binaryEncryptor = new BasicBinaryEncryptor();
    private Logger logger = LogManager.getLogger(FileEncryptor.class);

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

                FileInputStream inputStream = new FileInputStream(inputFile);
                byte[] inputBytes = new byte[(int) inputFile.length()];
                inputStream.read(inputBytes);

                byte[] outputBytes;
                if (cipherMode == Cipher.ENCRYPT_MODE)
                    outputBytes = binaryEncryptor.encrypt(inputBytes);
                else
                    outputBytes = binaryEncryptor.decrypt(inputBytes);

                FileOutputStream outputStream = new FileOutputStream(outputFile);
                outputStream.write(outputBytes);

                inputStream.close();
                outputStream.close();


            }
        } catch (IOException ex) {
            throw new RuntimeException(MessageFormat.format("Error cryptFile: {0}", ex.getMessage()));
        }

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
                        cryptFile(Cipher.ENCRYPT_MODE, file, new File(result));
                } else {
                    result = FilenameUtils.removeExtension(file.getAbsolutePath());
                    if (FilenameUtils.getExtension(file.getAbsolutePath()).equals("crypt"))
                        cryptFile(Cipher.DECRYPT_MODE, file, new File(result));
                }
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
        if (key != null && key.length() > 0)
            binaryEncryptor.setPassword(key);
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}





/*
*
* package com.mitc.util.crypto;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

public class FileEncryptor {

    private String key, path;
    private final String ALGORITHM = "AES";
    private final String TRANSFORMATION = "AES";

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

                Key secretKey = new SecretKeySpec(getKey().getBytes(), ALGORITHM);
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(cipherMode, secretKey);

                FileInputStream inputStream = new FileInputStream(inputFile);
                byte[] inputBytes = new byte[(int) inputFile.length()];
                inputStream.read(inputBytes);

                byte[] outputBytes = cipher.doFinal(inputBytes);

                FileOutputStream outputStream = new FileOutputStream(outputFile);
                outputStream.write(outputBytes);

                inputStream.close();
                outputStream.close();
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
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
        File[] root = new File(getPath()).listFiles();
        scanFiles(isEncrypted ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, root);
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


* */