package nextstep.jwp.controller;

import java.net.URL;
import java.util.Map;

import org.apache.coyote.http11.exception.EmptyBodyException;
import org.apache.coyote.http11.handler.HttpController;
import org.apache.coyote.http11.handler.HttpHandle;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.QueryParser;
import org.apache.coyote.http11.util.StaticResourceResolver;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterController implements HttpController {

	private static final Map<HttpMethod, HttpHandle> HANDLE_MAP = Map.of(
		HttpMethod.GET, request -> servingStaticResource(),
		HttpMethod.POST, RegisterController::registerProcess
	);
	private static final String END_POINT = "/register";
	private static final String STATIC_RESOURCE_FILE_PATH = "static/register.html";
	private static final String QUERY_ACCOUNT_KEY = "account";
	private static final String QUERY_PASSWORD_KEY = "password";
	private static final String QUERY_EMAIL_KEY = "email";
	private static final String REGISTER_SUCCESS_LOCATION = "http://localhost:8080/index.html";

	@Override
	public boolean isSupported(final HttpRequest request) {
		return request.equalPath(END_POINT)
			&& HANDLE_MAP.containsKey(request.getHttpMethod());
	}

	@Override
	public HttpResponse handleTo(final HttpRequest request) {
		final HttpMethod httpMethod = request.getHttpMethod();
		return HANDLE_MAP.get(httpMethod)
			.handle(request);
	}

	private static HttpResponse registerProcess(final HttpRequest request) {
		return request.getBody()
			.map(RegisterController::register)
			.orElseThrow(EmptyBodyException::new);
	}

	private static HttpResponse register(final String body) {
		final Map<String, String> parseQuery = QueryParser.parse(body);
		final User user = new User(
			parseQuery.get(QUERY_ACCOUNT_KEY),
			parseQuery.get(QUERY_PASSWORD_KEY),
			parseQuery.get(QUERY_EMAIL_KEY)
		);
		InMemoryUserRepository.save(user);
		return HttpResponse.redirect(REGISTER_SUCCESS_LOCATION);
	}

	private static HttpResponse servingStaticResource() {
		final URL url = RegisterController.class.getClassLoader()
			.getResource(STATIC_RESOURCE_FILE_PATH);
		return StaticResourceResolver.resolve(url);
	}
}
