package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.coyote.exception.FileNotExistException;

public class ViewResolver {

    private static final String STATIC_RESOURCE_PATH = "static";

    public static HttpResponse staticFileRequest(HttpRequest httpRequest) {

        return staticFileRequest(httpRequest.getUri());
    }

    public static HttpResponse staticFileRequest(String fileName) {
        try {
            Path filePath = findFilePath(fileName);
            String content = new String(Files.readAllBytes(filePath));

            String contentType = FileExtension.findContentType(fileName);
            return new HttpResponse(HttpStatus.OK, contentType, content);
        } catch (IOException | FileNotExistException e) {
            return new HttpResponse(HttpStatus.NOT_FOUND, null, "");
        }
    }

    private static Path findFilePath(String fileName) {
        try {
            return Path.of(Objects.requireNonNull(FrontController.class.getClassLoader()
                    .getResource(STATIC_RESOURCE_PATH + fileName)).getPath());
        } catch (NullPointerException e) {
            throw new FileNotExistException(fileName + " 파일이 존재하지 않습니다.");
        }
    }
}
