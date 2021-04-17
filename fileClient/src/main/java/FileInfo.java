import java.util.Date;


public class FileInfo {
    private String name;

    private Long size;

    private String type;

    private String originalName;

    private Date createTime;

    private String dir;

    public FileInfo() {
    }

    public FileInfo(String name, Long size, String type, String originalName, Date createTime, String dir) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.originalName = originalName;
        this.createTime = createTime;
        this.dir = dir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", type='" + type + '\'' +
                ", originalName='" + originalName + '\'' +
                ", createTime=" + createTime +
                ", dir='" + dir + '\'' +
                '}';
    }
}