package top.tiliang.fileserver.mapper;

import org.springframework.stereotype.Repository;
import top.tiliang.fileserver.entity.FileInfo;


@Repository
public interface FileInfoMapper {
    /**
     * 依据id删除文件
     * @param id 文件主键
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入
     * @param record
     * @return
     */
    int insert(FileInfo record);

    /**
     * 插入非空属性
     * @param record
     * @return
     */
    int insertSelective(FileInfo record);

    /**
     * 通过id检索
     * @param id
     * @return
     */
    FileInfo selectByPrimaryKey(Integer id);

    /**
     * 通过文件名，即uuid检索
     * @param name 文件名（uuid）
     * @return
     */
    FileInfo selectByName(String name);

    /**
     * 依据id更新非空属性
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(FileInfo record);

    /**
     * 依据id更新全部属性
     * @param record
     * @return
     */
    int updateByPrimaryKey(FileInfo record);
}