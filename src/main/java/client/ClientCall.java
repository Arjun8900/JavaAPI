package client;

import com.fasterxml.jackson.core.type.TypeReference;
import data.ClientCallData;
import httputils.HttpUtils;
import httputils.NameValueList;
import httputils.RetriableTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ClientCall<T> implements IClient<T> {
    private static final int RETRY_COUNT = 3;
    public static final int RETRY_INTERVAL = 2000;
    public static final HttpUtils httpUtils = new HttpUtils();

    public T getData(String host, int port, String endPoint) {
        T clientCallData = null;
        try {
            log.info("BADGER END POINT: host = {}, port = {}", host, port);

            HttpUtils.Response<T> response = (new RetriableTask<>(3, 2000, () -> {
                HttpUtils.Response<T> getResponse =  httpUtils.get(host, port, endPoint, new NameValueList(), new TypeReference<T>(){});
                if (getResponse == null || (getResponse.getStatusCode() != 200 && getResponse.getStatusCode() != 201)) {
                    log.warn("Retrying");
                    throw new RuntimeException("Retrying");
                }
                return getResponse;
            })).call();
            if (response == null) {
                throw new RuntimeException("Retrying");
            }
            clientCallData = response.getValue();
            log.info("BADGER: View Api Response = {}", clientCallData);

            return clientCallData;
        }
        catch (Exception ex) {
            log.error("ERROR calling Client and Error = ",  ex);
            ex.printStackTrace();
        }
        return  clientCallData;
    }

    public T putData(String host, int port, String endPoint, Object postBody, Map<String, String> headers) {
        T clientCallData = null;
        try {
            System.out.printf("END POINT: host = %s, endPoint = %s, port = %s%n", host, endPoint, port);
            HttpUtils.Response<T> response = (new RetriableTask<>(0, 100, () -> {
                HttpUtils.Response<T> getResponse = httpUtils.putBody(host, port, endPoint, new NameValueList(), postBody, new TypeReference<T>() {}, headers);
                if (getResponse == null || (getResponse.getStatusCode() != 200 && getResponse.getStatusCode() != 201)) {
                    System.out.printf("Retrying with END POINT: host = %s, endPoint = %s, port = %s%n", host, endPoint, port);
                    throw new RuntimeException(String.format("Retrying with END POINT: host = %s, endPoint = %s, port = %s%n", host, endPoint, port));
                }
                return getResponse;
            })).call();
            if (response == null) {
                throw new RuntimeException(String.format("Retrying with END POINT: host = %s, endPoint = %s, port = %s%n", host, endPoint, port));
            }
            clientCallData = response.getValue();
            System.out.println("View Api Response = " + response);
            return clientCallData;
        } catch (Exception ex) {
            System.out.printf("ERROR calling Client and Error = %s%n", ex);
            ex.printStackTrace();
        }
        return clientCallData;
    }

    public T postData(String host, int port, String endPoint, Object postBody, Map<String, String> headers) {
        T clientCallData = null;
        try {
            System.out.println();
            HttpUtils.Response<T> response = (new RetriableTask<>(RETRY_COUNT, RETRY_INTERVAL, () -> {
                HttpUtils.Response<T> getResponse = httpUtils.postBody(host, port, endPoint, postBody, new TypeReference<T>() {}, headers);
                if (getResponse == null || (getResponse.getStatusCode() != 200 && getResponse.getStatusCode() != 201)) {
                    System.out.printf("Retrying with END POINT: host = %s, endPoint = %s, port = %s%n", host, endPoint, port);
                    throw new RuntimeException(String.format("Retrying with END POINT: host = %s, endPoint = %s, port = %s%n", host, endPoint, port));
                }
                return getResponse;
            })).call();
            if (response == null) {
                throw new RuntimeException(String.format("Retrying with END POINT: host = %s, endPoint = %s, port = %s%n", host, endPoint, port));
            }
            clientCallData = response.getValue();
            System.out.println("View Api Response = " + response);
            return clientCallData;
        } catch (Exception ex) {
            System.out.printf("ERROR calling Client and Error = %s%n", ex);
            ex.printStackTrace();
        }
        return clientCallData;
    }


    public T getData(String host, String endPoint) {
        T clientCallData = null;
        try {
            log.info("BADGER END POINT: host = {}, port = {}", host, 0);
            HttpUtils httpUtils = new HttpUtils();
            HttpUtils.Response<T> response = (new RetriableTask<>(3, 2000, () -> {
                HttpUtils.Response<T> getResponse =  httpUtils.get(host, 0, endPoint, new NameValueList(), new TypeReference<T>(){});
                if (getResponse == null || (getResponse.getStatusCode() != 200 && getResponse.getStatusCode() != 201)) {
                    log.warn("Retrying");
                    throw new RuntimeException("Retrying");
                }
                return getResponse;
            })).call();
            if (response == null) {
                throw new RuntimeException("Retrying");
            }
            clientCallData = response.getValue();
            log.info("BADGER: View Api Response = {}", clientCallData);

            return clientCallData;
        }
        catch (Exception ex) {
            log.error("ERROR calling Client and Error = ",  ex);
            ex.printStackTrace();
        }
        return  clientCallData;
    }

    public ClientCallData getDataUsingOriginal()  {
        // https://reqres.in/api/products/3
        final String host = "reqres.in";
        final int port = 0;
        final String endPoint = "/api/products/3";
        ClientCallData clientCallData = null;
        try {
            log.info("BADGER END POINT: host = {}, port = {}", host, port);
            HttpUtils httpUtils = new HttpUtils();
            HttpUtils.Response<ClientCallData> response = (new RetriableTask<>(3, 2000, () -> {
                HttpUtils.Response<ClientCallData> getResponse =  httpUtils.get(host, port, endPoint, new NameValueList(), new TypeReference<ClientCallData>(){});
                if (getResponse == null || (getResponse.getStatusCode() != 200 && getResponse.getStatusCode() != 201)) {
                    log.warn("Retrying");
                    throw new RuntimeException("Retrying");
                }
                return getResponse;
            })).call();
            if (response == null) {
                throw new RuntimeException("Retrying");
            }
            clientCallData = response.getValue();
            log.info("BADGER: View Api Response = {}", clientCallData);

            return clientCallData;
        }
        catch (Exception ex) {
            log.error("ERROR calling Client and Error = ",  ex);
            ex.printStackTrace();
        }
        return clientCallData;
    }
}
