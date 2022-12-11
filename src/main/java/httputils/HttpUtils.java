package httputils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpUtils {
    private static final Set<Integer> OK_SET = ImmutableSet.of(javax.ws.rs.core.Response.Status.OK.getStatusCode(), javax.ws.rs.core.Response.Status.CREATED.getStatusCode());
    private static final Set<Integer> CREATED_SET = ImmutableSet.of(javax.ws.rs.core.Response.Status.OK.getStatusCode(), javax.ws.rs.core.Response.Status.CREATED.getStatusCode());
    private static final String ENCODING = "UTF-8";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ACCEPT = "ACCEPT";
    private static final String APPLICATION_JSON = "application/json";
    public static HttpUtils instance;
    private final ObjectMapper objectMapper;
    private final RequestHandler requestHandler;

    public HttpUtils() {
        this.objectMapper = new ObjectMapper();
        this.requestHandler = new RealRequestHandler();
    }

    public static String combine(String path, NameValueList parameters) {
        String params = parameters == null ? "" : URLEncodedUtils.format(parameters.build(), ENCODING);
        String paramStr = params.length() > 0 ? ("?" + params) : "";
        return path + paramStr;
    }

    public <T> Response<T> get(String host, int port, String path, NameValueList params, TypeReference<T> type) {
        return get(host, port, path, params, type, new HashMap<>());
    }

    public <T> Response<T> get(String host, int port, String path, NameValueList params, TypeReference<T> type, Map<String, String> headers) {
        HttpGet request = new HttpGet(combine(path, params));
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(1800000).setConnectTimeout(1800000).setConnectionRequestTimeout(1800000).build();
        request.setConfig(requestConfig);
        headers.entrySet().forEach(header -> request.setHeader(header.getKey(), header.getValue()));
        return this.requestHandler.executeHttpRequest(host, port, request, null, OK_SET, type);
    }

    public <T> Response<T> putBody(String host, int port, String path, NameValueList params, Object postBody, TypeReference<T> type, Map<String, String> headers)
    {
        HttpPut request = new HttpPut(combine(path, params));
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(1800000)
                .setConnectTimeout(1800000)
                .setConnectionRequestTimeout(1800000)
                .build();
        request.setConfig(requestConfig);
        setEntity(request, postBody);
        request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.setHeader(ACCEPT, APPLICATION_JSON);
        headers.forEach(request::setHeader);
        return this.requestHandler.executeHttpRequest(host, port, request, null, CREATED_SET, type);
    }

    public <T> Response<T> postBody(String host, int port, String path, Object postBody, TypeReference<T> type, Map<String, String> headers)
    {
        HttpPost request = new HttpPost(path);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(1800000)
                .setConnectTimeout(1800000)
                .setConnectionRequestTimeout(1800000)
                .build();
        request.setConfig(requestConfig);
        request.setHeader("Content-Type", "application/json");
        headers.entrySet().forEach(header -> request.setHeader(header.getKey(), header.getValue()));
        setEntity(request, postBody);
        return this.requestHandler.executeHttpRequest(host, port, request, null, CREATED_SET, type);
    }

    private void setEntity(HttpEntityEnclosingRequestBase request, Object obj)
    {
        try {
            StringWriter writer = new StringWriter();
            this.objectMapper.writeValue(writer, obj);
            request.setEntity(new StringEntity(writer.getBuffer().toString()));
        }
        catch (IOException e) {
            throw new RuntimeException("Error while serializing: " + obj);
        }
    }
    /**
     * Will only have either of value / rawResponse.
     * value would be populated when the return status is in expected states.
     * If not in expected states, will populate raw response string.
     */
    public static class Response<T> {
        private final int statusCode;
        private final T value;
        private final String rawResponse;
        private final String statusPhrase;

        public Response(int statusCode, T value, String rawResponse, String statusPhrase) {
            this.statusCode = statusCode;
            this.value = value;
            this.rawResponse = rawResponse;
            this.statusPhrase = statusPhrase;
        }

        public String getRawResponse() {
            return rawResponse;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public T getValue() {
            return value;
        }

        public String getStatusPhrase() {
            return statusPhrase;
        }
    }
}
