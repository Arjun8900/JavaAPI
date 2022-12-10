package client;

import com.fasterxml.jackson.core.type.TypeReference;
import data.ClientCallData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientCall {
//    private static final String host = "https://reqres.in/api/products/3";
    private static final String host = "reqres.in";
    private static final int port = 0;
    private static final String endPoint = "/api/products/3";

    public ClientCallData getDummyData2()  {
        ClientCallData clientCallData = null;
        log.info("Trying: {}", host);
        log.warn("Trying: {}", host);
        try {
            System.out.println("BADGER END POINT: " + host + " " + port);
            HttpUtils httpUtils = new HttpUtils();
            HttpUtils.Response<ClientCallData> response = (new RetriableTask<>(3, 2000, () -> {
                HttpUtils.Response<ClientCallData> getResponse =  httpUtils.get(host, port, endPoint, new NameValueList(), new TypeReference<ClientCallData>(){});
                if (getResponse == null || (getResponse.getStatusCode() != 200 && getResponse.getStatusCode() != 201)) {
                    System.out.println("Retrying");
                    throw new RuntimeException("Retrying");
                }
                return getResponse;
            })).call();
            if (response == null) {
                throw new RuntimeException("Retrying");

            }
            clientCallData = response.getValue();
            System.out.println("BADGER: View Api Response = " + clientCallData);

            return clientCallData;
        }
        catch (Exception ex) {
//            System.out.printf("ERROR calling Badger Client for pid = %s and Error = %s%n", pid, ex);
            System.out.printf("ERROR calling  Client and Error = %s%n",  ex);
            ex.printStackTrace();
//            throw new DataCopyFailedException("ERROR: Badger View API Failed even after retrying ");
        }
        return clientCallData;
    }
}
