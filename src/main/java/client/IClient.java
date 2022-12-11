package client;

public interface IClient<T> {
    T getData(String host, int port, String endPoint);
}
