package nextstep.jwp.http;

public enum HttpMethod {

    GET(),
    POST() {
        @Override
        public boolean isBody() {
            return true;
        }
    };

    public boolean isBody() {
        return false;
    }
}
