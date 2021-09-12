package nextstep.jwp.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.exception.ResourceNotFoundException;

public class FileReader {

    private static final String STATIC_RESOURCE = "./static";

    public static String file(String uri) throws Exception {
        ContentType contentType = ContentType.findBy(uri);
        final URL resource = FileReader.class
                .getClassLoader()
                .getResource(STATIC_RESOURCE + withExtension(contentType, uri));

        checkResourceNotExist(resource);

        final Path path = new File(resource.getFile()).toPath();
        return Files.readString(path);
    }

    private static void checkResourceNotExist(URL resource) throws ResourceNotFoundException {
        if (Objects.isNull(resource)) {
            throw new ResourceNotFoundException("해당하는 파일을 찾을 수 없습니다.");
        }
    }

    private static String withExtension(ContentType contentType, String uri) {
        if (contentType.isNone()) {
            return uri + ".html";
        }
        return uri;
    }
}
