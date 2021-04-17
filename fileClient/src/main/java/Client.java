import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

public class Client {
    private static final String boundary="-------------client";
    private static final String nextLine = "\r\n";

    private String serverUrl;
    private Proxy proxy=null;

    public Client(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public Client(String serverUrl, Proxy proxy) {
        this.serverUrl = serverUrl;
        this.proxy = proxy;
    }

    private HttpURLConnection getConn(String subUrl) throws IOException {
        if(proxy!=null){
            return  (HttpURLConnection) new URL(serverUrl+subUrl).openConnection(proxy);
        }
        return  (HttpURLConnection) new URL(serverUrl+subUrl).openConnection();
    }

    public String uploadFile(File file) throws IOException {
        HttpURLConnection conn= getConn("/upload");
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Accept-Charset", "utf-8");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        conn.setRequestProperty("Accept", "application/json");
        conn.connect();

        OutputStream out = new DataOutputStream(conn.getOutputStream());
        String header = "--" + boundary + nextLine;
        header += "Content-Disposition: form-data;name=\"uploadFile\";" + "filename=\"" + file.getName() + "\"" + nextLine + nextLine;
        out.write(header.getBytes());
//        out.write(nextLine.getBytes());
        DataInputStream fileIn = new DataInputStream(new FileInputStream(file));
        int length = 0;
        byte[] bufferOut = new byte[2048];
        while ((length = fileIn.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, length);
        }
        fileIn.close();
        String footer = nextLine + "--" + boundary + "--" + nextLine;
        out.write(footer.getBytes());
        out.flush();
        out.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }
        conn.disconnect();
        System.out.println(stringBuilder.toString());

        return null;
    }
    public FileInfo getFileInfo(String name) throws Exception {
        HttpURLConnection conn= getConn("/info/"+name);
        conn.setRequestMethod("GET");
        conn.connect();
        InputStream inputStream = conn.getInputStream();
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }
        conn.disconnect();
        FileInfo res=null;
        JSONObject jsonObject= JSON.parseObject(stringBuilder.toString());
        if(jsonObject.get("status").equals("success")){
            res=jsonObject.getObject("data",FileInfo.class);
        }else{
            throw new Exception("something is error："+jsonObject.get("info"));
        }
        return res;
    }
    public void downloadFile(String name,File targetFile) throws IOException {
        HttpURLConnection conn=getConn("/download/"+name);
        conn.setRequestMethod("GET");
        conn.connect();
        Map<String, List<String>> headers = conn.getHeaderFields();
        String fileName = headers.get("Content-Disposition").get(0);
        System.out.println(fileName);


        if(!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        };
        FileOutputStream fos = new FileOutputStream(targetFile);
        InputStream is = conn.getInputStream();
        int len;
        byte[] b = new byte[2048];
        while((len = is.read(b)) != -1) {
            fos.write(b,0,len);
        }
        fos.close();
        is.close();
        conn.disconnect();

    }
}
