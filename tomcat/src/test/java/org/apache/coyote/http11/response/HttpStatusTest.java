package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpStatusTest {

    @Test
    void getCodeAndMessage() {
        assertThat(HttpStatus.getStatusCodeAndMessage(200)).isEqualTo("200 OK");
    }
}