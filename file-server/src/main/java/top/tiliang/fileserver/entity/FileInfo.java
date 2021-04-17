package top.tiliang.fileserver.entity;

import lombok.Data;

import java.util.Date;

@Data
public class FileInfo {
    private Integer id;

    private String name;

    private Long size;

    private String type;

    private String originalName;

    private Date createTime;

    private String dir;
}