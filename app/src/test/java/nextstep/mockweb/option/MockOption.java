package nextstep.mockweb.option;

import nextstep.jwp.MockSocket;
import nextstep.jwp.RequestHandler;
import nextstep.jwp.core.mvc.FrontHandler;
import nextstep.mockweb.result.MockResult;
import nextstep.mockweb.request.RequestInfo;

public class MockOption {

    private final RequestInfo requestInfo;
    private final OptionInfo optionInfo;
    private final FrontHandler frontHandler;

    public MockOption(RequestInfo requestInfo, FrontHandler frontHandler) {
        this.requestInfo = requestInfo;
        this.optionInfo = new OptionInfo();
        this.frontHandler = frontHandler;
    }

    public MockResult result() {
        String result = doRequest(requestInfo, optionInfo);
        return new MockResult(result);
    }

    public MockOption logAll() {
        optionInfo.logAll();
        return this;
    }

    public MockOption addHeader(String key, String value) {
        optionInfo.addHeader(key, value);
        return this;
    }

    private String doRequest(RequestInfo requestInfo, OptionInfo optionInfo) {
        final MockSocket mockSocket = new MockSocket(requestInfo.asRequest());
        optionInfo.executeBeforeOption(requestInfo);
        new RequestHandler(mockSocket, frontHandler).run();
        final String result = mockSocket.output();
        optionInfo.executeAfterOption(result);
        return result;
    }
}
