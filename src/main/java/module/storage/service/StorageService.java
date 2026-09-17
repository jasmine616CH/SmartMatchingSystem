package module.storage.service;

import module.storage.dto.PartAttachmentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {

    /**
     * 上传文件
     * @param file 上传文件
     * @param dto 上传文件参数
     */
    void uploadFile(MultipartFile file, PartAttachmentDTO dto, String objectName) throws Exception;

    /**
     * 删除文件
     * @param fileName 文件名
     * @param userId 用户id
     */
    void deleteFile(String fileName , Long userId);

    /**
     * 下载文件
     * @param fileName 文件名
     * @param userId 用户id
     * @param localFilePath 下载路径
     */
    void downloadFile(String fileName, Long userId, String localFilePath);

    /**
     * 浏览文件
     * @param fileName 文件名字
     * @param userId 用户id
     */
    InputStream previewFile(String fileName, Long userId)throws Exception;

    /**
     * 文件格式获取
     */
    String contentType(String fileName, Long userId);
}
