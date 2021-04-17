import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    private Client client;

    @BeforeEach
    void init(){
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8888);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, inetSocketAddress);
        client=new Client("http://localhost:8080/file",proxy);
    }


    @Test
    void uploadFile() throws Exception {
        System.out.println(client.uploadFile(new File("C:\\Users\\31261\\Desktop\\小程序测试.mp4")));
    }

    @Test
    void getFileInfo() throws Exception {
        FileInfo fileInfo= client.getFileInfo("358da090-d69e-4406-b6ad-ab127f871acf");
        System.out.println(fileInfo);
    }

    @Test
    void downloadFile() throws Exception {
        String uuid="450b0674-f189-4e42-b805-57c962ca0372";
        String fileName=client.getFileInfo(uuid).getOriginalName();
        client.downloadFile(uuid,new File("D:\\"+fileName));
    }
}