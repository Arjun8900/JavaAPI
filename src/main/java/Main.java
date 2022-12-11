import client.ClientCall;
import client.IClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.ClientCallData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args)  {
        /*
            We need TypeReference bcoz our method is generic. The type parameter T cannot be reified at runtime,
            even if we pass a TypeReference instance with the type parameter T.
         */
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<ClientCallData> typeReference = new TypeReference<ClientCallData>(){};

        IClient<ClientCallData> clientCall2 = new ClientCall<>();
        ClientCallData accountList = mapper.convertValue(clientCall2.getData("reqres.in", 0, "/api/products/3"), typeReference);
        System.out.println(accountList);
    }
}