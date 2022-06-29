package com.jinwon.ssoauth.infra.utils;

import com.jinwon.ssoauth.web.exception.EncryptionException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 암복호화 유틸
 */
@Slf4j
@UtilityClass
public class EncryptionUtil {

    private final String ALGORITHM = "AES";
    private final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private final String ENCRYPT_COMPONENT_ROLE = "encrypt_component_role";

    private final SecretKeySpec secretKey;
    private final IvParameterSpec ivParameter;

    static {
        final byte[] role = Optional.ofNullable(System.getProperty(ENCRYPT_COMPONENT_ROLE))
                .map(String::getBytes)
                .orElseThrow(EncryptionException::new);

        secretKey = new SecretKeySpec(role, ALGORITHM);
        ivParameter = new IvParameterSpec(secretKey.getEncoded());
    }

    /**
     * Encode Text 반환
     *
     * @param plainText 평문
     */
    public String encode(@NotNull String plainText) {
        if (StringUtils.isEmpty(plainText)) {
            return plainText;
        }

        try {
            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameter);
            final byte[] encryption = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(encryption);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new EncryptionException();
        }
    }

    /**
     * Plain Text 반환
     *
     * @param encodeText 인코딩 문자
     */
    public String decode(@NotNull String encodeText) {
        if (StringUtils.isEmpty(encodeText)) {
            return encodeText;
        }

        try {
            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameter);
            final byte[] decryption = Hex.decodeHex(encodeText);
            return new String(cipher.doFinal(decryption), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new EncryptionException();
        }
    }

}
