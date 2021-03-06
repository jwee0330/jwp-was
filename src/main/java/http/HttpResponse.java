package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static http.HttpHeader.COOKIE;
import static http.HttpHeader.SET_COOKIE;
import static java.util.Arrays.asList;

public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private static final String CARRIAGE_RETURN = "\r\n";
    private static final String JSESSIONID = "JSESSIONID";

    private final OutputStream out;
    private final DataOutputStream dos;

    private ResponseLine responseLine;
    private ResponseHeaders responseHeaders = new ResponseHeaders();
    private byte[] responseBody;
    private String sessionId;

    public HttpResponse(final OutputStream out) {
        this.out = out;
        this.dos = new DataOutputStream(out);
    }

    public void forward(final String path) {
        buildResponseLine(HttpStatus.OK);
        setResponseBody(path);
        print();
    }

    public void sendRedirect(final String requestPath) {
        buildResponseLine(HttpStatus.FOUND);
        responseHeaders.addHeader(HttpHeader.LOCATION, Arrays.asList(requestPath));
        setResponseBody(requestPath);
        print();
    }

    public void badRequest() {
        buildResponseLine(HttpStatus.BAD_REQUEST);
        setResponseBody("/error/4xx.html");
        print();
    }

    private void buildResponseLine(HttpStatus httpStatus) {
        this.responseLine = new ResponseLine(httpStatus.getCodeValue(httpStatus), httpStatus.getValue());
    }

    private void setResponseBody(final String requestPath) {
        if (responseBody != null) {
            return;
        }
        try {
            responseBody = loadFileFromClasspath(requestPath);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private byte[] loadFileFromClasspath(final String requestPath) throws IOException, URISyntaxException {
        logger.info("requestPath: " + requestPath);
        String prefix = "./static";

        if (requestPath.contains(".html")) {
            prefix = "./templates";
            responseHeaders.addHeader(HttpHeader.CONTENT_TYPE, asList("text/html;charset=utf-8"));
        } else if (requestPath.contains(".ico")) {
            responseHeaders.addHeader(HttpHeader.CONTENT_TYPE, asList("image/x-icon"));
        }
        byte[] bytes = FileIoUtils.loadFileFromClasspath(prefix + requestPath);
        return bytes;
    }

    private void print() {
        try {
            dos.writeBytes(responseLine.getResponseLine() + CARRIAGE_RETURN);
            dos.writeBytes(responseHeaders.getResponseHeaders() + CARRIAGE_RETURN);
            dos.writeBytes(CARRIAGE_RETURN);
            dos.write(responseBody, 0, responseBody.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void setTemplate(final String template) {
        this.responseBody = template.getBytes();
    }

    public void setCookie(final boolean cookie) {
        if (cookie) {
            List<String> loginedSign = asList("logined=true; Path=/; JSESSIONID=" + sessionId + ";");
            logger.debug("loginedSign: {}", loginedSign);
            responseHeaders.addHeader(SET_COOKIE, loginedSign);
            return;
        }
        List<String> unloginedSign = asList("logined=false; JSESSIONID=" + sessionId + ";");
        responseHeaders.addHeader(SET_COOKIE, unloginedSign);
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
        List<String> existingCookies = responseHeaders.get(COOKIE);
        List<String> existingWithoutLogined = existingCookies.stream()
                .filter(token -> !token.contains("logined"))
                .collect(Collectors.toList());
        List<String> cookies = asList(JSESSIONID + "=" + sessionId);
        cookies.addAll(existingWithoutLogined);
        responseHeaders.addHeader(SET_COOKIE, cookies);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "responseLine=" + responseLine +
                ", responseHeaders=" + responseHeaders.getResponseHeaders() +
                ", responseBody=" + responseBody +
                '}';
    }

}
