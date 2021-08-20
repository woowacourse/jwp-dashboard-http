package nextstep.learning.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * 입출력(I/O)은 하나의 시스템에서 다른 시스템으로 데이터를 이동 시킬 때 사용한다.<br>
 * 자바는 <b>스트림(Stream)</b>으로부터 I/O를 사용한다.<br>
 *
 * <b>InputStream</b>은 데이터를 읽고, <b>OutputStream</b>은 데이터를 쓴다.<br>
 * <b>FilterStream</b>은 InputStream이나 OutputStream에 연결될 수 있다.<br>
 * FilterStream은 읽거나 쓰는 데이터를 수정할 때 사용한다. (e.g. 암호화, 압축, 포맷 변환)<br>
 *<br>
 * <b>Stream</b>은 데이터를 <b>바이트</b>로 읽고 쓴다.<br>
 * 바이트가 아닌 <b>텍스트(문자)</b>를 읽고 쓰려면 <b>Reader와 Writer</b> 클래스를 연결한다.<br>
 * Reader, Writer는 다양한 문자 인코딩(e.g. UTF-8)을 처리할 수 있다.
 */
@DisplayName("Java I/O Stream 클래스 학습 테스트")
class IOStreamTest {

    /**
     * 자바의 기본 출력 클래스는 <b>java.io.OutputStream</b>이다.<br>
     * OutputStream의 <b>write(int b)</b> 메서드는 기반 메서드이다.<br><br>
     * <code>public abstract void write(int b) throws IOException;</code><br>
     */
    @Nested
    class OutputStream_학습_테스트 {

        /**
         * OutputStream은 다른 매체에 바이트로 데이터를 쓸 때 사용한다.<br>
         * OutputStream의 서브 클래스(subclass)는 특정 매체에 데이터를 쓰기 위해 <b>write(int b)</b> 메서드를 사용한다.<br>
         * 예를 들어, FilterOutputStream은 파일로 데이터를 쓸 때,<br>
         * DataOutputStream은 자바의 primitive type data를 다른 매체로 데이터를 쓸 때 사용한다.<br>
         * <br>
         * write 메서드는 데이터를 바이트로 출력하기 때문에 비효율적이다.<br>
         * <code>write(byte[] data)</code>와 <code>write(byte b[], int off, int len)</code> 메서드는<br>
         * 1바이트 이상을 한 번에 전송 할 수 있어 훨씬 효율적이다.<br>
         */
        @Test
        void OutputStream은_데이터를_바이트로_처리한다() throws IOException {
            byte[] bytes = {110, 101, 120, 116, 115, 116, 101, 112};
            final OutputStream outputStream = new ByteArrayOutputStream(bytes.length);

            outputStream.write(bytes);
            final String actual = outputStream.toString();

            assertThat(actual).isEqualTo("nextstep");
            outputStream.close();
        }

        /**
         * 효율적인 전송을 위해 스트림에서 버퍼링을 사용 할 수 있다.<br>
         * <b>BufferedOutputStream</b> 필터를 연결하면 버퍼링이 가능하다.<br>
         * <br>
         * 버퍼링을 사용하면 OutputStream을 사용할 때 flush를 사용하자.<br>
         * <b>flush()</b> 메서드는 버퍼가 아직 가득 차지 않은 상황에서 강제로 버퍼의 내용을 전송한다.<br>
         * Stream은 동기(synchronous)로 동작하기 때문에 버퍼가 찰 때까지 기다리면 데드락(deadlock) 상태가 되기 때문에 flush로 해제한다.<br>
         */
        @Test
        void BufferedOutputStream을_사용하면_버퍼링이_가능하다() throws IOException {
            final OutputStream outputStream = mock(BufferedOutputStream.class);

            outputStream.flush();

            verify(outputStream, atLeastOnce()).flush();
            outputStream.close();
        }

        /**
         * 스트림 사용이 끝나면 항상 <b>close()</b> 메서드를 호출하여 스트림을 닫는다.<br>
         * 장시간 스트림을 닫지 않으면 파일, 포트 등 다양한 리소스에서 누수(leak)가 발생한다.
         */
        @Test
        void OutputStream은_사용하고_나서_close_처리를_해준다() throws IOException {
            final OutputStream outputStream = mock(OutputStream.class);

            try (outputStream) {}

            verify(outputStream, atLeastOnce()).close();
        }
    }

