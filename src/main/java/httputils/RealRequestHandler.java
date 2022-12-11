package httputils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * Thread safe class, all the variables it has are thread safe
 * and there's no state.
 */
@ThreadSafe
@Slf4j
class RealRequestHandler implements RequestHandler
{
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String clientName = "exodus-http-client";

    RealRequestHandler()
    {
        this.httpClient = new DefaultHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    private static void addHeaders(HttpRequestBase httpRequest, Map<String, String> headers)
    {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpRequest.addHeader(entry.getKey(), entry.getValue());
        }
    }

    public <T> HttpUtils.Response<T> executeHttpRequest(String hostName, int port, HttpRequestBase request,
                                                        Map<String, String> headers,
                                                        Set<Integer> expected, TypeReference<T> type)
    {
        HttpUtils.Response<T> result = null;
        try {
            log.info("URL {}, {}, {}, {}", hostName, port, request, headers);
            HttpHost host = new HttpHost(hostName, port);
            if (headers != null) {
                addHeaders(request, headers);
            }
            HttpResponse response = this.httpClient.execute(host, request);
            InputStream stream = response.getEntity().getContent();
            if (expected.contains(response.getStatusLine().getStatusCode())) {
                T value = null;
                if (type == null || response.getEntity().getContentLength() == 0) {
                    value = (T) EntityUtils.toString(response.getEntity());
                }
                else {
                    value = this.objectMapper.<T>readValue(stream, type);
                }
                result = new HttpUtils.Response<T>(response.getStatusLine().getStatusCode(), value, null,
                        response.getStatusLine().getReasonPhrase());
            }
            else {
                result = new HttpUtils.Response<T>(response.getStatusLine().getStatusCode(), null,
                        EntityUtils.toString(response.getEntity()), response.getStatusLine().getReasonPhrase());
            }
            log.info("Returning {}", result);
        }
        catch (IOException e) {
            log.error("Error while getting response " + hostName + ", " + port + ", " + request, e);
        }

        return result;
    }

    @Override
    public void stop()
    {
        this.httpClient.getConnectionManager().shutdown();
    }
}
