import client.ClientCall;
import client.IClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.ClientCallData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args)  {
//        ClientCall clientCall = new ClientCall();
//        log.info("Output {}", clientCall.getDataUsingOriginal());

        IClient<ClientCallData> clientCall2 = new ClientCall<>();
        ClientCallData accountList = new ObjectMapper().convertValue(clientCall2.getData("reqres.in", 0, "/api/products/3"), new TypeReference<ClientCallData>(){});
        System.out.println(accountList);
    }
}