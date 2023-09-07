package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.requestline.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

  @Override
  public HttpResponse service(final HttpRequest request) throws IOException {
    if (request.getMethod().equals(HttpMethod.GET)) {
      return doGet(request);
    }
    if (request.getMethod().equals(HttpMethod.POST)) {
      return doPost(request);
    }
    return null;
  }

  protected HttpResponse doPost(final HttpRequest request) {
    throw new UnsupportedOperationException();
  }

  protected HttpResponse doGet(final HttpRequest request) throws IOException {
    throw new UnsupportedOperationException();
  }
}