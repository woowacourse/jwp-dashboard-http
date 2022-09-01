package study;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import support.ApplicationContainer;
import support.FileUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 웹서버는 사용자가 요청한 html 파일을 제공 할 수 있어야 한다.
 * File 클래스를 사용해서 파일을 읽어오고, 사용자에게 전달한다.
 */
@DisplayName("File 클래스 학습 테스트")
class FileTest {

    ApplicationContainer applicationContainer = new ApplicationContainer();

    /**
     * File 객체를 생성하려면 파일의 경로를 알아야 한다.
     * 자바 애플리케이션은 resource 디렉터리에 정적 파일을 저장한다.
     * resource 디렉터리의 경로는 어떻게 알아낼 수 있을까?
     */
    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다() {
        final String fileName = "nextstep.txt";

        final String actual = new File(getClass().getClassLoader().getResource(fileName).getFile()).getName();

        assertThat(actual).endsWith(fileName);
    }

    /**
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다() throws URISyntaxException, FileNotFoundException {
        final String fileName = "nextstep.txt";

/*
        final Path path = Paths.get(Thread.currentThread().getClass().getClassLoader().getResource(fileName).toURI());
        final File file = path.toFile();
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        final List<String> actual = bufferedReader.lines().collect(Collectors.toCollection(LinkedList::new));
*/

        final FileUtils fileUtils = applicationContainer.getSingletonObject(FileUtils.class);
        final List<String> actual = fileUtils.readFileLines(fileName);

        assertThat(actual).containsOnly("nextstep");
    }
}
