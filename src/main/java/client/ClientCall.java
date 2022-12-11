package client;

import com.fasterxml.jackson.core.type.TypeReference;
import data.ClientCallData;
import httputils.HttpUtils;
import httputils.NameValueList;
import httputils.RetriableTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientCall<T> implements IClient<T> {


    public T getData(String host, int port, String endPoint) {
        T clientCallData = null;
        try {
            log.info("BADGER END POINT: host = {}, port = {}", host, port);
            HttpUtils httpUtils = new HttpUtils();
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
