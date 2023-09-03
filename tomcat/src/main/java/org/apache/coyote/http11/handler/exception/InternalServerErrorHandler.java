package org.apache.coyote.http11.handler.exception;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class InternalServerErrorHandler implements ExceptionHandler {

	private static final String FILE_PATH = "static/500.html";

	@Override
	public boolean isSupported(final Exception exception) {
		return false;
	}

	@Override
	public HttpResponse handleTo() throws IOException {
		final URL url = getClass().getClassLoader()
			.getResource(FILE_PATH);
		final String body = new String(Files.readAllBytes(new File(url.getFile()).toPath()));
		return new HttpResponse(
			HttpStatusCode.INTERNAL_SERVER_ERROR_500,
			body,
			resolveHeader(body)
		);
	}
}
