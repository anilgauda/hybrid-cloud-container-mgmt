package ie.ncirl.container.manager.app.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Uses AES encryption to encrypt/decrypt strings
 * Tutorial Referred: https://www.javacodegeeks.com/2018/03/aes-encryption-and-decryption-in-javacbc-mode.html
 * Uses Base64 enc/dec so that encrypted values are stored and retrieved properly from the database
 */
@Component
@Slf4j
public class CryptUtil {

    @Value("${app.vm.aes.key}")
    private String appVmAESKey;

    @Value("${app.vm.aes.vector}")
    private String appVmAESVector;

    /**
     * Uses AES encryption with key stored in application.properties to encrypt incoming bytes
     *
     * @param bytes input bytes
     * @return encrypted string
     * @throws RuntimeException Wrapped application exception
     */
    public String encryptBytes(byte[] bytes) throws RuntimeException {
        try {
            Cipher cipher = getAESCipher(Cipher.ENCRYPT_MODE);
            byte[] encryptedData = cipher.doFinal(bytes);
            return new String(Base64.encodeBase64(encryptedData));
        } catch (Exception e) {
            log.error("Exception while encryption", e);
            throw new RuntimeException("Unable to encrypt data", e);
        }
    }

    /**
     * Uses AES encryption with key stored in application.properties to encrypt incoming bytes
     *
     * @param encBytes input bytes encrypted using AES encryption
     * @return encrypted string
     * @throws RuntimeException Wrapped application exception
     */
    public String decryptBytes(byte[] encBytes) throws RuntimeException {
        try {
            Cipher cipher = getAESCipher(Cipher.DECRYPT_MODE);
            return new String(cipher.doFinal(Base64.decodeBase64(encBytes)));
        } catch (Exception e) {
            log.error("Exception while decryption", e);
            throw new RuntimeException("Unable to decrypt data", e);
        }
    }

    public String encrypt(String data) throws RuntimeException {
        return encryptBytes(data.getBytes());
    }

    public String decrypt(String data) throws RuntimeException {
        return decryptBytes(data.getBytes());
    }

    /**
     * Creates an AES Cipher object for encryption/decryption
     *
     * @param mode ENCRYPT or DECRYPT ?
     * @return Cipher
     * @throws Exception any exception while creating cipher
     */
    private Cipher getAESCipher(int mode) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(appVmAESVector.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec keySpec = new SecretKeySpec(appVmAESKey.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(mode, keySpec, iv);
        return cipher;
    }

}
