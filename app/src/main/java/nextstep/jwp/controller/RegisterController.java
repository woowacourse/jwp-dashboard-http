package nextstep.jwp.controller;

import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.StaticFileReader;
import nextstep.jwp.model.User;

public class RegisterController implements Controller {

    private static final String REGISTER_URI = "/register";

    @Override
    public HttpResponse get(HttpRequest request) {
        StaticFileReader staticFileReader = new StaticFileReader();
        String htmlOfRegister = staticFileReader.read("register.html");
        return new HttpResponse(HttpStatus.OK, htmlOfRegister);
    }

    @Override
    public HttpResponse post(HttpRequest request) {
        Map<String, String> formData = request.extractFormData();
        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");
        if (Objects.nonNull(account) && Objects.nonNull(password) && Objects.nonNull(email)) {
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
            httpResponse.putHeader("Location", "/index.html");
            return httpResponse;
        }
        return new HttpResponse(HttpStatus.BAD_REQUEST);
    }

    @Override
    public boolean isSatisfiedBy(String httpUriPath) {
        return httpUriPath.equals(REGISTER_URI);
    }
}
