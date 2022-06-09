package com.jinwon.ssoauth.infra.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 네트워크 관련 Util
 */
@UtilityClass
public class NetworkUtil {

    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";

    private static final String REST = ",";

    public String getClientIp(HttpServletRequest request) {
        final String clientIp = request.getHeader(X_FORWARDED_FOR);

        return Optional.ofNullable(clientIp)
                .map(ip -> ip.split(REST))
                .map(List::of)
                .orElseGet(Collections::emptyList)
                .stream()
                .findFirst()
                .map(String::trim)
                .orElse(StringUtils.EMPTY);
    }


}
