package nextstep.jwp.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class FileConverter {

    private FileConverter() {
    }

    public static String fileToString(String uri) throws IOException {
        URL resource = FileConverter.class.getClassLoader().getResource("static" + uri);

        return Files.readString(
            new File(Objects.requireNonNull(resource).getFile()).toPath());
    }
}
