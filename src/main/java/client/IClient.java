package client;

import java.util.Map;

public interface IClient<T> {
    T getData(String host, int port, String endPoint);
    T postData(String host, int port, String endPoint, Object postBody, Map<String, String> headers);
    T putData(String host, int port, String endPoint, Object postBody, Map<String, String> headers);
}
