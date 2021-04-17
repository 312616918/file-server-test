package top.tiliang.fileserver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.tiliang.fileserver.entity.FileInfo;
import top.tiliang.fileserver.mapper.FileInfoMapper;
import top.tiliang.fileserver.service.FileInfoService;

@Service
public class FileInfoServiceImpl implements FileInfoService {
    private FileInfoMapper fileInfoMapper;

    @Autowired
    public FileInfoServiceImpl(FileInfoMapper fileInfoMapper) {
        this.fileInfoMapper = fileInfoMapper;
    }

    @Override
    public int insert(FileInfo record) {
        fileInfoMapper.insert(record);
        return 0;
    }

    @Override
    public FileInfo selectByPrimaryKey(Integer id) {
        return fileInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public FileInfo selectByName(String name) {
        return fileInfoMapper.selectByName(name);
    }
}
