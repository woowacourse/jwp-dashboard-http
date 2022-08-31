package nextstep.jwp.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import nextstep.jwp.exception.ResourceNotFoundException;

public class ResourcesUtil {

    private static final String STATIC_RESOURCES_PATH = "/static/%s";

    private ResourcesUtil() {
    }

    public static String readResource(final String path, final Class<?> classes) {
        try {
            URL url = classes.getResource(String.format(STATIC_RESOURCES_PATH, path));
            Objects.requireNonNull(url);
            Path filePath = Paths.get(url.toURI());
            return Files.readString(filePath);
        } catch (URISyntaxException | IOException | NullPointerException e) {
            throw new ResourceNotFoundException("파일을 찾을 수 없습니다.");
        }
    }
}
