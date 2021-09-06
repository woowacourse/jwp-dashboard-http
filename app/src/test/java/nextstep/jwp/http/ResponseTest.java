package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ResponseTest")
class ResponseTest {

    @Test
    @DisplayName("변경할 주소를 넣으면 302 리스폰스가 반환된다.")
    void create302Found() {
        Response response = Response.create302Found("/index.html");
        List<String> expected = foundExpected();

        testCheck(response, expected);
    }

    @Test
    @DisplayName("문제발생시 에러메시지를 반환한다.")
    void createErrorRequest() {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Response response = new Response();
        response.setErrorResponse("error", httpStatus);
        List<String> expected = new ArrayList<>();
        expected.add(
            String.format("HTTP/1.1 %d %s", httpStatus.getStatus(), httpStatus.getStatusMessage()));
        expected.add("error");

        testCheck(response, expected);
    }

    @Test
    @DisplayName("200 반환 리스폰스로 변경한다.")
    void set200OK() {
        Response response = new Response();
        Map<String, String> header = new HashMap<>();
        header.put("Accept", "application/javascript");
        RequestHeader requestHeader = new RequestHeader(header);
        Request request = new Request.Builder()
            .header(requestHeader)
            .build();
        List<String> expected = new ArrayList<>();
        HttpStatus httpStatus = HttpStatus.OK;
        expected.add(
            String.format("HTTP/1.1 %d %s", httpStatus.getStatus(), httpStatus.getStatusMessage()));
        expected.add("Content-Type: application/javascript;charset=utf-8");
        expected.add("Content-Length: 4");
        expected.add("body");

        response.set200OK(request, "body");

        testCheck(response, expected);
    }

    @Test
    @DisplayName("302 반환 리스폰스로 변경한다.")
    void set302Found() {
        Response response = new Response();
        List<String> expected = foundExpected();

        response.set302Found("/index.html");

        testCheck(response, expected);
    }

    @Test
    @DisplayName("헤더를 추가한다.")
    void addHeader() {
        Response response = new Response();
        response.addHeader("test", "pass");

        String expected = "test: pass";

        assertThat(response.message()).contains(expected);
    }

    private void testCheck(Response response, List<String> expected) {
        for (String date : expected) {
            assertThat(response.message()).contains(date);
        }
    }

    private List<String> foundExpected() {
        List<String> expected = new ArrayList<>();
        expected.add("HTTP/1.1 302 Found");
        expected.add("Location: /index.html");
        return expected;
    }
}