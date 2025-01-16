package com.example.lkdientu.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlUtils {
    public static String encodeUrlParam(String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }
}
