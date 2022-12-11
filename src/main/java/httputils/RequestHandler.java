package httputils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.Map;
import java.util.Set;

public interface RequestHandler {
    <T> HttpUtils.Response<T> executeHttpRequest(String hostName, int port, HttpRequestBase request, Map<String, String> headers, Set<Integer> expected, TypeReference<T> type);

    void stop();
}
