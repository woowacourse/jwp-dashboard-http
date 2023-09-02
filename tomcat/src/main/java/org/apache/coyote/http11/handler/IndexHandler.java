package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.ContentTypeParser;
import org.apache.coyote.http11.httpmessage.*;

public class IndexHandler extends Handler {

    @Override
    public Response handle(Request request) throws IOException {
        String absolutePath = "index.html";

        String resource = findResourceWithPath(absolutePath);
        Headers headers = new Headers(Map.of(
                "Content-Type", ContentTypeParser.parse(absolutePath),
                "Content-Length", String.valueOf(resource.getBytes().length)
        ));
        ResponseBody responseBody = new ResponseBody(resource);

        return Response.from(request.getHttpVersion(), HttpStatus.OK,
                headers,responseBody);
    }
}
