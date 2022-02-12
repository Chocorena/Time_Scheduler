package G13.security;

import G13.RegisterController;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncodeDecode extends RegisterController  {
    private static final String SECRET_KEY = "Secret_Key_For_TimeScheduler";
    private static final String SALT = "Salt_For_Time_Scheduler";

    /**
     * Generation of 16 bytes with IV length
     * Get the Algorithm (PBKDF2-HMAC-SHA256) which encodes the password
     * The combination of the Secret-Key and the Salt with the length of 256 (16^2) and 65536 iterations describes our PBE-Key which we us for encryption
     * Generate our private key with secretKeyFactory and PBEKeySpec
     * The encryption takes place with the AES algorithm
     * Our key is xored byte by byte
     * Takes our password and encrypts it
     * @param password
     * @return encryptedPassword
     * @author Ercan
     */
    public static String encodePW (String password) {
        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec keySpec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            String encryptedPassword = Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));

            return encryptedPassword;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generation of 16 bytes with IV length
     * Get the Algorithm (PBKDF2-HMAC-SHA256) which encodes the password
     * The combination of the Secret-Key and the Salt with the length of 256 (16^2) and 65536 iterations describes our PBE-Key which we us for encryption
     * Generate our private key with secretKeyFactory and PBEKeySpec
     * The encryption takes place with the AES algorithm
     * Our key is xored byte by byte
     * Takes our password and decrypts it
     * @param encryptedPassword
     * @return decodedPassword
     * @author Ercan
     */
    public static String decodePW (String encryptedPassword) {
        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec keySpec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(),65536, 256);
            SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            String decodedPassword = String.valueOf((cipher.doFinal(Base64.getDecoder().decode(encryptedPassword))));

            return decodedPassword;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