    /**
     * 자바의 기본 입력 클래스는 <b>java.io.InputStream</b>이다.<br>
     * InputStream은 다른 매체로부터 바이트로 데이터를 읽을 때 사용한다.<br>
     * InputStream의 <b>read()</b> 메서드는 기반 메서드이다.<br><br>
     * <code>public abstract int read() throws IOException;</code><br>
     * <br>
     * InputStream의 서브 클래스(subclass)는 특정 매체에 데이터를 읽기 위해 read() 메서드를 사용한다.<br>
     */
    @Nested
    class InputStream_학습_테스트 {

        /**
         * read() 메서드는 매체로부터 단일 바이트를 읽는데, 0부터 255 사이의 값을 int 타입으로 반환한다.<br>
         * int 값을 byte 타입으로 변환하면 -128부터 127 사이의 값으로 변환된다.<br>
         * 그리고 Stream 끝에 도달하면 <b>-1</b>을 반환한다.<br>
         */
        @Test
        void InputStream은_데이터를_바이트로_읽는다() throws IOException {
            byte[] bytes = {-16, -97, -92, -87};
            final InputStream inputStream = new ByteArrayInputStream(bytes);

            final String actual = new String(inputStream.readAllBytes());

            assertThat(actual).isEqualTo("🤩");
            assertThat(inputStream.read()).isEqualTo(-1);
            inputStream.close();
        }

        /**
         * 스트림 사용이 끝나면 항상 <b>close()</b> 메서드를 호출하여 스트림을 닫는다.<br>
         * 장시간 스트림을 닫지 않으면 파일, 포트 등 다양한 리소스에서 누수(leak)가 발생한다.
         */
        @Test
        void InputStream은_사용하고_나서_close_처리를_해준다() throws IOException {
            final InputStream inputStream = mock(InputStream.class);

            try (inputStream) {}

            verify(inputStream, atLeastOnce()).close();
        }
    }

    /**
     * 필터는 필터 스트림, reader, writer로 나뉜다.<br>
     * 필터는 바이트를 다른 데이터 형식으로 변환 할 때 사용한다.<br>
     * reader, writer는 UTF-8, ISO 8859-1 같은 형식으로 인코딩된 텍스트를 처리하는 데 사용된다.
     */
    @Nested
    class FilterStream_학습_테스트 {

        /**
         * <b>BufferedInputStream</b>은 데이터 처리 속도를 높이기 위해 데이터를 버퍼에 저장한다.<br>
         * InputStream 객체를 생성하고 필터 생성자에 전달하면 필터에 연결된다.<br>
         * 버퍼 크기를 지정하지 않으면 버퍼의 기본 사이즈는 얼마일까?
         */
        @Test
        void 필터인_BufferedInputStream를_사용해보자() throws IOException {
            final String text = "필터에 연결해보자.";
            final InputStream inputStream = new ByteArrayInputStream(text.getBytes());
            final InputStream bufferedInputStream = new BufferedInputStream(inputStream);

            final byte[] actual = bufferedInputStream.readAllBytes();

            assertThat(bufferedInputStream).isInstanceOf(FilterInputStream.class);
            assertThat(actual).isEqualTo("필터에 연결해보자.".getBytes());
        }
    }

    /**
     * 자바의 기본 문자열은 UTF-16 유니코드 인코딩을 사용한다.
     * 바이트를 문자(char)로 처리하려면 인코딩을 신경 써야 한다.
     * InputStreamReader를 사용하면 지정된 인코딩에 따라 유니코드 문자로 변환할 수 있다.
     * reader, writer를 사용하면 입출력 스트림을 <b>바이트</b>가 아닌 <b>문자</b> 단위로 데이터를 처리하게 된다.
     */
    @Nested
    class InputStreamReader_학습_테스트 {

        /**
         * <b>InputStreamReader</b>를 사용해서 바이트를 문자(char)로 읽어온다.
         * 필터인 <b>BufferedReader</b>를 사용하면 <b>readLine</b> 메서드를 사용해서 문자열(String)을 한 줄 씩 읽어올 수 있다.
         */
        @Test
        void BufferedReader를_사용하여_문자열을_읽어온다() throws IOException {
            final String emoji = String.join("\r\n",
                    "😀😃😄😁😆😅😂🤣🥲☺️😊",
                    "😇🙂🙃😉😌😍🥰😘😗😙😚",
                    "😋😛😝😜🤪🤨🧐🤓😎🥸🤩",
                    "");
            final InputStream inputStream = new ByteArrayInputStream(emoji.getBytes());
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            final StringBuilder actual = new StringBuilder();
            while (reader.ready()) {
                actual.append(reader.readLine())
                        .append("\r\n");
            }

            assertThat(actual).hasToString(emoji);
        }
    }
}
