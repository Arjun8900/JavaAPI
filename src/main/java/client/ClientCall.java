package client;

import com.fasterxml.jackson.core.type.TypeReference;
import data.ClientCallData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientCall {
    // https://reqres.in/api/products/3
    private static final String host = "reqres.in";
    private static final int port = 0;
    private static final String endPoint = "/api/products/3";

    public ClientCallData getDummyApiData()  {
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
