package com.mitc.crypto;

import com.mitc.util.Config;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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

public class Crypt {

    private final String ALGORITHM = "AES";
    private final String TRANSFORMATION = "AES";

    private final static Logger logger = Logger.getLogger(Crypt.class);
    public static Config config = Config.getInstance();

    private static Crypt instance = null;

    public static Crypt getInstance() {
        if (instance == null) {
            instance = new Crypt();
        }
        return instance;
    }

    private void cryptFile(int cipherMode, File inputFile, File outputFile) {

        try {
            if (inputFile.exists() && !inputFile.isDirectory()) {

                logger.setLevel(Level.toLevel(config.getSettings().getLevel()));
                logger.trace(inputFile.getName() + (cipherMode == 1 ? " >> " : " << ") + outputFile.getName());

                Key secretKey = new SecretKeySpec(config.getSettings().getKey().getBytes(), ALGORITHM);
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
            throw new RuntimeException(MessageFormat.format(
                    config.translate("error.cryptfile"), ex.getMessage()));
        }

    }

    public void scanFiles(int cipherMode, File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                logger.trace(MessageFormat.format(config.translate("scanning.directory"),
                        file.getAbsolutePath()));
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
                    logger.error(MessageFormat.format(
                            config.translate("could.not.delete.file"), file.getAbsolutePath()));
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
                    logger.error(MessageFormat.format(
                            config.translate("could.not.delete.file"), file.getAbsolutePath()));
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return "";
    }

    // scan the site root to encrypt / decrypt the bin folder
    public void init() {
        if (config.getSettings().isEncrypted()) {
            File[] root = new File(config.getSettings().getSite() + "bin").listFiles();
            scanFiles(Cipher.ENCRYPT_MODE, root);
        } else {
            File[] root = new File(config.getSettings().getSite() + "bin").listFiles();
            scanFiles(Cipher.DECRYPT_MODE, root);
        }
    }
}

