package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.model.request.Request;
import nextstep.jwp.model.response.Response;

public class StaticFileController extends AbstractController {

    @Override
    public Response doGet(Request request) throws IOException {
        final String responseBody = createResponseBody(request.getPath());
        return staticFileMessage(request, request.getFileType(), responseBody);
    }
}
