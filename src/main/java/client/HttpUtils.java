package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpUtils {
    private ObjectMapper objectMapper;
    public static HttpUtils instance;
    private static final Set<Integer> OK_SET = ImmutableSet.of(
            javax.ws.rs.core.Response.Status.OK.getStatusCode(),
            javax.ws.rs.core.Response.Status.CREATED.getStatusCode());
    private static final Set<Integer> CREATED_SET = ImmutableSet.of(
            javax.ws.rs.core.Response.Status.OK.getStatusCode(),
            javax.ws.rs.core.Response.Status.CREATED.getStatusCode());
    private static final String ENCODING = "UTF-8";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ACCEPT = "ACCEPT";
    private static final String APPLICATION_JSON = "application/json";
    private  RequestHandler requestHandler;
    public HttpUtils() {
        this.objectMapper = new ObjectMapper();
        this.requestHandler = new RealRequestHandler();
    }
    public static class Response<T>
    {
        private final int statusCode;
        private final T value;
        private final String rawResponse;
        private final String statusPhrase;

        public Response(int statusCode, T value, String rawResponse, String statusPhrase)
        {
            this.statusCode = statusCode;
            this.value = value;
            this.rawResponse = rawResponse;
            this.statusPhrase = statusPhrase;
        }

        public String getRawResponse()
        {
            return rawResponse;
        }

        public int getStatusCode()
        {
            return statusCode;
        }

        public T getValue()
        {
            return value;
        }

        public String getStatusPhrase()
        {
            return statusPhrase;
        }
    }

    public <T> Response<T> get(String host, int port, String path, NameValueList params, TypeReference<T> type)
    {
        return get(host, port, path, params, type, new HashMap<>());
    }

    public <T> Response<T> get(String host, int port, String path, NameValueList params, TypeReference<T> type,
                               Map<String, String> headers)
    {
        HttpGet request = new HttpGet(combine(path, params));
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(1800000)
                .setConnectTimeout(1800000)
                .setConnectionRequestTimeout(1800000)
                .build();
        request.setConfig(requestConfig);
        headers.entrySet().forEach(header -> request.setHeader(header.getKey(), header.getValue()));
        return this.requestHandler.executeHttpRequest(host, port, request, null, OK_SET, type);
    }

    public static String combine(String path,  NameValueList parameters)
    {
        String params = parameters == null ? "" : URLEncodedUtils.format(parameters.build(), ENCODING);
        String paramStr = params.length() > 0 ? ("?" + params) : "";
        return path + paramStr;
    }
}
