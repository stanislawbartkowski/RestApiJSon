import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

// -c src/test/resources/testpar/restpar20.properties -p 7999


public class Test20 extends PTestRestHelper {

    private static String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJSTTc4Qkhjc1R0cWRqcTlGNDNYUklIQ21DT21YVS1yRi12VVRsUm1meVZ3In0.eyJleHAiOjE2OTEwMTcwNTksImlhdCI6MTY5MTAxNjE1OSwiYXV0aF90aW1lIjoxNjkxMDEzMjcwLCJqdGkiOiIxYzdkYTcxMC04Nzc0LTQyNzItYmFiMy05ZDEwMjBhOThmNWQiLCJpc3MiOiJodHRwOi8vNzcuNzkuMjA4LjYyOjgwODAvcmVhbG1zL1BlcnNldXMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZTc5MjUwYTctOWZmOC00NDUxLWJmYzEtNjQ3ZjIxODUxMDMwIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiUmVhY3QtYXV0aCIsIm5vbmNlIjoiZGQ1NjcyZTktYmFmNy00ZmJiLThlMjAtNTk1MzYwNTMxZjYyIiwic2Vzc2lvbl9zdGF0ZSI6ImUxYjA0MDE3LTA1YTctNDVlMS1iY2UxLTU0MGQyYzBiMWFiNyIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtcGVyc2V1cyJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJzaWQiOiJlMWIwNDAxNy0wNWE3LTQ1ZTEtYmNlMS01NDBkMmMwYjFhYjciLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJGaXJtYSBQZXJzZXVzIiwicHJlZmVycmVkX3VzZXJuYW1lIjoicGVyc2V1cyIsImdpdmVuX25hbWUiOiJGaXJtYSIsImZhbWlseV9uYW1lIjoiUGVyc2V1cyIsImVtYWlsIjoicGVyc2V1c0BwZXJzZXVzLmNvbSJ9.rWZE-A7RABupMzZ1ky0aSH9e7DmlK2dODskLebJybhbrCFoSllYMs9VszcQPBNK9ZenzZ7CTFrEIdAOqPwRSTJuRlvHTuk4eUjMB6BfiGHnxbgtkCWoWIkS_LnOxJGxAXkB2ESGWXSavyrab9bhkqr4btZz2FfxR12lL2Wx4L2tnn8KHswXE5lkQCeMmdAqzo4D26WdIVxcMYAriX436WHQdlF_3LS-cNa5LvBtW4Y0N-8VK0_1DyOrmEPQZwv4lk9Cwih1RWAGzEKEzWr48LY-HwV0G7peknztoAXwY-XcWJl58pbJDp8pdGZz3aIw2UuB84-Q5ncv8kaHj4GXLIg";

    @Test
    public void test1() throws IOException {

        Map<String, String> props = new HashMap<String, String>() {{
            put("uuid", "111111111");
            put("user", "perseus");
        }};
        String res = getok("/test1", null, props);
        System.out.println(res);
        String[] lines = res.split("\n");
        for (String l : lines) {
            System.out.println(l);
        }
        assertEquals("perseus", lines[1]);
        assertEquals("111111111", lines[2]);
    }

    @Test
    public void test2() throws IOException {
        String res = "{\"res\":{\"a\":1,\"b\":2,\"c\":3,\"authlabel\":\"admin\"}}";
        testok("/getres", "resource=list", res);

        //  now authorization label
        Map<String, String> props = new HashMap<String, String>() {{
            put("authlabel", "xxx");
        }};
        testok("/getres", "resource=list", "{}", props);

        Map<String, String> props1 = new HashMap<String, String>() {{
            put("authlabel", "admin");
        }};
        testok("/getres", "resource=list", res, props1);

    }

    @Test
    public void test_authorization() throws IOException {
        Map<String, String> props = new HashMap<String, String>() {{
            put("uuid", "111111111");
            put("user", "perseus");
            put("Authorization","Bearer xxxxxxxx" );
        }};
        String res = getok("/test1", null, props);

    }



    @Test
    public void test_authorization_long() throws IOException {
        Map<String, String> props = new HashMap<String, String>() {{
            put("uuid", "111111111");
            put("user", "perseus");
            put("Authorization","Bearer " + token );
        }};
        String res = getok("/test1", null, props);

    }


}
