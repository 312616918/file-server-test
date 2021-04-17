import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

public class Client {
    private static final String boundary="-------------client";
    private static final String nextLine = "\r\n";

    //服务器路径
    private String serverUrl;

    //代理
    private Proxy proxy=null;

    /**
     * 设置服务器url
     * @param serverUrl
     */
    public Client(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * 设置服务器url和代理
     * @param serverUrl
     * @param proxy
     */
    public Client(String serverUrl, Proxy proxy) {
        this.serverUrl = serverUrl;
        this.proxy = proxy;
    }

    /**
     * 获取HttpURLConnection
     * @param subUrl 请求子url
     * @return
     * @throws IOException
     */
    private HttpURLConnection getConn(String subUrl) throws IOException {
        if(proxy!=null){
            return  (HttpURLConnection) new URL(serverUrl+subUrl).openConnection(proxy);
        }
        return  (HttpURLConnection) new URL(serverUrl+subUrl).openConnection();
    }

    /**
     * 上传文件
     * @param file 本地文件
     * @return 文件上传后uuid
     * @throws IOException
     */
    public String uploadFile(File file) throws Exception {
        HttpURLConnection conn= getConn("/upload");
        //设置连接信息
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Accept-Charset", "utf-8");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        conn.setRequestProperty("Accept", "application/json");
        conn.connect();

        //设置上传数据
        OutputStream out = new DataOutputStream(conn.getOutputStream());
        String header = "--" + boundary + nextLine;
        header += "Content-Disposition: form-data;name=\"uploadFile\";" + "filename=\"" + file.getName() + "\"" + nextLine + nextLine;
        out.write(header.getBytes());
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

        //读取下载信息
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }
        conn.disconnect();

        //解析json
        JSONObject jsonObject= JSON.parseObject(stringBuilder.toString());
        if(jsonObject.get("status").equals("success")){
            return (String) jsonObject.get("uid");
        }else{
            throw new Exception("something is wrong："+jsonObject.get("info"));
        }
    }

    /**
     * 获取文件信息
     * @param name 文件uuid
     * @return
     * @throws Exception
     */
    public FileInfo getFileInfo(String name) throws Exception {
        //设置请求信息
        HttpURLConnection conn= getConn("/info/"+name);
        conn.setRequestMethod("GET");
        conn.connect();

        //获取下载后信息
        InputStream inputStream = conn.getInputStream();
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }
        conn.disconnect();

        //解析json数据
        FileInfo res=null;
        JSONObject jsonObject= JSON.parseObject(stringBuilder.toString());
        if(jsonObject.get("status").equals("success")){
            res=jsonObject.getObject("data",FileInfo.class);
        }else{
            throw new Exception("something is wrong："+jsonObject.get("info"));
        }
        return res;
    }

    /**
     * 下载文件
     * @param name 文件uuid
     * @param targetFile 目标存储文件
     * @throws IOException
     */
    public void downloadFile(String name,File targetFile) throws IOException {

        //设置文件信息
        HttpURLConnection conn=getConn("/download/"+name);
        conn.setRequestMethod("GET");
        conn.connect();
        //获取文件名，此方法中不需要
//        Map<String, List<String>> headers = conn.getHeaderFields();
//        String fileName = headers.get("Content-Disposition").get(0);
//        System.out.println(fileName);


        //写文件
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
