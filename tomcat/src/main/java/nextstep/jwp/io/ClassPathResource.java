package nextstep.jwp.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ClassPathResource {

    public String getFileContents(final String url) {
        URL resource = getClass().getClassLoader().getResource("static" + url);
        try {
            File file = new File(resource.getFile());
            byte[] fileContents = Files.readAllBytes(file.toPath());
            return new String(fileContents);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
