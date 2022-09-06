package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.constant.HttpMethods;

public class Http11Request {

    private final String method;
    private final String url;
    private final Map<String, String> header;
    private final String body;

    public Http11Request(String method, String url, Map<String, String> header, String body) {
        this.method = method;
        this.url = url;
        this.header = header;
        this.body = body;
    }

    public boolean isResource() {
        return getUrl().endsWith(".html") || url.endsWith(".css") || url.endsWith(".js");
    }

    public boolean hasCookie() {
        return header.containsKey("Cookie");
    }

    public Cookie getCookies() {
        String rawCookie = header.get("Cookie");
        Cookie cookie = new Cookie();

        if (rawCookie == null) {
            return cookie;
        }

        String[] eachCookies = rawCookie.split(";");
        for (String eachCookie : eachCookies) {
            String[] params = eachCookie.split("=");
            String cookieKey = params[0].strip();
            String cookieValue = params[1].strip();
            cookie.setCookie(cookieKey, cookieValue);
        }

        return cookie;
    }

    public Map<String, String> getBody() {
        Map<String, String> bodies = new HashMap<>();
        String[] rawBodies = body.split("&");

        for (String rawBody : rawBodies) {
            String[] params = rawBody.split("=");
            if (params.length != 2) {
                continue;
            }
            String paramName = params[0];
            String paramValue = params[1];

            bodies.put(paramName, paramValue);
        }

        return bodies;
    }

    public String getUrl() {
        int queryIndex = url.indexOf("?");
        if (queryIndex == -1) {
            queryIndex = url.length();
        }

        return url.substring(0, queryIndex);
    }

    public Map<String, String> getQueryString() {
        int queryIndex = url.indexOf("?");
        if (queryIndex == -1) {
            return Collections.emptyMap();
        }

        Map<String, String> queries = new HashMap<>();
        String rawQuery = url.substring(queryIndex + 1);
        for (String query : rawQuery.split("&")) {
            String[] temp = query.split("=");
            queries.put(temp[0].toLowerCase(), temp[1].toLowerCase());
        }

        return queries;
    }

    public HttpMethods getMethod() {
        return HttpMethods.toHttpMethod(method);
    }

    @Override
    public String toString() {
        return "Http11Request{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", header=" + header +
                ", body='" + body + '\'' +
                '}';
    }
}
