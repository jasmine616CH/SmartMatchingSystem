package module.storage.service.impl;

import org.springframework.stereotype.Service;

import module.storage.service.StorageService;

/**
 * 文件存储业务实现类
 * <p>
 * <b>功能尚未实现。</b>接口方法都是无参占位签名，未确定存储后端
 * （part_attachment 表中有 bucket_name / object_name 两列，指向对象存储）。
 * <p>
 * 实现前保持「显式失败」而不是静默返回，避免调用方误以为上传成功。
 */
@Service
public class StorageServiceImpl implements StorageService {

    private static final String NOT_IMPLEMENTED = "文件存储功能尚未实现，请先确定存储后端（对象存储/本地磁盘）";

    @Override
    public String uploadFile() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void deleteFile() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void previewFile() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void downloadFile() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }
}
