# 简介
一个简易文件服务器，包括服务端和其客户端sdk

# 项目技术以及主要依赖
- 通过 maven 构建
- 后端使用 SpringBoot+mybatis，数据库为MySQL 8.0.19
- 客户端通过 HttpURLConnection 实现网络传输，仅依赖 fastjson 解析 json 数据

# 服务器配置
数据文件夹、数据源、日志路径、上传大小限制（默认10GB）均在application.properties中
```properties中
# 数据目录
config.fileDir=D:\\dev_data

# 数据源
spring.datasource.url=jdbc:mysql://localhost:3306/file-server?serverTimezone=GMT%2B8
spring.datasource.username=root
spring.datasource.password=000000

# 文件上传大小限制
spring.servlet.multipart.maxFileSize=10GB
spring.servlet.multipart.maxRequestSize=10GB

spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
spring.jackson.time-zone=GMT+8

mybatis.mapper-locations=classpath:mapper/*.xml

# 日志路径
logging.file.name=D://dev_data/log/file-server-spring.log
logging.level.root=info
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} %clr(%5p) [%thread] %clr(%logger{45}){cyan} : %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger : %msg%n

```

# 服务器网络接口
默认部署路径为http://localhost:8080，依据实际部署，下文url可能需要修改。
## 文件上传
- url：http://localhost:8080/file/upload
- 请求类型：post
- 参数：

| 字段       | 解释     |
| ---------- | -------- |
| uploadFile | 上传文件 |

**返回数据**

- 数据格式：json
- 字段解释

| 字段   | 解释                                 |
| ------ | ------------------------------------ |
| status | 上传结果："success"\|"error"         |
| info   | 错误信息，status为"error”时有效      |
| uid    | 文件uuid，用于下载文件和查询文件信息 |





## 获取文件信息

- url：http://localhost:8080/file/info/{uuid}
- 请求类型：get
- 参数：{uuid}应当替换为上传文件后返回的文件uid字段

**返回数据**

- 数据格式：json

- 字段解释：

| 字段   | 解释                            |
| ------ | ------------------------------- |
| status | 查询结果："success"\|"error"    |
| info   | 错误信息，status为"error”时有效 |
| data   | 文件数据                        |

- data字段：

| 属性         | 解释     |
| ------------ | -------- |
| originalName | 原文件名 |
| name         | 文件uuid |
| createTime   | 创建时间 |
| size         | 文件大小 |
| type         | 文件类型 |

例如：

```json
{
  "data": {
    "originalName": "视频测试.mp4",
    "size": 3017056,
    "createTime": "2021-04-16 23:49:47",
    "name": "450b0674-f189-4e42-b805-57c962ca0372",
    "type": "mp4"
  },
  "status": "success"
}
```



## 下载文件

- url：http://localhost:8080/file/download/{uuid}
- 请求类型：get
- 参数：{uuid}应当替换为上传文件后返回的文件uid字段

**返回数据：**

失败状态码为410，无返回数据

成功，返回文件下载数据流，浏览器可直接下载

# 客户端接口

- FileInfo.java：文件信息实体类
- Client.java：客户端类，实现具体请求

## Client.java 方法

```java
/**
* 设置服务器url
* @param serverUrl
*/
public Client(String serverUrl) ;

/**
* 设置服务器url和代理
* @param serverUrl
* @param proxy
*/
public Client(String serverUrl, Proxy proxy);

/**
* 上传文件
* @param file 本地文件
* @return 文件上传后uuid
* @throws IOException
*/
public String uploadFile(File file) throws Exception;

/**
* 获取文件信息
* @param name
* @return
* @throws Exception
*/
public FileInfo getFileInfo(String name) throws Exception ;

/**
* 下载文件
* @param name 文件uuid
* @param targetFile 目标存储文件
* @throws IOException
*/
public void downloadFile(String name,File targetFile) throws IOException;
```

其中，构造函数需要服务器url进行初始化，支持使用代理。

url为【服务器部署路径】+ file

例如：

```java
//无代理
Client client=new Client("http://localhost:8080/file");

//代理
InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8888);
Proxy proxy = new Proxy(Proxy.Type.HTTP, inetSocketAddress);
Client client=new Client("http://localhost:8080/file",proxy);
```

