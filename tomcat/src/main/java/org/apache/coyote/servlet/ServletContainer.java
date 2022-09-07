package org.apache.coyote.servlet;

import java.util.HashSet;
import java.util.Set;

import org.apache.coyote.http11.SessionFactory;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.ResourceSearcher;
import org.apache.coyote.servlet.servlets.HelloWorldAbstractServlet;
import org.apache.coyote.servlet.servlets.LoginAbstractServlet;
import org.apache.coyote.servlet.servlets.RegisterAbstractServlet;
import org.apache.coyote.servlet.servlets.ResourceAbstractServlet;
import org.apache.coyote.servlet.servlets.AbstractServlet;

public class ServletContainer {

    private static final ServletContainer SERVLET_CONTAINER = new ServletContainer();

    private static final Set<Mapping> MAPPINGS = new HashSet<>();

    private final SessionFactory sessionFactory;
    private final ResourceAbstractServlet resourceServlet;

    private ServletContainer() {
        sessionFactory = SessionFactory.init();
        resourceServlet = new ResourceAbstractServlet(sessionFactory);
    }

    public static ServletContainer init() {
        final ServletContainer servletContainer = SERVLET_CONTAINER;

        mapUrlToServlet(new HelloWorldAbstractServlet(servletContainer.sessionFactory), "/");
        mapUrlToServlet(new LoginAbstractServlet(servletContainer.sessionFactory), "/login");
        mapUrlToServlet(new RegisterAbstractServlet(servletContainer.sessionFactory), "/register");

        return servletContainer;
    }

    public HttpResponse service(final HttpRequest httpRequest) {
        if (isResource(httpRequest)) {
            return resourceServlet.service(httpRequest);
        }

        final AbstractServlet abstractServlet = search(httpRequest);
        return abstractServlet.service(httpRequest);
    }

    private static void mapUrlToServlet(final AbstractServlet abstractServlet, final String url) {
        final Mapping mapping = new Mapping(abstractServlet, url);

        validateMappingIsNew(mapping);
        MAPPINGS.add(mapping);
    }

    private static void validateMappingIsNew(final Mapping mapping) {
        if (MAPPINGS.contains(mapping)) {
            throw new IllegalArgumentException(String.format("중복적으로 매핑되었습니다. [%s]", mapping));
        }
    }

    private boolean isResource(final HttpRequest httpRequest) {
        return ResourceSearcher.getInstance().isFile(httpRequest.getUrl());
    }

    private AbstractServlet search(final HttpRequest httpRequest) {
        final String url = httpRequest.getUrl();

        return MAPPINGS.stream()
            .filter(mapping -> mapping.isMapping(url))
            .map(Mapping::getServlet)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("매핑되지 않은 url 입니다. [%s]", url)));
    }
}
