import cn.lazy.visbody.utils.HttpClient;

import java.io.IOException;

public class HttpClientTest {

    public static void main(String[] args) {
        HttpClientTest.testGet(args);
    }

    public static void testGet(String[] args) {

        try {
            String response = HttpClient.get("http://www.baidu.com");
            System.out.println("response = " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testPost(String[] args) {


    }
}
