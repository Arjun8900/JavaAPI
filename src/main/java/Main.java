import client.ClientCall;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        ClientCall clientCall = new ClientCall();
//        clientCall.getDummyData();
        System.out.println(clientCall.getDummyData2());
    }
}