package top.tiliang.fileserver.service;

import top.tiliang.fileserver.entity.FileInfo;

public interface FileInfoService {

    /**
     * 插入，id自动生成
     * @param record
     * @return 0|1 成功|失败
     */
    int insert(FileInfo record);

    /**
     * 通过id查询
     * @param id
     * @return
     */
    FileInfo selectByPrimaryKey(Integer id);

    /**
     * 通过文件名（uuid）查询
     * @param name 文件名（uuid）
     * @return
     */
    FileInfo selectByName(String name);
}
