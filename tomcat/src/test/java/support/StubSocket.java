package support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class StubSocket extends Socket {

    private final String request;
    private final ByteArrayOutputStream outputStream;

    public StubSocket(final String request) {
        this.request = request;
        this.outputStream = new ByteArrayOutputStream();
    }

    public StubSocket() {
        this("GET / HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
    }

    @Override
    public InetAddress getInetAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (final UnknownHostException ignored) {
            return null;
        }
    }

    @Override
    public int getPort() {
        return 8080;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(request.getBytes());
    }

    @Override
    public OutputStream getOutputStream() {
        return new OutputStream() {
            @Override
            public void write(final int b) {
                outputStream.write(b);
            }
        };
    }

    public String output() {
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
