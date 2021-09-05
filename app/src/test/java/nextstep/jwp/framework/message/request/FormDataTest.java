package nextstep.jwp.framework.message.request;

import nextstep.jwp.framework.message.MessageBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FormDataTest {

    @DisplayName("MessageBody 를 생성한다.")
    @Test
    void create() {
        // given
        Map<String, String> params = new LinkedHashMap<>();
        params.put("account", "ggyool");
        params.put("password", "password");
        params.put("email", "ggyool@never.com");

        // when
        FormData formData = new FormData(params);

        // then
        assertThat(formData.toMap()).containsExactlyEntriesOf(params);
    }

    @DisplayName("MessageBody 로 FormData 를 생성한다.")
    @Test
    void createFromBody() {
        // given
        Map<String, String> params = new LinkedHashMap<>();
        params.put("account", "ggyool");
        params.put("password", "password");
        params.put("email", "ggyool@never.com");

        String formDataMessage = "account=ggyool&password=password&email=ggyool@never.com";
        MessageBody messageBody = new MessageBody(formDataMessage);

        // when
        FormData formData = FormData.from(messageBody);

        // then
        assertThat(formData.toMap()).containsExactlyEntriesOf(params);
    }

    @DisplayName("equals 와 hashCode 검증")
    @Test
    void equalsAndHashCode() {
        // given
        Map<String, String> params = new LinkedHashMap<>();
        params.put("account", "ggyool");
        params.put("password", "password");
        params.put("email", "ggyool@never.com");

        FormData formData = new FormData(params);
        FormData otherFormData = new FormData(params);

        // then
        assertThat(formData).isEqualTo(otherFormData);
        assertThat(formData.hashCode()).isEqualTo(otherFormData.hashCode());
    }
}
