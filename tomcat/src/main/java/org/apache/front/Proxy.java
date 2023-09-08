package org.apache.front;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.ResponseEntity;

public class Proxy {

    private final StaticController staticController;

    private final DynamicController dynamicController;

    public Proxy() {
        this.staticController = new StaticController();
        this.dynamicController = new DynamicController();
    }

    public ResponseEntity process(Request request){
        if(request.isStatic()){
            return doProcess(staticController, request);
        }
        return doProcess(dynamicController, request);
    }

    private ResponseEntity doProcess(FrontController frontController, Request request) {
        return frontController.process(request);
    }

}