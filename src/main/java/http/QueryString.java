package http;

public class QueryString {
    private String key;
    private String value;

    public QueryString(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
