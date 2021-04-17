package top.tiliang.fileserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.tiliang.fileserver.entity.FileInfo;
import top.tiliang.fileserver.service.FileInfoService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileInfoController {
    private FileInfoService fileInfoService;

    @Value("${config.fileDir}")
    private String fileDir;

    @Autowired
    public FileInfoController(FileInfoService fileInfoService) {
        this.fileInfoService = fileInfoService;
    }

    /**
     * 接受上传文件
     * @param uploadFile
     * @return
     */
    @PostMapping("/upload")
    public Map<String,String> upload(@RequestParam("uploadFile") MultipartFile uploadFile){
        Map<String,String> res=new HashMap<>();
        //处理空文件
        if(uploadFile==null){
            res.put("status","error");
            res.put("info","File is null");
            log.info("上传失败，空文件");
            return res;
        }

        //获取原始名称、类型名
        String originalName=uploadFile.getOriginalFilename().toLowerCase();
        String type=originalName.substring(originalName.lastIndexOf('.')+1);
        //处理无类型名
        if(type.equals(originalName)){
            type="";
        }

        //获取日期、存储子目录、uuid
        Date curDate=new Date();
        String dir=new SimpleDateFormat("yyyyMMdd").format(curDate);
        String name=UUID.randomUUID().toString();

        //设置信息
        FileInfo fileInfo=new FileInfo();
        fileInfo.setOriginalName(originalName);
        fileInfo.setCreateTime(curDate);
        fileInfo.setSize(uploadFile.getSize());
        fileInfo.setType(type);
        fileInfo.setName(name);
        fileInfo.setDir(dir);
        log.info("解析上传信息：{}",fileInfo.toString());

        //存储文件
        File file=new File(fileDir+"/"+dir+"/"+name);
        try {
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            uploadFile.transferTo(file);
            log.info("转存到文件：{}",file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            res.put("status","error");
            res.put("info","File storage error");
            log.error("转存失败：{}",file.getPath());
            return res;
        }

        //存储数据库
        if(fileInfoService.insert(fileInfo)==0){
            res.put("status","success");
            res.put("uid",name);
            log.info("数据库插入：{}",fileInfo);
        }else{
            res.put("status","error");
            res.put("info","File storage error");
            //失败后删除已存储的文件
            file.delete();
            log.error("插入失败：{}，删除文件：{}",fileInfo,file.getPath());
        }
        return res;
    }

    /**
     * 下载文件
     * @param name 文件uuid
     * @return
     */
    @GetMapping("/download/{name}")
    public ResponseEntity<Object> download(@PathVariable("name")String name){
        log.info("下载请求：{}",name);
        FileInfo fileInfo=fileInfoService.selectByName(name);
        log.info("检索数据库：{}",fileInfo);
        //出错时的回复
        ResponseEntity<Object> errorRes= ResponseEntity.status(410).build();
        if(fileInfo==null){
            return errorRes;
        }

        //获取文件流
        File file=new File(fileDir+"/"+fileInfo.getDir()+"/"+name);
        InputStreamResource resource=null;
        try {
            resource = new InputStreamResource( new FileInputStream( file ) );
            log.info("获取文件流：{}",file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return errorRes;
        }

        //设置返回信息
        HttpHeaders headers = new HttpHeaders();
        headers.add ( "Content-Disposition",
                "attachment;filename="+ URLEncoder.encode(fileInfo.getOriginalName()).replace("+", "%20"));
        headers.add ( "Cache-Control","no-cache,no-store,must-revalidate" );
        headers.add ( "Pragma","no-cache" );
        headers.add ( "Expires","0" );

        ResponseEntity<Object> res = ResponseEntity.ok()
                .headers ( headers )
                .contentLength ( file.length ())
                .contentType(MediaType.parseMediaType ( "application/octet-stream" ))
                .body(resource);

        return res;
    }

    /**
     * 获取文件信息
     * @param name 文件uuid
     * @return
     */
    @GetMapping("/info/{name}")
    public Map<String,Object>getFileInfo(@PathVariable("name")String name){
        log.info("查询请求：{}",name);
        Map<String,Object> res=new HashMap<>();
        FileInfo fileInfo=fileInfoService.selectByName(name);
        log.info("检索数据库：{}",fileInfo);

        //设置返回信息
        if(fileInfo==null){
            res.put("status","error");
            res.put("info","no such file");
        }else{
            Map<String,Object> data=new HashMap<>();
            data.put("name",fileInfo.getName());
            data.put("originalName",fileInfo.getOriginalName());
            data.put("size",fileInfo.getSize());
            data.put("type",fileInfo.getType());
            data.put("createTime",fileInfo.getCreateTime());
            res.put("status","success");
            res.put("data",data);
        }

        log.info("返回数据：{}",res);
        return res;
    }
}
