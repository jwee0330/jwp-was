package http;

import java.util.*;

public class QueryStrings {
    private final List<QueryString> queryStrings;

    QueryStrings(List<QueryString> queryStrings) {
        this.queryStrings = queryStrings;
    }

    public static QueryStrings of(String path) {
        if ("".equals(path) || path == null) {
            return new QueryStrings(Collections.emptyList());
        }

        if (!path.contains("?")) {
            return new QueryStrings(Collections.emptyList());
        }

        final String[] pathValues = path.split("\\?");
        final String queryStringSource = pathValues[1];

        if (queryStringSource.contains("&")) {
            final String[] values = queryStringSource.split("&");
            return new QueryStrings(buildQueryStrings(values));
        }
        final String[] keyAndValue = queryStringSource.split("=");
        return new QueryStrings(Arrays.asList(new QueryString(keyAndValue)));
    }

    private static List<QueryString> buildQueryStrings(String[] values) {
        final List<QueryString> target = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            String[] keyAndValue = values[i].split("=");
            target.add(new QueryString(keyAndValue));
        }
        return target;
    }

    public List<QueryString> getQueryStrings() {
        return queryStrings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryStrings)) return false;
        QueryStrings that = (QueryStrings) o;
        return Objects.equals(getQueryStrings(), that.getQueryStrings());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQueryStrings());
    }

    @Override
    public String toString() {
        return "QueryStrings{" +
                "queryStrings=" + queryStrings +
                '}';
    }
}
