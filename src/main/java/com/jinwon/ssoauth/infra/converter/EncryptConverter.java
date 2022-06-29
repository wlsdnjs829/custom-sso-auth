package com.jinwon.ssoauth.infra.converter;

import com.jinwon.ssoauth.infra.utils.EncryptionUtil;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;

/**
 * 암복호화 컨버터
 */
@Component
public class EncryptConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return EncryptionUtil.encode(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return EncryptionUtil.decode(dbData);
    }

}
