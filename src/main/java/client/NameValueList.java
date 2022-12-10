package client;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class NameValueList {
    private List<NameValuePair> argList = new ArrayList<NameValuePair>();

    public NameValueList add(String key, String value)
    {
        argList.add(new BasicNameValuePair(key, value));
        return this;
    }

    public List<NameValuePair> build()
    {
        return argList;
    }
}
