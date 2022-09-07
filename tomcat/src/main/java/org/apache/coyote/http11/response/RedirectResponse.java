package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatus;

public class RedirectResponse extends HttpResponse {

    private RedirectResponse(final ContentType contentType,
                             final HttpStatus status,
                             final HttpHeaders unnecessaryHeaders,
                             final String responseBody) {
        super(contentType, status, unnecessaryHeaders, responseBody);
    }

    public static RedirectResponse of(final String redirectUri) {
        final Map<String, String> header = new HashMap<>();
        header.put(HttpHeaders.LOCATION, redirectUri);
        return new RedirectResponse(
                ContentType.TEXT_HTML,
                HttpStatus.FOUND,
                new HttpHeaders(header),
                ""
        );
    }
}
