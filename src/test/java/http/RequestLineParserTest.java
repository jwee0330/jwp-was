package http;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineParserTest {

    @Test
    public void parseGet() {
        RequestLine requestLine = RequestLineParser.parse("GET /users HTTP/1.1");

        assertThat(requestLine).isEqualTo(RequestLine.of("GET", "/users", "HTTP/1.1"));
        assertThat(requestLine.getProtocol()).isEqualTo("HTTP/1.1");
    }

    @Test
    public void parsePost() {
        RequestLine requestLine = RequestLineParser.parse("POST /users HTTP/1.1");

        assertThat(requestLine).isEqualTo(RequestLine.of("POST", "/users", "HTTP/1.1"));
        assertThat(requestLine.getProtocol()).isEqualTo("HTTP/1.1");
    }

    @Test
    public void queryString() {
        String source = "GET /users?userId=javajigi&password=password&name=JaeSung HTTP/1.1";

        RequestLine requestLine = RequestLineParser.parse(source);

    }

    @Test
    public void 학습테스트() {
        String source = "/users?userId=javajigi&password=password&name=JaeSung";

        final String[] pathValues = source.split("//?");
        final String queryStrings = pathValues[1];

        assertThat(pathValues.length).isEqualTo(2);
    }


}
