package nextstep.jwp.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.response.ContentType;
import nextstep.jwp.http.response.HttpResponse;

public class View {

    private static final View EMPTY = new View("");

    private final String content;
    private final ContentType contentType;

    public View(String content, ContentType contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public View(String content) {
        this(content, ContentType.empty());
    }

    public static View of(String content) {
        return new View(content, ContentType.PLAIN_UTF8);
    }

    public static View of(File file) throws IOException {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        String content = String.join("\n", Files.readAllLines(file.toPath())) + "\n";
        ContentType contentType = ContentType.parseFromExtension(extension);

        return new View(content, contentType);
    }

    public static View empty() {
        return EMPTY;
    }

    public String contentType() {
        return contentType.value();
    }

    public int contentLength() {
        return content.getBytes().length;
    }

    public String content() {
        return content;
    }

    public void render(ModelAndView modelAndView, HttpResponse httpResponse) {
        if(!content.isEmpty()){
            httpResponse.addHeader("Content-Type", contentType());
            httpResponse.addHeader("Content-Length", String.valueOf(contentLength()));
            httpResponse.setContent(content, contentType.value());
        }
    }
}
