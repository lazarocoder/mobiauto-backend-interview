package com.mobiauto.util;

//import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Base64;

public final class HttpServletUtil {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic ";

    public static String getUsername() {
        var request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        var rawCredentials = decodeBase64(getHeader(request).replace(BASIC, ""));
        var credentials = rawCredentials.split(":");
        return credentials[0];
    }

    private static String decodeBase64(String base64) {
        byte[] decodeBytes = Base64.getDecoder().decode(base64);
        return new String(decodeBytes);
    }

    private static boolean isBasicAuthentication(HttpServletRequest request) {
        String header = getHeader(request);
        return header != null && header.startsWith(BASIC);
    }

    private static String getHeader(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }
}