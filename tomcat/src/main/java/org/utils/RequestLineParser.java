package org.utils;

import org.apache.coyote.common.FileExtension;

public class RequestLineParser {

    public static final String INDEX_PAGE_URL = "/";

    private static final int URL = 1;
    private static final String STATIC_RESOURCE_PATH_PREFIX = "static";
    private static final String INDEX_PAGE_PATH = "/index.html";

    public static String getStaticResourcePath(final String requestLine) {
        final String requestUrl = requestLine.split(" ")[URL];
        if (requestUrl.equals(INDEX_PAGE_URL)) {
            return STATIC_RESOURCE_PATH_PREFIX + INDEX_PAGE_PATH;
        }
        if (!FileExtension.hasExtension(requestUrl)) {
            return STATIC_RESOURCE_PATH_PREFIX + FileExtension.addHtmlExtension(requestUrl);
        }
        return STATIC_RESOURCE_PATH_PREFIX + requestUrl;
    }
}
